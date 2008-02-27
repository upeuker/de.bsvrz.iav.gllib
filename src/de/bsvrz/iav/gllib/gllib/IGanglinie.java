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

import java.util.List;
import java.util.SortedMap;

import com.bitctrl.util.Interval;

/**
 * Definiert eine gemeinsame Schnittstelle für Ganglinien.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 * @param <T>
 *            der Typ des Wertes eines Stützstelle.
 */
public interface IGanglinie<T> extends SortedMap<Long, T> {

	/**
	 * Aktualisiert die Approximation. Muss bei &Auml;nderung an den
	 * Stützstellen der Ganglinie aufgerufen werden.
	 */
	void aktualisiereApproximation();

	/**
	 * Gibt die Anzahl der Stützstellen der Ganglinie zurück.
	 * 
	 * @return Stützstellenanzahl
	 */
	int anzahlStuetzstellen();

	/**
	 * Prüft ob zu einem Zeitstempel eine reale Stützstelle existiert.
	 * 
	 * @param zeitstempel
	 *            Ein Zeitstempel
	 * @return {@code true}, wenn die Ganglinie eine Stützstelle zum
	 *         Zeitpunkt speichert und {@code false}, wenn zu dem Zeitpunkt die
	 *         Stützstelle berechnet werden muss
	 */
	boolean existsStuetzstelle(long zeitstempel);

	/**
	 * Die Ganglinie als Approximation zuück.
	 * 
	 * @return die Approximation der Ganglinie oder {@code null}, wenn keine
	 *         Approximation festgelegt wurde.
	 */
	Approximation getApproximation();

	/**
	 * Gibt das Zeitintervall der Ganglinie zurück.
	 * 
	 * @return Ein {@link Interval} oder {@code null}, wenn keine
	 *         Stützstellen vorhanden sind
	 */
	Interval getIntervall();

	/**
	 * Bestimmt die Intervalle in denen die Ganglinie definiert ist.
	 * 
	 * @return Liste von Intervallen
	 */
	List<Interval> getIntervalle();

	/**
	 * Gibt die Stützstelle zu einem bestimmten Zeitpunkt zurück. Es
	 * wird die mit der Approximation berechnete Stützstelle ausgeliefert.
	 * Die Approximation muss dazu zuvor festgelegt worden sein.
	 * 
	 * @param zeitstempel
	 *            Der Zeitstempel zu dem eine Stützstelle gesucht wird.
	 * @return Die gesuchte Stützstelle.
	 */
	Stuetzstelle<T> getStuetzstelle(long zeitstempel);

	/**
	 * Gibt ein sortiertes Feld der existierenden Stützstellen
	 * zurück;.
	 * 
	 * @return Nach Zeitstempel sortiere Stützstellen
	 */
	List<Stuetzstelle<T>> getStuetzstellen();

	/**
	 * Die existierenden Stützstellen im angegebenen Intervall zurück.
	 * 
	 * @param intervall
	 *            ein zeitliches Intervall.
	 * @return die Liste der Stützstellen im Intervall, sortiert nach
	 *         Zeitstempel.
	 */
	List<Stuetzstelle<T>> getStuetzstellen(Interval intervall);

	/**
	 * Gibt {@code false} zurück, wenn die Approximation aktuallisiert
	 * werden muss, weil sich die Ganglinie geändert hat.
	 * 
	 * @return {@code true}, wenn Ganglinie und Approximation konform gehen und
	 *         {@code false}, wenn die Approximation aktualisiert werden muss.
	 */
	boolean isApproximationAktuell();

	/**
	 * Prüft ob ein Teilintervall der Ganglinie vollständig definiert ist, also
	 * keine undefinierten Berreiche enthält..
	 * 
	 * @param intervall
	 *            das zu prüfende Intervall
	 * @return <code>true</code>, wenn das Teilintervall der Ganglinie keine
	 *         undefinierten Bereiche enthält.
	 * @see #getIntervalle()
	 */
	boolean isValid(Interval intervall);

	/**
	 * Prüft ob ein Zeitstempel im Definitionsbereich der Ganglinie liegt.
	 * 
	 * @param zeitstempel
	 *            zu prüfender Zeitstempel
	 * @return <code>true</code>, wenn <code>zeitstempel</code> im
	 *         definierten Bereich der Ganglinie liegt
	 * @see #getIntervalle()
	 */
	boolean isValid(long zeitstempel);

	/**
	 * Entfernt eine Stützstelle.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel der Stützstelle, die entfernt werden soll
	 */
	void remove(long zeitstempel);

	/**
	 * Legt das Approximationsverfahren fest, mit dem die Werte zwischen den
	 * Stützstellen bestimmt werden soll.
	 * 
	 * @param approximation
	 *            Klasse eines Approximationsverfahrens. Die Klasse müss
	 *            einen parameterlosen Konstruktor besitzen.
	 * @throws IllegalArgumentException
	 *             Wenn die Klassen keinen öffentlichen parameterlosen
	 *             Konstruktor besitzt
	 */
	void setApproximation(Approximation approximation);

	/**
	 * Nimmt eine Stützstelle in die Ganglinie auf. Existiert zu dem
	 * Zeitpunkt bereits eine, wird diese überschrieben.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel der Stützstelle
	 * @param wert
	 *            Wert der Stützstelle
	 * @return {@code true}, wenn die Stützstelle neu angelegt wurde und
	 *         {@code false}, wenn eine vorhandene Stützstelle ersetzt
	 *         wurde.
	 */
	boolean setStuetzstelle(long zeitstempel, T wert);

	/**
	 * Nimmt eine Stützstelle in die Ganglinie auf. Existiert zu dem
	 * Zeitpunkt bereits eine, wird diese überschrieben.
	 * 
	 * @param s
	 *            Die neue Stuützstelle
	 * @return {@code true}, wenn die Stützstelle neu angelegt wurde und
	 *         {@code false}, wenn eine vorhandene Stützstelle ersetzt
	 *         wurde.
	 */
	boolean setStuetzstelle(Stuetzstelle<T> s);

}
