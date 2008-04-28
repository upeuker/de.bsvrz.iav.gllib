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

import java.util.ArrayList;
import java.util.List;

import com.bitctrl.Constants;
import com.bitctrl.util.Interval;

/**
 * Approximation einer Ganglinie mit Hilfe eines B-Splines beliebiger Ordung.
 * Der B-Spline legt eine geglättete Kurve zwischen der ersten und letzten
 * Stützstelle. Da hierbei sowohl der Stützstellenwert als auch der Zeitstempel
 * der Stützstellen gewichtet wird, wird die Kurve mit einer Polylinie
 * interpoliert. Das Intervall der Interpolation kann frei gewält werden,
 * Standard ist eine Minute.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class BSpline extends AbstractApproximation<Double> {

	/**
	 * Die maximale Differenz ({@value}) zwischen Näherungswert und Zielwert.
	 * 
	 * @see #get(long)
	 */
	public static final long DELTA = 1000;

	/**
	 * Wenn die Optimierung eingeschalten ist, wird der B-Spline bei der
	 * Initialisierung im Minutentakt ausgerechnet und als Polylinie gesichert.
	 */
	public static final boolean OPTIMIERUNG = true;

	/** Die Ordnung des B-Splines. */
	private int ordnung;

	/** Grenzstellen der Interpolationsintervalle, aufsteigend sortiert. */
	private int[] t;

	/** Cacht die Interpolation der Approximation. */
	private Polyline polyline;

	/** Das Interpolationsintervall für die Polylinie, die den B-Spline cacht. */
	private long interpolationsintervall;

	/**
	 * Gibt das Interpolationsintervall für die Polylinie, die den B-Spline
	 * cacht, zurück.
	 * 
	 * @return das Interpolationsintervall.
	 * @see #setInterpolationsintervall(long)
	 */
	public long getInterpolationsintervall() {
		return interpolationsintervall;
	}

	/**
	 * 
	 * Legt das Interpolationsintervall für die Polylinie, die den B-Spline
	 * cacht, fest.
	 * <p>
	 * <em>Hinweis:</em> Nach Änderung des Interpolationsintervalls muss
	 * {@link #initialisiere()} aufgerufen werden, um die Änderung zu
	 * übernehmen.
	 * 
	 * @param interpolationsintervall
	 *            das Interpolationsintervall.
	 */
	public void setInterpolationsintervall(final long interpolationsintervall) {
		this.interpolationsintervall = interpolationsintervall;
	}

	/**
	 * Erzeugt einen B-Spline mit der Ordnung 5.
	 */
	public BSpline() {
		this(5);
	}

	/**
	 * Erzeugt einen B-Spline mit beliebiger Ordnung.
	 * 
	 * @param ordnung
	 *            die Ordnung des Bspline.
	 */
	public BSpline(final int ordnung) {
		this.ordnung = ordnung;
		interpolationsintervall = Constants.MILLIS_PER_MINUTE;
	}

	/**
	 * {@inheritDoc}
	 */
	public Stuetzstelle<Double> get(final long zeitstempel) {
		if (OPTIMIERUNG) {
			return polyline.get(zeitstempel);
		}

		double t0, f;
		Stuetzstelle<Double> s;

		if (getStuetzstellen().size() == 0
				|| (zeitstempel < getStuetzstellen().get(0).getZeitstempel() || zeitstempel > getStuetzstellen()
						.get(getStuetzstellen().size() - 1).getZeitstempel())) {
			// Zeitstempel liegt außerhalb der Ganglinie
			return new Stuetzstelle<Double>(zeitstempel, null);
		}

		// Da der B-Spline nicht die Enden enthalten muss, werden die erste und
		// letzte Stützstelle einfach ausgeliefert.
		if (getStuetzstellen().get(0).getZeitstempel() == zeitstempel) {
			return getStuetzstellen().get(0);
		} else if (getStuetzstellen().get(getStuetzstellen().size() - 1)
				.getZeitstempel() == zeitstempel) {
			return getStuetzstellen().get(getStuetzstellen().size() - 1);
		}

		// Sonderfall
		if (ordnung == 1) {
			s = bspline(zeitstempelNachT(zeitstempel));
			return new Stuetzstelle<Double>(zeitstempel, s.getWert());
		}

		// Da B-Spline nicht den gesuchten Wert liefert, muss er erraten werden
		t0 = zeitstempelNachT(zeitstempel);
		s = bspline(t0);
		f = zeitstempelNachT(zeitstempel)
				- zeitstempelNachT(s.getZeitstempel()); // Schrittweite
		while (Math.abs(zeitstempel - s.getZeitstempel()) > DELTA) {
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
	 * Gibt die Ordnung des B-Splines zurück.
	 * 
	 * @return Ordnung
	 */
	public int getOrdnung() {
		return ordnung;
	}

	/**
	 * Bestimmt die Intervallgrenzen der Interpolation. Es gibt n+k-1 Intervalle
	 * mit n&nbsp;=&nbsp;Knotenanzahl und k&nbsp;=&nbsp;Ordnung des B-Spline.
	 * Ist die Ordnung des B-Spline größer als die Anzahl der Stützstellen, dann
	 * wird die Ordnung auf die Stützstellenanzahl reduziert.
	 * 
	 * {@inheritDoc}
	 */
	public void initialisiere() {
		if (getStuetzstellen().size() == 0) {
			return;
		}

		if (getStuetzstellen().size() < getOrdnung()) {
			// Ordnung größer als Anzahl der Stützstellen, Ordung anpassen
			setOrdnung((byte) getStuetzstellen().size());
		}

		t = new int[getStuetzstellen().size() + ordnung];
		for (int j = 0; j < t.length; j++) {
			if (j < ordnung) {
				t[j] = 0;
			} else if (ordnung <= j && j <= getStuetzstellen().size() - 1) {
				t[j] = j - ordnung + 1;
			} else if (j > getStuetzstellen().size() - 1) {
				t[j] = getStuetzstellen().size() - 1 - ordnung + 2;
			} else {
				throw new IllegalStateException();
			}
		}

		final List<Stuetzstelle<Double>> liste;
		final long intervall, aufloesung;

		intervall = getStuetzstellen().get(getStuetzstellen().size() - 1)
				.getZeitstempel()
				- getStuetzstellen().get(0).getZeitstempel();
		aufloesung = intervall / getInterpolationsintervall();
		polyline = new Polyline();
		liste = new ArrayList<Stuetzstelle<Double>>();
		for (long i = 0; i <= aufloesung; i++) {
			Stuetzstelle<Double> s;
			double t1;

			t1 = i;
			t1 *= t[t.length - 1];
			t1 /= aufloesung;

			if (Double.isNaN(t1) || Double.isInfinite(t1)) {
				continue;
			}
			s = bspline(t1);
			liste.add(s);
		}
		polyline.setStuetzstellen(liste);
	}

	/**
	 * Verwendet eine Polyline-Approximation des Splines zur näherungsweisen
	 * Bestimmung des Integrals.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see #setIntegrationsintervall(long)
	 */
	public double integral(final Interval intervall) {
		return polyline.integral(intervall);
	}

	/**
	 * Legt die Ordnung des B-Splines fest.
	 * 
	 * @param ordnung
	 *            Ordnung
	 */
	public void setOrdnung(final byte ordnung) {
		if (ordnung < 1 || ordnung > getStuetzstellen().size()) {
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
	private Stuetzstelle<Double> bspline(final double t0) {
		double bx, by;
		final int i;

		// Ränder der Ganglinie werden 1:1 übernommen
		if (t0 <= t[0]) {
			return getStuetzstellen().get(0);
		} else if (t0 >= t[t.length - 1]) {
			return getStuetzstellen().get(getStuetzstellen().size() - 1);
		}

		bx = by = 0;
		i = (int) t0 + ordnung - 1;
		// for (int j = 0; j < p.length; j++) {
		for (int j = i - ordnung + 1; j <= i; j++) {
			final double n;

			n = n(j, ordnung, t0);
			bx += getStuetzstellen().get(j).getZeitstempel() * n;
			by += getStuetzstellen().get(j).getWert().doubleValue() * n;
		}

		return new Stuetzstelle<Double>(Math.round(bx), by);
	}

	/**
	 * Berechnet rekursiv das Gewicht einer Stützstelle.
	 * 
	 * @param i
	 *            Index der Stützstelle, dessen Gewicht gesucht ist
	 * @param m
	 *            Ordnung des B-Spline und gleichzeitig Invariante der Rekursion
	 * @param t0
	 *            Wert im Intervall des Parameters t
	 * @return Das Gewicht der i-ten Stützstelle
	 */
	private double n(final int i, final int m, final double t0) {
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
	private double zeitstempelNachT(final long zeitstempel) {
		double t0;

		t0 = zeitstempel;
		t0 /= getStuetzstellen().get(getStuetzstellen().size() - 1)
				.getZeitstempel()
				- getStuetzstellen().get(0).getZeitstempel();
		t0 *= t[t.length - 1];

		return t0;
	}

}
