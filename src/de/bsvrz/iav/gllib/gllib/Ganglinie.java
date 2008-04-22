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
 * Wei�enfelser Stra�e 67
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
 * Repr�sentiert eine allgemeine Ganglinie, bestehend aus einer sortierten Menge
 * von St�tzstellen und der Angabe eines Interpolationsverfahren.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 * @param <T>
 *            der Typ der Ganglinie.
 */
public class Ganglinie<T> extends TreeMap<Long, T> {

	/** Die Eigenschaft {@code serialVersionUID}. */
	private static final long serialVersionUID = 0;

	/** Verfahren zur Berechnung der Punkte zwischen den St�tzstellen. */
	private Approximation<T> approximation;

	/** Flag, ob die Approximation aktuallisiert werden muss. */
	private boolean approximationAktuell;

	/** Cached die Teilintervalle der Ganglinie mit definierten St�tzstellen. */
	private final List<Interval> intervalle;

	/**
	 * Konstruiert eine Ganglinie ohne St�tzstellen.
	 */
	public Ganglinie() {
		intervalle = new ArrayList<Interval>();
		setApproximationAktuell(false);
	}

	/**
	 * Markiert zus�tzlich die Approximation als nicht mehr aktuell.
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
	 * Kopiert die St�tzstellen und das Approximationsverfahren. Der Wert f�r
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
	 * Gibt die Approximation der Ganglinie zur�ck. Die Approximation wird falls
	 * n�tig vorher initialisiert.
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
	 * Gibt das Intervall der Ganglinie zur�ck. Das Intervall besteht aus der
	 * ersten und letzten St�tzstelle. Existieren keine St�tzstellen wird
	 * {@code null} zur�ckgegeben.
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
						// Die letzte St�tzstelle ist das letzte Intervall
						intervalle.add(new Interval(t, t));
					} else {
						start = t;
					}
				}
			} else {
				if (get(t) == null) {
					// Definitionsl�cke gefunden
					if (ende != null) {
						intervalle.add(new Interval(start, ende));
						start = null;
						ende = null;
					} else {
						intervalle.add(new Interval(start, start));
						start = null;
					}
				} else {
					// Intervall verl�ngern
					if (get(t) != null) {
						ende = t;
					}
					if (t == lastKey()) {
						// Die letzte St�tzstelle ist das letzte Intervall
						intervalle.add(new Interval(start, ende));
					}
				}
			}
		}

		return intervalle;
	}

	/**
	 * Gibt die St�tzstelle zu einem bestimmten Zeitpunkt zur�ck. Es wird die
	 * mit der Approximation berechnete St�tzstelle ausgeliefert. Die
	 * Approximation muss dazu zuvor festgelegt worden sein.
	 * 
	 * @param zeitstempel
	 *            der Zeitstempel zu dem eine St�tzstelle gesucht wird.
	 * @return die gesuchte St�tzstelle.
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
	 * Gibt ein sortiertes Feld der existierenden St�tzstellen zur�ck.
	 * 
	 * @return die nach Zeitstempel sortierten St�tzstellen.
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
	 * Gibt die existierenden St�tzstellen im angegebenen Intervall zur�ck.
	 * 
	 * @param intervall
	 *            ein Intervall.
	 * @return die Liste der St�tzstellen im Intervall, sortiert nach
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
	 * Pr�ft ob ein Teilintervall der Ganglinie vollst�ndig definiert ist, also
	 * keine undefinierten Berreiche enth�lt.
	 * 
	 * @param intervall
	 *            das zu pr�fende Intervall.
	 * @return {@code true}, wenn das Teilintervall der Ganglinie keine
	 *         undefinierten Bereiche enth�lt.
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
	 * Pr�ft ob ein Zeitstempel im definiterten Bereich der Ganglinie liegt.
	 * 
	 * @param zeitstempel
	 *            der zu pr�fender Zeitstempel.
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
	 * Markiert zus�tzlich die Approximation als nicht mehr aktuell.
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
	 * Markiert zus�tzlich die Approximation als nicht mehr aktuell.
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
	 * Markiert zus�tzlich die Approximation als nicht mehr aktuell.
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
	 * St�tzstellen bestimmt werden soll.
	 * 
	 * @param approximation
	 *            das Approximationsverfahren.
	 */
	public void setApproximation(final Approximation<T> approximation) {
		setApproximationAktuell(false);
		this.approximation = approximation;
	}

	/**
	 * Nimmt eine St�tzstelle in die Ganglinie auf. Existiert zu dem Zeitpunkt
	 * bereits eine, wird diese �berschrieben.
	 * 
	 * @param s
	 *            die neue Stu�tzstelle.
	 * @return {@code true}, wenn die St�tzstelle neu angelegt wurde und
	 *         {@code false}, wenn eine vorhandene St�tzstelle ersetzt wurde.
	 */
	public boolean setStuetzstelle(final Stuetzstelle<T> s) {
		return put(s.getZeitstempel(), s.getWert()) == null;
	}

	/**
	 * Ersetzt die St�tzstellen der Ganglinie.
	 * 
	 * @param stuetzstellen
	 *            die neuen St�tzstellen
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
	 * Aktualisiert falls n�tig die Approximation.
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
	 * Gibt {@code false} zur�ck, wenn die Approximation aktuallisiert werden
	 * muss, weil sich die Ganglinie ge�ndert hat.
	 * 
	 * @return {@code true}, wenn Ganglinie und Approximation konform gehen und
	 *         {@code false}, wenn die Approximation aktualisiert werden muss.
	 */
	protected boolean isApproximationAktuell() {
		return approximationAktuell;
	}

	/**
	 * Setzt das Flag, ob die Approximation noch g�ltig ist oder nicht.
	 * 
	 * @param approximationAktuell
	 *            {@code false}, wenn die Approximation aktualisiert werden
	 *            muss.
	 */
	protected void setApproximationAktuell(final boolean approximationAktuell) {
		this.approximationAktuell = approximationAktuell;
	}

}
