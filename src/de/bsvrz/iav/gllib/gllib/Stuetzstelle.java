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

import com.bitctrl.util.Timestamp;

/**
 * Repr�sentiert eine allgemeine St�tzstelle f�r Ganglinien bestehend aus
 * Zeitstempel und Wert. Die St�tzstellen k�nnen nach den Zeitstempeln sortiert
 * werden. Ist der Wert einer St�tzstelle <em>undefiniert</em> ({@code null}),
 * so ist auch das Intervall bis zur vorherigen und n�chsten St�tzstelle
 * <em>undefiniert</em>.
 * <p>
 * <strong>Hinweis:</strong> Die nat�rliche Ordnung der St�tzstellen ist
 * <em>nicht</em> konsistent mit der Gleichheit. Zwei St�tzstellen sind gleich,
 * wenn sie in Zeitstempel und Wert �bereinstimmen. Die nat�rliche Ordung
 * hingegen bassiert ausschlie�lich auf den Zeitstempeln und ignoriert die
 * Werte.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 * @param <T>
 *            der Typ des Wertes der St�tzstelle.
 */
public class Stuetzstelle<T> implements Comparable<Stuetzstelle<T>> {

	/** Der Wert an der St�tzstelle. */
	private final T wert;

	/** Zeitpunkt der St�tzstelle. */
	private final long zeitstempel;

	/**
	 * Initialisierung. F�r den Wert wird <code>null</code> (=undefiniert)
	 * angenommen.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel
	 */
	public Stuetzstelle(final long zeitstempel) {
		this(zeitstempel, null);
	}

	/**
	 * Zuweisungskonstruktor.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel
	 * @param wert
	 *            Wert oder {@code null} f�r "undefiniert"
	 */
	public Stuetzstelle(final long zeitstempel, final T wert) {
		this.zeitstempel = zeitstempel;
		this.wert = wert;
	}

	/**
	 * Eine St�tzstelle ist kleiner bzw gr��er, wenn der Zeitstempel kleiner bzw
	 * gr��er ist.
	 * 
	 * @param stuetzstelle
	 *            Eine St�tzstelle zum Vergleichen
	 * @return -1, 0 oder +1, wenn der Zeitstempel dieser St�tzstelle kleiner,
	 *         gleich oder gr��er als die St�tzstelle im Parameter ist
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final Stuetzstelle<T> stuetzstelle) {
		if (zeitstempel < stuetzstelle.zeitstempel) {
			return -1;
		} else if (zeitstempel > stuetzstelle.zeitstempel) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Zwei St�tzstellen sind identisch, wenn beide den selben Zeitstempel und
	 * Wert haben.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Stuetzstelle<?>) {
			Stuetzstelle<?> s;

			s = (Stuetzstelle<?>) obj;
			if (wert != null) {
				return zeitstempel == s.zeitstempel && wert.equals(s.wert);
			}
			return zeitstempel == s.zeitstempel && wert == s.wert;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Long.valueOf(zeitstempel).hashCode() ^ wert.hashCode();
	}

	/**
	 * Gibt den Wert der St�tzstelle zur�ck.
	 * 
	 * @return Wert oder {@code null} f�r "undefiniert"
	 */
	public T getWert() {
		return wert;
	}

	/**
	 * Gibt den Zeitstempel der St�tzstelle zur�ck.
	 * 
	 * @return Zeitstempel
	 */
	public long getZeitstempel() {
		return zeitstempel;
	}

	@Override
	public String toString() {
		return getClass().getName() + "[zeitstempel="
				+ Timestamp.absoluteTime(zeitstempel) + ", wert=" + wert + "]";
	}

}
