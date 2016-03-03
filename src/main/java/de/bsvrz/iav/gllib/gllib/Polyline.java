/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.5 Funktionen Ganglinie
 * Copyright (C) 2011-2015 BitCtrl Systems GmbH
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

import com.bitctrl.util.Interval;

/**
 * Approximation einer Ganglinie mit Hilfe von Polylines. Der Wert der
 * Stützstelle zu einem Zeitstempel wird nach folgender Formel berechnet:
 * <p>
 * <img src="doc-files/formel_polyline.png" alt="Formel für Polylinie">
 *
 *
 * @author BitCtrl Systems GmbH, Falko Schumann
 */
public class Polyline extends AbstractApproximation<Double> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Stuetzstelle<Double> get(final long zeitstempel) {
		Stuetzstelle<Double> s0, s1;
		Double wert;
		double x0, x1, y0, y1;
		int index;

		if ((getStuetzstellen().size() == 0)
				|| ((getStuetzstellen().get(0).getZeitstempel() < zeitstempel)
						&& (zeitstempel > getStuetzstellen()
								.get(getStuetzstellen().size() - 1)
								.getZeitstempel()))) {
			// Zeitstempel liegt außerhalb der Ganglinie
			return new Stuetzstelle<Double>(zeitstempel, null);
		}

		index = -1;
		for (int i = 0; i < getStuetzstellen().size(); i++) {
			if (getStuetzstellen().get(i).getZeitstempel() == zeitstempel) {
				return getStuetzstellen().get(i);
			} else
				if (getStuetzstellen().get(i).getZeitstempel() > zeitstempel) {
				index = i - 1;
				break;
			}
		}

		if (index == -1) {
			// Zeitstempel liegt außerhalb der Ganglinie
			return new Stuetzstelle<Double>(zeitstempel, null);
		}

		s0 = getStuetzstellen().get(index);
		s1 = getStuetzstellen().get(index + 1);
		x0 = s0.getZeitstempel();
		y0 = s0.getWert().doubleValue();
		x1 = s1.getZeitstempel();
		y1 = s1.getWert().doubleValue();
		wert = y0 + (((y1 - y0) / (x1 - x0)) * (zeitstempel - x0));
		return new Stuetzstelle<Double>(zeitstempel, wert);
	}

	/**
	 * Hier gibt es nichts zu tun.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	public void initialisiere() {
		// nichts
	}

	/**
	 * Die festgelegte Breite der Teilintervalle wird ignoriert, da sich das
	 * Integral der Polylinie exakt bestimmen lässt.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public double integral(final Interval intervall) {
		final int start;
		final int ende;
		double flaeche = 0;

		start = findeStuetzstelleVor(intervall.getStart());
		ende = findeStuetzstelleNach(intervall.getEnd());

		for (int i = start; i < ende; ++i) {
			long breite;
			int iRechteck, iDreieck;

			breite = getStuetzstellen().get(i + 1).getZeitstempel()
					- getStuetzstellen().get(i).getZeitstempel();
			if (getStuetzstellen().get(i).getWert() < getStuetzstellen()
					.get(i + 1).getWert()) {
				iRechteck = i;
				iDreieck = i + 1;
			} else {
				iRechteck = i + 1;
				iDreieck = i;
			}

			flaeche += getStuetzstellen().get(iRechteck).getWert() * breite;
			flaeche += ((getStuetzstellen().get(iDreieck).getWert()
					- getStuetzstellen().get(iRechteck).getWert()) * breite)
					/ 2;
		}

		if (getStuetzstellen().get(start).getZeitstempel() < intervall
				.getStart()) {
			// Erste Stützstelle liegt vor Intervall
			long breite;
			double hDreieck, hRechteck;

			hRechteck = getStuetzstellen().get(start).getWert();
			hDreieck = get(intervall.getStart()).getWert();
			if (hRechteck > hDreieck) {
				// Das Rechteck muss die kleinere Höhe haben.
				double tmp;

				tmp = hRechteck;
				hRechteck = hDreieck;
				hDreieck = tmp;
			}

			breite = intervall.getStart()
					- getStuetzstellen().get(start).getZeitstempel();
			flaeche -= hRechteck * breite;
			flaeche -= ((hDreieck - hRechteck) * breite) / 2;
		}

		if (getStuetzstellen().get(ende).getZeitstempel() > intervall
				.getEnd()) {
			// Letzte Stützstelle liegt vor Intervall
			long breite;
			double hDreieck, hRechteck;

			hRechteck = getStuetzstellen().get(ende).getWert();
			hDreieck = get(intervall.getEnd()).getWert();
			if (hRechteck > hDreieck) {
				// Das Rechteck muss die kleinere Höhe haben.
				double tmp;

				tmp = hRechteck;
				hRechteck = hDreieck;
				hDreieck = tmp;
			}

			breite = getStuetzstellen().get(ende).getZeitstempel()
					- intervall.getEnd();
			flaeche -= hRechteck * breite;
			flaeche -= ((hDreieck - hRechteck) * breite) / 2;
		}

		return flaeche;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Polylinie"; //$NON-NLS-1$
	}

}
