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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import de.bsvrz.sys.funclib.bitctrl.util.Intervall;

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
 * @author BitrCtrl, Schumann
 * @version $Id$
 * @param <T>
 *            der Typ der Werte an den St&uuml;tzstellen der Ganglinie.
 */
public class Ganglinie<T> {

	/** Speicher der St&uuml;tzstellen. */
	protected SortedMap<Long, T> stuetzstellen;

	/** Verfahren zur Berechnung der Punkte zwischen den St&uuml;tzstellen. */
	private Approximation approximation;

	/**
	 * Konstruiert eine Ganglinie ohne St&uuml;tzstellen.
	 */
	public Ganglinie() {
		stuetzstellen = new TreeMap<Long, T>();
	}

	/**
	 * Kopierkonstruktor. Es werden die St&uuml;tzstellen aus der
	 * <em>Collection</em> &uuml;bernommen.
	 * 
	 * @param stuetzstellen
	 *            Die St&uuml;tzstellen der Ganglinie.
	 */
	public Ganglinie(Collection<Stuetzstelle<T>> stuetzstellen) {
		this();
		for (Stuetzstelle<T> s : stuetzstellen) {
			this.stuetzstellen.put(s.getZeitstempel(), s.getWert());
		}
		aktualisiereApproximation();
	}

	/**
	 * Kopierkonstruktor. Es werden die St&uuml;tzstellen aus der <em>Map</em>
	 * &uuml;bernommen, wobei die Schl&uuml;ssel als Zeitstempel interpretiert
	 * werden (Zeitstempel -> Wert).
	 * 
	 * @param stuetzstellen
	 *            Die St&uuml;tzstellen der Ganglinie.
	 */
	public Ganglinie(Map<Long, T> stuetzstellen) {
		this();
		for (long t : stuetzstellen.keySet()) {
			this.stuetzstellen.put(t, stuetzstellen.get(t));
		}
		aktualisiereApproximation();
	}

	/**
	 * Gibt die Anzahl der St&uuml;tzstellen der Ganglinie zur&uuml;ck.
	 * 
	 * @return St&uuml;tzstellenanzahl
	 */
	public int anzahlStuetzstellen() {
		return stuetzstellen.size();
	}

	/**
	 * Pr&uuml;ft ob zu einem Zeitstempel eine reale St&uuml;tzstelle existiert.
	 * 
	 * @param zeitstempel
	 *            Ein Zeitstempel
	 * @return {@code true}, wenn die Ganglinie eine St&uuml;tzstelle zum
	 *         Zeitpunkt speichert und {@code false}, wenn zu dem Zeitpunkt die
	 *         St&uuml;tzstelle berechnet werden muss
	 */
	public boolean existsStuetzstelle(long zeitstempel) {
		return stuetzstellen.containsKey(zeitstempel);
	}

	/**
	 * Pr&uuml;ft ob ein Zeitstempel im Definitionsbereich der Ganglinie liegt.
	 * 
	 * @param zeitstempel
	 *            zu pr&uuml;fender Zeitstempel
	 * @return <code>true</code>, wenn <code>zeitstempel</code> im
	 *         definierten Bereich der Ganglinie liegt
	 * @see #getIntervalle()
	 */
	public boolean isValid(long zeitstempel) {
		boolean ok;

		ok = false;
		for (Intervall i : getIntervalle()) {
			if (i.isEnthalten(zeitstempel)) {
				ok = true;
				break;
			}
		}

		return ok;
	}

	/**
	 * Gibt die St&uuml;tzstelle zu einem bestimmten Zeitpunkt zur&uuml;ck. Es
	 * wird die mit der Approximation berechnete St&uuml;tzstelle ausgeliefert.
	 * Wurde keine Approximation festgelegt ({@code getApproximation() == null}),
	 * dann wird die real existierende St&uuml;tzstelle ausgeliefert. Existiert
	 * zum angefragten Zeitpunkt keine St&uuml;tzstelle, wird {@code null}
	 * zur&uuml;ckgegegeben.
	 * 
	 * @param zeitstempel
	 *            Der Zeitstempel zu dem eine St&uuml;tzstelle gesucht wird
	 * @return Die gesuchte St&uuml;tzstelle oder {@code null}, wenn keine
	 *         berechnet werden konnte und keine existiert
	 */
	public Stuetzstelle<T> getStuetzstelle(long zeitstempel) {
		if (approximation != null) {
			return approximation.get(zeitstempel);
		}

		// Rückfallebene, wenn keine Approximation festgelegt, wird falls
		// vorhanden eine existierende Stützstelle zurückgegeben.
		if (stuetzstellen.containsKey(zeitstempel)) {
			return new Stuetzstelle<T>(zeitstempel, stuetzstellen
					.get(zeitstempel));
		}

		// Mehr geht nicht, dann gibt es eben keine Stützstelle
		return null;
	}

	/**
	 * Gibt ein sortiertes Feld der existierenden St&uuml;tzstellen
	 * zur&uuml;ck;.
	 * 
	 * @return Nach Zeitstempel sortiere St&uuml;tzstellen
	 */
	public List<Stuetzstelle<T>> getStuetzstellen() {
		List<Stuetzstelle<T>> liste;

		liste = new ArrayList<Stuetzstelle<T>>();
		for (long t : stuetzstellen.keySet()) {
			liste.add(getStuetzstelle(t));
		}

		return liste;
	}

