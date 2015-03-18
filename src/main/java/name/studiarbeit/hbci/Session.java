/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.studiarbeit.hbci;

/**
 *
 * @author alex
 */
public interface Session {

    public void logIn();
    
    public void acquireBalance();

    public void acquireTransactions();
}
