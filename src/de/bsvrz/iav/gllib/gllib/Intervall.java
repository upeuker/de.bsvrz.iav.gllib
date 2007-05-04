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
 * Weiﬂenfelser Straﬂe 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

import de.bsvrz.sys.funclib.bitctrl.i18n.Messages;

/**
 * Repr&auml;sentiert ein Intervall f&uuml;r <code>long</code>-Werte. Wird
 * f&uuml;r Zeitintervalle genutzt, die mit Zeitstempeln arbeiten.
 * 
 * @author BitCtrl, Schumann
 * @version $Id$
 */
public class Intervall {

	/** Startzeitpunkt des Intervall. */
	public final long start;

	/** Endzeitpunkt des Intervall. */
	public final long ende;

	/**
	 * Konstruiert das Intervall mit dem angegebenen Grenzen.
	 * 
	 * @param start
	 *            Start des Intervalls
	 * @param ende
	 *            Ende des Intervalls
	 */
	public Intervall(long start, long ende) {
		if (start > ende) {
			throw new IllegalArgumentException(Messages.get(
					GlLibMessages.BadIntervall, start, ende));
		}

		this.start = start;
		this.ende = ende;
	}

	/**
	 * Gibt den Anfang des Intervalls zur&uuml;ck.
	 * 
	 * @return Zeitstempel
	 */
	public long getStart() {
		return start;
	}

	/**
	 * Gibt das Ende des Intervalls zur&uuml;ck.
	 * 
	 * @return Zeitstempel
	 */
	public long getEnde() {
		return ende;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "[" + start + ", " + ende + "]";
	}

}
