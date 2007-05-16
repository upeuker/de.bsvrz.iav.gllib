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

import java.util.List;
import java.util.ListIterator;

import de.bsvrz.iav.gllib.gllib.events.GanglinienEvent;
import de.bsvrz.iav.gllib.gllib.events.GanglinienListener;
import de.bsvrz.iav.gllib.gllib.util.UndefiniertException;
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
public class CubicSpline extends AbstractApproximation implements
		GanglinienListener {

	/** Enth&auml;lt nur die definierten St&uuml;tzstellen der Ganglinie. */
	private Stuetzstelle[] stuetzstellen;

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
	 * Tut nichts. Standardkonstruktor ist f&uuml;r Festlegen der
	 * Ganglinienapproximation notwendig.
	 */
	public CubicSpline() {
		// nix
	}

	/**
	 * Konstruiert eine Approximation durch einen Cubic-Spline f&uuml;r eine
	 * Ganglinie. Die in der Ganglinie festgelegte Approximation wird nicht
	 * ver&auml;ndert.
	 * 
	 * @param ganglinie
	 *            Eine Ganglinie
	 */
	public CubicSpline(Ganglinie ganglinie) {
		setGanglinie(ganglinie);
		bestimmeKoeffizienten();
	}

	/**
	 * {@inheritDoc}
	 */
	public Stuetzstelle get(long zeitstempel) throws UndefiniertException {
		if (!ganglinie.isValid(zeitstempel)) {
			throw new UndefiniertException("Die Ganglinie ist zum Zeitpunkt "
					+ zeitstempel + " undefiniert.");
		}

		if (ganglinie.getIntervall().start == zeitstempel
				|| ganglinie.getIntervall().ende == zeitstempel) {
			return ganglinie.getStuetzstelle(zeitstempel);
		}

		return berechneStuetzstelle(zeitstempel);
	}

	/**
	 * {@inheritDoc}
	 */
	public void ganglinieAktualisiert(GanglinienEvent e) {
		if (e.getSource() == ganglinie) {
			bestimmeKoeffizienten();
		}
	}

	/**
	 * Ruft den Setter der Superklasse auf und aktuallsiert anschlie&szlig;end
	 * die Koeffizienten. Es wird ebenfalls der Ganglinie diese Approximation
	 * als GanglinienListener hinzugef&uuml;gt.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	public void setGanglinie(Ganglinie ganglinie) {
		super.setGanglinie(ganglinie);
		ganglinie.addGanglinienListener(this);
		bestimmeKoeffizienten();
	}

	/**
	 * Berechnet die St&uuml;tzstelle.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel der gesuchten St&uuml;tzstelle
	 * @return Die gesuchte St&uuml;tzstelle
	 */
	private Stuetzstelle berechneStuetzstelle(long zeitstempel) {
		RationaleZahl r, x, xi;
		int index;

		index = -1;
		for (int i = 0; i < stuetzstellen.length; i++) {
			if (stuetzstellen[i].zeitstempel > zeitstempel) {
				index = i - 1;
				break;
			}
		}
		if (index < 0) {
			throw new IllegalStateException();
		}
		xi = new RationaleZahl(stuetzstellen[index].zeitstempel);
		x = new RationaleZahl(zeitstempel);

		r = addiere(addiere(addiere(a[index], multipliziere(b[index],
				subtrahiere(x, xi))), multipliziere(c[index], potenz(
				subtrahiere(x, xi), 2))), multipliziere(d[index], potenz(
				subtrahiere(x, xi), 3)));

		return new Stuetzstelle(zeitstempel, r.intValue());
	}

	/**
	 * Berechnet die Koeffizienten des Polynoms.
	 */
	private void bestimmeKoeffizienten() {
		int n;
		List<Stuetzstelle> stuetzstellenListe;
		ListIterator<Stuetzstelle> iterator;
		Matrix m;
		Vektor v;

		stuetzstellenListe = ganglinie.getStuetzstellen();
		iterator = stuetzstellenListe.listIterator();
		while (iterator.hasNext()) {
			Stuetzstelle s;

			s = iterator.next();
			if (s.wert == null) {
				iterator.remove();
			}
		}
		n = stuetzstellenListe.size();
		stuetzstellen = new Stuetzstelle[n];
		stuetzstellen = stuetzstellenListe.toArray(new Stuetzstelle[0]);

		a = new RationaleZahl[n];
		b = new RationaleZahl[n];
		c = new RationaleZahl[n];
		d = new RationaleZahl[n];
		h = new RationaleZahl[n - 1];

		for (int i = 0; i < n; i++) {
			// Erster Koeffizent
			a[i] = new RationaleZahl(stuetzstellen[i].wert);

			// Intervallbreite
			if (i < n - 1) {
				h[i] = RationaleZahl.subtrahiere(new RationaleZahl(
						stuetzstellen[i + 1].zeitstempel), new RationaleZahl(
						stuetzstellen[i].zeitstempel));
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
}
