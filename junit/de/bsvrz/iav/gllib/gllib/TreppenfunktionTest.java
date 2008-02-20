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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.bitctrl.util.Interval;

/**
 * Testet die Approximation einer Ganglinie mit Hilfe einer Treppenfunktion.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class TreppenfunktionTest {

	/** Die Testganglinie. */
	private Ganglinie ganglinie;

	/**
	 * Testganglinie initialisieren.
	 */
	@Before
	public void setUp() {
		ganglinie = new Ganglinie();
		ganglinie.setStuetzstelle(0, 0.0);
		ganglinie.setStuetzstelle(3000, 30.0);
		ganglinie.setStuetzstelle(4000, 20.0);
		ganglinie.setStuetzstelle(6000, 40.0);
		ganglinie.setStuetzstelle(9000, 10.0);
	}

	/**
	 * Testet ob der Spline durch alle St&uuml;tzstellen durchgeht.
	 */
	@Test
	public void testGet() {
		Treppenfunktion treppe;
		long t;

		System.out.println("Treppenfunktion:");
		treppe = new Treppenfunktion();
		treppe.setStuetzstellen(ganglinie.getStuetzstellen());

		// Die existierenden Stützstellen
		t = 0;
		assertEquals(ganglinie.getStuetzstellen().get(0), treppe.get(t));
		System.out.println(treppe.get(t));
		t = 3000;
		assertEquals(ganglinie.getStuetzstellen().get(1), treppe.get(t));
		System.out.println(treppe.get(t));
		t = 4000;
		assertEquals(ganglinie.getStuetzstellen().get(2), treppe.get(t));
		System.out.println(treppe.get(t));
		t = 6000;
		assertEquals(ganglinie.getStuetzstellen().get(3), treppe.get(t));
		System.out.println(treppe.get(t));
		t = 9000;
		assertEquals(ganglinie.getStuetzstellen().get(4), treppe.get(t));
		System.out.println(treppe.get(t));

		// Punkte zwischen den Stützstellen
		t = 1000;
		assertEquals(new Stuetzstelle<Double>(t, 0.0), treppe.get(t));
		System.out.println(treppe.get(t));
		t = 2000;
		assertEquals(new Stuetzstelle<Double>(t, 0.0), treppe.get(t));
		System.out.println(treppe.get(t));
		t = 5000;
		assertEquals(new Stuetzstelle<Double>(t, 20.0), treppe.get(t));
		System.out.println(treppe.get(t));
		t = 7000;
		assertEquals(new Stuetzstelle<Double>(t, 40.0), treppe.get(t));
		System.out.println(treppe.get(t));
		t = 8000;
		assertEquals(new Stuetzstelle<Double>(t, 40.0), treppe.get(t));
		System.out.println(treppe.get(t));
	}

	/**
	 * Testet die Methode {@link Treppenfunktion#integral(Intervall)}.
	 */
	@Test
	public void testIntegral() {
		Treppenfunktion treppe;
		Interval intervall;

		System.out.println("Treppenfunktion: Methode integral()");
		treppe = new Treppenfunktion();
		treppe.setStuetzstellen(ganglinie.getStuetzstellen());

		// Intervallgrenzen liegen auf Stützstellen
		intervall = new Interval(0, 9000);
		assertEquals(190000, treppe.integral(intervall));

		// Intervallgrenzen liegen nicht auf Stützstellen
		intervall = new Interval(3500, 8500);
		assertEquals(170000, treppe.integral(intervall));
	}

}
