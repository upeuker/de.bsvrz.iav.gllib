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

import java.util.ArrayList;

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
public class Polyline extends AbstractApproximation<Double> {

	/**
	 * {@inheritDoc}
	 */
	public Stuetzstelle<Double> get(long zeitstempel) {
		Stuetzstelle<Double> s0, s1;
		Double wert;
		double x0, x1, y0, y1;
		int index;

		index = -1;
		for (int i = 0; i < stuetzstellen.size(); i++) {
			if (stuetzstellen.get(i).getZeitstempel() == zeitstempel) {
				return stuetzstellen.get(i);
			} else if (stuetzstellen.get(i).getZeitstempel() > zeitstempel) {
				index = i - 1;
				break;
			}
		}

		if (index == -1) {
			// Zeitstempel liegt auﬂerhalb der Ganglinie
			return new Stuetzstelle<Double>(zeitstempel, null);
		}

		s0 = stuetzstellen.get(index);
		s1 = stuetzstellen.get(index + 1);
		x0 = s0.getZeitstempel();
		y0 = s0.getWert().doubleValue();
		x1 = s1.getZeitstempel();
		y1 = s1.getWert().doubleValue();
		wert = y0 + (y1 - y0) / (x1 - x0) * (zeitstempel - x0);
		return new Stuetzstelle<Double>(zeitstempel, wert);
	}

	/**
	 * Hier gibt es nichts zu tun.
	 * <p>
	 * {@inheritDoc}
	 */
	public void initialisiere() {
		// nichts
	}

	/**
	 * Erzeugt eine flache Kopie der Polylinie.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	public Approximation<Double> clone() {
		Polyline klon;

		klon = new Polyline();
		klon.stuetzstellen = new ArrayList<Stuetzstelle<Double>>(stuetzstellen);
		klon.initialisiere();

		return klon;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Polylinie";
	}

}
