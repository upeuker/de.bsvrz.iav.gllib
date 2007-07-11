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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import de.bsvrz.sys.funclib.bitctrl.util.Intervall;

/**
 * Testet die Verwendung einer Ganglinie.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */

public class GanglinieTest {

	/**
	 * Ausgabe, welche Klasse gerade getestet wird.
	 */
	@BeforeClass
	public static void beforeClass() {
		System.out.println("### Teste Klasse Ganglinie ###");
	}

	/**
	 * Testet den Umgang einer Ganglinie ohne St&uuml;tzstellen.
	 */
	@Test
	public void testGanglinieOhneStuetzstellen() {
		System.out.println("Ganglinie ohne Stützstellen ...");

		Ganglinie g;

		g = new Ganglinie();

		assertNull(g.getStuetzstelle(1));

		assertEquals(0, g.anzahlStuetzstellen());
		assertEquals(0, g.getStuetzstellen().size());

		assertEquals(0, g.getIntervalle().size());
		assertEquals(null, g.getIntervall());
		assertFalse(g.existsStuetzstelle(2));
		assertFalse(g.isValid(2));

		assertEquals("{}", g.toString());
	}

	/**
	 * Testet das Anlegen einer Ganglinien und einfache Bearbeitung derselben.
	 */
	@Test
	public void testGanglinieAnlegen() {
		System.out.println("Stützstellen anlegen und bearbeiten ...");

		Ganglinie g;

		g = new Ganglinie();
		g.setStuetzstelle(10, 25);
		g.setStuetzstelle(30, 40);
		g.setStuetzstelle(40, 35);

		assertEquals(3, g.anzahlStuetzstellen());
		assertEquals(3, g.getStuetzstellen().size());

		assertTrue(g.existsStuetzstelle(10));
		assertTrue(g.existsStuetzstelle(30));
		assertTrue(g.existsStuetzstelle(40));
		assertFalse(g.existsStuetzstelle(5));
		assertFalse(g.existsStuetzstelle(-10));
		assertFalse(g.existsStuetzstelle(60));

		assertEquals(new Intervall(10, 40), g.getIntervall());
		assertEquals(1, g.getIntervalle().size());
		assertEquals(new Intervall(10, 40), g.getIntervalle().get(0));

		assertTrue(g.isValid(15));
		assertTrue(g.isValid(30));
		assertTrue(g.isValid(35));
		assertFalse(g.isValid(2));
		assertFalse(g.isValid(9));
		assertFalse(g.isValid(45));

		assertEquals(new Stuetzstelle(10, 25), g.getStuetzstelle(10));
		assertEquals(new Stuetzstelle(30, 40), g.getStuetzstelle(30));
		assertEquals(new Stuetzstelle(40, 35), g.getStuetzstelle(40));

		assertEquals("{10=25, 30=40, 40=35}", g.toString());
	}

	/**
	 * Testet die Addition zweier Ganglinien.
	 */
	@Test
	public void testAddiere() {
		System.out.println("Addiere zwei Ganglinien ...");

		Ganglinie g1, g2, ist, soll;

		g1 = new Ganglinie();
		g1.setStuetzstelle(0, 0);
		g1.setStuetzstelle(30, 30);
		g1.setStuetzstelle(40, 20);
		g1.setStuetzstelle(60, 40);
		g1.setStuetzstelle(90, 10);

		g2 = new Ganglinie();
		g2.setStuetzstelle(10, 20);
		g2.setStuetzstelle(30, 40);
		g2.setStuetzstelle(70, 0);
		g2.setStuetzstelle(90, 20);

		ist = Ganglinie.addiere(g1, g2);

		soll = new Ganglinie();
		soll.setStuetzstelle(0, null);
		soll.setStuetzstelle(10, 30);
		soll.setStuetzstelle(30, 70);
		soll.setStuetzstelle(40, 50);
		soll.setStuetzstelle(60, 50);
		soll.setStuetzstelle(70, 30);
		soll.setStuetzstelle(90, 30);

		assertEquals(soll, ist);
	}

	/**
	 * Testet die Subtraktion zweier Ganglinien.
	 */
	@Test
	public void testSubtrahiere() {
		System.out.println("Subtrahiere zwei Ganglinien ...");

		Ganglinie g1, g2, ist, soll;

		g1 = new Ganglinie();
		g1.setStuetzstelle(0, 0);
		g1.setStuetzstelle(30, 30);
		g1.setStuetzstelle(40, 20);
		g1.setStuetzstelle(60, 40);
		g1.setStuetzstelle(90, 10);

		g2 = new Ganglinie();
		g2.setStuetzstelle(10, 20);
		g2.setStuetzstelle(30, 40);
		g2.setStuetzstelle(70, 0);
		g2.setStuetzstelle(90, 20);

		ist = Ganglinie.subtrahiere(g1, g2);

		soll = new Ganglinie();
		soll.setStuetzstelle(0, null);
		soll.setStuetzstelle(10, -10);
		soll.setStuetzstelle(30, -10);
		soll.setStuetzstelle(40, -10);
		soll.setStuetzstelle(60, 30);
		soll.setStuetzstelle(70, 30);
		soll.setStuetzstelle(90, -10);

		assertEquals(soll, ist);
	}

	/**
	 * Testet die Multiplikation zweier Ganglinien.
	 */
	@Test
	public void testMultipliziere() {
		System.out.println("Multipliziere zwei Ganglinien ...");

		Ganglinie g1, g2, ist, soll;

		g1 = new Ganglinie();
		g1.setStuetzstelle(0, 0);
		g1.setStuetzstelle(30, 30);
		g1.setStuetzstelle(40, 20);
		g1.setStuetzstelle(60, 40);
		g1.setStuetzstelle(90, 10);

		g2 = new Ganglinie();
		g2.setStuetzstelle(10, 20);
		g2.setStuetzstelle(30, 40);
		g2.setStuetzstelle(70, 0);
		g2.setStuetzstelle(90, 20);

		ist = Ganglinie.multipliziere(g1, g2);

		soll = new Ganglinie();
		soll.setStuetzstelle(0, null);
		soll.setStuetzstelle(10, 200);
		soll.setStuetzstelle(30, 1200);
		soll.setStuetzstelle(40, 600);
		soll.setStuetzstelle(60, 400);
		soll.setStuetzstelle(70, 0);
		soll.setStuetzstelle(90, 200);

		assertEquals(soll, ist);
	}

	/**
	 * Testet die Division zweier Ganglinien.
	 */
	@Test
	public void testDividiere() {
		System.out.println("Dividiere zwei Ganglinien ...");

		Ganglinie g1, g2, ist, soll;

		g1 = new Ganglinie();
		g1.setStuetzstelle(0, 0);
		g1.setStuetzstelle(30, 30);
		g1.setStuetzstelle(40, 20);
		g1.setStuetzstelle(60, 40);
		g1.setStuetzstelle(90, 10);

		g2 = new Ganglinie();
		g2.setStuetzstelle(10, 20);
		g2.setStuetzstelle(30, 40);
		g2.setStuetzstelle(70, 0);
		g2.setStuetzstelle(90, 20);

		ist = Ganglinie.dividiere(g1, g2);

		soll = new Ganglinie();
		soll.setStuetzstelle(0, null);
		soll.setStuetzstelle(10, 1);
		soll.setStuetzstelle(30, 1);
		soll.setStuetzstelle(40, 1);
		soll.setStuetzstelle(60, 4);
		soll.setStuetzstelle(70, null);
		soll.setStuetzstelle(90, 1);

		assertEquals(soll, ist);
	}

}
