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

package de.bsvrz.iav.gllib.gllib.math;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Testet alle relevanten Funktionen der Klasse.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class TestVektor {

	/** Erster Vektor f&uuml;r alle Testf&auml;lle. */
	private Vektor a;

	/** Zweiter Vektor f&uuml;r alle Testf&auml;lle. */
	private Vektor b;

	/**
	 * Initialisiert die beiden Testvektoren vor jedem Testfall.
	 */
	@Before
	public void setUp() {
		a = new Vektor(new long[] { 3, 2, 7 });
		b = new Vektor(new long[] { 5, 8, 1 });
	}

	/**
	 * Testet das Eintreten der Ausnahme.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testKonstruktorA() {
		new Vektor(0);
	}

	/**
	 * Testet das Eintreten der Ausnahme.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testKonstruktorB() {
		new Vektor(0);
	}

	/**
	 * Testet die Methode {@link Vektor#addiere(Vektor, Vektor)}.
	 */
	@Test
	public void testAddiere() {
		Vektor c;

		c = new Vektor(8, 10, 8);
		assertEquals(c, Vektor.addiere(a, b));
		assertEquals(c, Vektor.addiere(b, a));
	}

	/**
	 * Testet auf das Eintreten der Ausnahme.
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddiereException() {
		Vektor c;

		c = new Vektor(8, 10);
		Vektor.addiere(a, c);
	}

	/**
	 * Testet die Methode {@link Vektor#subtrahiere(Vektor, Vektor)}.
	 */
	@Test
	public void testSubtrahiere() {
		Vektor c;

		c = new Vektor(-2, -6, 6);
		assertEquals(c, Vektor.subtrahiere(a, b));
	}

	/**
	 * Testet auf das Eintreten der Ausnahme.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSubtrahiereException() {
		Vektor c;

		c = new Vektor(8, 10);
		Vektor.subtrahiere(a, c);
	}

	/**
	 * Testet die Methode {@link Vektor#multipliziere(Vektor, long)}.
	 */
	@Test
	public void testMultipliziere() {
		Vektor c;

		c = new Vektor(6, 4, 14);
		assertEquals(c, Vektor.multipliziere(a, 2));

		c = new Vektor(5, 8, 1);
		assertEquals(c, Vektor.multipliziere(b, 1));
	}

	/**
	 * Testet die Methode {@link Vektor#skalarprodukt(Vektor, Vektor)}.
	 */
	@Test
	public void testSkalarprodukt() {
		assertEquals(new RationaleZahl(38), Vektor.skalarprodukt(a, b));
		assertEquals(new RationaleZahl(38), Vektor.skalarprodukt(b, a));
	}

	/**
	 * Testet auf das Eintreten der ersten Ausnahme.
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSkalarproduktExceptionA() {
		Vektor c;

		c = new Vektor(8, 10);
		Vektor.skalarprodukt(a, c);
	}

	/**
	 * Testet auf das Eintreten der zweiten Ausnahme.
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSkalarproduktExceptionB() {
		Vektor c, d;

		c = new Vektor(8, 1, 5, 5);
		d = new Vektor(7, 3, 6, 4);
		Vektor.skalarprodukt(c, d);
	}

	/**
	 * Testet die Methode {@link Vektor#vektorprodukt(Vektor, Vektor)}.
	 */
	@Test
	public void testVektorprodukt() {
		Vektor c;

		c = new Vektor(-54, 32, 14);
		assertEquals(c, Vektor.vektorprodukt(a, b));
	}

	/**
	 * Testet auf das Eintreten der ersten Ausnahme.
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testVektorproduktExceptionA() {
		Vektor c;

		c = new Vektor(8, 10);
		Vektor.vektorprodukt(a, c);
	}

	/**
	 * Testet auf das Eintreten der zweiten Ausnahme.
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testVektorproduktExceptionB() {
		Vektor c, d;

		c = new Vektor(8, 1);
		d = new Vektor(7, 3);
		Vektor.vektorprodukt(c, d);
	}

	/**
	 * Testet die Methode {@link Vektor#betragQuadrat()}.
	 */
	@Test
	public void testBetragQuadrat() {
		assertEquals(new RationaleZahl(62), a.betragQuadrat());
		assertEquals(new RationaleZahl(90), b.betragQuadrat());
	}

	/**
	 * Testet die Methode {@link Vektor#equals(Object)}.
	 */
	@Test
	public void testEqualsObject() {
		Vektor c;

		assertEquals(a, a);
		assertEquals(b, b);

		c = new Vektor(a);
		assertEquals(a, c);

		c = new Vektor(5, 8, 1);
		assertEquals(b, c);

		c = new Vektor(3, 2);
		assertFalse(a.equals(c));

		assertFalse(a.equals("Unsinns"));
		assertFalse(a.equals(b));
	}

	/**
	 * Testet pro forma die toString()-Methode.
	 * 
	 */
	@Test
	public void testToString() {
		System.out.println(a);
		System.out.println(b);
	}

}
