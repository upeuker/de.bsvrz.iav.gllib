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

import java.util.HashMap;
import java.util.Map;

import com.bitctrl.Constants;
import com.bitctrl.util.Interval;
import com.bitctrl.util.Timestamp;

import de.bsvrz.sys.funclib.debug.Debug;

/**
 * Approximation einer Ganglinie mit Hilfe eines B-Splines beliebiger Ordung.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 * @todo Optimierung entfernen oder lassen?
 */
public class BSpline extends AbstractApproximation<Double> {

	/**
	 * Die maximale Differenz ({@value}) zwischen N�herungswert und Zielwert.
	 * 
	 * @see #get(long)
	 */
	public static final long DELTA = 1000;

	/**
	 * Flag, ob die Optimierung der iterativen N�herung ein oder ausgeschalten
	 * ist. Wenn {@code true}, dann wird die Schrittweite nicht nur
	 * verkleinert, sondern auch wieder vergr��ert, wenn sich die Iteration vom
	 * Zielwert entfernt.
	 * 
	 * @see #get(long)
	 */
	public static final boolean OPTIMIERUNG = false;

	/**
	 * Der Durchschnitt der bisher notwendigen Iterationen pro Aufruf von
	 * {@link #get(long)}.
	 */
	private static long iterationen = 0;

	/**
	 * Gibt den Durchschnitt der bisher notwendigen Iterationen pro Aufruf von
	 * {@link #get(long)} zur�ck.
	 * 
	 * @return die durchschnittliche Anzahl von Iterationen.
	 */
	static long getIterationen() {
		return iterationen;
	}

	/** Der Logger der Klasse. */
	private final Debug log = Debug.getLogger();

	/** Die Breite der Teilintervalle beim Integrieren: eine Minute. */
	public static final long INTEGRATIONSINTERVALL = 60 * 1000;

	/** Die Ordnung des B-Splines. */
	private int ordnung;

	/** Grenzstellen der Interpolationsintervalle, aufsteigend sortiert. */
	private int[] t;

