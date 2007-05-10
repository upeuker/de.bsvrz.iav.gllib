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

import static de.bsvrz.iav.gllib.gllib.math.RationaleZahl.*;

import de.bsvrz.iav.gllib.gllib.math.RationaleZahl;
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
	private long[] t;

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
		setGanglinie(ganglinie);
		setOrdnung(ordnung);
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
	 * Erstellt einen B-Spline beliebiger Ordnung.
	 * 
	 * @see AbstractApproximation
	 * @param ordnung
	 *            Ordnung
	 */
	public BSpline(int ordnung) {
		setOrdnung(ordnung);
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
	}

	public long[] getInterpolationsintervalle() {
		long[] intervall;

		intervall = new long[t.length];
		System.arraycopy(t, 0, intervall, 0, t.length);

		return intervall;
	}

	/**
	 * {@inheritDoc}
	 */
	public Stuetzstelle get(long zeitstempel) {
		if (!ganglinie.isValid(zeitstempel)) {
			// Zeitstempel gehˆrt nicht zur Ganglinie
			return null;
		}

		RationaleZahl wert = RationaleZahl.NULL;

		for (int i = 0; i < ganglinie.anzahlStuetzstellen(); i++) {
			wert = addiere(wert, multipliziere(
					gewicht(i, ordnung, zeitstempel), p[i].wert));
		}

		return new Stuetzstelle(zeitstempel, wert.intValue());
	}

	@Override
	public void setGanglinie(Ganglinie ganglinie) {
		super.setGanglinie(ganglinie);
		bestimmeKontrollpunkte();
		bestimmeIntervalle();
	}

	/**
	 * Kontrollpunkte sind die St&uuml;tzstellen der Ganglinie.
	 */
	private void bestimmeKontrollpunkte() {
		p = ganglinie.getStuetzstellen().toArray(new Stuetzstelle[0]);
	}

	/**
	 * Bestimmt die Intervallgrenzen der Interpolation. Es gibt n+k Intervalle
	 * mit n&nbsp;=&nbsp;Knotenanzahl und k&nbsp;=&nbsp;Ordnung des B-Spline.
	 */
	private void bestimmeIntervalle() {
		int n, k;
		long intervall;

		n = ganglinie.anzahlStuetzstellen();
		k = ordnung;
		t = new long[n + k + 1];
		intervall = p[n - 1].zeitstempel - p[0].zeitstempel;

		for (int j = 0; j < t.length; j++) {
			t[j] = addiere(multipliziere(dividiere(intervall, n + k), j),
					p[0].zeitstempel).longValue();
		}
	}

	/**
	 * Berechnet rekursiv das Gewicht einer St&uuml;tzstelle.
	 * 
	 * @param j
	 *            Index des betrachteten Interpolationsintervalls
	 * @param k
	 *            Ordnung und Invariante der Rekursion
	 * @param t0
	 *            St&uuml;tzstelle deren Gewicht gesucht ist
	 * @return Das Gewicht der St&uuml;tzstelle
	 */
	RationaleZahl gewicht(int j, int k, long t0) {
		RationaleZahl n;

		if (k == 1) {
			if (t[j] <= t0 && t0 < t[j + 1]) {
				n = RationaleZahl.EINS;
			} else {
				n = RationaleZahl.NULL;
			}
		} else {
			// TODO: Gewicht f¸r Ordnung > 1
			n = null;
		}

		return n;
	}

}
