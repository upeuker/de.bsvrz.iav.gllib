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

import java.util.List;
import java.util.SortedMap;

import com.bitctrl.util.Interval;

/**
 * Definiert eine gemeinsame Schnittstelle f�r Ganglinien.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 * @param <T>
 *            der Typ des Wertes eines St�tzstelle.
 */
public interface IGanglinie<T> extends SortedMap<Long, T> {

	/**
	 * Aktualisiert die Approximation. Muss bei &Auml;nderung an den
	 * St�tzstellen der Ganglinie aufgerufen werden.
	 */
	void aktualisiereApproximation();

	/**
	 * Gibt die Anzahl der St�tzstellen der Ganglinie zur�ck.
	 * 
	 * @return St�tzstellenanzahl
	 */
	int anzahlStuetzstellen();

	/**
	 * Pr�ft ob zu einem Zeitstempel eine reale St�tzstelle existiert.
	 * 
	 * @param zeitstempel
	 *            Ein Zeitstempel
	 * @return {@code true}, wenn die Ganglinie eine St�tzstelle zum
	 *         Zeitpunkt speichert und {@code false}, wenn zu dem Zeitpunkt die
	 *         St�tzstelle berechnet werden muss
	 */
	boolean existsStuetzstelle(long zeitstempel);

	/**
	 * Die Ganglinie als Approximation zu�ck.
	 * 
	 * @return die Approximation der Ganglinie oder {@code null}, wenn keine
	 *         Approximation festgelegt wurde.
	 */
	Approximation getApproximation();

	/**
	 * Gibt das Zeitintervall der Ganglinie zur�ck.
	 * 
	 * @return Ein {@link Interval} oder {@code null}, wenn keine
	 *         St�tzstellen vorhanden sind
	 */
	Interval getIntervall();

	/**
	 * Bestimmt die Intervalle in denen die Ganglinie definiert ist.
	 * 
	 * @return Liste von Intervallen
	 */
	List<Interval> getIntervalle();

	/**
	 * Gibt die St�tzstelle zu einem bestimmten Zeitpunkt zur�ck. Es
	 * wird die mit der Approximation berechnete St�tzstelle ausgeliefert.
	 * Die Approximation muss dazu zuvor festgelegt worden sein.
	 * 
	 * @param zeitstempel
	 *            Der Zeitstempel zu dem eine St�tzstelle gesucht wird.
	 * @return Die gesuchte St�tzstelle.
	 */
	Stuetzstelle<T> getStuetzstelle(long zeitstempel);

	/**
	 * Gibt ein sortiertes Feld der existierenden St�tzstellen
	 * zur�ck;.
	 * 
	 * @return Nach Zeitstempel sortiere St�tzstellen
	 */
	List<Stuetzstelle<T>> getStuetzstellen();

	/**
	 * Die existierenden St�tzstellen im angegebenen Intervall zur�ck.
	 * 
	 * @param intervall
	 *            ein zeitliches Intervall.
	 * @return die Liste der St�tzstellen im Intervall, sortiert nach
	 *         Zeitstempel.
	 */
	List<Stuetzstelle<T>> getStuetzstellen(Interval intervall);

	/**
	 * Gibt {@code false} zur�ck, wenn die Approximation aktuallisiert
	 * werden muss, weil sich die Ganglinie ge�ndert hat.
	 * 
	 * @return {@code true}, wenn Ganglinie und Approximation konform gehen und
	 *         {@code false}, wenn die Approximation aktualisiert werden muss.
	 */
	boolean isApproximationAktuell();

	/**
	 * Pr�ft ob ein Teilintervall der Ganglinie vollst�ndig definiert ist, also
	 * keine undefinierten Berreiche enth�lt..
	 * 
	 * @param intervall
	 *            das zu pr�fende Intervall
	 * @return <code>true</code>, wenn das Teilintervall der Ganglinie keine
	 *         undefinierten Bereiche enth�lt.
	 * @see #getIntervalle()
	 */
	boolean isValid(Interval intervall);

	/**
	 * Pr�ft ob ein Zeitstempel im Definitionsbereich der Ganglinie liegt.
	 * 
	 * @param zeitstempel
	 *            zu pr�fender Zeitstempel
	 * @return <code>true</code>, wenn <code>zeitstempel</code> im
	 *         definierten Bereich der Ganglinie liegt
	 * @see #getIntervalle()
	 */
	boolean isValid(long zeitstempel);

	/**
	 * Entfernt eine St�tzstelle.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel der St�tzstelle, die entfernt werden soll
	 */
	void remove(long zeitstempel);

	/**
	 * Legt das Approximationsverfahren fest, mit dem die Werte zwischen den
	 * St�tzstellen bestimmt werden soll.
	 * 
	 * @param approximation
	 *            Klasse eines Approximationsverfahrens. Die Klasse m�ss
	 *            einen parameterlosen Konstruktor besitzen.
	 * @throws IllegalArgumentException
	 *             Wenn die Klassen keinen �ffentlichen parameterlosen
	 *             Konstruktor besitzt
	 */
	void setApproximation(Approximation approximation);

	/**
	 * Nimmt eine St�tzstelle in die Ganglinie auf. Existiert zu dem
	 * Zeitpunkt bereits eine, wird diese �berschrieben.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel der St�tzstelle
	 * @param wert
	 *            Wert der St�tzstelle
	 * @return {@code true}, wenn die St�tzstelle neu angelegt wurde und
	 *         {@code false}, wenn eine vorhandene St�tzstelle ersetzt
	 *         wurde.
	 */
	boolean setStuetzstelle(long zeitstempel, T wert);

	/**
	 * Nimmt eine St�tzstelle in die Ganglinie auf. Existiert zu dem
	 * Zeitpunkt bereits eine, wird diese �berschrieben.
	 * 
	 * @param s
	 *            Die neue Stu�tzstelle
	 * @return {@code true}, wenn die St�tzstelle neu angelegt wurde und
	 *         {@code false}, wenn eine vorhandene St�tzstelle ersetzt
	 *         wurde.
	 */
	boolean setStuetzstelle(Stuetzstelle<T> s);

}
