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
 * Weiﬂenfelser Straﬂe 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Testet die Approximation einer Ganglinie mit Hilfe eines B-Spline.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class TestBSpline {

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
		g.setStuetzstelle(300, 300.0);
		g.setStuetzstelle(400, 200.0);
		g.setStuetzstelle(600, 400.0);
		g.setStuetzstelle(900, 100.0);

		spline = new BSpline();
		spline.setStuetzstellen(g.getStuetzstellen());
		spline.initialisiere();

		// Rechnen
		for (byte k = 1; k <= g.anzahlStuetzstellen() && k <= 10; k++) {
			spline.setOrdnung(k);
			for (long t = g.getIntervall().start; t <= g.getIntervall().ende; t += 1) {
				assertEquals(t, spline.get(t).getZeitstempel());
			}
		}
	}

	/**
	 * Pr&uuml;ft ob bei Anfrage einer St&uuml;tzstelle auch der richtige
	 * Zeitstempel kommt. Beim B-Spline ist dies nicht trivial. Dieser Test
	 * arbeitet mit einer zuf&auml;lligen Ganglinie.
	 */
	@Test
	public void testGetB() {
		Ganglinie g;
		BSpline spline;

		g = new Ganglinie();
		for (int i = 0; i < 100; i++) {
			long x = (long) (Math.random() * 1000);
			double y = (int) (Math.random() * 1000);
			g.setStuetzstelle(x, y);
		}

		spline = new BSpline();
		spline.setStuetzstellen(g.getStuetzstellen());
		spline.initialisiere();

		// Rechnen
		for (byte k = 1; k < 10; k++) {
			spline.setOrdnung(k);
			for (long t = g.getIntervall().start; t <= g.getIntervall().ende; t += 1) {
				assertEquals(t, spline.get(t).getZeitstempel());
			}
		}
	}

}
