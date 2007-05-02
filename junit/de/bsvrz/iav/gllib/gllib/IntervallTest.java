package de.bsvrz.iav.gllib.gllib;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Testet relevante Funktionen der Klasse
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class IntervallTest {

	/**
	 * Testet pro forma die Getter-Methoden
	 */
	@Test
	public void testGetter() {
		Intervall a;

		a = new Intervall(3, 6);
		assertEquals(3L, a.getStart());
		assertEquals(6L, a.getEnde());
	}

	/**
	 * Wenn Start und Ende vertauscht sind, also Start gr&ouml;&szlig;er als
	 * Ende, dann muss eine Exception geworfen werden
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testKonstruktor() {
		new Intervall(8, 2);
	}

}
