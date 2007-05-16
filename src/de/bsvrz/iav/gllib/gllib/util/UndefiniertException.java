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

package de.bsvrz.iav.gllib.gllib.util;

/**
 * Beschreibt eine Ausnahme, die eintritt, wenn ein Wert angefragt wird, der
 * undefiniert ist.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class UndefiniertException extends Exception {

	/** Version f&uuml;r der Serialisierung. */
	private static final long serialVersionUID = 1L;

	/**
	 * Legt die Standardfehlernachricht fest.
	 */
	public UndefiniertException() {
		super("Der angefragte Wert ist undefiniert.");
	}

	/**
	 * Ruft den Superkonstruktor auf.
	 * 
	 * @param message
	 *            Eine Nachricht die die Ausnahme beschreibt
	 * @param cause
	 *            Der Grund der Ausnahme
	 * @see java.lang.Exception
	 */
	public UndefiniertException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Ruft den Superkonstruktor auf.
	 * 
	 * @param message
	 *            Eine Nachricht die die Ausnahme beschreibt
	 * @see java.lang.Exception
	 */
	public UndefiniertException(String message) {
		super(message);

	}

	/**
	 * Ruft den Superkonstruktor auf.
	 * 
	 * @param cause
	 *            Der Grund der Ausnahme
	 * @see java.lang.Exception
	 */

	public UndefiniertException(Throwable cause) {
		super(cause);

	}

}
