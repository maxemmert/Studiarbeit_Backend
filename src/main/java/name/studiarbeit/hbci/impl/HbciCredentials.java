/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.studiarbeit.hbci.impl;

import name.studiarbeit.hbci.Credentials;

/**
 *
 * @author alex
 */
public final class HbciCredentials implements Credentials {
	private String customerId = null;
	private String userId = null;
	private String pin = null;

	public HbciCredentials(String customerId) {
		this.setCustomerId(customerId);
	}

	public HbciCredentials() {
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
