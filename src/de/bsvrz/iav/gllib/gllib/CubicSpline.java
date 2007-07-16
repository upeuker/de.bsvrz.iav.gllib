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

import static de.bsvrz.sys.funclib.bitctrl.math.RationaleZahl.addiere;
import static de.bsvrz.sys.funclib.bitctrl.math.RationaleZahl.dividiere;
import static de.bsvrz.sys.funclib.bitctrl.math.RationaleZahl.multipliziere;
import static de.bsvrz.sys.funclib.bitctrl.math.RationaleZahl.potenz;
import static de.bsvrz.sys.funclib.bitctrl.math.RationaleZahl.subtrahiere;

import java.util.ArrayList;

import de.bsvrz.sys.funclib.bitctrl.math.RationaleZahl;
import de.bsvrz.sys.funclib.bitctrl.math.algebra.Gauss;
import de.bsvrz.sys.funclib.bitctrl.math.algebra.Matrix;
import de.bsvrz.sys.funclib.bitctrl.math.algebra.Vektor;

/**
 * Approximation einer Ganglinie mit Hilfe eines Cubic-Splines.
 * 
 * @author BitCtrl, Schumann
 * @version $Id$
 */
public class CubicSpline extends AbstractApproximation<Integer> {

	/** Der erste Koeffizient des Polynoms. */
	private RationaleZahl[] a;

	/** Der zweite Koeffizient des Polynoms. */
	private RationaleZahl[] b;

	/** Der dritte Koeffizient des Polynoms. */
	private RationaleZahl[] c;

	/** Der vierte Koeffizient des Polynoms. */
	private RationaleZahl[] d;

	/** Die Abst&auml;nde der St&uuml;tzstellen. */
	private RationaleZahl[] h;

	/**
	 * {@inheritDoc}
	 */
	public Stuetzstelle<Integer> get(long zeitstempel) {
		if (zeitstempel < stuetzstellen.get(0).getZeitstempel()
				|| zeitstempel > stuetzstellen.get(stuetzstellen.size() - 1)
						.getZeitstempel()) {
			// Zeitstempel liegt auﬂerhalb der Ganglinie
			return new Stuetzstelle<Integer>(zeitstempel, null);
		}

		// R‰nder der Ganglinie unver‰ndert ausliefern
		if (stuetzstellen.get(0).getZeitstempel() == zeitstempel) {
			return stuetzstellen.get(0);
		} else if (stuetzstellen.get(stuetzstellen.size() - 1).getZeitstempel() == zeitstempel) {
			return stuetzstellen.get(stuetzstellen.size() - 1);
		}

		return berechneStuetzstelle(zeitstempel);
	}

	/**
	 * Berechnet die St&uuml;tzstelle.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel der gesuchten St&uuml;tzstelle
	 * @return Die gesuchte St&uuml;tzstelle
	 */
	private Stuetzstelle<Integer> berechneStuetzstelle(long zeitstempel) {
		RationaleZahl r, x, xi;
		int index;

		index = -1;
		for (int i = 0; i < stuetzstellen.size(); i++) {
			if (stuetzstellen.get(i).getZeitstempel() > zeitstempel) {
				index = i - 1;
				break;
			}
		}
		xi = new RationaleZahl(stuetzstellen.get(index).getZeitstempel());
		x = new RationaleZahl(zeitstempel);

		r = addiere(addiere(addiere(a[index], multipliziere(b[index],
				subtrahiere(x, xi))), multipliziere(c[index], potenz(
				subtrahiere(x, xi), 2))), multipliziere(d[index], potenz(
				subtrahiere(x, xi), 3)));

		return new Stuetzstelle<Integer>(zeitstempel, r.intValue());
	}

	/**
	 * Berechnet die Koeffizienten des Polynoms.
	 */
	public void initialisiere() {
		int n;
		Matrix m;
		Vektor v;

		n = stuetzstellen.size();

		a = new RationaleZahl[n];
		b = new RationaleZahl[n];
		c = new RationaleZahl[n];
		d = new RationaleZahl[n];
		h = new RationaleZahl[n - 1];

		for (int i = 0; i < n; i++) {
			// Erster Koeffizent
			a[i] = new RationaleZahl(stuetzstellen.get(i).getWert());

			// Intervallbreite
			if (i < n - 1) {
				h[i] = RationaleZahl
						.subtrahiere(new RationaleZahl(stuetzstellen.get(i + 1)
								.getZeitstempel()), new RationaleZahl(
								stuetzstellen.get(i).getZeitstempel()));
			}
		}

		// Dritter Koeffizient
		c[0] = c[n - 1] = RationaleZahl.NULL;
		m = new Matrix(n - 2, n - 2);
		v = new Vektor(n - 2);
		for (int i = 1; i < n - 1; i++) {
			RationaleZahl ci, m1, m2, m3;

			ci = multipliziere(subtrahiere(dividiere(
					subtrahiere(a[i + 1], a[i]), h[i]), dividiere(subtrahiere(
					a[i], a[i - 1]), h[i - 1])), 3);
			v.set(i - 1, ci);

			m1 = h[i - 1];
			m2 = multipliziere(addiere(h[i - 1], h[i]), 2);
			m3 = h[i];
			if (i > 1) {
				m.set(i - 1, i - 2, m1);
			}
			m.set(i - 1, i - 1, m2);
			if (i < n - 2) {
				m.set(i - 1, i, m3);
			}
		}
		Vektor c0 = Gauss.loeseLGS(m, v);
		for (int i = 1; i < n - 1; i++) {
			c[i] = c0.get(i - 1);
		}

		// Zweiter und vierter Koeffizient
		for (int i = 1; i < n; i++) {
			d[i - 1] = dividiere(subtrahiere(c[i], c[i - 1]), multipliziere(
					h[i - 1], 3));
			b[i - 1] = subtrahiere(dividiere(subtrahiere(a[i], a[i - 1]),
					(h[i - 1])), multipliziere(dividiere(addiere(multipliziere(
					c[i - 1], 2), c[i]), 3), h[i - 1]));
		}
	}

	/**
	 * Erzeugt eine flache Kopie des Cubic-Splines.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	public Approximation<Integer> clone() {
		CubicSpline klon;

		klon = new CubicSpline();
		klon.stuetzstellen = new ArrayList<Stuetzstelle<Integer>>(stuetzstellen);
		klon.initialisiere();

		return klon;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Cubic-Spline";
	}

}
