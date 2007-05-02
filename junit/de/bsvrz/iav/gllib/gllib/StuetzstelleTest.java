package de.bsvrz.iav.gllib.gllib;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Testet relevante Funktionen der Klasse
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class StuetzstelleTest {

	/**
	 * Testet den Vergleich der St&uuml;tzstellen nach ihren Zeitstempeln
	 */
	@Test
	public void testCompareTo() {
		Stuetzstelle a, b;

		// Kleiner
		a = new Stuetzstelle(1, 234);
		b = new Stuetzstelle(2, 64);
		assertEquals(-1, a.compareTo(b));

		// Gleich
		a = new Stuetzstelle(2, 456);
		b = new Stuetzstelle(2, 522);
		assertEquals(0, a.compareTo(b));

		// Größer
		a = new Stuetzstelle(2, 133);
		b = new Stuetzstelle(1, 73);
		assertEquals(1, a.compareTo(b));
	}

	/**
	 * Testet den Test auf Gleichheit
	 */
	@Test
	public void testEqualsObject() {
		Stuetzstelle a, b, c;

		// reflexiv
		a = new Stuetzstelle(1);
		assertEquals("reflexiv, nur Zeitstempel", a, a);
		a = new Stuetzstelle(1, 45);
		assertEquals("reflexiv, Zeitstempel und Wert", a, a);
		
		// symetrisch
		a = new Stuetzstelle(1);
		b = new Stuetzstelle(1);
		assertEquals("symetrisch, nur Zeitstempel", a, b);
		assertEquals("symetrisch, nur Zeitstempel", b, a);
		a = new Stuetzstelle(1, 45);
		b = new Stuetzstelle(1, 45);
		assertEquals("symetrisch, Zeitstempel und Wert", a, b);
		assertEquals("symetrisch, Zeitstempel und Wert", b, a);

		// transitiv
		a = new Stuetzstelle(1);
		b = new Stuetzstelle(1);
		c = new Stuetzstelle(1);
		assertEquals("transitiv, nur Zeitstempel", a, b);
		assertEquals("transitiv, nur Zeitstempel", b, c);
		assertEquals("transitiv, nur Zeitstempel", a, c);
		a = new Stuetzstelle(1, 45);
		b = new Stuetzstelle(1, 45);
		c = new Stuetzstelle(1, 45);
		assertEquals("transitiv, Zeitstempel und Wert", a, b);
		assertEquals("transitiv, Zeitstempel und Wert", b, c);
		assertEquals("transitiv, Zeitstempel und Wert", a, c);
		
		// ungleich
		a = new Stuetzstelle(1, 734);
		b = new Stuetzstelle(1, 113);
		assertFalse(a.equals(b));
		assertFalse(b.equals(a));
		assertFalse(a.equals("Unsinn"));
	}
	
	/**
	 * Testet pro forma die toString()-Methode
	 */
	@Test
	public void testToString() {
		Stuetzstelle a;
		
		a = new Stuetzstelle(23);
		System.out.println(a);
		
		a = new Stuetzstelle(34, 4123);
		System.out.println(a);
	}
	
	/**
	 * Testet pro forma die Getter-Methoden
	 */
	@Test
	public void testGetter() {
		Stuetzstelle a;
		
		a = new Stuetzstelle(4);
		assertEquals(4L, a.getZeitstempel());
		assertNull(a.getWert());
		
		a = new Stuetzstelle(4, 6234);
		assertEquals(4L, a.getZeitstempel());
		assertEquals(6234, a.getWert());
	}

}