	public List<Stuetzstelle<T>> getStuetzstellen(Intervall intervall) {
		SortedMap<Long, T> menge;
		List<Stuetzstelle<T>> liste;

		menge = stuetzstellen.subMap(intervall.getStart(),
				intervall.getEnde() + 1);
		liste = new ArrayList<Stuetzstelle<T>>();
		for (long t : menge.keySet()) {
			liste.add(getStuetzstelle(t));
		}

		return liste;

	}

	/**
	 * Nimmt eine St&uuml;tzstelle in die Ganglinie auf. Existiert zu dem
	 * Zeitpunkt bereits eine, wird diese &uuml;berschrieben.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel der St&uuml;tzstelle
	 * @param wert
	 *            Wert der St&uuml;tzstelle
	 * @return {@code true}, wenn die St&uuml;tzstelle neu angelegt wurde und
	 *         {@code false}, wenn eine vorhandene St&uuml;tzstelle ersetzt
	 *         wurde.
	 */
	public boolean setStuetzstelle(long zeitstempel, T wert) {
		boolean neu;

		neu = stuetzstellen.put(zeitstempel, wert) == null;
		aktualisiereApproximation();

		return neu;
	}

	/**
	 * Nimmt eine St&uuml;tzstelle in die Ganglinie auf. Existiert zu dem
	 * Zeitpunkt bereits eine, wird diese &uuml;berschrieben. Ruft intern
	 * {@link #setStuetzstelle(long, Object)} auf.
	 * 
	 * @param s
	 *            Die neue Stu&uuml;tzstelle
	 * @return {@code true}, wenn die St&uuml;tzstelle neu angelegt wurde und
	 *         {@code false}, wenn eine vorhandene St&uuml;tzstelle ersetzt
	 *         wurde.
	 */
	public boolean setStuetzstelle(Stuetzstelle<T> s) {
		return setStuetzstelle(s.getZeitstempel(), s.getWert());
	}

	/**
	 * Entfernt eine St&uuml;tzstelle.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel der St&uuml;tzstelle, die entfernt werden soll
	 */
	public void remove(long zeitstempel) {
		stuetzstellen.remove(zeitstempel);
		aktualisiereApproximation();
	}

	/**
	 * Gibt das Zeitintervall der Ganglinie zur&uuml;ck.
	 * 
	 * @return Ein {@link Intervall} oder {@code null}, wenn keine
	 *         St&uuml;tzstellen vorhanden sind
	 */
	public Intervall getIntervall() {
		if (stuetzstellen.size() == 0) {
			return null;
		}

		return new Intervall(stuetzstellen.firstKey(), stuetzstellen.lastKey());
	}

	/**
	 * Bestimmt die Intervalle in denen die Ganglinie definiert ist.
	 * 
	 * @return Liste von Intervallen
	 */
	public List<Intervall> getIntervalle() {
		List<Intervall> intervalle;
		Long start, ende;

		intervalle = new ArrayList<Intervall>();
		start = null;
		ende = null;

		for (long t : stuetzstellen.keySet()) {
			if (start == null && stuetzstellen.get(t) != null) {
				start = t;
			}

			if (stuetzstellen.get(t) == null) {
				// Definitionslücke gefunden
				if (start != null && ende != null) {
					intervalle.add(new Intervall(start, ende));
					start = null;
					ende = null;
				}
			} else {
				// Intervall verlängern
				if (stuetzstellen.get(t) != null) {
					ende = t;
				}
			}

			if (t == stuetzstellen.lastKey()) {
				intervalle.add(new Intervall(start, ende));
			}
		}

		return intervalle;
	}

	/**
	 * Die Ganglinie als Approximation zu&uuml;ck.
	 * 
	 * @return die Approximation der Ganglinie oder {@code null}, wenn keine
	 *         Approximation festgelegt wurde.
	 */
	public Approximation getApproximation() {
		return approximation;
	}

	/**
	 * Legt das Approximationsverfahren fest, mit dem die Werte zwischen den
	 * St&uuml;tzstellen bestimmt werden soll.
	 * 
	 * @param approximation
	 *            Klasse eines Approximationsverfahrens. Die Klasse m&uuml;ss
	 *            einen parameterlosen Konstruktor besitzen.
	 * @throws IllegalArgumentException
	 *             Wenn die Klassen keinen &ouml;ffentlichen parameterlosen
	 *             Konstruktor besitzt
	 */
	public void setApproximation(Approximation approximation) {
		this.approximation = approximation;
		aktualisiereApproximation();
	}

	/**
	 * Aktualisiert die Approximation. Muss bei &Auml;nderung an den
	 * St&uuml;tzstellen der Ganglinie aufgerufen werden.
	 */
	protected void aktualisiereApproximation() {
		if (approximation != null) {
			approximation.initialisiere();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if (o instanceof Ganglinie) {
			Ganglinie g;

			g = (Ganglinie) o;
			return stuetzstellen.equals(g.stuetzstellen);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getStuetzstellen().toString();
	}

}
