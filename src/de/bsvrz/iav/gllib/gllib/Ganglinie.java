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
 * Repräsentiert eine allgemeine Ganglinie, bestehend aus einer sortierten Menge
 * von Stützstellen und der Angabe eines Interpolationsverfahren.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 * @param <T>
 *            der Typ der Ganglinie.
 */
public class Ganglinie<T> extends TreeMap<Long, T> {

	/** Die Eigenschaft {@code serialVersionUID}. */
	private static final long serialVersionUID = 0;

	/** Verfahren zur Berechnung der Punkte zwischen den Stützstellen. */
	private Approximation<T> approximation;

	/** Flag, ob die Approximation aktuallisiert werden muss. */
	private boolean approximationAktuell;

	/** Cached die Teilintervalle der Ganglinie mit definierten Stützstellen. */
	private final List<Interval> intervalle;

	/**
	 * Konstruiert eine Ganglinie ohne Stützstellen.
	 */
	public Ganglinie() {
		intervalle = new ArrayList<Interval>();
		setApproximationAktuell(false);
	}

	/**
	 * Markiert zusätzlich die Approximation als nicht mehr aktuell.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see java.util.TreeMap#clear()
	 */
	@Override
	public void clear() {
		setApproximationAktuell(false);
		super.clear();
	}

	/**
	 * Kopiert die Stützstellen und das Approximationsverfahren. Der Wert für
	 * {@code approximationAktuell} wird auf false gesetzt.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Ganglinie<T> clone() {
		Ganglinie<T> g;

		g = new Ganglinie<T>();
		g.putAll(this);
		g.setApproximation(approximation);
		return g;
	}

	/**
	 * Gibt die Approximation der Ganglinie zurück. Die Approximation wird falls
	 * nötig vorher initialisiert.
	 * 
	 * @return die Approximation.
	 */
	public Approximation<T> getApproximation() {
		if (approximation != null) {
			aktualisiereApproximation();
		}
		return approximation;
	}

	/**
	 * Gibt das Intervall der Ganglinie zurück. Das Intervall besteht aus der
	 * ersten und letzten Stützstelle. Existieren keine Stützstellen wird
	 * {@code null} zurückgegeben.
	 * 
	 * @return das Ganglinienintervall oder {@code null}.
	 */
	public Interval getIntervall() {
		if (size() == 0) {
			return null;
		}

		return new Interval(firstKey(), lastKey());
	}

	/**
	 * Bestimmt die Intervalle in denen die Ganglinie definiert ist.
	 * 
	 * @return eine Liste von Intervallen.
	 */
	public List<Interval> getIntervalle() {
		if (isApproximationAktuell()) {
			return intervalle;
		}

		Long start, ende;

		intervalle.clear();
		start = null;
		ende = null;

		for (final long t : keySet()) {
			if (start == null) {
				// Beginn eines neuen Intervalls
				if (get(t) != null) {
					if (t == lastKey()) {
						// Die letzte Stützstelle ist das letzte Intervall
						intervalle.add(new Interval(t, t));
					} else {
						start = t;
					}
				}
			} else {
				if (get(t) == null) {
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
					if (get(t) != null) {
						ende = t;
					}
					if (t == lastKey()) {
						// Die letzte Stützstelle ist das letzte Intervall
						intervalle.add(new Interval(start, ende));
					}
				}
			}
		}

		return intervalle;
	}

	/**
	 * Gibt die Stützstelle zu einem bestimmten Zeitpunkt zurück. Es wird die
	 * mit der Approximation berechnete Stützstelle ausgeliefert. Die
	 * Approximation muss dazu zuvor festgelegt worden sein.
	 * 
	 * @param zeitstempel
	 *            der Zeitstempel zu dem eine Stützstelle gesucht wird.
	 * @return die gesuchte Stützstelle.
	 */
	public Stuetzstelle<T> getStuetzstelle(final long zeitstempel) {
		if (!isValid(zeitstempel)) {
			// Zeitstempel liegt in einem undefinierten Teilintervall
			return new Stuetzstelle<T>(zeitstempel, null);
		}

		if (approximation != null) {
			aktualisiereApproximation();
			return approximation.get(zeitstempel);
		}

		throw new IllegalStateException(
				"Es wurde keine Approximation festgelegt.");
	}

	/**
	 * Gibt ein sortiertes Feld der existierenden Stützstellen zurück.
	 * 
	 * @return die nach Zeitstempel sortierten Stützstellen.
	 */
	public List<Stuetzstelle<T>> getStuetzstellen() {
		List<Stuetzstelle<T>> liste;

		liste = new ArrayList<Stuetzstelle<T>>();
		for (final long t : keySet()) {
			liste.add(new Stuetzstelle<T>(t, get(t)));
		}

		return liste;
	}

