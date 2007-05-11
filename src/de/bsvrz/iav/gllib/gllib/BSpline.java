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

import static de.bsvrz.iav.gllib.gllib.math.RationaleZahl.EINS;
import static de.bsvrz.iav.gllib.gllib.math.RationaleZahl.NULL;
import static de.bsvrz.iav.gllib.gllib.math.RationaleZahl.addiere;
import static de.bsvrz.iav.gllib.gllib.math.RationaleZahl.dividiere;
import static de.bsvrz.iav.gllib.gllib.math.RationaleZahl.multipliziere;
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
		bestimmeKontrollpunkte();
		bestimmeIntervalle();
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
		bestimmeIntervalle();
	}

	/**
	 * {@inheritDoc}
	 */
	public Stuetzstelle get(long zeitstempel) {
		if (!ganglinie.isValid(zeitstempel)) {
			// Zeitstempel gehˆrt nicht zur Ganglinie
			return null;
		}

		return null;
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
		// TODO
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
	RationaleZahl gewicht(int j, int k, int t0) {
		RationaleZahl a, b, n, ga, gb;
		try {
			if (k == 1) {
				if (t[j] <= t0 && t0 <= t[j + 1]) {
					n = EINS;
				} else {
					n = NULL;
				}
			} else {
				// Die beiden Quotienten
				a = dividiere(t0 - t[j], t[j + k - 1] - t[j]);
				b = dividiere(t[j + k] - t0, t[j + k] - t[j + 1]);

				// Gewicht aus vorheriger Rekursion einbeziehen
				ga = gewicht(j, k - 1, t0);
				a = multipliziere(a, ga);
				gb = gewicht(j + 1, k - 1, t0);
				b = multipliziere(b, gb);

				// Die beiden Quotienten addieren
				n = addiere(a, b);
			}
		} catch (ArithmeticException e) {
			n = EINS;
		}
		return n;
	}

}
