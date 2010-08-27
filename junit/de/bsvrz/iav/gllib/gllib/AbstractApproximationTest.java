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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Testet die Klasse {@link AbstractApproximation} am Beispiel der Polylinie.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class AbstractApproximationTest {

	/** Die Teststützstellen. */
	private List<Stuetzstelle<Double>> stuetzstellen;

	/**
	 * Teststütztellen initialisieren.
	 */
	@Before
	public void setUp() {
		stuetzstellen = new ArrayList<Stuetzstelle<Double>>();
		stuetzstellen.add(new Stuetzstelle<Double>(1000, 60.0));
		stuetzstellen.add(new Stuetzstelle<Double>(2000, 20.0));
		stuetzstellen.add(new Stuetzstelle<Double>(3000, 70.0));
		stuetzstellen.add(new Stuetzstelle<Double>(4000, 20.0));
		stuetzstellen.add(new Stuetzstelle<Double>(5000, 60.0));
		stuetzstellen.add(new Stuetzstelle<Double>(6000, 80.0));
	}

	/**
	 * Testet die Funktion
	 * {@link AbstractApproximation#findeStuetzstelleNach(long)}.
	 */
	@Test
	public void testFindeStuetzstelleNach() {
		Polyline polyline;

		polyline = new Polyline();
		polyline.setStuetzstellen(stuetzstellen);

		// Die Stützstellen selber
		assertEquals(0, polyline.findeStuetzstelleNach(1000));
		assertEquals(1, polyline.findeStuetzstelleNach(2000));
		assertEquals(2, polyline.findeStuetzstelleNach(3000));
		assertEquals(3, polyline.findeStuetzstelleNach(4000));
		assertEquals(4, polyline.findeStuetzstelleNach(5000));
		assertEquals(5, polyline.findeStuetzstelleNach(6000));

		assertEquals(-1, polyline.findeStuetzstelleNach(500));
		assertEquals(1, polyline.findeStuetzstelleNach(1500));
		assertEquals(2, polyline.findeStuetzstelleNach(2500));
		assertEquals(3, polyline.findeStuetzstelleNach(3500));
		assertEquals(4, polyline.findeStuetzstelleNach(4500));
		assertEquals(5, polyline.findeStuetzstelleNach(5500));
		assertEquals(-1, polyline.findeStuetzstelleNach(6500));
	}

	/**
	 * Testet die Funktion
	 * {@link AbstractApproximation#findeStuetzstelleVor(long)}.
	 */
	@Test
	public void testFindeStuetzstelleVor() {
		Polyline polyline;

		polyline = new Polyline();
		polyline.setStuetzstellen(stuetzstellen);

		// Die Stützstellen selber
		assertEquals(0, polyline.findeStuetzstelleVor(1000));
		assertEquals(1, polyline.findeStuetzstelleVor(2000));
		assertEquals(2, polyline.findeStuetzstelleVor(3000));
		assertEquals(3, polyline.findeStuetzstelleVor(4000));
		assertEquals(4, polyline.findeStuetzstelleVor(5000));
		assertEquals(5, polyline.findeStuetzstelleVor(6000));

		assertEquals(-1, polyline.findeStuetzstelleVor(500));
		assertEquals(0, polyline.findeStuetzstelleVor(1500));
		assertEquals(1, polyline.findeStuetzstelleVor(2500));
		assertEquals(2, polyline.findeStuetzstelleVor(3500));
		assertEquals(3, polyline.findeStuetzstelleVor(4500));
		assertEquals(4, polyline.findeStuetzstelleVor(5500));
		assertEquals(-1, polyline.findeStuetzstelleVor(6500));
	}

	/**
	 * Testet die Funktion {@link AbstractApproximation#isValid(long)}.
	 */
	@Test
	public void testIsValid() {
		Polyline polyline;

		polyline = new Polyline();
		polyline.setStuetzstellen(stuetzstellen);

		assertFalse(polyline.isValid(500));
		assertFalse(polyline.isValid(999));
		assertTrue(polyline.isValid(1000));
		assertTrue(polyline.isValid(5000));
		assertFalse(polyline.isValid(6001));
		assertFalse(polyline.isValid(7000));
	}

}
