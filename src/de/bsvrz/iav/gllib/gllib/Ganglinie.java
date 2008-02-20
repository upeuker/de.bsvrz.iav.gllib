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
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.bitctrl.util.Interval;

/**
 * Repr&auml;sentiert eine allgemeine Ganglinie, bestehend aus einer sortierten
 * Menge von St&uuml;tzstellen und der Angabe eines Interpolationsverfahren.
 * Wird kein Approximationsverfahren festgelegt, wird ein
 * {@link de.bsvrz.iav.gllib.gllib.BSpline B-Spline} mit Standardordnung
 * angenommen.
 * <p>
 * Da Ganglinien verschiedene Approximationsverfahren verwenden können, wird für
 * neue Ganglinien, die bei den Operationen (z. B. Addition) entstehen, das
 * Standardverfahren festgelegt (B-Spline mit Ordnung 5).
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class Ganglinie implements IGanglinie<Double> {

	/** Speicher der St&uuml;tzstellen. */
	SortedMap<Long, Double> stuetzstellen;

	/** Verfahren zur Berechnung der Punkte zwischen den St&uuml;tzstellen. */
	private Approximation approximation;

	/** Flag, ob die Approximation aktuallisiert werden muss. */
	private boolean approximationAktuell = false;

	/**
	 * Konstruiert eine Ganglinie ohne St&uuml;tzstellen.
	 */
	public Ganglinie() {
		stuetzstellen = new TreeMap<Long, Double>();
	}

	/**
	 * Kopierkonstruktor. Es werden die St&uuml;tzstellen aus der
	 * <em>Collection</em> &uuml;bernommen.
	 * 
	 * @param stuetzstellen
	 *            Die St&uuml;tzstellen der Ganglinie.
	 */
	public Ganglinie(Collection<Stuetzstelle<Double>> stuetzstellen) {
		this();
		for (Stuetzstelle<Double> s : stuetzstellen) {
			this.stuetzstellen.put(s.getZeitstempel(), s.getWert());
		}
	}

	/**
	 * Kopierkonstruktor. Es werden die St&uuml;tzstellen aus der <em>Map</em>
	 * &uuml;bernommen, wobei die Schl&uuml;ssel als Zeitstempel interpretiert
	 * werden (Zeitstempel -> Wert).
	 * 
	 * @param stuetzstellen
	 *            Die St&uuml;tzstellen der Ganglinie.
	 */
	public Ganglinie(Map<Long, Double> stuetzstellen) {
		this();
		for (long t : stuetzstellen.keySet()) {
			this.stuetzstellen.put(t, stuetzstellen.get(t));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void aktualisiereApproximation() {
		if (approximation == null) {
			throw new IllegalStateException(
					"Es wurde keine Approximation festgelegt.");
		}
		approximation.setStuetzstellen(getStuetzstellen());
		approximation.initialisiere();
		approximationAktuell = true;
	}

	/**
	 * {@inheritDoc}
	 */
	public int anzahlStuetzstellen() {
		return stuetzstellen.size();
	}

	/**
	 * Kopiert die St&uumltzstellen und das Approximationsverfahren. Der Wert
	 * f&uuml;r {@code approximationAktuell} wird auf false gesetzt.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Ganglinie clone() {
		Ganglinie g;

		g = new Ganglinie(stuetzstellen);
		g.setApproximation(approximation);
		return g;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsStuetzstelle(long zeitstempel) {
		return stuetzstellen.containsKey(zeitstempel);
	}

	/**
	 * {@inheritDoc}
	 */
	public Approximation getApproximation() {
		return approximation;
	}

	/**
	 * {@inheritDoc}
	 */
	public Interval getIntervall() {
		if (stuetzstellen.size() == 0) {
			return null;
		}

		return new Interval(stuetzstellen.firstKey(), stuetzstellen.lastKey());
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Interval> getIntervalle() {
		List<Interval> intervalle;
		Long start, ende;

		intervalle = new ArrayList<Interval>();
		start = null;
		ende = null;

		for (long t : stuetzstellen.keySet()) {
			if (start == null) {
				// Beginn eines neuen Intervalls
				if (stuetzstellen.get(t) != null) {
					if (t == stuetzstellen.lastKey()) {
						// Die letzte Stützstelle ist das letzte Intervall
						intervalle.add(new Interval(t, t));
					} else {
						start = t;
					}
				}
			} else {
				if (stuetzstellen.get(t) == null) {
					// Definitionslücke gefunden
					if (ende != null) {
						intervalle.add(new Interval(start, ende));
						start = null;
						ende = null;
					} else {
						intervalle.add(new Interval(start, start));
						start = null;
					}
				} else {
					// Intervall verlängern
					if (stuetzstellen.get(t) != null) {
						ende = t;
					}
					if (t == stuetzstellen.lastKey()) {
						// Die letzte Stützstelle ist das letzte Intervall
						intervalle.add(new Interval(start, ende));
					}
				}
			}
		}

		return intervalle;
	}

	/**
	 * {@inheritDoc}
	 */
	public Stuetzstelle<Double> getStuetzstelle(long zeitstempel) {
		if (!isValid(zeitstempel)) {
			// Zeitstempel liegt in einem undefinierten Teilintervall
			return new Stuetzstelle<Double>(zeitstempel, null);
		}

		if (approximation != null) {
			if (!isApproximationAktuell()) {
				aktualisiereApproximation();
			}
			return approximation.get(zeitstempel);
		}

		throw new IllegalStateException(
				"Es wurde keine Approximation festgelegt.");
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Stuetzstelle<Double>> getStuetzstellen() {
		List<Stuetzstelle<Double>> liste;

		liste = new ArrayList<Stuetzstelle<Double>>();
		for (long t : stuetzstellen.keySet()) {
			liste.add(new Stuetzstelle<Double>(t, stuetzstellen.get(t)));
		}

		return liste;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Stuetzstelle<Double>> getStuetzstellen(Interval intervall) {
		SortedMap<Long, Double> menge;
		List<Stuetzstelle<Double>> liste;

		menge = stuetzstellen.subMap(intervall.getStart(),
				intervall.getEnd() + 1);
		liste = new ArrayList<Stuetzstelle<Double>>();
		for (long t : menge.keySet()) {
			liste.add(getStuetzstelle(t));
		}

		return liste;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isApproximationAktuell() {
		return approximationAktuell;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.gllib.IGanglinie#isValid(de.bsvrz.sys.funclib.bitctrl.util.Intervall)
	 */
	public boolean isValid(Interval intervall) {
		boolean ok;

		ok = false;
		for (Interval i : getIntervalle()) {
			if (i.contains(intervall)) {
				ok = true;
				break;
			}
		}

		return ok;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isValid(long zeitstempel) {
		boolean ok;

		ok = false;
		for (Interval i : getIntervalle()) {
			if (i.contains(zeitstempel)) {
				ok = true;
				break;
			}
		}

		return ok;
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove(long zeitstempel) {
		stuetzstellen.remove(zeitstempel);
		approximationAktuell = false;
	}

	/**
	 * Entfernt alle St&uuml;tzstellen.
	 */
	public void removeAll() {
		stuetzstellen.clear();
		approximationAktuell = false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setApproximation(Approximation approximation) {
		this.approximation = approximation;
		approximationAktuell = false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean setStuetzstelle(long zeitstempel, Double wert) {
		boolean neu;

		neu = stuetzstellen.put(zeitstempel, wert) == null;
		approximationAktuell = false;
		return neu;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean setStuetzstelle(Stuetzstelle<Double> s) {
		boolean neu;

		neu = setStuetzstelle(s.getZeitstempel(), s.getWert());
		approximationAktuell = false;
		return neu;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getName() + "[" + getStuetzstellen().toString() + "]";
	}

}
