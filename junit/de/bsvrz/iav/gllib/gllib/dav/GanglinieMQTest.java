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
	 * Testet das Anlegen einer Ganglinien und einfache Bearbeitung derselben.
	 */
	@Test
	public void testGanglinieAnlegen() {
		GanglinieMQ g;

		g = new GanglinieMQ();

		g.put(1000L, new Messwerte(90.0, 10.0, 130.0, 80.0));
		g.put(3000L, new Messwerte(50.0, 5.0, 180.0, 90.0));
		g.put(4000L, new Messwerte(70.0, 20.0, 100.0, 70.0));

		assertEquals(3, g.size());
		assertEquals(3, g.getStuetzstellen().size());

		assertTrue(g.containsKey(1000L));
		assertTrue(g.containsKey(3000L));
		assertTrue(g.containsKey(4000L));
		assertFalse(g.containsKey(500L));
		assertFalse(g.containsKey(-1000L));
		assertFalse(g.containsKey(6000L));

		assertEquals(new Interval(1000, 4000), g.getIntervall());

		assertEquals(new Stuetzstelle<Messwerte>(1000, new Messwerte(90.0,
				10.0, 130.0, 80.0)), g.getStuetzstellen().get(0));
		assertEquals(new Stuetzstelle<Messwerte>(3000, new Messwerte(50.0, 5.0,
				180.0, 90.0)), g.getStuetzstellen().get(1));
		assertEquals(new Stuetzstelle<Messwerte>(4000, new Messwerte(70.0,
				20.0, 100.0, 70.0)), g.getStuetzstellen().get(2));
	}

	/**
	 * Testet den Umgang einer Ganglinie ohne Stützstellen.
	 */
	@Test
	public void testGanglinieOhneStuetzstellen() {
		GanglinieMQ g;

		g = new GanglinieMQ();

		assertNull(g.getStuetzstelle(1).getWert().getQKfz());
		assertNull(g.getStuetzstelle(1).getWert().getQLkw());
		assertNull(g.getStuetzstelle(1).getWert().getVPkw());
		assertNull(g.getStuetzstelle(1).getWert().getVLkw());

		assertEquals(0, g.size());
		assertEquals(0, g.getStuetzstellen().size());

		assertEquals(null, g.getIntervall());
		assertFalse(g.containsKey(2));

		assertEquals(0, g.size());
	}

}
