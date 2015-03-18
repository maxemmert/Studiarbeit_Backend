package name.studiarbeit.hbci.facade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import name.studiarbeit.hbci.AccountReference;
import name.studiarbeit.hbci.Balance;
import name.studiarbeit.hbci.Money;
import name.studiarbeit.hbci.Transaction;
import name.studiarbeit.hbci.Transactions;
import name.studiarbeit.hbci.impl.HbciAccount;
import name.studiarbeit.hbci.impl.HbciCredentials;
import name.studiarbeit.hbci.impl.HbciSession;
import name.studiarbeit.hbci.impl.HbciVersion;

import org.kapott.hbci.manager.XMLStorage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws IOException {

		Helper helper = new Helper();
		Document document = helper.getDocument();
		HashMap<String, Element> hashMap = App.buildHashMap(document);
		XMLStorage.setHashMap(hashMap);
		System.out.println("Starting ...");
		String acct, code, name, securityCode;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.print("Account: ");
		acct = br.readLine();

		System.out.print("Bank code: ");
		code = br.readLine();
		long startT = System.currentTimeMillis();
		HbciAccount a = new HbciAccount(acct, code, document);
		a.setVersion(HbciVersion.PLUS);
		a.setCredentials(new HbciCredentials());
		a.setAccountNumber("1000482517"); // TODO: kann man bestimmt wieder
											// rausnehmen!!

		System.out.print("PIN: ");
		a.getCredentials().setPin(br.readLine());

		// TODO:Kann raus
		// System.out.print("Passwort fuer Sicherungsdatei: ");
		// securityCode = br.readLine();
		//
		// HbciSession.setPASSPHRASE(securityCode);
		long accountT = System.currentTimeMillis();

		HbciSession session = (HbciSession) a.createHbciSession();
		long sessionT = System.currentTimeMillis();

		String blz = "61050000";
		String country = "DE";
		String userId = "max_emmert";
		int port = 443;
		String filterType = "Base64";
		String host = "hbci-pintan-bw.s-hbci.de/PinTanServlet";
		String currentTanMethod = "911";
		session.setCredentials(blz, country, userId, port, filterType, host,
				currentTanMethod);

		// session.setPassportPath("my_passport_pintan.dat");
		session.setPassportPath(null);

		session.logIn();
		long loginT = System.currentTimeMillis();

		session.acquireBalance();
		long balanceT = System.currentTimeMillis();

		session.acquireTransactions();
		long transT = System.currentTimeMillis();

		Transactions transactions = a.getTransactions();

		Balance balance = a.getBalance();

		if (transactions != null) {

			Iterator<Transaction> transactionIterator = transactions
					.getIterator();

			int i = 0;
			while (i < 10 && transactionIterator.hasNext()) {
				Transaction currentTransaction = transactionIterator.next();
				// Hole Überweisungsbetrag
				Money money = currentTransaction.getBalance();
				System.out.println("Money: " + money.toString());
				// Hole Überweisungsdatum
				Date date = currentTransaction.getBookingDate();
				System.out.println("Date: " + date.toString());
				// Hole Zielaccount bzw. "Gegenaccount"
				AccountReference counterAccount = currentTransaction
						.getCounterAccount();
				System.out
						.println("Gegenaccount: " + counterAccount.toString());
				// Hole Überweisungstext als List<String> KP OB ES FUNZT!
				List<String> textList = currentTransaction.getUsageLines();
				if (textList != null) {
					System.out.print(textList.toString());
				}
				System.out.println("\n");
			}

			System.out.print("Latest Transaction: \n"
					+ transactions.latestTransaction().toString() + "\n"
					+ transactions.latestTransaction());
		}
		System.out.println("\n");
		// Timestamp und Money vom Balance-Objekt holen
		if (balance != null) {
			Money money = balance.getAvailable();
			System.out.println("Available Money: " + money.toString() + "\n");
			Date date = balance.getTimestamp();
			System.out.println("Time: " + date.toString());
		}

		System.out.println("\n");
		System.out.println("_END_");

		System.out
				.println("Total execution time trans: " + (transT - balanceT));
		System.out.println("Total execution time balance: "
				+ (balanceT - loginT));
		System.out
				.println("Total execution time login: " + (loginT - sessionT));
		System.out.println("Total execution time session: "
				+ (sessionT - accountT));
		System.out.println("Total execution time account: "
				+ (accountT - startT));
	}

	public static HashMap<String, Element> buildHashMap(Document doc) {
		HashMap<String, Element> hm = new HashMap<String, Element>();
		NodeList nodeList = doc.getDocumentElement().getChildNodes();

		for (int i = 0; i <= nodeList.getLength(); i++) {
			if (nodeList.item(i) instanceof Element) {
				NodeList nodeList2 = nodeList.item(i).getChildNodes();
				for (int j = 0; j <= nodeList2.getLength(); j++) {
					if (nodeList2.item(j) instanceof Element) {
						NamedNodeMap attributes = nodeList2.item(j)
								.getAttributes();
						if (attributes != null) {
							if (attributes.getNamedItem("id") != null) {
								hm.put(((Element) nodeList2.item(j))
										.getAttribute("id"),
										(Element) nodeList2.item(j));
							}
						}
					}
				}
			}
		}

		return hm;
	}
}
