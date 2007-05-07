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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Testet alle relevanten Funktionen der Klasse.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class TestMatrix {

	/** Erste Matrix f&uuml;r alle Testf&auml;lle. */
	private Matrix a;

	/** Zweite Matrix f&uuml;r alle Testf&auml;lle. */
	private Matrix b;

	/** Dritte Matrix f&uuml;r alle Testf&auml;lle. */
	private Matrix c;

	/**
	 * Initialisierung der Testmatrizen.
	 */
	@Before
	public void setUp() {
		a = new Matrix(3, 2);
		a.set(0, 0, 5);
		a.set(0, 1, 2);
		a.set(1, 0, 7);
		a.set(1, 1, 6);
		a.set(2, 0, 3);
		a.set(2, 1, 5);

		b = new Matrix(2, 3);
		b.set(0, 0, 1);
		b.set(0, 1, 3);
		b.set(0, 2, 8);
		b.set(1, 0, 5);
		b.set(1, 1, 4);
		b.set(1, 2, 4);

		c = new Matrix(2, 3);
		c.set(0, 0, 5);
		c.set(0, 1, 3);
		c.set(0, 2, 6);
		c.set(1, 0, 8);
		c.set(1, 1, 1);
		c.set(1, 2, 6);
	}

	/**
	 * Testet das Eintreten der Ausnahme.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testKonstruktorA() {
		new Matrix(0, 1);
	}

	/**
	 * Testet das Eintreten der Ausnahme.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testKonstruktorB() {
		new Matrix(1, 0);
	}

	/**
	 * Testet das Eintreten der Ausnahme.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testKonstruktorC() {
		new Matrix(0, 0);
	}

	/**
	 * Testet den Konstruktor der aus einem Vektor eine Matrix konstruiert.
	 * 
	 */
	@Test
	public void testKonstruktorD() {
		Matrix m;
		Vektor v;

		v = new Vektor(5, 3, 7);
		m = new Matrix(1, 3);
		m.set(0, 0, 5);
		m.set(0, 1, 3);
		m.set(0, 2, 7);
		assertEquals(m, new Matrix(v, true));

		v = new Vektor(2, 9);
		m = new Matrix(2, 1);
		m.set(0, 0, 2);
		m.set(1, 0, 9);
		assertEquals(m, new Matrix(v, false));
	}

	/**
	 * Test f&uuml;r {@link Matrix#addiere(Matrix, Matrix)}.
	 */
	@Test
	public void testAddiere() {
		Matrix m;

		m = new Matrix(2, 3);
		m.set(0, 0, 6);
		m.set(0, 1, 6);
		m.set(0, 2, 14);
		m.set(1, 0, 13);
		m.set(1, 1, 5);
		m.set(1, 2, 10);

		assertEquals(m, Matrix.addiere(b, c));
		assertEquals(m, Matrix.addiere(c, b));
	}

	/**
	 * Testet auf das Eintreten der Ausnahme.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddiereException() {
		Matrix.addiere(a, b);
	}

	/**
	 * Test f&uuml;r {@link Matrix#subtrahiere(Matrix, Matrix)}.
	 */
	@Test
	public void testSubtrahiere() {
		Matrix m;

		m = new Matrix(2, 3);
		m.set(0, 0, -4);
		m.set(0, 1, 0);
		m.set(0, 2, 2);
		m.set(1, 0, -3);
		m.set(1, 1, 3);
		m.set(1, 2, -2);

		assertEquals(m, Matrix.subtrahiere(b, c));
	}

	/**
	 * Testet auf das Eintreten der Ausnahme.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSubtrahiereException() {
		Matrix.subtrahiere(a, b);
	}

	/**
	 * Test f&uuml;r {@link Matrix#multipliziere(Matrix, long)}.
	 */
	@Test
	public void testMultipliziereMatrixLong() {
		Matrix m;

		m = new Matrix(3, 2);
		m.set(0, 0, 10);
		m.set(0, 1, 4);
		m.set(1, 0, 14);
		m.set(1, 1, 12);
		m.set(2, 0, 6);
		m.set(2, 1, 10);
		assertEquals(m, Matrix.multipliziere(a, 2));
	}

	/**
	 * Test f&uuml;r {@link Matrix#multipliziere(Matrix, Vektor)}.
	 */
	@Test
	public void testMultipliziereMatrixVektor() {
		Matrix m;
		Vektor v;

		v = new Vektor(5, 1, 3);

		m = new Matrix(2, 1);
		m.set(0, 0, 46);
		m.set(1, 0, 59);
		assertEquals(m, Matrix.multipliziere(c, v));
	}

	/**
	 * Testet auf das Eintreten der Ausnahme.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testMultipliziereMatrixVektorException() {
		Matrix.multipliziere(a, a.getSpaltenvektor(1));
	}

	/**
	 * Test f&uuml;r {@link Matrix#multipliziere(Matrix, Matrix)}.
	 */
	@Test
	public void testMultipliziereMatrixMatrix() {
		Matrix m;

		m = new Matrix(3, 3);
		m.set(0, 0, 15);
		m.set(0, 1, 23);
		m.set(0, 2, 48);
		m.set(1, 0, 37);
		m.set(1, 1, 45);
		m.set(1, 2, 80);
		m.set(2, 0, 28);
		m.set(2, 1, 29);
		m.set(2, 2, 44);
		assertEquals(m, Matrix.multipliziere(a, b));
	}

	/**
	 * Testet auf das Eintreten der Ausnahme.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testMultipliziereMatrixMatrixException() {
		Matrix.multipliziere(b, c);
	}

	/**
	 * Test f&uuml;r {@link Matrix#getZeilenvektor(int)}.
	 */
	@Test
	public void testGetZeilenvektor() {
		Vektor v;

		v = new Vektor(3, 5);
		assertEquals(v, a.getZeilenvektor(2));

		v = new Vektor(5, 4, 4);
		assertEquals(v, b.getZeilenvektor(1));
	}

	/**
	 * Test f&uuml;r {@link Matrix#setZeilenvektor(int, Vektor)}.
	 */
	@Test
	public void testSetZeilenvektor() {
		Vektor v;
		Matrix m;

		v = new Vektor(2, 5);

		m = new Matrix(a);
		m.set(1, 0, 2);
		m.set(1, 1, 5);

		a.setZeilenvektor(1, v);
		assertEquals(m, a);
	}

	/**
	 * Test f&uuml;r das Auftreten einer Exception in
	 * {@link Matrix#setZeilenvektor(int, Vektor)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetZeilenvektorException() {
		Vektor v;

		v = new Vektor(2, 5, 3);
		a.setZeilenvektor(1, v);
	}

	/**
	 * Test f&uuml;r {@link Matrix#getSpaltenvektor(int)}.
	 */
	@Test
	public void testGetSpaltenvektor() {
		Vektor v;

		v = new Vektor(5, 7, 3);
		assertEquals(v, a.getSpaltenvektor(0));

		v = new Vektor(3, 4);
		assertEquals(v, b.getSpaltenvektor(1));
	}

	/**
	 * Test f&uuml;r das Auftreten einer Exception in
	 * {@link Matrix#setSpaltenvektor(int, Vektor)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetSpaltenvektorException() {
		Vektor v;

		v = new Vektor(1, 1);
		a.setSpaltenvektor(1, v);
	}

	/**
	 * Test f&uuml;r {@link Matrix#setSpaltenvektor(int, Vektor)}.
	 */
	@Test
	public void testSetSpaltenvektor() {
		Vektor v;
		Matrix m;

		v = new Vektor(1, 1, 1);

		m = new Matrix(a);
		m.set(0, 1, 1);
		m.set(1, 1, 1);
		m.set(2, 1, 1);

		a.setSpaltenvektor(1, v);
		assertEquals(m, a);
	}

	/**
	 * Test f&uuml;r {@link Matrix#transponiert()}.
	 */
	@Test
	public void testTransponiert() {
		Matrix m;

		m = new Matrix(2, 3);
		m.set(0, 0, 5);
		m.set(0, 1, 7);
		m.set(0, 2, 3);
		m.set(1, 0, 2);
		m.set(1, 1, 6);
		m.set(1, 2, 5);
		assertEquals(m, a.transponiert());
	}

	/**
	 * Test f&uuml;r {@link Matrix#symetrisch()}.
	 */
	@Test
	public void testSymetrisch() {
		Matrix m;

		assertFalse(a.symetrisch());
		assertFalse(b.symetrisch());
		assertFalse(c.symetrisch());

		m = new Matrix(3, 3);
		m.set(0, 0, 1);
		m.set(0, 1, 3);
		m.set(0, 2, 2);
		m.set(1, 0, 3);
		m.set(1, 1, 1);
		m.set(1, 2, 7);
		m.set(2, 0, 2);
		m.set(2, 1, 7);
		m.set(2, 2, 1);
		assertTrue(m.symetrisch());
	}

	/**
	 * Test f&uuml;r {@link Matrix#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		Matrix m;

		assertEquals(a, a);
		assertEquals(b, b);

		m = new Matrix(a);
		assertEquals(a, m);

		m = new Matrix(2, 3);
		m.set(0, 0, 1);
		m.set(0, 1, 3);
		m.set(0, 2, 8);
		m.set(1, 0, 5);
		m.set(1, 1, 4);
		m.set(1, 2, 4);
		assertEquals(b, m);

		assertFalse(a.equals("Unsinn"));
		assertFalse(a.equals(b));
		assertFalse(b.equals(c));
	}

	/**
	 * Test f&uuml;r {@link Matrix#toString()}.
	 */
	@Test
	public void testToString() {
		System.out.println("Ausgabe Matrix:\n" + a.toString());
		System.out.println("Ausgabe Matrix:\n" + b.toString());
	}

}
