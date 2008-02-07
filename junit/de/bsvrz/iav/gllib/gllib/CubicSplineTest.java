/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.5 Funktionen Ganglinie
 * Copyright (C) 2007 BitCtrl Systems GmbH 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA.
 *
 * Contact Information:
 * BitCtrl Systems GmbH
 * Weißenfelser Straße 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

import static de.bsvrz.sys.funclib.bitctrl.util.Konstanten.MILLIS_PER_STUNDE;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.bsvrz.sys.funclib.bitctrl.math.RationaleZahl;

/**
 * Testet die Approximation einer Ganglinie mit Hilfe eines Cubic-Splines.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class CubicSplineTest {

	/**
	 * Kennzeichnet de Beginn dieses JUnit-Testfalls.
	 */
	@BeforeClass
	public static void beforeClass() {
		System.out.println();
		System.out.println();
		System.out.println("Teste Klasse CubicSpline");
		System.out.println("========================");
		System.out.println();
	}

	/** Die Testganglinie. */
	private Ganglinie ganglinie;

	/**
	 * Testganglinie initialisieren.
	 */
	@Before
	public void setUp() {
		ganglinie = new Ganglinie();
		ganglinie.setStuetzstelle(0, 0.0);
		ganglinie.setStuetzstelle(3 * MILLIS_PER_STUNDE, 3000.0);
		ganglinie.setStuetzstelle(4 * MILLIS_PER_STUNDE, 2000.0);
		ganglinie.setStuetzstelle(6 * MILLIS_PER_STUNDE, 4000.0);
		ganglinie.setStuetzstelle(9 * MILLIS_PER_STUNDE, 1000.0);
		ganglinie.setApproximation(new Polyline());
		ganglinie.aktualisiereApproximation();
	}

	/**
	 * Testet ob der Spline durch ausgewählte vorher berechnete Punkte
	 * durchgeht.
	 */
	@Test
	public void testGet() {
		CubicSpline spline;
		long t;

		System.out
				.print("Prüfe ob Cubic-Spline durch vorher berechnete Punkte läuft ... ");

		spline = new CubicSpline();
		spline.setStuetzstellen(ganglinie.getStuetzstellen());
		spline.initialisiere();

		t = 0;
		System.out.println(t + ". Stunde : " + spline.get(t));
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));

		t = 1 * MILLIS_PER_STUNDE;
		System.out.println(t + ". Stunde : " + spline.get(t));
		assertEquals(new RationaleZahl(1201000, 657).doubleValue(), spline.get(
				t).getWert());

		t = 2 * MILLIS_PER_STUNDE;
		System.out.println(t + ". Stunde : " + spline.get(t));
		assertEquals(new RationaleZahl(1994000, 657).doubleValue(), spline.get(
				t).getWert());

		t = 3 * MILLIS_PER_STUNDE;
		System.out.println(t + ". Stunde : " + spline.get(t));
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));

		t = 4 * MILLIS_PER_STUNDE;
		System.out.println(t + ". Stunde : " + spline.get(t));
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));

		t = 5 * MILLIS_PER_STUNDE;
		System.out.println(t + ". Stunde : " + spline.get(t));
		assertEquals(new RationaleZahl(198500, 73).doubleValue(), spline.get(t)
				.getWert());

		t = 6 * MILLIS_PER_STUNDE;
		System.out.println(t + ". Stunde : " + spline.get(t));
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));

		t = 7 * MILLIS_PER_STUNDE;
		System.out.println(t + ". Stunde : " + spline.get(t));
		assertEquals(new RationaleZahl(2621000, 657).doubleValue(), spline.get(
				t).getWert());

		t = 8 * MILLIS_PER_STUNDE;
		System.out.println(t + ". Stunde : " + spline.get(t));
		assertEquals(new RationaleZahl(1834000, 657).doubleValue(), spline.get(
				t).getWert());

		t = 9 * MILLIS_PER_STUNDE;
		System.out.println(t + ". Stunde : " + spline.get(t));
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));

		System.out.println("O.k.");
	}

	/**
	 * Testet ob der Spline durch alle Stützstellen durchgeht.
	 */
	@Test
	public void testGetStuetzstellen() {
		CubicSpline spline;
		long t;

		System.out
				.print("Prüfe ob Cubic-Spline durch alle Stützstellen läuft ... ");

		spline = new CubicSpline();
		spline.setStuetzstellen(ganglinie.getStuetzstellen());
		spline.initialisiere();

		t = 0;
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));
		t = 3 * MILLIS_PER_STUNDE;
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));
		t = 4 * MILLIS_PER_STUNDE;
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));
		t = 6 * MILLIS_PER_STUNDE;
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));
		t = 9 * MILLIS_PER_STUNDE;
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));

		System.out.println("O.k.");
	}

}
