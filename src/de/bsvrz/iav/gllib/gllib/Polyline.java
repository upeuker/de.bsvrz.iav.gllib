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
 * Wei�enfelser Stra�e 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

/**
 * Approximation einer Ganglinie mit Hilfe von Polylines. Der Wert der
 * St&uuml;tzstelle zu einem Zeitstempel wird nach folgender Formel berechnet:
 * <p>
 * <img src="doc-files/formel_polyline.png">
 * 
 * 
 * @author BitCtrl, Schumann
 * @version $Id$
 */
public class Polyline extends AbstractApproximation {

	/**
	 * {@inheritDoc}
	 */
	public Stuetzstelle getStuetzstelle(long zeitstempel) {
		Stuetzstelle s0;
		Stuetzstelle s1;
		Stuetzstelle s;
		Long wert;

		if (!ganglinie.contains(zeitstempel)) {
			// Zeitstempel geh�rt nicht zur Ganglinie
			return null;
		}

		s = new Stuetzstelle(zeitstempel);
		if (ganglinie.contains(s)) {
			return ganglinie.getStuetzstelle(zeitstempel);
		}

		// St�tzstelle muss berechnet werden
		s0 = naechsteStuetzstelleDavor(zeitstempel);
		s1 = naechsteStuetzstelleDanach(zeitstempel);
		wert = s0.wert + (s1.wert - s0.wert)
				/ (s1.zeitstempel - s0.zeitstempel)
				* (zeitstempel - s0.zeitstempel);
		return new Stuetzstelle(zeitstempel, wert.intValue());
	}

}
