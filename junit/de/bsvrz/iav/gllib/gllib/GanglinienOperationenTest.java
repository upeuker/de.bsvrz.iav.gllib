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

import org.junit.Test;

/**
 * Testet die Verwendung einer Ganglinie.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */

public class GanglinienOperationenTest {

	/**
	 * Testet die Addition zweier Ganglinien.
	 */
	@Test
	public void testAddiere() {
		Ganglinie<Double> g1, g2, ist, soll;

		g1 = new Ganglinie<Double>();
		g1.put(0L, 0.0);
		g1.put(30L, 30.0);
		g1.put(40L, 20.0);
		g1.put(60L, 40.0);
		g1.put(90L, 10.0);

		g2 = new Ganglinie<Double>();
		g2.put(20L, 50.0);
		g2.put(30L, 40.0);
		g2.put(60L, 10.0);
		g2.put(90L, 40.0);
		g2.put(100L, 30.0);

		ist = GanglinienOperationen.addiere(g1, g2);

		soll = new Ganglinie<Double>();
		soll.put(0L, 50.0);
		soll.put(20L, 70.0);
		soll.put(30L, 70.0);
		soll.put(40L, 50.0);
		soll.put(60L, 50.0);
		soll.put(90L, 50.0);
		soll.put(100L, 40.0);

		assertEquals("Die Anzahl der Stützstellen muss übereinstimmen.", soll
				.size(), ist.size());
		for (final Long t : soll.keySet()) {
			assertEquals("Der Stützstellenwert für " + t
					+ " muss übereinstimmen.", soll.get(t), ist.get(t));
		}
	}

	/**
	 * Testet die Division zweier Ganglinien.
	 */
	@Test
	public void testDividiere() {
		Ganglinie<Double> g1, g2, ist, soll;

		g1 = new Ganglinie<Double>();
		g1.put(0L, 0.0);
		g1.put(30L, 30.0);
		g1.put(40L, 20.0);
		g1.put(60L, 40.0);
		g1.put(90L, 10.0);

		g2 = new Ganglinie<Double>();
		g2.put(20L, 50.0);
		g2.put(30L, 40.0);
		g2.put(60L, 10.0);
		g2.put(90L, 40.0);
		g2.put(100L, 30.0);

		ist = GanglinienOperationen.dividiere(g1, g2);

		soll = new Ganglinie<Double>();
		soll.put(0L, 0.0);
		soll.put(20L, 0.4);
		soll.put(30L, 0.75);
		soll.put(40L, 0.6666666666);
		soll.put(60L, 4.0);
		soll.put(90L, 0.25);
		soll.put(100L, 0.3333333333);

		assertEquals("Die Anzahl der Stützstellen muss übereinstimmen.", soll
				.size(), ist.size());
		for (final Long t : soll.keySet()) {
			assertEquals("Der Stützstellenwert für " + t
					+ " muss übereinstimmen.", soll.get(t), ist.get(t));
		}
	}

	/**
	 * Testet die Multiplikation zweier Ganglinien.
	 */
	@Test
	public void testMultipliziere() {
		Ganglinie<Double> g1, g2, ist, soll;

		g1 = new Ganglinie<Double>();
		g1.put(0L, 0.0);
		g1.put(30L, 30.0);
		g1.put(40L, 20.0);
		g1.put(60L, 40.0);
		g1.put(90L, 10.0);

		g2 = new Ganglinie<Double>();
		g2.put(20L, 50.0);
		g2.put(30L, 40.0);
		g2.put(60L, 10.0);
		g2.put(90L, 40.0);
		g2.put(100L, 30.0);

		ist = GanglinienOperationen.multipliziere(g1, g2);

		soll = new Ganglinie<Double>();
		soll.put(0L, 0.0);
		soll.put(20L, 1000.0);
		soll.put(30L, 1200.0);
		soll.put(40L, 600.0);
		soll.put(60L, 400.0);
		soll.put(90L, 400.0);
		soll.put(100L, 300.0);

		assertEquals("Die Anzahl der Stützstellen muss übereinstimmen.", soll
				.size(), ist.size());
		for (final Long t : soll.keySet()) {
			assertEquals("Der Stützstellenwert für " + t
					+ " muss übereinstimmen.", soll.get(t), ist.get(t));
		}
	}

	/**
	 * Testet die Subtraktion zweier Ganglinien.
	 */
	@Test
	public void testSubtrahiere() {
		Ganglinie<Double> g1, g2, ist, soll;

		g1 = new Ganglinie<Double>();
		g1.put(0L, 0.0);
		g1.put(30L, 30.0);
		g1.put(40L, 20.0);
		g1.put(60L, 40.0);
		g1.put(90L, 10.0);

		g2 = new Ganglinie<Double>();
		g2.put(20L, 50.0);
		g2.put(30L, 40.0);
		g2.put(60L, 10.0);
		g2.put(90L, 40.0);
		g2.put(100L, 30.0);

		ist = GanglinienOperationen.subtrahiere(g1, g2);

		soll = new Ganglinie<Double>();
		soll.put(0L, -50.0);
		soll.put(20L, -30.0);
		soll.put(30L, -10.0);
		soll.put(40L, -10.0);
		soll.put(60L, 30.0);
		soll.put(90L, -30.0);
		soll.put(100L, -20.0);

		assertEquals("Die Anzahl der Stützstellen muss übereinstimmen.", soll
				.size(), ist.size());
		for (final Long t : soll.keySet()) {
			assertEquals("Der Stützstellenwert für " + t
					+ " muss übereinstimmen.", soll.get(t), ist.get(t));
		}
	}

	/**
	 * Testet die Berechnungs des Abstands zweier Ganglinien mit Hilfe des
	 * Basisabstandsverfahrens.
	 */
	@Test
	public void testBasisabstand() {
		Ganglinie<Double> g1, g2;

		g1 = new Ganglinie<Double>();
		g1.put(0L, 10.0);
		g1.put(10L, 20.0);
		g1.put(20L, 30.0);

		g2 = new Ganglinie<Double>();
		g2.put(0L, 20.0);
		g2.put(10L, 30.0);
		g2.put(20L, 10.0);

		assertEquals(69, GanglinienOperationen.basisabstand(g1, g2));
	}

	/**
	 * Testet die Berechnungs des Abstands zweier Ganglinien mit Hilfe des
	 * komplexen Abstandsverfahrens.
	 */
	@Test
	public void testKomplexerAbstand() {
		Ganglinie<Double> g1, g2;

		g1 = new Ganglinie<Double>();
		g1.setApproximation(new Polyline());
		g1.put(0L, 10.0);
		g1.put(10L, 20.0);
		g1.put(20L, 30.0);

		g2 = new Ganglinie<Double>();
		g2.setApproximation(new Polyline());
		g2.put(0L, 20.0);
		g2.put(10L, 30.0);
		g2.put(20L, 10.0);

		assertEquals(69, GanglinienOperationen.komplexerAbstand(g1, g2, 10L));
	}
}
