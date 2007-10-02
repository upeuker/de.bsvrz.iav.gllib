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
 * Weiﬂenfelser Straﬂe 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Testet die Verwendung einer Ganglinie.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id: TestGanglinienOperationen.java 3962 2007-10-01 13:09:43Z
 *          Schumann $
 */

public class TestGanglinienOperationen {

	/**
	 * Ausgabe, welche Klasse gerade getestet wird.
	 */
	@BeforeClass
	public static void beforeClass() {
		System.out.println("### Teste Klasse GanglinienOperationen ###");
	}

	/**
	 * Testet die Addition zweier Ganglinien.
	 */
	@Test
	public void testAddiere() {
		System.out.println("Addiere zwei Ganglinien ...");

		Ganglinie g1, g2, ist, soll;

		g1 = new Ganglinie();
		g1.setStuetzstelle(0, 0.0);
		g1.setStuetzstelle(30, 30.0);
		g1.setStuetzstelle(40, 20.0);
		g1.setStuetzstelle(60, 40.0);
		g1.setStuetzstelle(90, 10.0);

		g2 = new Ganglinie();
		g2.setStuetzstelle(10, 20.0);
		g2.setStuetzstelle(30, 40.0);
		g2.setStuetzstelle(70, 0.0);
		g2.setStuetzstelle(90, 20.0);

		ist = GanglinienOperationen.addiere(g1, g2);

		soll = new Ganglinie();
		soll.setStuetzstelle(0, null);
		soll.setStuetzstelle(10, 30.0);
		soll.setStuetzstelle(30, 70.0);
		soll.setStuetzstelle(40, 50.0);
		soll.setStuetzstelle(60, 50.0);
		soll.setStuetzstelle(70, 30.0);
		soll.setStuetzstelle(90, 30.0);

		assertEquals(soll.getStuetzstellen(), ist.getStuetzstellen());
	}

	/**
	 * Testet die Division zweier Ganglinien.
	 */
	@Test
	public void testDividiere() {
		System.out.println("Dividiere zwei Ganglinien ...");

		Ganglinie g1, g2, ist, soll;

		g1 = new Ganglinie();
		g1.setStuetzstelle(0, 0.0);
		g1.setStuetzstelle(30, 30.0);
		g1.setStuetzstelle(40, 20.0);
		g1.setStuetzstelle(60, 40.0);
		g1.setStuetzstelle(90, 10.0);

		g2 = new Ganglinie();
		g2.setStuetzstelle(10, 20.0);
		g2.setStuetzstelle(30, 40.0);
		g2.setStuetzstelle(70, 0.0);
		g2.setStuetzstelle(90, 20.0);

		ist = GanglinienOperationen.dividiere(g1, g2);

		soll = new Ganglinie();
		soll.setStuetzstelle(0, null);
		soll.setStuetzstelle(10, 0.5);
		soll.setStuetzstelle(30, 0.75);
		soll.setStuetzstelle(40, 0.6666666666666666);
		soll.setStuetzstelle(60, 4.0);
		soll.setStuetzstelle(70, null);
		soll.setStuetzstelle(90, 0.5);

		assertEquals(soll.getStuetzstellen(), ist.getStuetzstellen());
	}

	/**
	 * Testet die Multiplikation zweier Ganglinien.
	 */
	@Test
	public void testMultipliziere() {
		System.out.println("Multipliziere zwei Ganglinien ...");

		Ganglinie g1, g2, ist, soll;

		g1 = new Ganglinie();
		g1.setStuetzstelle(0, 0.0);
		g1.setStuetzstelle(30, 30.0);
		g1.setStuetzstelle(40, 20.0);
		g1.setStuetzstelle(60, 40.0);
		g1.setStuetzstelle(90, 10.0);

		g2 = new Ganglinie();
		g2.setStuetzstelle(10, 20.0);
		g2.setStuetzstelle(30, 40.0);
		g2.setStuetzstelle(70, 0.0);
		g2.setStuetzstelle(90, 20.0);

		ist = GanglinienOperationen.multipliziere(g1, g2);

		soll = new Ganglinie();
		soll.setStuetzstelle(0, null);
		soll.setStuetzstelle(10, 200.0);
		soll.setStuetzstelle(30, 1200.0);
		soll.setStuetzstelle(40, 600.0);
		soll.setStuetzstelle(60, 400.0);
		soll.setStuetzstelle(70, 0.0);
		soll.setStuetzstelle(90, 200.0);

		assertEquals(soll.getStuetzstellen(), ist.getStuetzstellen());
	}

	/**
	 * Testet die Subtraktion zweier Ganglinien.
	 */
	@Test
	public void testSubtrahiere() {
		System.out.println("Subtrahiere zwei Ganglinien ...");

		Ganglinie g1, g2, ist, soll;

		g1 = new Ganglinie();
		g1.setStuetzstelle(0, 0.0);
		g1.setStuetzstelle(30, 30.0);
		g1.setStuetzstelle(40, 20.0);
		g1.setStuetzstelle(60, 40.0);
		g1.setStuetzstelle(90, 10.0);

		g2 = new Ganglinie();
		g2.setStuetzstelle(10, 20.0);
		g2.setStuetzstelle(30, 40.0);
		g2.setStuetzstelle(70, 0.0);
		g2.setStuetzstelle(90, 20.0);

		ist = GanglinienOperationen.subtrahiere(g1, g2);

		soll = new Ganglinie();
		soll.setStuetzstelle(0, null);
		soll.setStuetzstelle(10, -10.0);
		soll.setStuetzstelle(30, -10.0);
		soll.setStuetzstelle(40, -10.0);
		soll.setStuetzstelle(60, 30.0);
		soll.setStuetzstelle(70, 30.0);
		soll.setStuetzstelle(90, -10.0);

		assertEquals(soll.getStuetzstellen(), ist.getStuetzstellen());
	}

}
