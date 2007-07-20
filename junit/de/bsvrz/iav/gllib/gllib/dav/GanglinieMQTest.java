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

package de.bsvrz.iav.gllib.gllib.dav;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import de.bsvrz.iav.gllib.gllib.Stuetzstelle;
import de.bsvrz.sys.funclib.bitctrl.util.Intervall;

/**
 * Testet die Verwendung einer Ganglinie.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */

public class GanglinieMQTest {

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

		GanglinieMQ g;

		g = new GanglinieMQ();

		assertNull(g.getStuetzstelle(1));

		assertEquals(0, g.anzahlStuetzstellen());
		assertEquals(0, g.getStuetzstellen().size());

		assertEquals(0, g.getIntervalle().size());
		assertEquals(null, g.getIntervall());
		assertFalse(g.existsStuetzstelle(2));
		assertFalse(g.isValid(2));

		assertEquals("null: []", g.toString());
	}

	/**
	 * Testet das Anlegen einer Ganglinien und einfache Bearbeitung derselben.
	 */
	@Test
	public void testGanglinieAnlegen() {
		System.out.println("Stützstellen anlegen und bearbeiten ...");

		GanglinieMQ g;

		g = new GanglinieMQ();
		g.setStuetzstelle(10, new Messwerte(90.0, 10.0, 130.0, 80.0));
		g.setStuetzstelle(30, new Messwerte(50.0, 5.0, 180.0, 90.0));
		g.setStuetzstelle(40, new Messwerte(70.0, 20.0, 100.0, 70.0));

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

		assertEquals(new Stuetzstelle<Messwerte>(10, new Messwerte(90.0, 10.0,
				130.0, 80.0)), g.getStuetzstelle(10));
		assertEquals(new Stuetzstelle<Messwerte>(30, new Messwerte(50.0, 5.0,
				180.0, 90.0)), g.getStuetzstelle(30));
		assertEquals(new Stuetzstelle<Messwerte>(40, new Messwerte(70.0, 20.0,
				100.0, 70.0)), g.getStuetzstelle(40));
	}

}
