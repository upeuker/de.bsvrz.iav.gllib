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

import org.junit.Test;

import de.bsvrz.sys.funclib.bitctrl.util.Intervall;

/**
 * Testet die Approximation einer Ganglinie mit Hilfe eines B-Spline.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class BSplineTest {

	/**
	 * Pr&uuml;ft ob bei Anfrage einer St&uuml;tzstelle auch der richtige
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

			for (long t = g.getIntervall().start; t <= g.getIntervall().ende; t += 60 * 1000) {
				assertEquals(t, spline.get(t).getZeitstempel());
			}
		}

		System.err.println("Integral B-Spline: "
				+ spline.integral(new Intervall(0, 900)));
	}

	// /**
	// * Pr&uuml;ft ob bei Anfrage einer St&uuml;tzstelle auch der richtige
	// * Zeitstempel kommt. Beim B-Spline ist dies nicht trivial. Dieser Test
	// * arbeitet mit einer zuf&auml;lligen Ganglinie.
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
