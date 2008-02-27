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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.bitctrl.util.Interval;

/**
 * Testet die Verwendung einer Ganglinie.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */

public class GanglinieTest {

	/**
	 * Testet das Anlegen einer Ganglinien und einfache Bearbeitung derselben.
	 */
	@Test
	public void testGanglinieAnlegen() {
		Ganglinie g;

		g = new Ganglinie();
		g.setStuetzstelle(1000, 25.0);
		g.setStuetzstelle(3000, 40.0);
		g.setStuetzstelle(4000, 35.0);

		assertEquals(3, g.anzahlStuetzstellen());
		assertEquals(3, g.getStuetzstellen().size());

		assertTrue(g.existsStuetzstelle(1000));
		assertTrue(g.existsStuetzstelle(3000));
		assertTrue(g.existsStuetzstelle(4000));
		assertFalse(g.existsStuetzstelle(500));
		assertFalse(g.existsStuetzstelle(-1000));
		assertFalse(g.existsStuetzstelle(6000));

		assertEquals(new Interval(1000, 4000), g.getIntervall());
		assertEquals(1, g.getIntervalle().size());
		assertEquals(new Interval(1000, 4000), g.getIntervalle().get(0));

		assertTrue(g.isValid(1500));
		assertTrue(g.isValid(3000));
		assertTrue(g.isValid(3500));
		assertFalse(g.isValid(200));
		assertFalse(g.isValid(900));
		assertFalse(g.isValid(4500));

		assertEquals(new Stuetzstelle<Double>(1000, 25.0), g.getStuetzstellen()
				.get(0));
		assertEquals(new Stuetzstelle<Double>(3000, 40.0), g.getStuetzstellen()
				.get(1));
		assertEquals(new Stuetzstelle<Double>(4000, 35.0), g.getStuetzstellen()
				.get(2));

		assertEquals(3, g.anzahlStuetzstellen());
	}

	/**
	 * Testet den Umgang einer Ganglinie ohne Stützstellen.
	 */
	@Test
	public void testGanglinieOhneStuetzstellen() {
		Ganglinie g;

		g = new Ganglinie();

		assertNull(g.getStuetzstelle(1).getWert());

		assertEquals(0, g.anzahlStuetzstellen());
		assertEquals(0, g.getStuetzstellen().size());

		assertEquals(0, g.getIntervalle().size());
		assertEquals(null, g.getIntervall());
		assertFalse(g.existsStuetzstelle(2));
		assertFalse(g.isValid(2));

		assertEquals(0, g.anzahlStuetzstellen());
	}

