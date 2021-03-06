/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.studiarbeit.hbci.impl;

import java.util.Date;

import name.studiarbeit.hbci.Balance;
import name.studiarbeit.hbci.Money;

/**
 *
 * @author alex
 */
public class HbciBalance implements Balance {
    private Money available= null;
    private Date timestamp= null;
    
    public Money getAvailable() {
        return this.available;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setAvailable(Money m) {
        this.available= m;
    }

    public void setTimestamp(Date d) {
        this.timestamp= d;
    }

    @Override public String toString() {
        return this.available + " @" + timestamp;
    }
}
