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
 * Weißenfelser Straße 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

/**
 * Approximation einer Ganglinie mit Hilfe eines B-Splines beliebiger Ordung.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class BSpline extends AbstractApproximation {

	/** Die Ordnung des B-Splines. */
	private byte ordnung;

	/** Grenzstellen der Interpolationsintervalle, aufsteigend sortiert. */
	private int[] t;

	/**
	 * Erzeugt einen B-Spline mit der Ordnung 5.
	 */
	public BSpline() {
		this((byte) 5);
	}

	/**
	 * Erzeugt einen B-Spline mit beliebiger Ordnung.
	 * 
	 * @param ordnung
	 *            die Ordnung des Bspline.
	 */
	public BSpline(byte ordnung) {
		this.ordnung = ordnung;
	}

	/**
	 * {@inheritDoc}
	 */
	public Stuetzstelle<Double> get(long zeitstempel) {
		double t0, f;
		Stuetzstelle<Double> s;

		if (anzahl() == 0
				|| (get(0).getZeitstempel() < zeitstempel && zeitstempel > get(
						anzahl() - 1).getZeitstempel())) {
			// Zeitstempel liegt außerhalb der Ganglinie
			return new Stuetzstelle<Double>(zeitstempel, null);
		}

		// TODO Wird dieses IF-ELSE benötigt?
		if (get(0).getZeitstempel() == zeitstempel) {
			return get(0);
		} else if (get(anzahl() - 1).getZeitstempel() == zeitstempel) {
			return get(anzahl() - 1);
		}

		// Sonderfall
		if (ordnung == 1) {
			s = bspline(zeitstempelNachT(zeitstempel));
			return new Stuetzstelle<Double>(zeitstempel, s.getWert());
		}

		// Da B-Spline nicht den gesuchten Wert liefert, muss sich ihm genähert
		// werden
		t0 = zeitstempelNachT(zeitstempel);
		s = bspline(t0);
		f = zeitstempelNachT(zeitstempel)
				- zeitstempelNachT(s.getZeitstempel());
		while (s.getZeitstempel() != zeitstempel) {
			if (s.getZeitstempel() > zeitstempel) {
				if (f > 0) {
					f /= -2;
				}
			} else {
				if (f < 0) {
					f /= -2;
				}
			}
			t0 += f;
			s = bspline(t0);
		}

		return s;
	}

	/**
	 * Gibt die Ordgung des B-Splines zur&uuml;ck.
	 * 
	 * @return Ordnung
	 */
	public byte getOrdnung() {
		return ordnung;
	}

	/**
	 * Bestimmt die Intervallgrenzen der Interpolation. Es gibt n+k-1 Intervalle
	 * mit n&nbsp;=&nbsp;Knotenanzahl und k&nbsp;=&nbsp;Ordnung des B-Spline.
	 * 
	 * {@inheritDoc}
	 */
	public void initialisiere() {
		t = new int[anzahl() + ordnung];
		for (int j = 0; j < t.length; j++) {
			if (j < ordnung) {
				t[j] = 0;
			} else if (ordnung <= j && j <= anzahl() - 1) {
				t[j] = j - ordnung + 1;
			} else if (j > anzahl() - 1) {
				t[j] = anzahl() - 1 - ordnung + 2;
			} else {
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * Legt die Ordnung des B-Splines fest.
	 * 
	 * @param ordnung
	 *            Ordnung
	 */
	public void setOrdnung(byte ordnung) {
		if (ordnung < 1 || ordnung > anzahl()) {
			throw new IllegalArgumentException(
					"Die Ordnung muss zwischen 1 und der Anzahl der definierten Stützstellen liegen.");
		}

		this.ordnung = ordnung;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "B-Spline mit Ordnung " + ordnung;
	}

	/**
	 * Berechnet die Stützstelle zu einer Intervallstelle.
	 * 
	 * @param t0
	 *            Eine Stelle im Intervall des Parameters t
	 * @return Die berechnete Stützstelle
	 */
	private Stuetzstelle<Double> bspline(double t0) {
		double bx, by;
		int i;

		// Ränder der Ganglinie werden 1:1 übernommen
		if (t0 <= t[0]) {
			return get(0);
		} else if (t0 >= t[t.length - 1]) {
			return get(anzahl() - 1);
		}

		bx = by = 0;
		i = (int) t0 + ordnung - 1;
		// for (int j = 0; j < p.length; j++) {
		for (int j = i - ordnung + 1; j <= i; j++) {

			double n;

			n = n(j, ordnung, t0);
			bx += get(j).getZeitstempel() * n;
			by += get(j).getWert().doubleValue() * n;

		}

		return new Stuetzstelle<Double>(Math.round(bx), by);
	}

	/**
	 * Berechnet rekursiv das Gewicht einer St&uuml;tzstelle.
	 * 
	 * @param i
	 *            Index der St&uuml;tzstelle, dessen Gewicht gesucht ist
	 * @param m
	 *            Ordnung des B-Spline und gleichzeitig Invariante der Rekursion
	 * @param t0
	 *            Wert im Intervall des Parameters t
	 * @return Das Gewicht der i-ten St&uuml;tzstelle
	 */
	private double n(int i, int m, double t0) {
		double n; // Gewicht
		double a, b; // Erster und zweiter Summand
		int an, bn; // Nenner des ersten und zweiten Summanden

		if (m == 1) {
			if (t[i] <= t0 && t0 < t[i + 1]) {
				n = 1.0;
			} else {
				n = 0.0;
			}
		} else {
			an = t[i + m - 1] - t[i];
			bn = t[i + m] - t[i + 1];

			// Erster Summand
			if (an != 0) {
				a = (t0 - t[i]) / an; // Quotient
				a *= n(i, m - 1, t0); // Gewicht einbeziehen
			} else {
				a = 0;
			}

			// Zweiter Summand
			if (bn != 0) {
				b = (t[i + m] - t0) / bn; // Quotient
				b *= n(i + 1, m - 1, t0); // Gewicht einbeziehen
			} else {
				b = 0;
			}

			// Die beiden Summanden addieren
			n = a + b;
		}

		return n;
	}

	/**
	 * Bestimmt zu einem Zeitstempel die Intervallposition.
	 * 
	 * @param zeitstempel
	 *            Ein Zeitstempel
	 * @return Die dazugehörige Intervallposition
	 */
	private double zeitstempelNachT(long zeitstempel) {
		double t0;

		t0 = zeitstempel;
		t0 /= get(anzahl() - 1).getZeitstempel() - get(0).getZeitstempel();
		t0 *= t[t.length - 1];

		return t0;
	}

}
