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

import static com.bitctrl.Constants.MILLIS_PER_HOUR;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Testet die Approximation einer Ganglinie mit Hilfe eines B-Spline.
 * 
 * @todo B-Spline nachrechnen
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class BSplineTest {

	/**
	 * Prüft das Verhalten des B-Spline bei (zu) wenigen Stützstellen.
	 */
	@Test
	public void testAnzahlStuetzstelle() {
		List<Stuetzstelle<Double>> stuetzstellen;
		BSpline spline;

		stuetzstellen = new ArrayList<Stuetzstelle<Double>>();

		// Nur eine Stützstelle
		stuetzstellen
				.add(new Stuetzstelle<Double>(100 * MILLIS_PER_HOUR, 30.0));
		spline = new BSpline();
		spline.setStuetzstellen(stuetzstellen);
		spline.setOrdnung((byte) 1);
		spline.initialisiere();
		assertEquals(new Stuetzstelle<Double>(0, null), spline.get(0));
		assertEquals(new Stuetzstelle<Double>(100 * MILLIS_PER_HOUR, 30.0),
				spline.get(100 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(120 * MILLIS_PER_HOUR, null),
				spline.get(120 * MILLIS_PER_HOUR));

		// Nur zwei Stützstellen
		stuetzstellen
				.add(new Stuetzstelle<Double>(200 * MILLIS_PER_HOUR, 80.0));
		spline = new BSpline();
		spline.setStuetzstellen(stuetzstellen);
		spline.setOrdnung((byte) 2);
		spline.initialisiere();
		assertEquals(new Stuetzstelle<Double>(0, null), spline.get(0));
		assertEquals(new Stuetzstelle<Double>(100 * MILLIS_PER_HOUR, 30.0),
				spline.get(100 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(200 * MILLIS_PER_HOUR, 80.0),
				spline.get(200 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(320 * MILLIS_PER_HOUR, null),
				spline.get(320 * MILLIS_PER_HOUR));

		// Nur drei Stützstellen
		stuetzstellen
				.add(new Stuetzstelle<Double>(300 * MILLIS_PER_HOUR, 40.0));
		spline = new BSpline();
		spline.setStuetzstellen(stuetzstellen);
		spline.setOrdnung((byte) 3);
		spline.initialisiere();
		assertEquals(new Stuetzstelle<Double>(0, null), spline.get(0));
		assertEquals(new Stuetzstelle<Double>(100 * MILLIS_PER_HOUR, 30.0),
				spline.get(100 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(200 * MILLIS_PER_HOUR, 57.5),
				spline.get(200 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(300 * MILLIS_PER_HOUR, 40.0),
				spline.get(300 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(320 * MILLIS_PER_HOUR, null),
				spline.get(320 * MILLIS_PER_HOUR));

		// Nur drei Stützstellen, mit Ordnung 5
		spline = new BSpline();
		spline.setStuetzstellen(stuetzstellen);
		spline.initialisiere();
		assertEquals(new Stuetzstelle<Double>(0, null), spline.get(0));
		assertEquals(new Stuetzstelle<Double>(100 * MILLIS_PER_HOUR, 30.0),
				spline.get(100 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(200 * MILLIS_PER_HOUR, 57.5),
				spline.get(200 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(300 * MILLIS_PER_HOUR, 40.0),
				spline.get(300 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(320 * MILLIS_PER_HOUR, null),
				spline.get(320 * MILLIS_PER_HOUR));
	}

	/**
	 * Prüft ob bei Anfrage einer Stützstelle auch der richtige
	 * Zeitstempel kommt. Beim B-Spline ist dies nicht trivial. Dieser Test
	 * arbeitet mit einer vorgegebenen Ganglinie.
	 */
	@Test
	public void testGetA() {
		Ganglinie g;
		BSpline spline;

		g = new Ganglinie();
		g.setStuetzstelle(0, 0.0);
		g.setStuetzstelle(3 * 60 * 1000, 300.0);
		g.setStuetzstelle(4 * 60 * 1000, 200.0);
		g.setStuetzstelle(6 * 60 * 1000, 400.0);
		g.setStuetzstelle(9 * 60 * 1000, 100.0);

		spline = new BSpline();
		spline.setStuetzstellen(g.getStuetzstellen());

		// Rechnen
		for (byte k = 1; k <= g.anzahlStuetzstellen() && k <= 10; k++) {
			spline.setOrdnung(k);
			spline.initialisiere();

			for (long t = g.getIntervall().getStart(); t <= g.getIntervall()
					.getEnd(); t += 60 * 1000) {
				assertEquals(t, spline.get(t).getZeitstempel());
			}
		}
	}

	// /**
	// * Prüft ob bei Anfrage einer Stützstelle auch der richtige
	// * Zeitstempel kommt. Beim B-Spline ist dies nicht trivial. Dieser Test
	// * arbeitet mit einer zufälligen Ganglinie.
	// */
	// @Test
	// public void testGetB() {
	// Ganglinie g;
	// BSpline spline;
	//
	// g = new Ganglinie();
	// for (int i = 0; i < 100; i++) {
	// long x = (long) (Math.random() * 1000);
	// double y = (int) (Math.random() * 1000);
	// g.setStuetzstelle(x, y);
	// }
	//
	// spline = new BSpline();
	// spline.setStuetzstellen(g.getStuetzstellen());
	// spline.initialisiere();
	//
	// // Rechnen
	// for (byte k = 1; k < 10; k++) {
	// spline.setOrdnung(k);
	// for (long t = g.getIntervall().start; t <= g.getIntervall().ende; t += 1)
	// {
	// assertEquals(t, spline.get(t).getZeitstempel());
	// }
	// }
	// }

}
