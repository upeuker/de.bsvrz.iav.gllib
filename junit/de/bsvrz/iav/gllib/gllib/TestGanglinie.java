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
 * Wei�enfelser Stra�e 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

import static org.junit.Assert.*;

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

		ganglinie.add(s4);
		ganglinie.add(s2);
		ganglinie.add(s5);
		ganglinie.add(s1);
		ganglinie.add(s3);
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

		// Sonderfall, eine Ganglinie ohne St�tzstellen
		g = new Ganglinie();
		intervall = g.getIntervall();
		assertNull(intervall);
	}

	/**
	 * Pr&uuml;ft ob konkrete Zeitstempel innerhalb der Ganglinien liegen.
	 */
	@Test
	public void testContains() {
		Ganglinie g;

		// davor
		assertFalse(ganglinie.contains(-10));

		// dazwischen, auf St�tzstelle
		assertTrue(ganglinie.contains(30));

		// dazwischen, nicht auf St�tzstelle
		assertTrue(ganglinie.contains(50));

		// dahinter
		assertFalse(ganglinie.contains(100));

		// Sonderfall, eine Ganglinie ohne St�tzstellen
		g = new Ganglinie();
		assertFalse(g.contains(30));
	}

	/**
	 * Testet die Suche nach St&uuml;tzstellen.
	 */
	@Test
	public void testGetStuetzstelle() {
		Stuetzstelle s, s0;

		// existierende St�tzstelle
		s = ganglinie.getStuetzstelle(30);
		assertEquals(s2, s);

		// innerhalb der Ganglinie, aber keine St�tzstelle
		ganglinie.setApproximation(Polyline.class);
		s = ganglinie.getStuetzstelle(50);
		s0 = new Stuetzstelle(50, 30);
		assertEquals(s0, s);

		// au�erhalb der Ganglinie
		s = ganglinie.getStuetzstelle(100);
		assertNull(s);
	}

	/**
	 * Testet pro forma die toString()-Methode.
	 */
	@Test
	public void testToString() {
		System.out.println(ganglinie);
	}

	/**
	 * Testet die Mengenbedingungen. Zu jedem Zeitstempel, darf nur genau ein
	 * Wert existieren. Ein Hinzuf&uuml;gen eines Wertes zu einem existierenden
	 * Zeitstemepel wird ignoriert.
	 */
	@Test
	public void testMengen() {
		Stuetzstelle s = new Stuetzstelle(30, 666);

		ganglinie.add(s);
		assertEquals(s2, ganglinie.getStuetzstelle(30));

		ganglinie.remove(s2);
		ganglinie.add(s);
		assertEquals(s, ganglinie.getStuetzstelle(30));
	}

}