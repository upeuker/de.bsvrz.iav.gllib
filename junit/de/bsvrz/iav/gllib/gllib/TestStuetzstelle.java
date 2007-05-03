/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.5 Funktionen Ganglinie
 * Copyright (C) 2007 BitCtrl Systems GmbH 
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact Information:
 * BitCtrl Systems GmbH
 * Weiﬂenfelser Straﬂe 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Testet relevante Funktionen der Klasse.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
@SuppressWarnings("nls")
public class TestStuetzstelle {

	/**
	 * Testet den Vergleich der St&uuml;tzstellen nach ihren Zeitstempeln.
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

		// Grˆﬂer
		a = new Stuetzstelle(2, 133);
		b = new Stuetzstelle(1, 73);
		assertEquals(1, a.compareTo(b));
	}

	/**
	 * Testet den Test auf Gleichheit.
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
	 * Testet pro forma die toString()-Methode.
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
	 * Testet pro forma die Getter-Methoden.
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
