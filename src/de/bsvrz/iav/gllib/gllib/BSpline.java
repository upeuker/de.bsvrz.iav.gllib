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
 * Weißenfelser Straße 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

import java.util.List;
import java.util.ListIterator;

import de.bsvrz.iav.gllib.gllib.events.GanglinienEvent;
import de.bsvrz.iav.gllib.gllib.events.GanglinienListener;
import de.bsvrz.iav.gllib.gllib.util.UndefiniertException;

/**
 * Approximation einer Ganglinie mit Hilfe eines B-Splines beliebiger Ordung.
 * 
 * @author BitCtrl, Schumann
 * @version $Id$
 */
public class BSpline extends AbstractApproximation implements
		GanglinienListener {

	/** Enth&auml;lt nur die definierten St&uuml;tzstellen der Ganglinie. */
	private Stuetzstelle[] stuetzstellen;

	/** Die Ordnung des B-Splines. */
	private short ordnung = 5;

	/** Grenzstellen der Interpolationsintervalle, aufsteigend sortiert. */
	private int[] t;

	/**
	 * Tut nichts. Standardkonstruktor ist f&uuml;r Festlegen der
	 * Ganglinienapproximation notwendig.
	 */
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
	public BSpline(Ganglinie ganglinie, short ordnung) {
		set(ganglinie, ordnung);
	}

	/**
	 * Erstellt einen B-Spline der Ordung 5.
	 * 
	 * @param ganglinie
	 *            Die Ganglinie, die approximiert werden soll
	 */
	public BSpline(Ganglinie ganglinie) {
		setGanglinie(ganglinie);
	}

	/**
	 * Gibt die Ordgung des B-Splines zur&uuml;ck.
	 * 
	 * @return Ordnung
	 */
	public short getOrdnung() {
		return ordnung;
	}

	/**
	 * Legt die Ordnung des B-Splines fest.
	 * 
	 * @param ordnung
	 *            Ordnung
	 */
	public void setOrdnung(short ordnung) {
		set(ganglinie, ordnung);
	}

	/**
	 * {@inheritDoc}
	 */
	public void ganglinieAktualisiert(GanglinienEvent e) {
		if (e.getSource() == ganglinie) {
			setGanglinie((Ganglinie) e.getSource());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Stuetzstelle get(long zeitstempel) throws UndefiniertException {
		if (!ganglinie.isValid(zeitstempel)) {
			throw new UndefiniertException("Die Ganglinie ist zum Zeitpunkt "
					+ zeitstempel + " undefiniert.");
		}

		double t0, f;
		Stuetzstelle s;

		// Sonderfall
		if (ordnung == 1) {
			s = bspline(zeitstempelNachT(zeitstempel));
			return new Stuetzstelle(zeitstempel, s.wert);
		}

		// Da B-Spline nicht den gesuchten Wert liefert, muss sich ihm genähert
		// werden
		t0 = zeitstempelNachT(zeitstempel);
		s = bspline(t0);
		f = zeitstempelNachT(zeitstempel) - zeitstempelNachT(s.zeitstempel);
		while (s.zeitstempel != zeitstempel) {
			if (s.zeitstempel > zeitstempel) {
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
	 * {@inheritDoc}
	 */
	@Override
	public void setGanglinie(Ganglinie ganglinie) {
		set(ganglinie, ordnung);
	}

	/**
	 * Ruft den Setter {@link Approximation#setGanglinie(Ganglinie)} der
	 * Superklasse auf und aktuallsiert anschlie&szlig;end die
	 * B-Spline-Paraemter. Es wird ebenfalls der Ganglinie diese Approximation
	 * als GanglinienListener hinzugef&uuml;gt.
	 * 
	 * @param ganglinie
	 *            Die Ganglinie, die approximiert werden soll
	 * @param k
	 *            Die Ordnung des B-Spline
	 * @throws IllegalArgumentException
	 *             Wenn die Ordung kleiner 1 oder gr&ouml;&szlig;er der Anzahl
	 *             der definierten St&uuml;tzstellen ist.
	 */
	public void set(Ganglinie ganglinie, short k) {
		super.setGanglinie(ganglinie);
		ganglinie.addGanglinienListener(this);

		bestimmeStuetzstellen();

		if (k < 1 || k > stuetzstellen.length) {
			throw new IllegalArgumentException(
					"Die Ordnung muss zwischen 1 und der Anzahl der definierten Stützstellen liegen.");
		}

		ordnung = k;
		bestimmeT();
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
		t0 /= stuetzstellen[stuetzstellen.length - 1].zeitstempel
				- stuetzstellen[0].zeitstempel;
		t0 *= t[t.length - 1];

		return t0;
	}

	/**
	 * Bestimmt die Liste der verwendeten St&uuml;tzstellen. Die Liste
	 * entspricht der Ganglinie, abz&uuml;glich der undefinierten
	 * St&uuml;tzstellen.
	 */
	private void bestimmeStuetzstellen() {
		List<Stuetzstelle> stuetzstellenListe;
		ListIterator<Stuetzstelle> iterator;

		stuetzstellenListe = ganglinie.getStuetzstellen();
		iterator = stuetzstellenListe.listIterator();
		while (iterator.hasNext()) {
			Stuetzstelle s;

			s = iterator.next();
			if (s.wert == null) {
				iterator.remove();
			}
		}
		stuetzstellen = stuetzstellenListe.toArray(new Stuetzstelle[0]);
	}

	/**
	 * Bestimmt die Intervallgrenzen der Interpolation. Es gibt n+k-1 Intervalle
	 * mit n&nbsp;=&nbsp;Knotenanzahl und k&nbsp;=&nbsp;Ordnung des B-Spline.
	 */
	private void bestimmeT() {
		t = new int[stuetzstellen.length + ordnung];
		for (int j = 0; j < t.length; j++) {
			if (j < ordnung) {
				t[j] = 0;
			} else if (ordnung <= j && j <= stuetzstellen.length - 1) {
				t[j] = j - ordnung + 1;
			} else if (j > stuetzstellen.length - 1) {
				t[j] = stuetzstellen.length - 1 - ordnung + 2;
			} else {
				throw new IllegalStateException();
			}
		}
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
	 * Berechnet die Stützstelle zu einer Intervallstelle.
	 * 
	 * @param t0
	 *            Eine Stelle im Intervall des Parameters t
	 * @return Die berechnete Stützstelle
	 */
	private Stuetzstelle bspline(double t0) {
		double bx, by;
		int i;

		// Ränder der Ganglinie werden 1:1 übernommen
		if (t0 <= t[0]) {
			return stuetzstellen[0];
		} else if (t0 >= t[t.length - 1]) {
			return stuetzstellen[stuetzstellen.length - 1];
		}

		bx = by = 0;
		i = (int) t0 + ordnung - 1;
		// for (int j = 0; j < p.length; j++) {
		for (int j = i - ordnung + 1; j <= i; j++) {

			double n;

			n = n(j, ordnung, t0);
			bx += stuetzstellen[j].zeitstempel * n;
			by += stuetzstellen[j].wert * n;

		}

		return new Stuetzstelle(Math.round(bx), (int) Math.round(by));
	}
}
