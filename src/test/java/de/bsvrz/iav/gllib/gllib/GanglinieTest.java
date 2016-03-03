/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.5 Funktionen Ganglinie
 * Copyright (C) 2011-2015 BitCtrl Systems GmbH
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
 */
public class GanglinieTest {

	/**
	 * Testet das Anlegen einer Ganglinien und einfache Bearbeitung derselben.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void testGanglinieAnlegen() {
		Ganglinie<Double> g;

		g = new Ganglinie<Double>();
		g.put(1000L, 25.0);
		g.put(3000L, 40.0);
		g.put(4000L, 35.0);

		assertEquals(3, g.size());
		assertEquals(3, g.getStuetzstellen().size());

		assertTrue(g.containsKey(1000L));
		assertTrue(g.containsKey(3000L));
		assertTrue(g.containsKey(4000L));
		assertFalse(g.containsKey(500L));
		assertFalse(g.containsKey(-1000L));
		assertFalse(g.containsKey(6000L));

		assertEquals(new Interval(1000, 4000), g.getIntervall());
		assertEquals(1, g.getIntervalle().size());
		assertEquals(new Interval(1000, 4000), g.getIntervalle().get(0));

		assertTrue(g.isValid(1500));
		assertTrue(g.isValid(3000));
		assertTrue(g.isValid(3500));
		assertFalse(g.isValid(200));
		assertFalse(g.isValid(900));
		assertFalse(g.isValid(4500));

		assertEquals(new Stuetzstelle<Double>(1000, 25.0),
				g.getStuetzstellen().get(0));
		assertEquals(new Stuetzstelle<Double>(3000, 40.0),
				g.getStuetzstellen().get(1));
		assertEquals(new Stuetzstelle<Double>(4000, 35.0),
				g.getStuetzstellen().get(2));

		assertEquals(3, g.size());
	}

	/**
	 * Testet den Umgang einer Ganglinie ohne Stützstellen.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void testGanglinieOhneStuetzstellen() {
		Ganglinie<Double> g;

		g = new Ganglinie<Double>();

		assertNull(g.getStuetzstelle(1).getWert());

		assertEquals(0, g.size());
		assertEquals(0, g.getStuetzstellen().size());

		assertEquals(0, g.getIntervalle().size());
		assertEquals(null, g.getIntervall());
		assertFalse(g.containsKey(2));
		assertFalse(g.isValid(2));

		assertEquals(0, g.size());
	}

	/**
	 * Testet den Umgang mit undefinierten Bereichen.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void testUndefinierteBereiche() {
		Ganglinie<Double> g;
		List<Interval> intervalle;

		g = new Ganglinie<Double>();
		g.put(0L, 30.0);
		g.put(1000L, 15.0);
		g.put(2000L, 34.0);
		g.put(3000L, 54.0);
		g.put(4000L, 23.0);
		g.put(5000L, 15.0);
		g.put(6000L, 34.0);
		g.put(7000L, 45.8);
		g.put(8000L, 23.0);
		g.put(9000L, 15.0);
		g.put(10000L, 34.0);
		g.put(11000L, 34.0);
		g.put(12000L, 34.0);
		intervalle = new ArrayList<Interval>();
		intervalle.add(new Interval(0, 12000));
		assertEquals(intervalle, g.getIntervalle());

		g = new Ganglinie<Double>();
		g.put(0L, 30.0);
		g.put(1000L, 15.0);
		g.put(2000L, 34.0);
		g.put(3000L, null);
		g.put(4000L, 23.0);
		g.put(5000L, 15.0);
		g.put(6000L, 34.0);
		g.put(7000L, 45.8);
		g.put(8000L, 23.0);
		g.put(9000L, 15.0);
		g.put(10000L, 34.0);
		g.put(11000L, 34.0);
		g.put(12000L, 34.0);
		intervalle = new ArrayList<Interval>();
		intervalle.add(new Interval(0, 2000));
		intervalle.add(new Interval(4000, 12000));
		assertEquals(intervalle, g.getIntervalle());

		g = new Ganglinie<Double>();
		g.put(0L, 30.0);
		g.put(1000L, 15.0);
		g.put(2000L, 34.0);
		g.put(3000L, null);
		g.put(4000L, 23.0);
		g.put(5000L, 15.0);
		g.put(6000L, 34.0);
		g.put(7000L, 45.8);
		g.put(8000L, null);
		g.put(9000L, 15.0);
		g.put(10000L, 34.0);
		g.put(11000L, null);
		g.put(12000L, 34.0);
		intervalle = new ArrayList<Interval>();
		intervalle.add(new Interval(0, 2000));
		intervalle.add(new Interval(4000, 7000));
		intervalle.add(new Interval(9000, 10000));
		intervalle.add(new Interval(12000, 12000));
		assertEquals(intervalle, g.getIntervalle());

		g = new Ganglinie<Double>();
		g.put(0L, 30.0);
		g.put(1000L, 15.0);
		g.put(2000L, 34.0);
		g.put(3000L, null);
		g.put(4000L, 23.0);
		g.put(5000L, 15.0);
		g.put(6000L, 34.0);
		g.put(7000L, 45.8);
		g.put(8000L, null);
		g.put(9000L, 15.0);
		g.put(10000L, 34.0);
		g.put(11000L, 54.1);
		g.put(12000L, null);
		intervalle = new ArrayList<Interval>();
		intervalle.add(new Interval(0, 2000));
		intervalle.add(new Interval(4000, 7000));
		intervalle.add(new Interval(9000, 11000));
		assertEquals(intervalle, g.getIntervalle());

		g = new Ganglinie<Double>();
		g.put(0L, null);
		g.put(1000L, 15.0);
		g.put(2000L, 34.0);
		g.put(3000L, null);
		g.put(4000L, 23.0);
		g.put(5000L, 15.0);
		g.put(6000L, 34.0);
		g.put(7000L, 45.8);
		g.put(8000L, null);
		g.put(9000L, 15.0);
		g.put(10000L, 34.0);
		g.put(11000L, null);
		g.put(12000L, 34.0);
		intervalle = new ArrayList<Interval>();
		intervalle.add(new Interval(1000, 2000));
		intervalle.add(new Interval(4000, 7000));
		intervalle.add(new Interval(9000, 10000));
		intervalle.add(new Interval(12000, 12000));
		assertEquals(intervalle, g.getIntervalle());

		g = new Ganglinie<Double>();
		g.put(0L, null);
		g.put(1000L, 15.0);
		g.put(2000L, null);
		g.put(3000L, 34.0);
		g.put(4000L, null);
		g.put(5000L, 15.0);
		g.put(6000L, 34.0);
		g.put(7000L, 45.8);
		g.put(8000L, null);
		g.put(9000L, 15.0);
		g.put(10000L, 34.0);
		g.put(11000L, 54.1);
		g.put(12000L, null);
		intervalle = new ArrayList<Interval>();
		intervalle.add(new Interval(1000, 1000));
		intervalle.add(new Interval(3000, 3000));
		intervalle.add(new Interval(5000, 7000));
		intervalle.add(new Interval(9000, 11000));
		assertEquals(intervalle, g.getIntervalle());
	}

}