	/** Cacht die bereits berechneten Minutenwerte der Approximation. */
	private final Map<Long, Double> cache;

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
		cache = new HashMap<Long, Double>();
	}

	/**
	 * Da der B-Spline wegen der Wichtung der Punkte nicht den St�tzstellenwert
	 * berechnet, der tats�chlich gesucht ist, muss sich ihm mit einem
	 * iterativen Verfahren angen�hert werden.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see #DELTA
	 * @see #OPTIMIERUNG
	 */
	public Stuetzstelle<Double> get(final long zeitstempel) {
		final long firstdelta;
		long i;
		double t0, f;
		Stuetzstelle<Double> s;

		if (getStuetzstellen().size() == 0
				|| (zeitstempel < getStuetzstellen().get(0).getZeitstempel() || zeitstempel > getStuetzstellen()
						.get(getStuetzstellen().size() - 1).getZeitstempel())) {
			// Zeitstempel liegt au�erhalb der Ganglinie
			return new Stuetzstelle<Double>(zeitstempel, null);
		}

		// TODO Wird dieses IF-ELSE ben�tigt?
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

		// Da B-Spline nicht den gesuchten Wert liefert, muss sich ihm gen�hert
		// werden
		t0 = zeitstempelNachT(zeitstempel);
		s = bspline(t0);
		f = zeitstempelNachT(zeitstempel)
				- zeitstempelNachT(s.getZeitstempel());

		firstdelta = zeitstempel - s.getZeitstempel();
		i = 0;
		while (s.getZeitstempel() != zeitstempel) {
			long delta;

			++i;
			delta = Math.abs(zeitstempel - s.getZeitstempel());

			if (delta < DELTA) {
				s = new Stuetzstelle<Double>(zeitstempel, s.getWert());
				break;
			}

			if (s.getZeitstempel() > zeitstempel) {
				if (f > 0) {
					f /= -2;
				}
			} else {
				if (f < 0) {
					f /= -2;
				}
			}
			if (OPTIMIERUNG && Math.abs(f) < zeitstempelNachT(delta) / 10) {
				f *= 2;
			}
			t0 += f;
			s = bspline(t0);

			// Wenn der Zeitstempel auf eine volle Minute f�llt, dann cachen.
			if (s.getZeitstempel() % Constants.MILLIS_PER_MINUTE == 0) {
				cache.put(s.getZeitstempel(), s.getWert().doubleValue());
			}
		}

		iterationen = (iterationen + i) / 2;

		if (firstdelta > 0) {
			log.fine("positiv, delta="
					+ Timestamp.relativeTime(Math.abs(firstdelta))
					+ ", gesucht=" + Timestamp.relativeTime(zeitstempel) + ", "
					+ i + " ben�tigte Schritte");
		} else {
			log.fine("negativ, delta="
					+ Timestamp.relativeTime(Math.abs(firstdelta))
					+ ", gesucht=" + Timestamp.relativeTime(zeitstempel) + ", "
					+ i + " ben�tigte Schritte");
		}

		return s;
	}

	/**
	 * Gibt die Ordnung des B-Splines zur�ck.
	 * 
	 * @return Ordnung
	 */
	public int getOrdnung() {
		return ordnung;
	}

	/**
	 * Bestimmt die Intervallgrenzen der Interpolation. Es gibt n+k-1 Intervalle
	 * mit n&nbsp;=&nbsp;Knotenanzahl und k&nbsp;=&nbsp;Ordnung des B-Spline.
	 * Ist die Ordnung des B-Spline gr��er als die Anzahl der St�tzstellen, dann
	 * wird die Ordnung auf die St�tzstellenanzahl reduziert.
	 * 
	 * {@inheritDoc}
	 */
	public void initialisiere() {
		cache.clear();

		if (getStuetzstellen().size() == 0) {
			return;
		}

		if (getStuetzstellen().size() < getOrdnung()) {
			// Ordnung gr��er als Anzahl der St�tzstellen, Ordung anpassen
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
	}

	/**
	 * Verwendet eine Polyline-Approximation des Splines zur n�herungsweisen
	 * Bestimmung des Integrals.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.gllib.Approximation#integral(com.bitctrl.util.Interval)
	 * @see #INTEGRATIONSINTERVALL
	 */
	public double integral(final Interval intervall) {
		Polyline polyline;

		polyline = new Polyline();
		polyline.setStuetzstellen(interpoliere(INTEGRATIONSINTERVALL));

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
					"Die Ordnung muss zwischen 1 und der Anzahl der definierten St�tzstellen liegen.");
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
	 * Berechnet die St�tzstelle zu einer Intervallstelle.
	 * 
	 * @param t0
	 *            Eine Stelle im Intervall des Parameters t
	 * @return Die berechnete St�tzstelle
	 */
	private Stuetzstelle<Double> bspline(final double t0) {
		double bx, by;
		int i;

		// R�nder der Ganglinie werden 1:1 �bernommen
		if (t0 <= t[0]) {
			return getStuetzstellen().get(0);
		} else if (t0 >= t[t.length - 1]) {
			return getStuetzstellen().get(getStuetzstellen().size() - 1);
		}

		bx = by = 0;
		i = (int) t0 + ordnung - 1;
		// for (int j = 0; j < p.length; j++) {
		for (int j = i - ordnung + 1; j <= i; j++) {

			double n;

			n = n(j, ordnung, t0);
			bx += getStuetzstellen().get(j).getZeitstempel() * n;
			by += getStuetzstellen().get(j).getWert().doubleValue() * n;

		}

		return new Stuetzstelle<Double>(Math.round(bx), by);
	}

	/**
	 * Berechnet rekursiv das Gewicht einer St�tzstelle.
	 * 
	 * @param i
	 *            Index der St�tzstelle, dessen Gewicht gesucht ist
	 * @param m
	 *            Ordnung des B-Spline und gleichzeitig Invariante der Rekursion
	 * @param t0
	 *            Wert im Intervall des Parameters t
	 * @return Das Gewicht der i-ten St�tzstelle
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
	 * @return Die dazugeh�rige Intervallposition
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
