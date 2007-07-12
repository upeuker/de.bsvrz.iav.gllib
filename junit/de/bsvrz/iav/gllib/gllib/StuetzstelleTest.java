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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Testet die Verwendung einer St�tzstelle.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class StuetzstelleTest {

	/**
	 * Ausgabe welche Klasse gerade getestet wird.
	 */
	@BeforeClass
	public static void beforeClass() {
		System.out.println("### Teste Klasse Stuetzstelle ###");
	}

	/**
	 * Test der Verwendung einer undefierten St�tzstelle.
	 */
	@Test
	public void testUndefinierteStuetzstelle() {
		System.out.println("Undefinierte St�tzstelle ...");

		Stuetzstelle<Integer> s;

		s = new Stuetzstelle<Integer>(10);
		assertEquals(10L, s.getZeitstempel());
		assertEquals(null, s.getWert());
		assertEquals("10 => null", s.toString());
	}

	/**
	 * Test der Verwendung einer "normalen", also defierten St�tzstelle.
	 */
	@Test
	public void testDefinierteStuetzstelle() {
		System.out.println("Definierte St�tzstelle ...");
		Stuetzstelle<Integer> s;

		s = new Stuetzstelle<Integer>(10, 50);
		assertEquals(10L, s.getZeitstempel());
		assertEquals(50, s.getWert());
		assertEquals("10 => 50", s.toString());
	}

	/**
	 * Testet den Test auf Gleichheit zweier St�tzstellen.
	 */
	@Test
	public void testGleichheit() {
		System.out.println("Gleicheit von St�tzstellen ...");
		Stuetzstelle<Integer> s1, s2;

		s1 = new Stuetzstelle<Integer>(15, 30);
		s2 = new Stuetzstelle<Integer>(15, 30);
		assertTrue(s1.equals(s2));
		s1 = new Stuetzstelle<Integer>(10, 10);
		s2 = new Stuetzstelle<Integer>(10, 10);
		assertTrue(s1.equals(s2));
		s1 = new Stuetzstelle<Integer>(40, null);
		s2 = new Stuetzstelle<Integer>(40, null);
		assertTrue(s1.equals(s2));
		s1 = new Stuetzstelle<Integer>(10, 10);
		s2 = new Stuetzstelle<Integer>(20, 50);
		assertFalse(s1.equals(s2));
		s1 = new Stuetzstelle<Integer>(10, 10);
		s2 = new Stuetzstelle<Integer>(40, 10);
		assertFalse(s1.equals(s2));
		s1 = new Stuetzstelle<Integer>(50, 10);
		s2 = new Stuetzstelle<Integer>(30, null);
		assertFalse(s1.equals(s2));

		assertFalse(s1.equals("Keine St�tzstelle"));
	}

	/**
	 * Testet die Ordung auf St�tzstellen.
	 */
	@Test
	public void testOrdnung() {
		System.out.println("Ordnung auf St�tzstellen ...");
		Stuetzstelle<Integer> s1, s2;

		s1 = new Stuetzstelle<Integer>(10, 10);
		s2 = new Stuetzstelle<Integer>(10, 50);
		assertEquals(0, s1.compareTo(s2));
		s1 = new Stuetzstelle<Integer>(10, 10);
		s2 = new Stuetzstelle<Integer>(10, 10);
		assertEquals(0, s1.compareTo(s2));
		s1 = new Stuetzstelle<Integer>(10, null);
		s2 = new Stuetzstelle<Integer>(10, 50);
		assertEquals(0, s1.compareTo(s2));
		s1 = new Stuetzstelle<Integer>(10, 10);
		s2 = new Stuetzstelle<Integer>(20, 50);
		assertEquals(-1, s1.compareTo(s2));
		s1 = new Stuetzstelle<Integer>(10, 10);
		s2 = new Stuetzstelle<Integer>(40, 10);
		assertEquals(-1, s1.compareTo(s2));
		s1 = new Stuetzstelle<Integer>(50, 10);
		s2 = new Stuetzstelle<Integer>(30, null);
		assertEquals(1, s1.compareTo(s2));
	}

}