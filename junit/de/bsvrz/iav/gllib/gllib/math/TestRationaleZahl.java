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
public class TestRationaleZahl {

	/** Erste rationale Zahl f&uuml;r alle Testf&auml;lle. */
	private RationaleZahl a;

	/** Zweite rationale Zahl f&uuml;r alle Testf&auml;lle. */
	private RationaleZahl b;

	/**
	 * Initialisiert die Testzahlen vor jedem Testfall.
	 */
	@Before
	public void setUp() {
		a = new RationaleZahl(1, 4);
		b = new RationaleZahl(5, 8);
	}

	/**
	 * Testet das Eintreten der Ausnahme beim Konstruktor.
	 */
	@Test(expected = ArithmeticException.class)
	public void testKonstruktor() {
		new RationaleZahl(3, 0);
	}

	/**
	 * Testet pro forma den Getter des Z&auml;hlers.
	 * 
	 */
	@Test
	public void testGetZaehler() {
		assertEquals(1L, a.getZaehler());
		assertEquals(5L, b.getZaehler());
	}

	/**
	 * Testet pro forma den Getter des Nenners.
	 * 
	 */
	@Test
	public void testGetNenenr() {
		assertEquals(4L, a.getNenner());
		assertEquals(8L, b.getNenner());
	}

	/**
	 * Testet die Methode {@link RationaleZahl#intValue()}.
	 */
	@Test
	public void testIntValue() {
		assertEquals(0, a.intValue());
		assertEquals(1, b.intValue());
	}

	/**
	 * Testet die Methode {@link RationaleZahl#longValue()}.
	 */
	@Test
	public void testLongValue() {
		assertEquals(0L, a.longValue());
		assertEquals(1L, b.longValue());
	}

	/**
	 * Testet die Methode {@link RationaleZahl#floatValue()}.
	 */
	@Test
	public void testFloatValue() {
		assertEquals(0.25f, a.floatValue());
		assertEquals(0.625f, b.floatValue());
	}

	/**
	 * Testet die Methode {@link RationaleZahl#doubleValue()}.
	 */
	@Test
	public void testDoubleValue() {
		assertEquals(0.25, a.doubleValue());
		assertEquals(0.625, b.doubleValue());
	}

	/**
	 * Testet die Methode
	 * {@link RationaleZahl#addiere(RationaleZahl, RationaleZahl)}.
	 */
	@Test
	public void testAddiere() {
		RationaleZahl r;

		r = new RationaleZahl(7, 8);
		assertEquals(r, RationaleZahl.addiere(a, b));
	}

	/**
	 * Testet die Methode
	 * {@link RationaleZahl#subtrahiere(RationaleZahl, RationaleZahl)}.
	 */
	@Test
	public void testSubtrahiere() {
		RationaleZahl r;

		r = new RationaleZahl(-3, 8);
		assertEquals(r, RationaleZahl.subtrahiere(a, b));
	}

	/**
	 * Testet die Methode
	 * {@link RationaleZahl#multipliziere(RationaleZahl, long)}.
	 */
	@Test
	public void testMultipliziereRationaleZahlLong() {
		RationaleZahl r;

		r = new RationaleZahl(3, 4);
		assertEquals(r, RationaleZahl.multipliziere(a, 3));
	}

	/**
	 * Testet die Methode
	 * {@link RationaleZahl#multipliziere(RationaleZahl, RationaleZahl)}.
	 */
	@Test
	public void testMultipliziereRationaleZahlRationaleZahl() {
		RationaleZahl r;

		r = new RationaleZahl(5, 32);
		assertEquals(r, RationaleZahl.multipliziere(a, b));
	}

	/**
	 * Testet die Methode {@link RationaleZahl#dividiere(RationaleZahl, long)}.
	 */
	@Test
	public void testDividiereRationaleZahlLong() {
		RationaleZahl r;

		r = new RationaleZahl(1, 16);
		assertEquals(r, RationaleZahl.dividiere(a, 4));
	}

	/**
	 * Testet die Methode
	 * {@link RationaleZahl#dividiere(RationaleZahl, RationaleZahl)}.
	 */
	@Test
	public void testDividiereRationaleZahlRationaleZahl() {
		RationaleZahl r;

		r = new RationaleZahl(8, 20);
		assertEquals(r, RationaleZahl.dividiere(a, b));
	}

	/**
	 * Testet die Methode {@link RationaleZahl#ggT(long, long)}.
	 */
	@Test
	public void testGgT() {
		assertEquals(1L, RationaleZahl.ggT(1, 3));
		assertEquals(1L, RationaleZahl.ggT(13, 19));
		assertEquals(2L, RationaleZahl.ggT(22, 8));
		assertEquals(3L, RationaleZahl.ggT(9, 3));
		assertEquals(4L, RationaleZahl.ggT(12, 28));
	}

	/**
	 * Testet die Methode {@link RationaleZahl#kuerze(RationaleZahl)}.
	 */
	@Test
	public void testKuerze() {
		RationaleZahl r;

		r = new RationaleZahl(2, 8);
		assertEquals(a, RationaleZahl.kuerze(r));
	}

	/**
	 * Testet die Methode {@link RationaleZahl#equals(Object)}.
	 */
	@Test
	public void testEqualsObject() {
		RationaleZahl r;

		r = new RationaleZahl(2, 8);
		assertEquals(r, a);
		assertEquals(a, r);

		assertFalse(a.equals(b));
		assertFalse(b.equals(a));
		assertFalse(b.equals("Unsinn"));
	}

	/**
	 * Testet pro forma die toString()-Methode.
	 */
	@Test
	public void testToString() {
		System.out.println(a);
		System.out.println(b);
		System.out.println(new RationaleZahl(7));
	}

}
