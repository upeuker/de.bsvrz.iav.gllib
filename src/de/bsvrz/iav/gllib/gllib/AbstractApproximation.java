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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bitctrl.util.Interval;

/**
 * Implementiert allgemeine Methoden der Schnittstelle.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public abstract class AbstractApproximation implements Approximation {

	/** Liste der verwendeten Stützstellen. */
	private final List<Stuetzstelle<Double>> stuetzstellen;

	/**
	 * Initialisiert die Stützstellenliste.
	 */
	public AbstractApproximation() {
		stuetzstellen = new ArrayList<Stuetzstelle<Double>>();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.gllib.Approximation#getIntervall()
	 */
	public Interval getIntervall() {
		return new Interval(stuetzstellen.get(0).getZeitstempel(),
				stuetzstellen.get(stuetzstellen.size() - 1).getZeitstempel());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.gllib.Approximation#getStuetzstellen()
	 */
	public List<Stuetzstelle<Double>> getStuetzstellen() {
		return Collections.unmodifiableList(stuetzstellen);
	}

	/**
	 * {@inheritDoc}
	 */
	public SortedSet<Stuetzstelle<Double>> interpoliere(long intervallBreite) {
		if (intervallBreite <= 0) {
			throw new IllegalArgumentException(
					"Intervallbreite muss größer null sein.");
		}

		SortedSet<Stuetzstelle<Double>> interpolation;
		long zeitstempel;

		interpolation = new TreeSet<Stuetzstelle<Double>>();

		// Sonderfall: keine Stützstellen vorhanden
		if (stuetzstellen.size() == 0) {
			return interpolation;
		}

		// Stützstellen an den Intervallgrenzen bestimmen
		zeitstempel = stuetzstellen.get(0).getZeitstempel();
		while (zeitstempel <= stuetzstellen.get(stuetzstellen.size() - 1)
				.getZeitstempel()) {
			interpolation.add(get(zeitstempel));
			zeitstempel += intervallBreite;
		}
		if (interpolation.last().getZeitstempel() < getIntervall().getEnd()) {
			interpolation.add(get(getIntervall().getEnd()));
		}

		return interpolation;
	}

	/**
	 * Prüft ob für den Zeitstempel eine Stützstelle berechnet werden kann. Für
	 * stetige Funktionen bedeutet dies, dass der Zeitstempel im Intervall der
	 * Stützstellen liegt. Für unstetige Funktionen muss diese Methode passende
	 * überschrieben werden.
	 * 
	 * @param t
	 *            ein Zeitstempel.
	 * @return {@code true}, wenn der Wert der Approximation zum angegebenen
	 *         Zeitpunkt definiert ist.
	 */
	public boolean isValid(long t) {
		return stuetzstellen.get(0).getZeitstempel() <= t
				&& t <= stuetzstellen.get(stuetzstellen.size() - 1)
						.getZeitstempel();
	}

	/**
	 * Bestimmt die Liste der verwendeten Stützstellen. Die Liste entspricht der
	 * Ganglinie, abzüglich der undefinierten Stützstellen.
	 * 
	 * {@inheritDoc}
	 */
	public void setStuetzstellen(Collection<Stuetzstelle<Double>> stuetzstellen) {
		this.stuetzstellen.clear();
		for (Stuetzstelle<Double> s : stuetzstellen) {
			if (s.getWert() != null) {
				this.stuetzstellen.add(s);
			}
		}
		Collections.sort(this.stuetzstellen);
	}

	/**
	 * Sucht nach der ersten Stützstelle nach einem Zeitstempel. Gibt es zu dem
	 * Zeitstempel eine Stützstelle wird diese angenommen.
	 * 
	 * @param t
	 *            ein Zeitstempel.
	 * @return der Index der gefundenen Stützstelle oder {@code -1}, wenn es
	 *         keine gibt.
	 */
	protected int findeStuetzstelleNach(long t) {
		int index, start, ende;
		int mitte = 0;

		index = -1;
		if (!isValid(t)) {
			return index;
		}

		start = 0;
		ende = stuetzstellen.size() - 1;
		while (start <= ende && index < 0) {
			mitte = start + (ende - start) / 2;
			if (stuetzstellen.get(mitte).getZeitstempel() < t) {
				// rechts weitersuchen
				start = mitte + 1;
			} else if (stuetzstellen.get(mitte).getZeitstempel() > t) {
				// links weitersuchen
				ende = mitte - 1;
			} else {
				// gefunden
				index = mitte;
				break;
			}
		}

		if (index == -1) {
			if (stuetzstellen.get(mitte).getZeitstempel() < t) {
				index = mitte + 1;
			} else {
				index = mitte;
			}
		}

		return index;
	}

	/**
	 * Sucht nach der ersten Stützstelle vor einem Zeitstempel. Gibt es zu dem
	 * Zeitstempel eine Stützstelle wird diese angenommen.
	 * 
	 * @param t
	 *            ein Zeitstempel.
	 * @return der Index der gefundenen Stützstelle oder {@code -1}, wenn es
	 *         keine gibt.
	 */
	protected int findeStuetzstelleVor(long t) {
		int index, start, ende;
		int mitte = 0;

		index = -1;
		if (!isValid(t)) {
			return index;
		}

		start = 0;
		ende = stuetzstellen.size() - 1;
		while (start <= ende && index < 0) {
			mitte = start + (ende - start) / 2;
			if (stuetzstellen.get(mitte).getZeitstempel() < t) {
				// rechts weitersuchen
				start = mitte + 1;
			} else if (stuetzstellen.get(mitte).getZeitstempel() > t) {
				// links weitersuchen
				ende = mitte - 1;
			} else {
				// gefunden
				index = mitte;
				break;
			}
		}

		if (index == -1) {
			if (stuetzstellen.get(mitte).getZeitstempel() > t) {
				index = mitte - 1;
			} else {
				index = mitte;
			}
		}

		return index;
	}

}
