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
 * Wei�enfelser Stra�e 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

import de.bsvrz.sys.funclib.bitctrl.i18n.Messages;

/**
 * Repr&auml;sentiert eine allgemeine St&uuml;tzstelle f&uuml;r Ganglinien
 * bestehend aus Zeitstempel und Wert. Die St&uuml;tzstellen k&ouml;nnen nach
 * den Zeitstempeln sortiert werden. Ist der Wert einer St&uuml;tzstelle
 * <em>undefiniert</em> ({@code null}), so ist auch das Intervall bis zur
 * vorherigen und n&auml;chsten St&uuml;tzstelle <em>undefiniert</em>.
 * <p>
 * <strong>Hinweis</strong>: Die nat&uuml;rliche Ordnung der St&uuml;tzstellen
 * ist <em>nicht</em> konsistent mit der Gleichheit. Zwei St&uuml;tzstellen
 * sind gleich, wenn sie in Zeitstempel und Wert &uuml;bereinstimmen. Die
 * nat&uuml;rliche Ordung hingegen bassiert ausschlie&szlig;lich auf den
 * Zeitstempeln und ignoriert die Werte.
 * 
 * @author BitCtrl, Schumann
 * @version $Id: Stuetzstelle.java 160 2007-02-23 15:09:31Z Schumann $
 */
public class Stuetzstelle implements Comparable<Stuetzstelle> {

	/** Der Messwert. */
	public final Integer wert;

	/** Zeitpunkt des Messwerts. */
	public final long zeitstempel;

	/**
	 * Initialisierung. F&uuml;r den Wert wird <code>null</code>
	 * (=undefiniert) angenommen.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel
	 */
	public Stuetzstelle(long zeitstempel) {
		this(zeitstempel, null);
	}

	/**
	 * Zuweisungskonstruktor.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel
	 * @param wert
	 *            Wert oder {@code null} f&uuml;r "undefiniert"
	 */
	public Stuetzstelle(long zeitstempel, Integer wert) {
		this.zeitstempel = zeitstempel;
		this.wert = wert;
	}

	/**
	 * Gibt den Zeitstempel der St&uuml;tzstelle zur&uuml;ck.
	 * 
	 * @return Zeitstempel
	 */
	public long getZeitstempel() {
		return zeitstempel;
	}

	/**
	 * Gibt den Wert der St&uuml;tzstelle zur&uuml;ck.
	 * 
	 * @return Wert oder {@code null} f&uuml;r "undefiniert"
	 */
	public Integer getWert() {
		return wert;
	}

	/**
	 * Eine St&uuml;tzstelle ist kleiner bzw gr&ouml;&szlig;er, wenn der
	 * Zeitstempel kleiner bzw gr&ouml;&szlig;er ist.
	 * 
	 * @param stuetzstelle
	 *            Eine St&uuml;tzstelle zum Vergleichen
	 * @return -1, 0 oder +1, wenn der Zeitstempel dieser St&uuml;tzstelle
	 *         kleiner, gleich oder gr&ouml;&szlig;er als die St&uuml;tzstelle
	 *         im Parameter ist
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Stuetzstelle stuetzstelle) {
		if (zeitstempel < stuetzstelle.zeitstempel) {
			return -1;
		} else if (zeitstempel > stuetzstelle.zeitstempel) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Zwei St&uuml;tzstellen sind identisch, wenn Zeitstempel und Wert
	 * identisch sind.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Stuetzstelle) {
			Stuetzstelle s;
			s = (Stuetzstelle) obj;
			return (zeitstempel == s.zeitstempel && wert == s.wert);
		}

		return false;
	}

	/**
	 * Gibt ein Tupel (Zeitstempel, Wert) zur&uuml;ck.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return Messages.get(GlLibMessages.Node) + "(" + zeitstempel + " => "
				+ wert + ")";
	}

}