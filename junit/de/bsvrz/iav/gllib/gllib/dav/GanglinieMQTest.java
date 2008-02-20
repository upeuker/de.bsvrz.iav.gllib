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

import com.bitctrl.util.Interval;

import de.bsvrz.iav.gllib.gllib.Stuetzstelle;

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

		g.setStuetzstelle(1000, new Messwerte(90.0, 10.0, 130.0, 80.0));
		g.setStuetzstelle(3000, new Messwerte(50.0, 5.0, 180.0, 90.0));
		g.setStuetzstelle(4000, new Messwerte(70.0, 20.0, 100.0, 70.0));

		assertEquals(3, g.anzahlStuetzstellen());
		assertEquals(3, g.getStuetzstellen().size());

		assertTrue(g.existsStuetzstelle(1000));
		assertTrue(g.existsStuetzstelle(3000));
		assertTrue(g.existsStuetzstelle(4000));
		assertFalse(g.existsStuetzstelle(500));
		assertFalse(g.existsStuetzstelle(-1000));
		assertFalse(g.existsStuetzstelle(6000));

		assertEquals(new Interval(1000, 4000), g.getIntervall());

		assertTrue(g.isValid(1500));
		assertTrue(g.isValid(3000));
		assertTrue(g.isValid(3500));
		assertFalse(g.isValid(200));
		assertFalse(g.isValid(900));
		assertFalse(g.isValid(4500));

		assertEquals(new Stuetzstelle<Messwerte>(1000, new Messwerte(90.0,
				10.0, 130.0, 80.0)), g.getStuetzstellen().get(0));
		assertEquals(new Stuetzstelle<Messwerte>(3000, new Messwerte(50.0, 5.0,
				180.0, 90.0)), g.getStuetzstellen().get(1));
		assertEquals(new Stuetzstelle<Messwerte>(4000, new Messwerte(70.0,
				20.0, 100.0, 70.0)), g.getStuetzstellen().get(2));
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
