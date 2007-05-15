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
 * Weißenfelser Straße 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Testet relevante Funktionen der Klasse.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class TestGanglinie {

	/** Eine Testst&uuml;tzstelle. */
	private Stuetzstelle s1;

	/** Eine Testst&uuml;tzstelle. */
	private Stuetzstelle s2;

	/** Eine Testst&uuml;tzstelle. */
	private Stuetzstelle s3;

	/** Eine Testst&uuml;tzstelle. */
	private Stuetzstelle s4;

	/** Eine Testst&uuml;tzstelle. */
	private Stuetzstelle s5;

	/** Eine Testganglinie. */
	private Ganglinie ganglinie;

	/**
	 * Initialisiert die Testganglinie vor jedem Test.
	 */
	@Before
	public void beforeTest() {
		ganglinie = new Ganglinie();

		s1 = new Stuetzstelle(0, 0);
		s2 = new Stuetzstelle(30, 30);
		s3 = new Stuetzstelle(40, 20);
		s4 = new Stuetzstelle(60, 40);
		s5 = new Stuetzstelle(90, 10);

		ganglinie.set(s4);
		ganglinie.set(s2);
		ganglinie.set(s5);
		ganglinie.set(s1);
		ganglinie.set(s3);
	}

	/**
	 * Testet die korrekte Bestimmung des Intervalls der
	 * St&uuml;tzstellenzeitstempel.
	 */
	@Test
	public void testGetIntervall() {
		Intervall intervall;
		Ganglinie g;

		intervall = ganglinie.getIntervall();
		assertEquals(0L, intervall.start);
		assertEquals(90L, intervall.ende);

		// Sonderfall, eine Ganglinie ohne Stützstellen
		g = new Ganglinie();
		intervall = g.getIntervall();
		assertNull(intervall);
	}

	@Test
	public void testGetIntervalle() {
		List<Intervall> intervalle;

		intervalle = ganglinie.getIntervalle();
		assertEquals(1, intervalle.size());
		assertEquals(ganglinie.getIntervall(), intervalle.get(0));

		ganglinie.set(50, null);
		intervalle = ganglinie.getIntervalle();
		assertEquals(2, intervalle.size());
		assertEquals(new Intervall(0, 40), intervalle.get(0));
		assertEquals(new Intervall(60, 90), intervalle.get(1));

		ganglinie.set(20, null);
		intervalle = ganglinie.getIntervalle();
		assertEquals(3, intervalle.size());
		assertEquals(new Intervall(0, 0), intervalle.get(0));
		assertEquals(new Intervall(30, 40), intervalle.get(1));
		assertEquals(new Intervall(60, 90), intervalle.get(2));

		ganglinie.set(10, null);
		intervalle = ganglinie.getIntervalle();
		assertEquals(3, intervalle.size());
		assertEquals(new Intervall(0, 0), intervalle.get(0));
		assertEquals(new Intervall(30, 40), intervalle.get(1));
		assertEquals(new Intervall(60, 90), intervalle.get(2));
	}

	/**
	 * Pr&uuml;ft ob konkrete Zeitstempel innerhalb der Ganglinien liegen.
	 */
	@Test
	public void testContains() {
		Ganglinie g;

		// davor
		assertFalse(ganglinie.isValid(-10));

		// dazwischen, auf Stützstelle
		assertTrue(ganglinie.isValid(30));

		// dazwischen, nicht auf Stützstelle
		assertTrue(ganglinie.isValid(50));

		// dahinter
		assertFalse(ganglinie.isValid(100));

		// Sonderfall, eine Ganglinie ohne Stützstellen
		g = new Ganglinie();
		assertFalse(g.isValid(30));
	}

	/**
	 * Testet die Suche nach St&uuml;tzstellen.
	 */
	@Test
	public void testGetStuetzstelle() throws Exception {
		Stuetzstelle s, s0;

		// existierende Stützstelle
		s = ganglinie.getStuetzstelle(30);
		assertEquals(s2, s);

		// innerhalb der Ganglinie, aber keine Stützstelle
		ganglinie.setApproximation(Polyline.class);
		s = ganglinie.get(50);
		s0 = new Stuetzstelle(50, 30);
		assertEquals(s0, s);

		// außerhalb der Ganglinie
		try {
			s = ganglinie.get(100);
			fail();
		} catch (UndefiniertException e) {
			// Alles O.k.
		}

	}

	/**
	 * Testet pro forma die toString()-Methode.
	 */
	@Test
	public void testToString() {
		System.out.println("Ausgabe Ganglinie:\n" + ganglinie.toString());
	}

}
