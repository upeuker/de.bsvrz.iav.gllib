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

import com.bitctrl.util.Timestamp;

/**
 * Repräsentiert eine allgemeine Stützstelle für Ganglinien bestehend aus
 * Zeitstempel und Wert. Die Stützstellen können nach den Zeitstempeln sortiert
 * werden. Ist der Wert einer Stützstelle <em>undefiniert</em> ({@code null}),
 * so ist auch das Intervall bis zur vorherigen und nächsten Stützstelle
 * <em>undefiniert</em>.
 * <p>
 * <strong>Hinweis:</strong> Die natürliche Ordnung der Stützstellen ist
 * <em>nicht</em> konsistent mit der Gleichheit. Zwei Stützstellen sind gleich,
 * wenn sie in Zeitstempel und Wert übereinstimmen. Die natürliche Ordung
 * hingegen bassiert ausschließlich auf den Zeitstempeln und ignoriert die
 * Werte.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 * @param <T>
 *            der Typ des Wertes der Stützstelle.
 */
public class Stuetzstelle<T> implements Comparable<Stuetzstelle<T>> {

	/** Der Wert an der Stützstelle. */
	private final T wert;

	/** Zeitpunkt der Stützstelle. */
	private final long zeitstempel;

	/**
	 * Initialisierung. Für den Wert wird <code>null</code> (=undefiniert)
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
	 *            Wert oder {@code null} für "undefiniert"
	 */
	public Stuetzstelle(final long zeitstempel, final T wert) {
		this.zeitstempel = zeitstempel;
		this.wert = wert;
	}

	/**
	 * Eine Stützstelle ist kleiner bzw größer, wenn der Zeitstempel kleiner bzw
	 * größer ist.
	 * 
	 * @param stuetzstelle
	 *            Eine Stützstelle zum Vergleichen
	 * @return -1, 0 oder +1, wenn der Zeitstempel dieser Stützstelle kleiner,
	 *         gleich oder größer als die Stützstelle im Parameter ist
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
	 * Zwei Stützstellen sind identisch, wenn beide den selben Zeitstempel und
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
	 * Gibt den Wert der Stützstelle zurück.
	 * 
	 * @return Wert oder {@code null} für "undefiniert"
	 */
	public T getWert() {
		return wert;
	}

	/**
	 * Gibt den Zeitstempel der Stützstelle zurück.
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
