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

import org.junit.Before;
import org.junit.Test;

import de.bsvrz.sys.funclib.bitctrl.util.UndefiniertException;

/**
 * Testet die Approximation einer Ganglinie mit Hilfe eines B-Spline.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class TestBSpline {

	/** Die Testganglinie. */
	private Ganglinie ganglinie;

	/** Initialisierung der Testganglinie. */
	@Before
	public void setUp() {
		ganglinie = new Ganglinie();
		ganglinie.set(new Stuetzstelle(0, 0));
		ganglinie.set(new Stuetzstelle(30, 30));
		ganglinie.set(new Stuetzstelle(40, 20));
		ganglinie.set(new Stuetzstelle(60, 40));
		ganglinie.set(new Stuetzstelle(90, 10));
	}

	/**
	 * Pr&uuml;ft ob bei Anfrage einer St&uuml;tzstelle auch der richtige
	 * Zeitstempel kommt. Beim B-Spline ist dies nicht trivial. Dieser Test
	 * arbeitet mit einer vorgegebenen Ganglinie.
	 * 
	 * @throws UndefiniertException
	 *             Wenn auf einen undefinierten Bereich der Ganglinie
	 *             zugegriffen wird
	 */
	@Test
	public void testGetA() throws UndefiniertException {
		Ganglinie g;
		BSpline spline;

		g = new Ganglinie();
		g.set(0, 0);
		g.set(300, 300);
		g.set(400, 200);
		g.set(600, 400);
		g.set(900, 100);

		spline = new BSpline(g);

		// Rechnen
		// for (long t = 0; t <= 900; t += 1) {
		for (short k = 1; k <= g.anzahlStuetzstellen(); k++) {
			spline.setOrdnung(k);
			for (long t = g.getIntervall().start; t <= g.getIntervall().ende; t += 1) {
				assertEquals(t, spline.get(t).zeitstempel);
			}
		}
	}

	/**
	 * Pr&uuml;ft ob bei Anfrage einer St&uuml;tzstelle auch der richtige
	 * Zeitstempel kommt. Beim B-Spline ist dies nicht trivial. Dieser Test
	 * arbeitet mit einer zuf&auml;lligen Ganglinie.
	 * 
	 * @throws UndefiniertException
	 *             Wenn auf einen undefinierten Bereich der Ganglinie
	 *             zugegriffen wird
	 */
	@Test
	public void testGetB() throws UndefiniertException {
		Ganglinie g;
		BSpline spline;

		g = new Ganglinie();
		for (int i = 0; i < 100; i++) {
			long x = (long) (Math.random() * 1000);
			int y = (int) (Math.random() * 1000);
			g.set(x, y);
		}

		spline = new BSpline(g);

		// Rechnen
		for (short k = 1; k < 10; k++) {
			spline.setOrdnung(k);
			for (long t = g.getIntervall().start; t <= g.getIntervall().ende; t += 1) {
				assertEquals(t, spline.get(t).zeitstempel);
			}
		}
	}

}
