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
		if (ordnung < 0) {
			throw new IllegalArgumentException(Messages.get(
					GlLibMessages.BadBSplineDegree, ordnung));
		}

		this.ordnung = ordnung;
	}

	/**
	 * {@inheritDoc}
	 */
	public Stuetzstelle get(long zeitstempel) {
		if (!ganglinie.isValid(zeitstempel)) {
			// Zeitstempel gehˆrt nicht zur Ganglinie
			return null;
		}

		RationaleZahl wert;

		wert = RationaleZahl.NULL;
		for (int i = 0; i < ganglinie.anzahlStuetzstellen() - ordnung - 1; i++) {
			wert = addiere(wert, multipliziere(b(i, ordnung, zeitstempel),
					ganglinie.getStuetzstellen().get(i).wert));
		}

		return new Stuetzstelle(zeitstempel, wert.intValue());
	}

	RationaleZahl b(int j, int n, long t0) {
		Stuetzstelle[] t;
		RationaleZahl b;

		t = ganglinie.getStuetzstellen().toArray(new Stuetzstelle[0]);

		if (n == 0) {
			// Der Wert ist 1, wenn zeitstempel zwischen der j-ten und (j+1)-ten
			// St¸tzstelle oder auf der j-ten St¸tzstelle liegt und sonst 0.
			if (t[j].zeitstempel <= t0 && t0 < t[j + 1].zeitstempel) {
				b = RationaleZahl.EINS;
			} else {
				b = RationaleZahl.NULL;
			}
		} else {
			b = addiere(multipliziere(b(j, n - 1, t0),
					dividiere(t0 - t[j].zeitstempel, t[j + n].zeitstempel
							- t[j].zeitstempel)), multipliziere(b(j + 1, n - 1,
					t0), dividiere(t[j + n + 1].zeitstempel - t0,
					t[j + n + 1].zeitstempel - t[j + 1].zeitstempel)));
		}

		// System.err.println("b(" + j + ", " + n + ", " + t0 + ") = " + b);

		return b;
	}
}
