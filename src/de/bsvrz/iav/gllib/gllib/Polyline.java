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

import de.bsvrz.iav.gllib.gllib.util.UndefiniertException;

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
	 * Tut nichts. Standardkonstruktor ist f&uuml;r Festlegen der
	 * Ganglinienapproximation notwendig.
	 */
	public Polyline() {
		// nix
	}

	/**
	 * Konstruiert eine Approximation durch Polyline f&uuml;r eine Ganglinie.
	 * Die in der Ganglinie festgelegte Approximation wird nicht ver&auml;ndert.
	 * 
	 * @param ganglinie
	 *            Eine Ganglinie
	 */
	public Polyline(Ganglinie ganglinie) {
		setGanglinie(ganglinie);
	}

	/**
	 * {@inheritDoc}
	 */
	public Stuetzstelle get(long zeitstempel) throws UndefiniertException {
		if (!ganglinie.isValid(zeitstempel)) {
			throw new UndefiniertException("Die Ganglinie ist zum Zeitpunkt "
					+ zeitstempel + " undefiniert.");
		}

		if (ganglinie.existsStuetzstelle(zeitstempel)) {
			return ganglinie.getStuetzstelle(zeitstempel);
		}

		Stuetzstelle s0;
		Stuetzstelle s1;
		Double wert;
		double x0, x1, y0, y1;

		s0 = ganglinie.naechsteStuetzstelleDavor(zeitstempel);
		s1 = ganglinie.naechsteStuetzstelleDanach(zeitstempel);
		x0 = s0.zeitstempel;
		y0 = s0.wert;
		x1 = s1.zeitstempel;
		y1 = s1.wert;
		wert = y0 + (y1 - y0) / (x1 - x0) * (zeitstempel - x0);
		return new Stuetzstelle(zeitstempel, wert.intValue());
	}

}
