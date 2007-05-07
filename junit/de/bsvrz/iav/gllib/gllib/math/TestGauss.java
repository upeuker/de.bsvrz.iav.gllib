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

import org.junit.Test;

/**
 * Testet alle relevanten Methoden der Klasse {@link Gauss}.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class TestGauss {

	/**
	 * Testmethode f&uuml;r {@link Gauss#bestimmeLRZerlegung(Matrix)}.
	 */
	@Test
	public void testBestimmeLRZerlegungA() {
		Matrix a, lr, l, r;

		a = new Matrix(4, 4);
		a.set(0, 0, 1);
		a.set(0, 1, 2);
		a.set(0, 2, -1);
		a.set(0, 3, 6);
		a.set(1, 0, 2);
		a.set(1, 1, -4);
		a.set(1, 2, 2);
		a.set(1, 3, -2);
		a.set(2, 0, -1);
		a.set(2, 1, 4);
		a.set(2, 2, 1);
		a.set(2, 3, 4);
		a.set(3, 0, 3);
		a.set(3, 1, -2);
		a.set(3, 2, 3);
		a.set(3, 3, 1);

		lr = Gauss.bestimmeLRZerlegung(a);
		r = Gauss.extrahiereMatrixR(lr);
		l = Gauss.extrahiereMatrixL(lr);
		assertEquals(a, Matrix.multipliziere(l, r));
	}

	/**
	 * Testmethode f&uuml;r {@link Gauss#bestimmeLRZerlegung(Matrix)}.
	 */
	@Test
	public void testBestimmeLRZerlegungB() {
		Matrix a, lr, l, r;

		a = new Matrix(3, 3);
		a.set(0, 0, 2);
		a.set(0, 1, 3);
		a.set(0, 2, 5);
		a.set(1, 0, 6);
		a.set(1, 1, 10);
		a.set(1, 2, 17);
		a.set(2, 0, 8);
		a.set(2, 1, 14);
		a.set(2, 2, 28);

		lr = Gauss.bestimmeLRZerlegung(a);
		r = Gauss.extrahiereMatrixR(lr);
		l = Gauss.extrahiereMatrixL(lr);
		assertEquals(a, Matrix.multipliziere(l, r));
	}

	/**
	 * Testmethode f&uuml;r {@link Gauss#loeseLGS(Matrix, Vektor)}.
	 */
	@Test
	public void testLoeseLGSA() {
		Matrix a;
		Vektor b, x;

		a = new Matrix(2, 2);
		a.set(0, 0, 8);
		a.set(0, 1, 3);
		a.set(1, 0, 5);
		a.set(1, 1, 2);

		b = new Vektor(30, 19);

		x = new Vektor(3, 2);
		assertEquals(x, Gauss.loeseLGS(a, b));
	}

	/**
	 * Testmethode f&uuml;r {@link Gauss#loeseLGS(Matrix, Vektor)}.
	 */
	@Test
	public void testLoeseLGSB() {
		Matrix a;
		Vektor b, x;

		a = new Matrix(4, 4);
		a.set(0, 0, 1);
		a.set(0, 1, 2);
		a.set(0, 2, -1);
		a.set(0, 3, 6);
		a.set(1, 0, 2);
		a.set(1, 1, -4);
		a.set(1, 2, 2);
		a.set(1, 3, -2);
		a.set(2, 0, -1);
		a.set(2, 1, 4);
		a.set(2, 2, 1);
		a.set(2, 3, 4);
		a.set(3, 0, 3);
		a.set(3, 1, -2);
		a.set(3, 2, 3);
		a.set(3, 3, 1);

		b = new Vektor(33, -6, 13, 11);

		x = new Vektor(5, 1, -2, 4);
		assertEquals(x, Gauss.loeseLGS(a, b));
	}

	/**
	 * Testmethode f&uuml;r {@link Gauss#loeseLGS(Matrix, Vektor)}.
	 */
	@Test
	public void testLoeseLGSC() {
		Matrix a;
		Vektor b, x;

		a = new Matrix(3, 3);
		a.set(0, 0, 2);
		a.set(0, 1, 3);
		a.set(0, 2, 5);
		a.set(1, 0, 6);
		a.set(1, 1, 10);
		a.set(1, 2, 17);
		a.set(2, 0, 8);
		a.set(2, 1, 14);
		a.set(2, 2, 28);

		b = new Vektor(1, 2, 3);

		x = new Vektor(3);
		x.set(0, new RationaleZahl(17, 8));
		x.set(1, new RationaleZahl(-3, 2));
		x.set(2, new RationaleZahl(1, 4));
		assertEquals(x, Gauss.loeseLGS(a, b));
	}

}
