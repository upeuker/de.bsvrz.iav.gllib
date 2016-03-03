/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.5 Funktionen Ganglinie
 * Copyright (C) 2011-2015 BitCtrl Systems GmbH
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

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import com.bitctrl.util.Interval;

/**
 * Schnittstelle f�r alle Approximationsmethoden von Ganglinien.
 * Implementierende Klassen sollten einen parameterlosen Konstruktor besitzen.
 *
 * @param <T>
 *            der Typ der Approximation.
 *
 * @author BitCtrl Systems GmbH, Falko Schumann
 */
public interface Approximation<T> {

	/**
	 * Gibt die St�tzstelle zum angegebenen Zeitstempel zur�ck.
	 *
	 * @param zeitstempel
	 *            Zeitstempel
	 * @return Wert als St�tzstelle
	 */
	Stuetzstelle<T> get(long zeitstempel);

	/**
	 * Gibt das Intervall zur�ck, in dem die Approximation definiert ist.
	 *
	 * @return das Intervall der Approximation.
	 */
	Interval getIntervall();

	/**
	 * Gibt die Liste der St�tzstellen zur�ck, die der Approximation zu Grunde
	 * liegen.
	 *
	 * @return die unver�nderliche Liste der St�tzstellen.
	 */
	List<Stuetzstelle<T>> getStuetzstellen();

	/**
	 * F�hrt notwendige Initialisierungsarbeiten der Approximation aus.
	 */
	void initialisiere();

	/**
	 * Bestimmt das Integrall �ber ein Intervall der Approximation.
	 *
	 * @param intervall
	 *            ein Intervall.
	 * @return das Integral bzw. der Fl�cheninhalt unter der Kurve im Intervall.
	 */
	double integral(Interval intervall);

	/**
	 * Gibt eine Interpolation der Approximation zur�ck. N�tzlich f�r die
	 * grafische Darstellung von Ganglinien, indem in einem festen Abstand
	 * St�tzstellen berechnet werden, die als Polygonzug darstellbar sind.
	 *
	 * @param intervallBreite
	 *            Die gew�nschte Breite der Intervalle
	 * @return Nach Zeitstempel sortierte Liste der St�tzstellen
	 * @throws IllegalArgumentException
	 *             Wenn die Intervallbreite kleiner oder gleich 0 ist
	 */
	SortedSet<Stuetzstelle<T>> interpoliere(long intervallBreite);

	/**
	 * Legt die St�tzstellen der Approximation fest.
	 *
	 * @param stuetzstellen
	 *            die Menge der bekannten St�tzstellen.
	 */
	void setStuetzstellen(Collection<Stuetzstelle<T>> stuetzstellen);

}
