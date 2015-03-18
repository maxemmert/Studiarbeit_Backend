package name.studiarbeit.hbci.impl;

import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.HBCIPassport;

public class Callback extends HBCICallbackConsole {
	private HbciSession session;
	private static String PASSPHRASE = "";

	public Callback(HbciSession session) {
		this.session = session;
	}

	@Override
	public synchronized void status(HBCIPassport passport, int statusTag,
			Object[] o) {
		// Intentionally empty
	}

	@Override
	public void callback(HBCIPassport passport, int reason, String msg,
			int datatype, StringBuffer retData) {
		HBCIUtils.log("[LOG] " + msg + " / Reason: " + reason + " / datatype: "
				+ datatype, HBCIUtils.LOG_DEBUG);

		switch (reason) {
		case NEED_BLZ:
			retData.append(this.session.acct.getBankCode());
			break;

		case NEED_CUSTOMERID:
			if (null != this.session.acct.getCredentials().getCustomerId()) {
				retData.append(this.session.acct.getCredentials()
						.getCustomerId());
			}
			break;

		case NEED_USERID:
			if (null != this.session.acct.getCredentials().getUserId()) {
				retData.append(this.session.acct.getCredentials().getUserId());
			}
			break;

		case NEED_PT_PIN:
			retData.append(this.session.acct.getCredentials().getPin());
			break;

		case NEED_PASSPHRASE_SAVE:
		case NEED_PASSPHRASE_LOAD:
			retData.append(PASSPHRASE);
			break;

		case NEED_PT_SECMECH:
			retData.append("mobileTAN");
			break;

		case NEED_COUNTRY:
		case NEED_HOST:
		case NEED_CONNECTION:
		case CLOSE_CONNECTION:
		default:
			// Intentionally empty!
		}

		HBCIUtils.log("Returning " + retData.toString(), HBCIUtils.LOG_DEBUG);
	}
}
