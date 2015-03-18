/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.studiarbeit.hbci.impl;

import name.studiarbeit.hbci.Account;
import name.studiarbeit.hbci.Balance;
import name.studiarbeit.hbci.Credentials;
import name.studiarbeit.hbci.Session;
import name.studiarbeit.hbci.Transaction;
import name.studiarbeit.hbci.Transactions;

import org.kapott.hbci.manager.XMLStorage;
import org.w3c.dom.Document;

/**
 *
 * @author alex
 */
public final class HbciAccount implements Account {
	private String account;
	private String bankCode;
	private Credentials credentials;
	private Balance balance = null;
	private Transactions transactions = null;
	private String url = null;
	private HbciVersion version = HbciVersion.PLUS;
	private Document document;

	public HbciAccount(final String acct, final String code, Document document) {
		this.setAccountNumber(acct);
		this.setBankCode(code);
		this.setDocument(document);
		this.transactions = new HbciTransactions();
	}

	@Override
	public void setAccountNumber(String acct) {
		this.account = acct;
	}

	@Override
	public String getAccountNumber() {
		return this.account;
	}

	@Override
	public void setBankCode(String code) {
		this.bankCode = code;
	}

	@Override
	public String getBankCode() {
		return this.bankCode;
	}

	@Override
	public void setCredentials(Credentials c) {
		this.credentials = c;
	}

	@Override
	public Credentials getCredentials() {
		if (null == this.credentials) {
			this.credentials = new HbciCredentials();
		}
		return this.credentials;
	}

	public Session createHbciSession() {
		return new HbciSession(this);
	}

	public HbciVersion getVersion() {
		return version;
	}

	public void setVersion(HbciVersion version) {
		this.version = version;
	}

	@Override
	public Balance getBalance() {
		if (null == this.balance) {
			this.balance = new HbciBalance();
		}

		return this.balance;
	}

	@Override
	public void addTransaction(Transaction t) {
		this.transactions.addTransaction(t);
	}

	@Override
	public Transactions getTransactions() {
		return this.transactions;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName()).append("(")
				.append(this.hashCode()).append(") {\n");
		sb.append("  [account  ] ").append(this.getAccountNumber())
				.append("\n");
		sb.append("  [bankCode ] ").append(this.getBankCode()).append("\n");
		if (null != this.balance) {
			sb.append("  [balance  ] ").append(this.balance.toString())
					.append("\n");
		}
		sb.append("}");
		return sb.toString();
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
		XMLStorage.setDocument(document);
	}
}
