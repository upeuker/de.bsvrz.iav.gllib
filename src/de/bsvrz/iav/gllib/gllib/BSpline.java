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

import javax.sound.midi.SysexMessage;

import de.bsvrz.sys.funclib.bitctrl.i18n.Messages;

/**
 * Approximation einer Ganglinie mit Hilfe eines B-Splines beliebiger Ordung.
 * 
 * @author BitCtrl, Schumann
 * @version $Id$
 */
public class BSpline extends AbstractApproximation {

	/** Die Ordnung des B-Splines. */
	private int ordnung;

	/** Grenzstellen der Interpolationsintervalle, aufsteigend sortiert. */
	private int[] t;

	/** Konrollpunkte des B-Spline, aufsteigend sortiert. */
	private Stuetzstelle[] p;

	public BSpline() {
		// nix
	}

	/**
	 * Erstellt einen B-Spline beliebiger Ordung.
	 * 
	 * @param ganglinie
	 *            Die Ganglinie, die approximiert werden soll
	 * @param ordnung
	 *            Die Ordung die der B-Spline besitzen soll
	 */
	public BSpline(Ganglinie ganglinie, int ordnung) {
		this.ganglinie = ganglinie;
		this.ordnung = ordnung;
		bestimmeP();
		bestimmeT();
	}

	/**
	 * Erstellt einen B-Spline der Ordung 5.
	 * 
	 * @param ganglinie
	 *            Die Ganglinie, die approximiert werden soll
	 */
	public BSpline(Ganglinie ganglinie) {
		this(ganglinie, 5);
	}

	/**
	 * Gibt die Ordgung des B-Splines zur&uuml;ck.
	 * 
	 * @return Ordnung
	 */
	public int getOrdnung() {
		return ordnung;
	}

	/**
	 * Legt die Ordnung des B-Splines fest.
	 * 
	 * @param ordnung
	 *            Ordnung
	 */
	public void setOrdnung(int ordnung) {
		if (ordnung < 1) {
			throw new IllegalArgumentException(Messages.get(
					GlLibMessages.BadBSplineDegree, ordnung));
		}

		this.ordnung = ordnung;
		bestimmeT();
	}

	/**
	 * {@inheritDoc}
	 */
	public Stuetzstelle get(long zeitstempel) {
		if (!ganglinie.isValid(zeitstempel)) {
			// Zeitstempel gehˆrt nicht zur Ganglinie
			return null;
		}

		int i;
		double t0, b;

		if (zeitstempel == p[0].zeitstempel) {
			return p[0];
		} else if (zeitstempel == p[p.length - 1].zeitstempel) {
			return p[p.length - 1];
		}

		b = 0.0;
		t0 = zeitstempel;
		t0 /= ganglinie.getIntervall().breite;
		t0 *= t[t.length - 1];
		// i = (int) t0 + ordnung - 1;
		// for (int j = i - ordnung + 1; j <= i; j++) {
		for (int j = 0; j < p.length; j++) {
			double y, n;

			y = p[j].wert;
			n = n(j, ordnung, t0);
			System.err.println("j=" + j + ", px=" + p[j].zeitstempel
					+ ", Gewicht=" + n);
			b += y * n;
		}

		// for (int j = 0; j < p.length; j++) {
		// double y, n;
		//
		// y = p[j].wert;
		// n = n(j, ordnung, t0);
		// System.err.println("px=" + p[j].zeitstempel + ", Gewicht=" + n);
		// b += y * n;
		// }

		System.err.println("Zeitstempel=" + zeitstempel + ", t0=" + t0
				+ ", Wert=" + b);

		return new Stuetzstelle(zeitstempel, Double.valueOf(b).intValue());
	}

	@Override
	public void setGanglinie(Ganglinie ganglinie) {
		super.setGanglinie(ganglinie);
		bestimmeP();
		bestimmeT();
	}

	/**
	 * Kontrollpunkte sind die St&uuml;tzstellen der Ganglinie.
	 */
	private void bestimmeP() {
		p = ganglinie.getStuetzstellen().toArray(new Stuetzstelle[0]);
	}

	/**
	 * Bestimmt die Intervallgrenzen der Interpolation. Es gibt n+k Intervalle
	 * mit n&nbsp;=&nbsp;Knotenanzahl und k&nbsp;=&nbsp;Ordnung des B-Spline.
	 */
	private void bestimmeT() {
		t = new int[p.length + ordnung];
		for (int j = 0; j < t.length; j++) {
			if (j < ordnung) {
				t[j] = 0;
			} else if (ordnung <= j && j <= p.length - 1) {
				t[j] = j - ordnung + 1;
			} else if (j > p.length - 1) {
				t[j] = p.length - 1 - ordnung + 2;
			} else {
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * Berechnet rekursiv das Gewicht einer St&uuml;tzstelle.
	 * 
	 * @param i
	 *            Index des betrachteten Interpolationsintervalls
	 * @param m
	 *            Ordnung und Invariante der Rekursion
	 * @param t0
	 * 
	 * @return Das Gewicht der i-ten St&uuml;tzstelle
	 */
	private double n(int i, int m, double t0) {
		double n; // Gewicht
		double a, b; // Erster und zweiter Summand
		int an, bn; // Nenner des ersten und zweiten Summanden

		if (m == 1) {
			if (t[i] <= t0 && t0 <= t[i + 1]) {
				n = 1.0;
			} else {
				n = 0.0;
			}

			// Sonderfall
			// if (t0 == t[t.length - 1] && i == p.length - 1) {
			// n = 1.0;
			// }
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

		System.err.println("i=" + i + ", m=" + m + ", t0=" + t0 + " => n=" + n);
		return n;
	}

	public static void main(String[] argv) {
		Ganglinie g;
		BSpline spline;
		int k;

		g = new Ganglinie();
		g.set(new Stuetzstelle(0, 0));
		g.set(new Stuetzstelle(30, 30));
		g.set(new Stuetzstelle(40, 20));
		g.set(new Stuetzstelle(60, 40));
		g.set(new Stuetzstelle(90, 10));

		k = 2;
		spline = new BSpline(g, k);

		for (double t = 0; t <= 4; t += 1) {
			for (int i = 0; i < spline.p.length; i++) {
				System.out.println("t=" + t + ", P" + i + " mit N="
						+ spline.n(i, k, t));
			}
			System.out.println();
		}

		for (int i = 0; i < spline.p.length; i++) {
			System.out.println(spline.get(spline.p[i].zeitstempel));
		}

		System.exit(0);
	}
}
