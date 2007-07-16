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

import java.util.Collection;
import java.util.SortedSet;

/**
 * Schnittstelle f&uuml;r alle Approximationsmethoden von Ganglinien.
 * Implementierende Klassen sollten einen parameterlosen Konstruktor besitzen.
 * <p>
 * TODO: Undefinierte Intervalle innerhalb der Ganglinie berücksichtigen
 * 
 * @author BitCtrl, Schumann
 * @version $Id$
 * @param <T>
 *            der Typ der approximierten Werte.
 */
public interface Approximation<T> {

	/**
	 * F&uuml;hrt notwendige Initialisierungsarbeiten der Approximation aus.
	 */
	void initialisiere();

	/**
	 * Legt die St&uuml;tzstellen der Approximation fest.
	 * 
	 * @param menge
	 *            die Menge der bekannten St&uuml;tzstellen.
	 */
	void setStuetzstellen(Collection<Stuetzstelle<T>> menge);

	/**
	 * Gibt die St&uuml;tzstelle zum angegebenen Zeitstempel zur&uuml;ck.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel
	 * @return Wert als St&uuml;tzstelle
	 */
	Stuetzstelle<T> get(long zeitstempel);

	/**
	 * Gibt eine Interpolation der Approximation zur&uuml;ck. N&uuml;tzlich
	 * f&uuml;r die grafische Darstellung von Ganglinien, indem in einem festen
	 * Abstand St&uuml;tzstellen berechnet werden, die als Polygonzug
	 * darstellbar sind.
	 * 
	 * @param intervallBreite
	 *            Die gew&uuml;nschte Breite der Intervalle
	 * @return Nach Zeitstempel sortierte Liste der St&uuml;tzstellen
	 * @throws IllegalArgumentException
	 *             Wenn die Intervallbreite kleiner oder gleich 0 ist
	 */
	SortedSet<Stuetzstelle<T>> interpoliere(long intervallBreite);

	/**
	 * Erzeugt eine Kopie der Approximation.
	 * 
	 * @return die kopierte Approximation.
	 * @throws CloneNotSupportedException
	 *             wenn das Klonen nicht unterst&uuml;tzt wird.
	 */
	Approximation<T> clone() throws CloneNotSupportedException;

}
