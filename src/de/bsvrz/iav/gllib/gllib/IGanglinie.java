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

import de.bsvrz.sys.funclib.bitctrl.util.Intervall;

/**
 * Definiert eine gemeinsame Schnittstelle für Ganglinien.
 * 
 * @author BitrCtrl, Schumann
 * @version $Id: Ganglinie.java 2394 2007-07-20 12:33:36Z Schumann $
 * @param <T>
 *            der Typ des Wertes eines St&uuml;tzstelle.
 */
public interface IGanglinie<T> {

	/**
	 * Gibt die Anzahl der St&uuml;tzstellen der Ganglinie zur&uuml;ck.
	 * 
	 * @return St&uuml;tzstellenanzahl
	 */
	int anzahlStuetzstellen();

	/**
	 * Pr&uuml;ft ob zu einem Zeitstempel eine reale St&uuml;tzstelle existiert.
	 * 
	 * @param zeitstempel
	 *            Ein Zeitstempel
	 * @return {@code true}, wenn die Ganglinie eine St&uuml;tzstelle zum
	 *         Zeitpunkt speichert und {@code false}, wenn zu dem Zeitpunkt die
	 *         St&uuml;tzstelle berechnet werden muss
	 */
	boolean existsStuetzstelle(long zeitstempel);

	/**
	 * Pr&uuml;ft ob ein Zeitstempel im Definitionsbereich der Ganglinie liegt.
	 * 
	 * @param zeitstempel
	 *            zu pr&uuml;fender Zeitstempel
	 * @return <code>true</code>, wenn <code>zeitstempel</code> im
	 *         definierten Bereich der Ganglinie liegt
	 * @see #getIntervalle()
	 */
	boolean isValid(long zeitstempel);

	/**
	 * Gibt die St&uuml;tzstelle zu einem bestimmten Zeitpunkt zur&uuml;ck. Es
	 * wird die mit der Approximation berechnete St&uuml;tzstelle ausgeliefert.
	 * Wurde keine Approximation festgelegt ({@code getApproximation() == null}),
	 * dann wird die real existierende St&uuml;tzstelle ausgeliefert. Existiert
	 * zum angefragten Zeitpunkt keine St&uuml;tzstelle, wird {@code null}
	 * zur&uuml;ckgegegeben.
	 * 
	 * @param zeitstempel
	 *            Der Zeitstempel zu dem eine St&uuml;tzstelle gesucht wird.
	 * @return Die gesuchte St&uuml;tzstelle oder {@code null}, wenn keine
	 *         berechnet werden konnte und keine existiert.
	 */
	Stuetzstelle<T> getStuetzstelle(long zeitstempel);

	/**
	 * Gibt ein sortiertes Feld der existierenden St&uuml;tzstellen
	 * zur&uuml;ck;.
	 * 
	 * @return Nach Zeitstempel sortiere St&uuml;tzstellen
	 */
	List<Stuetzstelle<T>> getStuetzstellen();

	/**
	 * Die existierenden St&uuml;tzstellen im angegebenen Intervall zur&uuml;ck.
	 * 
	 * @param intervall
	 *            ein zeitliches Intervall.
	 * @return die Liste der St&uuml;tzstellen im Intervall, sortiert nach
	 *         Zeitstempel.
	 */
	List<Stuetzstelle<T>> getStuetzstellen(Intervall intervall);

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
	boolean setStuetzstelle(long zeitstempel, T wert);

	/**
	 * Nimmt eine St&uuml;tzstelle in die Ganglinie auf. Existiert zu dem
	 * Zeitpunkt bereits eine, wird diese &uuml;berschrieben.
	 * 
	 * @param s
	 *            Die neue Stu&uuml;tzstelle
	 * @return {@code true}, wenn die St&uuml;tzstelle neu angelegt wurde und
	 *         {@code false}, wenn eine vorhandene St&uuml;tzstelle ersetzt
	 *         wurde.
	 */
	boolean setStuetzstelle(Stuetzstelle<T> s);

	/**
	 * Entfernt eine St&uuml;tzstelle.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel der St&uuml;tzstelle, die entfernt werden soll
	 */
	void remove(long zeitstempel);

	/**
	 * Gibt das Zeitintervall der Ganglinie zur&uuml;ck.
	 * 
	 * @return Ein {@link Intervall} oder {@code null}, wenn keine
	 *         St&uuml;tzstellen vorhanden sind
	 */
	Intervall getIntervall();

	/**
	 * Bestimmt die Intervalle in denen die Ganglinie definiert ist.
	 * 
	 * @return Liste von Intervallen
	 */
	List<Intervall> getIntervalle();

	/**
	 * Gibt {@code false} zur&uuml;ck, wenn die Approximation aktuallisiert
	 * werden muss, weil sich die Ganglinie ge&auml;ndert hat.
	 * 
	 * @return {@code true}, wenn Ganglinie und Approximation konform gehen und
	 *         {@code false}, wenn die Approximation aktualisiert werden muss.
	 */
	boolean isApproximationAktuell();

	/**
	 * Aktualisiert die Approximation. Muss bei &Auml;nderung an den
	 * St&uuml;tzstellen der Ganglinie aufgerufen werden.
	 */
	void aktualisiereApproximation();

	/**
	 * Die Ganglinie als Approximation zu&uuml;ck.
	 * 
	 * @return die Approximation der Ganglinie oder {@code null}, wenn keine
	 *         Approximation festgelegt wurde.
	 */
	Approximation getApproximation();

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
	void setApproximation(Approximation approximation);

}
