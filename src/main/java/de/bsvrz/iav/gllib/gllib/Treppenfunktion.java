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
 * Approximation durch eine Treppenfunktion. Von jeder Stützstelle geht eine
 * "Stufe" nach rechts, d.&nbsp;h. bis zur nächsten Stützstelle ändert sich der
 * Wert nicht.
 *
 * @author BitCtrl Systems GmbH, Falko Schumann
 */
public class Treppenfunktion extends AbstractApproximation<Double> {

	/**
	 * {@inheritDoc}
	 *
	 * @see de.bsvrz.iav.gllib.gllib.Approximation#get(long)
	 */
	@Override
	public Stuetzstelle<Double> get(final long zeitstempel) {
		Double d = null;

		if ((getStuetzstellen().size() == 0)
				|| ((getStuetzstellen().get(0).getZeitstempel() < zeitstempel)
						&& (zeitstempel > getStuetzstellen()
								.get(getStuetzstellen().size() - 1)
								.getZeitstempel()))) {
			// Zeitstempel liegt außerhalb der Ganglinie
			return new Stuetzstelle<Double>(zeitstempel, null);
		}

		if (getStuetzstellen().get(getStuetzstellen().size() - 1)
				.getZeitstempel() == zeitstempel) {
			// Sonderfall letzte Stützstelle
			return getStuetzstellen().get(getStuetzstellen().size() - 1);
		}

		for (int i = 0; i < getStuetzstellen().size(); ++i) {
			if (getStuetzstellen().get(i).getZeitstempel() > zeitstempel) {
				d = getStuetzstellen().get(i - 1).getWert();
				break;
			}
		}

		return new Stuetzstelle<Double>(zeitstempel, d);
	}

	/**
	 * Hier gibt es nichts zu tun.
	 *
	 * {@inheritDoc}
	 *
	 * @see de.bsvrz.iav.gllib.gllib.Approximation#initialisiere()
	 */
	@Override
	public void initialisiere() {
		// nix
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see de.bsvrz.iav.gllib.gllib.Approximation#integral(com.bitctrl.util.Interval)
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

			breite = getStuetzstellen().get(i + 1).getZeitstempel()
					- getStuetzstellen().get(i).getZeitstempel();
			flaeche += getStuetzstellen().get(i).getWert() * breite;
		}

		if (getStuetzstellen().get(start).getZeitstempel() < intervall
				.getStart()) {
			// Erste Stützstelle liegt vor Intervall
			long breite;

			breite = intervall.getStart()
					- getStuetzstellen().get(start).getZeitstempel();
			flaeche -= getStuetzstellen().get(start).getWert() * breite;
		}

		if (getStuetzstellen().get(ende).getZeitstempel() > intervall
				.getEnd()) {
			// Letzte Stützstelle liegt vor Intervall
			long breite;

			breite = getStuetzstellen().get(ende).getZeitstempel()
					- intervall.getEnd();
			flaeche -= getStuetzstellen().get(ende).getWert() * breite;
		}

		return flaeche;
	}
}
