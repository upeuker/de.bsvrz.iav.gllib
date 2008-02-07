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
 * Wei�enfelser Stra�e 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

import de.bsvrz.sys.funclib.bitctrl.util.Intervall;

/**
 * Approximation einer Ganglinie mit Hilfe von Polylines. Der Wert der
 * St&uuml;tzstelle zu einem Zeitstempel wird nach folgender Formel berechnet:
 * <p>
 * <img src="doc-files/formel_polyline.png">
 * 
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class Polyline extends AbstractApproximation {

	/**
	 * {@inheritDoc}
	 */
	public Stuetzstelle<Double> get(long zeitstempel) {
		Stuetzstelle<Double> s0, s1;
		Double wert;
		double x0, x1, y0, y1;
		int index;

		if (getStuetzstellen().size() == 0
				|| (getStuetzstellen().get(0).getZeitstempel() < zeitstempel && zeitstempel > getStuetzstellen()
						.get(getStuetzstellen().size() - 1).getZeitstempel())) {
			// Zeitstempel liegt au�erhalb der Ganglinie
			return new Stuetzstelle<Double>(zeitstempel, null);
		}

		index = -1;
		for (int i = 0; i < getStuetzstellen().size(); i++) {
			if (getStuetzstellen().get(i).getZeitstempel() == zeitstempel) {
				return getStuetzstellen().get(i);
			} else if (getStuetzstellen().get(i).getZeitstempel() > zeitstempel) {
				index = i - 1;
				break;
			}
		}

		if (index == -1) {
			// Zeitstempel liegt au�erhalb der Ganglinie
			return new Stuetzstelle<Double>(zeitstempel, null);
		}

		s0 = getStuetzstellen().get(index);
		s1 = getStuetzstellen().get(index + 1);
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
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.gllib.Approximation#integral(de.bsvrz.sys.funclib.bitctrl.util.Intervall)
	 */
	public double integral(Intervall intervall) {
		final int start;
		final int ende;
		double flaeche = 0;

		start = findeStuetzstelleVor(intervall.getStart());
		ende = findeStuetzstelleNach(intervall.getEnde());

		for (int i = start; i < ende; ++i) {
			long breite;
			int iRechteck, iDreieck;

			breite = getStuetzstellen().get(i + 1).getZeitstempel()
					- getStuetzstellen().get(i).getZeitstempel();
			if (getStuetzstellen().get(i).getWert() < getStuetzstellen().get(
					i + 1).getWert()) {
				iRechteck = i;
				iDreieck = i + 1;
			} else {
				iRechteck = i + 1;
				iDreieck = i;
			}

			flaeche += getStuetzstellen().get(iRechteck).getWert() * breite;
			flaeche += (getStuetzstellen().get(iDreieck).getWert() - getStuetzstellen()
					.get(iRechteck).getWert())
					* breite / 2;
		}

		if (getStuetzstellen().get(start).getZeitstempel() < intervall
				.getStart()) {
			// Erste St�tzstelle liegt vor Intervall
			long breite;
			double hDreieck, hRechteck;

			hRechteck = getStuetzstellen().get(start).getWert();
			hDreieck = get(intervall.getStart()).getWert();
			if (hRechteck > hDreieck) {
				// Das Rechteck muss die kleinere H�he haben.
				double tmp;

				tmp = hRechteck;
				hRechteck = hDreieck;
				hDreieck = tmp;
			}

			breite = intervall.getStart()
					- getStuetzstellen().get(start).getZeitstempel();
			flaeche -= hRechteck * breite;
			flaeche -= (hDreieck - hRechteck) * breite / 2;
		}

		if (getStuetzstellen().get(ende).getZeitstempel() > intervall.getEnde()) {
			// Letzte St�tzstelle liegt vor Intervall
			long breite;
			double hDreieck, hRechteck;

			hRechteck = getStuetzstellen().get(ende).getWert();
			hDreieck = get(intervall.getEnde()).getWert();
			if (hRechteck > hDreieck) {
				// Das Rechteck muss die kleinere H�he haben.
				double tmp;

				tmp = hRechteck;
				hRechteck = hDreieck;
				hDreieck = tmp;
			}

			breite = getStuetzstellen().get(ende).getZeitstempel()
					- intervall.getEnde();
			flaeche -= hRechteck * breite;
			flaeche -= (hDreieck - hRechteck) * breite / 2;
		}

		return flaeche;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Polylinie";
	}

}
