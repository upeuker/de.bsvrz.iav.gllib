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
	 * Testet das Anlegen einer Ganglinien und einfache Bearbeitung derselben.
	 */
	@Test
	public void testGanglinieAnlegen() {
		System.out.println("Stützstellen anlegen und bearbeiten ...");

		GanglinieMQ g;

		g = new GanglinieMQ();
		g.setApproximation(null);
		g.aktualisiereApproximation();

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

		assertTrue(g.isValid(15));
		assertTrue(g.isValid(30));
		assertTrue(g.isValid(35));
		assertFalse(g.isValid(2));
		assertFalse(g.isValid(9));
		assertFalse(g.isValid(45));

		assertEquals(new Stuetzstelle<Messwerte>(10, new Messwerte(90.0, 10.0,
				130.0, 80.0)), g.getStuetzstellen().get(0));
		assertEquals(new Stuetzstelle<Messwerte>(30, new Messwerte(50.0, 5.0,
				180.0, 90.0)), g.getStuetzstellen().get(1));
		assertEquals(new Stuetzstelle<Messwerte>(40, new Messwerte(70.0, 20.0,
				100.0, 70.0)), g.getStuetzstellen().get(2));
	}

	/**
	 * Testet den Umgang einer Ganglinie ohne St&uuml;tzstellen.
	 */
	@Test
	public void testGanglinieOhneStuetzstellen() {
		System.out.println("Ganglinie ohne Stützstellen ...");

		GanglinieMQ g;

		g = new GanglinieMQ();

		assertNull(g.getStuetzstelle(1).getWert().getQKfz());
		assertNull(g.getStuetzstelle(1).getWert().getQLkw());
		assertNull(g.getStuetzstelle(1).getWert().getVPkw());
		assertNull(g.getStuetzstelle(1).getWert().getVLkw());

		assertEquals(0, g.anzahlStuetzstellen());
		assertEquals(0, g.getStuetzstellen().size());

		assertEquals(null, g.getIntervall());
		assertFalse(g.existsStuetzstelle(2));
		assertFalse(g.isValid(2));

		assertEquals(0, g.anzahlStuetzstellen());
	}

}
