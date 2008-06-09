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

import java.util.ResourceBundle;
import java.util.logging.Level;

import de.bsvrz.sys.funclib.bitctrl.daf.LogNachricht;
import de.bsvrz.sys.funclib.debug.Debug;
import de.bsvrz.sys.funclib.operatingMessage.MessageGrade;

/**
 * Versorgt die Applikation mit lokalisierten Benutzernachrichten.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id: GlLibMsg.java 8834 2008-05-09 15:44:46Z Schumann $
 */
public enum GlLibMsg implements LogNachricht {

	/** Nicht für Log-Ausgaben vorgesehen. */
	Applikationsname(null),

	/** Nicht für Log-Ausgaben vorgesehen. */
	ApplikationsnameKurz(null),

	/** Parameter: 1) Exception. */
	ErrorAnmeldenSendenAnfrage(Debug.ERROR),

	/** Parameter: keine. */
	InfoGlProgBereit(Debug.INFO),

	/** Parameter: 1) der Listener. */
	FineGlProgNeuerListener(Debug.FINE),

	/** Parameter: 1) der Listener. */
	FineGlLibListenerEntfernt(Debug.FINE),

	/** Parameter: 1) Absenderzeichen. */
	FineGlProgAnfrageGesendet(Debug.FINE),

	/** Parameter: 1) das Ereignis. */
	FineGlProgAntwortVerteilt(Debug.FINE);

	/** Das Resource-Bundle. */
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(GlLibMsg.class.getCanonicalName());

	/** Die Eigenschaft {@code level}. */
	private final Level level;

	/**
	 * Initialisiert das Objekt.
	 * 
	 * @param level
	 *            der Log-Level.
	 */
	private GlLibMsg(final Level level) {
		this.level = level;
	}

	/**
	 * {@inheritDoc}
	 */
	public MessageGrade getBmvLevel() {
		if (level.equals(Debug.WARNING)) {
			return MessageGrade.WARNING;
		} else if (level.equals(Debug.ERROR)) {
			return MessageGrade.ERROR;
		}
		return null;

	}

	/**
	 * {@inheritDoc}
	 */
	public Level getLogLevel() {
		return level;
	}

	/**
	 * {@inheritDoc}
	 */
	public ResourceBundle getResourceBundle() {
		return RESOURCE_BUNDLE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getResourceBundle().getString(name());
	}

}
