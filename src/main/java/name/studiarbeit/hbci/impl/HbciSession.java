/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.studiarbeit.hbci.impl;

import java.util.Iterator;
import java.util.List;

import name.studiarbeit.hbci.Session;

import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.GV_Result.GVRKUms.UmsLine;
import org.kapott.hbci.GV_Result.GVRSaldoReq;
import org.kapott.hbci.GV_Result.GVRSaldoReq.Info;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.HBCIPassportNonPersistentPinTan;
import org.kapott.hbci.status.HBCIExecStatus;
import org.kapott.hbci.structures.Konto;

/**
 *
 * @author alex
 */
public class HbciSession implements Session {
	private String passportPath = null;
	HbciAccount acct;
	private String blz;
	private String country;
	private String userId;
	private int port;
	private String filterType;
	private String host;
	private String currentTanMethod;
	private static String PASSPHRASE = "";

	/**
	 * HBCI Session mit Credentials füllen mit Parametern. MUSS! verwendet
	 * werden bei NonPersistentPinTan. Und zwar bevor balance oder transaction
	 * geholt werden.
	 * 
	 * @param blz
	 *            Bankleiztahl
	 * @param country
	 *            Land (DE)
	 * @param userId
	 *            Benutzername
	 * @param port
	 *            Port (443)
	 * @param filterType
	 *            filtertyp (None oder Base64)
	 * @param host
	 *            host (hbci-pintan-bw.s-hbci.de/PinTanServlet)
	 * @param currentTanMethod
	 *            (z.b. 911 für optischTan)
	 * @return
	 */
	public void setCredentials(String blz, String country, String userId,
			int port, String filterType, String host, String currentTanMethod) {
		this.blz = blz;
		this.country = country;
		this.userId = userId;
		this.port = port;
		this.filterType = filterType;
		this.host = host;
		this.currentTanMethod = currentTanMethod;
	}

	public static void setPASSPHRASE(String pASSPHRASE) {
		PASSPHRASE = pASSPHRASE;
	}

	public HbciSession(HbciAccount a) {
		this.acct = a;

		this.initialize();
	}

	@Override
	public void logIn() {
		HBCIHandler handle = this.createHbciHandler();
	}

	private HBCIHandler createHbciHandler() {
		HBCIPassportNonPersistentPinTan passport = (HBCIPassportNonPersistentPinTan) AbstractHBCIPassport
				.getInstance();
		// HBCIPassport passport = AbstractPinTanPassport.getInstance();

		passport.setBLZ(this.blz);
		passport.setCountry(this.country);
		passport.setUserId(this.userId);
		passport.setPort(this.port);
		passport.setFilterType(this.filterType);
		passport.setHost(this.host);
		passport.setCurrentTANMethod(this.currentTanMethod);

		HBCIHandler handle = new HBCIHandler(this.acct.getVersion().toParam(),
				passport);
		return handle;
	}

	public void clearCachedDetails(HBCIPassport passport) {
		passport.clearBPD();
		passport.clearUPD();
	}

	private void initialize() {
		HBCIUtils.init(null, new Callback(this));

		// Set basic parameters
		HBCIUtils.setParam("client.passport.hbciversion.default", this.acct
				.getVersion().toParam());
		HBCIUtils.setParam("client.connection.localPort", null);
		HBCIUtils.setParam("log.loglevel.default", "3");
		HBCIUtils.setParam("kernel.rewriter",
				HBCIUtils.getParam("kernel.rewriter"));

		// Configure for PinTan
		HBCIUtils.setParam("client.passport.PinTan.checkcert", "1");
		HBCIUtils.setParam("client.passport.PinTan.certfile", null);
		HBCIUtils.setParam("client.passport.PinTan.init", "1");

		// Set path & passport implementation for passport
		if (null == this.passportPath) {
			HBCIUtils
					.setParam("client.passport.default", "NonPersistentPinTan");
			HBCIUtils.setParam("client.passport.PinTan.filename", null);
		} else {
			HBCIUtils.setParam("client.passport.default", "PinTan");
			HBCIUtils.setParam("client.passport.PinTan.filename",
					this.passportPath);
		}
	}

	public Konto findAccount(HBCIHandler handle) {
		Konto[] accounts = handle.getPassport().getAccounts();
		// TODO: konto nr rausnehmen
		if (accounts.length == 0) {
			Konto konto = new Konto();
			konto.blz = handle.getPassport().getBLZ();
			konto.country = handle.getPassport().getCountry();
			konto.curr = "EUR";
			konto.customerid = handle.getPassport().getCustomerId();
			konto.number = "1000482517";
			return konto;

		}

		for (Konto account : accounts) {
			if (this.acct.getBankCode().equals(account.blz)
					&& this.acct.getAccountNumber().equals(account.number))
				return account;
		}

		throw new IllegalStateException("Unable to find requested account "
				+ this.acct.getAccountNumber());
	}

	@Override
	public void acquireBalance() {
		HBCIHandler handle = this.createHbciHandler();

		Konto k = this.findAccount(handle);
		System.out.println("Have account: " + k);

		HBCIJob job = handle.newJob("SaldoReq");
		job.setParam("my", k);

		job.addToQueue();

		HBCIExecStatus ret = handle.execute();

		GVRSaldoReq result = (GVRSaldoReq) job.getJobResult();

		if (!result.isOK()) {
			throw new IllegalStateException("Fetching balance failed: "
					+ result.getJobStatus().getErrorString() + " / "
					+ result.getGlobStatus().getErrorString());
		}

		// TODO: Schleife anpassen, damit Saldo richtig eingetragen wird in das
		// Balance-Objekt!

		for (Info info : result.getEntries()) {
			// if (!k.equals(info.konto))
			// continue;

			this.acct.getBalance().setAvailable(
					HbciMoney.fromValue(info.ready.value));
			this.acct.getBalance().setTimestamp(info.ready.timestamp);
		}
	}

	@Override
	public void acquireTransactions() {
		HBCIHandler handle = this.createHbciHandler();

		Konto k = this.findAccount(handle);
		System.out.println("Using account " + k);

		HBCIJob job = handle.newJob("KUmsAll");
		job.setParam("my", k);

		job.addToQueue();

		HBCIExecStatus ret = handle.execute();

		GVRKUms result = (GVRKUms) job.getJobResult();
		if (!result.isOK()) {
			throw new IllegalStateException("Fetching balance failed: "
					+ result.getJobStatus().getErrorString() + " / "
					+ result.getGlobStatus().getErrorString());
		}

		List lines = result.getFlatData();
		for (Iterator i = lines.iterator(); i.hasNext();) {
			this.acct.addTransaction(HbciTransaction.fromUmsLine((UmsLine) i
					.next()));
		}
	}

	public String bankName() {
		return HBCIUtils.getNameForBLZ(this.acct.getBankCode());
	}

	public String hbciHost() {
		return HBCIUtils.getHBCIHostForBLZ(this.acct.getBankCode());
	}

	public String hbciVersion() {
		return HBCIUtils.getHBCIVersionForBLZ(this.acct.getBankCode());
	}

	public String getPassportPath() {
		return passportPath;
	}

	public void setPassportPath(String passportPath) {
		this.passportPath = passportPath;
	}

}
