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
 * Weißenfelser Straße 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

import de.bsvrz.iav.gllib.gllib.events.GanglinienEvent;
import de.bsvrz.iav.gllib.gllib.events.GanglinienListener;

/**
 * Approximation einer Ganglinie mit Hilfe eines Cubic-Splines.
 * 
 * @author BitCtrl, Schumann
 * @version $Id$
 */
public class CubicSpline extends AbstractApproximation implements
		GanglinienListener {

	/**
	 * {@inheritDoc}
	 */
	public Stuetzstelle get(long zeitstempel) {
		if (!ganglinie.isValid(zeitstempel)) {
			// Zeitstempel gehört nicht zur Ganglinie
			return null;
		}

		if (ganglinie.existsStuetzstelle(zeitstempel)) {
			// Zum Zeitstempel existiert eine Stützstelle
			return ganglinie.get(zeitstempel);
		}

		// Stützstelle muss berechnet werden
		return new Stuetzstelle(zeitstempel, berechneStuetzstelle(zeitstempel));
	}

	/**
	 * Berechnet die St&uuml;tzstelle.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel der gesuchten St&uuml;tzstelle
	 * @return Die gesuchte St&uuml;tzstelle
	 */
	private int berechneStuetzstelle(long zeitstempel) {
		Stuetzstelle s0; // vorletzte vor Zeitstempel
		Stuetzstelle s1; // vor Zeitstempel
		Stuetzstelle s2; // nach Zeitstempel
		long h0, c0;
		long h1, a1, b1, c1, d1;
		long a2, c2;

		s1 = ganglinie.naechsteStuetzstelleDavor(zeitstempel);
		s2 = ganglinie.naechsteStuetzstelleDanach(zeitstempel);
		s0 = ganglinie.naechsteStuetzstelleDavor(s1.zeitstempel);

		h0 = s1.zeitstempel - s0.zeitstempel;
		h1 = s2.zeitstempel - s1.zeitstempel;
		a1 = s1.wert;
		a2 = s2.wert;
		b1 = (a2 - a1) / h1;
		// b1 -= (2 * c1 + c2) / 3 * h1;
		// d1 = (c2 - c1) / (3 * h1);

		return 0;
	}

	public void ganglinieAktualisiert(GanglinienEvent e) {
		// TODO Auto-generated method stub

	}
}