	/**
	 * Gibt die existierenden Stützstellen im angegebenen Intervall zurück.
	 * 
	 * @param intervall
	 *            ein Intervall.
	 * @return die Liste der Stützstellen im Intervall, sortiert nach
	 *         Zeitstempel.
	 */
	public List<Stuetzstelle<T>> getStuetzstellen(final Interval intervall) {
		SortedMap<Long, T> menge;
		List<Stuetzstelle<T>> liste;

		menge = subMap(intervall.getStart(), intervall.getEnd() + 1);
		liste = new ArrayList<Stuetzstelle<T>>();
		for (final long t : menge.keySet()) {
			liste.add(getStuetzstelle(t));
		}

		return liste;
	}

	/**
	 * Prüft ob ein Teilintervall der Ganglinie vollständig definiert ist, also
	 * keine undefinierten Berreiche enthält.
	 * 
	 * @param intervall
	 *            das zu prüfende Intervall.
	 * @return {@code true}, wenn das Teilintervall der Ganglinie keine
	 *         undefinierten Bereiche enthält.
	 * @see #getIntervalle()
	 */
	public boolean isValid(final Interval intervall) {
		boolean ok;

		ok = false;
		for (final Interval i : getIntervalle()) {
			if (i.contains(intervall)) {
				ok = true;
				break;
			}
		}

		return ok;
	}

	/**
	 * Prüft ob ein Zeitstempel im definiterten Bereich der Ganglinie liegt.
	 * 
	 * @param zeitstempel
	 *            der zu prüfender Zeitstempel.
	 * @return {@code true}, wenn der Zeitstempel im definierten Bereich der
	 *         Ganglinie liegt.
	 * @see #getIntervalle()
	 */
	public boolean isValid(final long zeitstempel) {
		boolean ok;

		ok = false;
		for (final Interval i : getIntervalle()) {
			if (i.contains(zeitstempel)) {
				ok = true;
				break;
			}
		}

		return ok;
	}

	/**
	 * Markiert zusätzlich die Approximation als nicht mehr aktuell.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see java.util.TreeMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public T put(final Long key, final T value) {
		setApproximationAktuell(false);
		return super.put(key, value);
	}

	/**
	 * Markiert zusätzlich die Approximation als nicht mehr aktuell.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see java.util.TreeMap#putAll(java.util.Map)
	 */
	@Override
	public void putAll(final Map<? extends Long, ? extends T> map) {
		setApproximationAktuell(false);
		super.putAll(map);
	}

	/**
	 * Markiert zusätzlich die Approximation als nicht mehr aktuell.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see java.util.TreeMap#remove(java.lang.Object)
	 */
	@Override
	public T remove(final Object key) {
		setApproximationAktuell(false);
		return super.remove(key);
	}

	/**
	 * Legt das Approximationsverfahren fest, mit dem die Werte zwischen den
	 * Stützstellen bestimmt werden soll.
	 * 
	 * @param approximation
	 *            das Approximationsverfahren.
	 */
	public void setApproximation(final Approximation<T> approximation) {
		setApproximationAktuell(false);
		this.approximation = approximation;
	}

	/**
	 * Nimmt eine Stützstelle in die Ganglinie auf. Existiert zu dem Zeitpunkt
	 * bereits eine, wird diese überschrieben.
	 * 
	 * @param s
	 *            die neue Stuützstelle.
	 * @return {@code true}, wenn die Stützstelle neu angelegt wurde und
	 *         {@code false}, wenn eine vorhandene Stützstelle ersetzt wurde.
	 */
	public boolean setStuetzstelle(final Stuetzstelle<T> s) {
		return put(s.getZeitstempel(), s.getWert()) == null;
	}

	/**
	 * Ersetzt die Stützstellen der Ganglinie.
	 * 
	 * @param stuetzstellen
	 *            die neuen Stützstellen
	 */
	public void setStuetzstellen(final Collection<Stuetzstelle<T>> stuetzstellen) {
		setApproximationAktuell(false);
		clear();
		for (final Stuetzstelle<T> s : stuetzstellen) {
			put(s.getZeitstempel(), s.getWert());
		}
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

	/**
	 * Aktualisiert falls nötig die Approximation.
	 */
	private void aktualisiereApproximation() {
		if (!isApproximationAktuell()) {
			getIntervalle();
			approximation.setStuetzstellen(getStuetzstellen());
			approximation.initialisiere();
			setApproximationAktuell(true);
		}
	}

	/**
	 * Gibt {@code false} zurück, wenn die Approximation aktuallisiert werden
	 * muss, weil sich die Ganglinie geändert hat.
	 * 
	 * @return {@code true}, wenn Ganglinie und Approximation konform gehen und
	 *         {@code false}, wenn die Approximation aktualisiert werden muss.
	 */
	protected boolean isApproximationAktuell() {
		return approximationAktuell;
	}

	/**
	 * Setzt das Flag, ob die Approximation noch gültig ist oder nicht.
	 * 
	 * @param approximationAktuell
	 *            {@code false}, wenn die Approximation aktualisiert werden
	 *            muss.
	 */
	protected void setApproximationAktuell(final boolean approximationAktuell) {
		this.approximationAktuell = approximationAktuell;
	}

}
