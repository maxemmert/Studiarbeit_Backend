/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package name.studiarbeit.hbci.passport;

import org.kapott.hbci.passport.HBCIPassportPinTan;

/**
 *
 * @author alex
 */
public class HBCIPassportNonPersistentPinTan1 extends HBCIPassportPinTan {

	// TODO: Um Passport daten zu bearbeiten einfach setter-Methoden der
	// Parent-Klasse aufrufen und dann saveChanges()-Methode aufrufen!

	public HBCIPassportNonPersistentPinTan1(Object initObject) {
		super(initObject);
	}

	public HBCIPassportNonPersistentPinTan1(Object init, int dummy) {
		super(init, dummy);
	}

	@Override
	public void saveChanges() {
		// Intentionally empty
	}

}
