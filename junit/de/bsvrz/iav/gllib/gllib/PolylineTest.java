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

/**
 * Testet die Approximation einer Ganglinie mit Hilfe einer Polylinie.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class PolylineTest {

	/** Die Testganglinie. */
	private Ganglinie ganglinie;

	/**
	 * Testganglinie initialisieren.
	 */
	@Before
	public void setUp() {
		ganglinie = new Ganglinie();
		ganglinie.setStuetzstelle(0, 0.0);
		ganglinie.setStuetzstelle(30, 30.0);
		ganglinie.setStuetzstelle(40, 20.0);
		ganglinie.setStuetzstelle(60, 40.0);
		ganglinie.setStuetzstelle(90, 10.0);
	}

	/**
	 * Testet ob der Spline durch alle St&uuml;tzstellen durchgeht.
	 */
	@Test
	public void testGet() {
		Polyline polyline;
		long t;

		System.out.println("Polyline:");
		polyline = new Polyline();
		polyline.setStuetzstellen(ganglinie.getStuetzstellen());

		// Die existierenden Stützstellen
		t = 0;
		assertEquals(ganglinie.getStuetzstelle(t), polyline.get(t));
		System.out.println(polyline.get(t));
		t = 30;
		assertEquals(ganglinie.getStuetzstelle(t), polyline.get(t));
		System.out.println(polyline.get(t));
		t = 40;
		assertEquals(ganglinie.getStuetzstelle(t), polyline.get(t));
		System.out.println(polyline.get(t));
		t = 60;
		assertEquals(ganglinie.getStuetzstelle(t), polyline.get(t));
		System.out.println(polyline.get(t));
		t = 90;
		assertEquals(ganglinie.getStuetzstelle(t), polyline.get(t));
		System.out.println(polyline.get(t));

		// Punkte zwischen den Stützstellen
		t = 10;
		assertEquals(new Stuetzstelle<Double>(t, 10.0), polyline.get(t));
		System.out.println(polyline.get(t));
		t = 20;
		assertEquals(new Stuetzstelle<Double>(t, 20.0), polyline.get(t));
		System.out.println(polyline.get(t));
		t = 50;
		assertEquals(new Stuetzstelle<Double>(t, 30.0), polyline.get(t));
		System.out.println(polyline.get(t));
		t = 70;
		assertEquals(new Stuetzstelle<Double>(t, 30.0), polyline.get(t));
		System.out.println(polyline.get(t));
		t = 80;
		assertEquals(new Stuetzstelle<Double>(t, 20.0), polyline.get(t));
		System.out.println(polyline.get(t));
	}

}