	/**
	 * Testet den Umgang mit undefinierten Bereichen.
	 */
	@Test
	public void testUndefinierteBereiche() {
		Ganglinie g;
		List<Interval> intervalle;

		g = new Ganglinie();
		g.setStuetzstelle(0, 30.0);
		g.setStuetzstelle(1000, 15.0);
		g.setStuetzstelle(2000, 34.0);
		g.setStuetzstelle(3000, 54.0);
		g.setStuetzstelle(4000, 23.0);
		g.setStuetzstelle(5000, 15.0);
		g.setStuetzstelle(6000, 34.0);
		g.setStuetzstelle(7000, 45.8);
		g.setStuetzstelle(8000, 23.0);
		g.setStuetzstelle(9000, 15.0);
		g.setStuetzstelle(10000, 34.0);
		g.setStuetzstelle(11000, 34.0);
		g.setStuetzstelle(12000, 34.0);
		intervalle = new ArrayList<Interval>();
		intervalle.add(new Interval(0, 12000));
		assertEquals(intervalle, g.getIntervalle());

		g = new Ganglinie();
		g.setStuetzstelle(0, 30.0);
		g.setStuetzstelle(1000, 15.0);
		g.setStuetzstelle(2000, 34.0);
		g.setStuetzstelle(3000, null);
		g.setStuetzstelle(4000, 23.0);
		g.setStuetzstelle(5000, 15.0);
		g.setStuetzstelle(6000, 34.0);
		g.setStuetzstelle(7000, 45.8);
		g.setStuetzstelle(8000, 23.0);
		g.setStuetzstelle(9000, 15.0);
		g.setStuetzstelle(10000, 34.0);
		g.setStuetzstelle(11000, 34.0);
		g.setStuetzstelle(12000, 34.0);
		intervalle = new ArrayList<Interval>();
		intervalle.add(new Interval(0, 2000));
		intervalle.add(new Interval(4000, 12000));
		assertEquals(intervalle, g.getIntervalle());

		g = new Ganglinie();
		g.setStuetzstelle(0, 30.0);
		g.setStuetzstelle(1000, 15.0);
		g.setStuetzstelle(2000, 34.0);
		g.setStuetzstelle(3000, null);
		g.setStuetzstelle(4000, 23.0);
		g.setStuetzstelle(5000, 15.0);
		g.setStuetzstelle(6000, 34.0);
		g.setStuetzstelle(7000, 45.8);
		g.setStuetzstelle(8000, null);
		g.setStuetzstelle(9000, 15.0);
		g.setStuetzstelle(10000, 34.0);
		g.setStuetzstelle(11000, null);
		g.setStuetzstelle(12000, 34.0);
		intervalle = new ArrayList<Interval>();
		intervalle.add(new Interval(0, 2000));
		intervalle.add(new Interval(4000, 7000));
		intervalle.add(new Interval(9000, 10000));
		intervalle.add(new Interval(12000, 12000));
		assertEquals(intervalle, g.getIntervalle());

		g = new Ganglinie();
		g.setStuetzstelle(0, 30.0);
		g.setStuetzstelle(1000, 15.0);
		g.setStuetzstelle(2000, 34.0);
		g.setStuetzstelle(3000, null);
		g.setStuetzstelle(4000, 23.0);
		g.setStuetzstelle(5000, 15.0);
		g.setStuetzstelle(6000, 34.0);
		g.setStuetzstelle(7000, 45.8);
		g.setStuetzstelle(8000, null);
		g.setStuetzstelle(9000, 15.0);
		g.setStuetzstelle(10000, 34.0);
		g.setStuetzstelle(11000, 54.1);
		g.setStuetzstelle(12000, null);
		intervalle = new ArrayList<Interval>();
		intervalle.add(new Interval(0, 2000));
		intervalle.add(new Interval(4000, 7000));
		intervalle.add(new Interval(9000, 11000));
		assertEquals(intervalle, g.getIntervalle());

		g = new Ganglinie();
		g.setStuetzstelle(0, null);
		g.setStuetzstelle(1000, 15.0);
		g.setStuetzstelle(2000, 34.0);
		g.setStuetzstelle(3000, null);
		g.setStuetzstelle(4000, 23.0);
		g.setStuetzstelle(5000, 15.0);
		g.setStuetzstelle(6000, 34.0);
		g.setStuetzstelle(7000, 45.8);
		g.setStuetzstelle(8000, null);
		g.setStuetzstelle(9000, 15.0);
		g.setStuetzstelle(10000, 34.0);
		g.setStuetzstelle(11000, null);
		g.setStuetzstelle(12000, 34.0);
		intervalle = new ArrayList<Interval>();
		intervalle.add(new Interval(1000, 2000));
		intervalle.add(new Interval(4000, 7000));
		intervalle.add(new Interval(9000, 10000));
		intervalle.add(new Interval(12000, 12000));
		assertEquals(intervalle, g.getIntervalle());

		g = new Ganglinie();
		g.setStuetzstelle(0, null);
		g.setStuetzstelle(1000, 15.0);
		g.setStuetzstelle(2000, null);
		g.setStuetzstelle(3000, 34.0);
		g.setStuetzstelle(4000, null);
		g.setStuetzstelle(5000, 15.0);
		g.setStuetzstelle(6000, 34.0);
		g.setStuetzstelle(7000, 45.8);
		g.setStuetzstelle(8000, null);
		g.setStuetzstelle(9000, 15.0);
		g.setStuetzstelle(10000, 34.0);
		g.setStuetzstelle(11000, 54.1);
		g.setStuetzstelle(12000, null);
		intervalle = new ArrayList<Interval>();
		intervalle.add(new Interval(1000, 1000));
		intervalle.add(new Interval(3000, 3000));
		intervalle.add(new Interval(5000, 7000));
		intervalle.add(new Interval(9000, 11000));
		assertEquals(intervalle, g.getIntervalle());
	}

}
