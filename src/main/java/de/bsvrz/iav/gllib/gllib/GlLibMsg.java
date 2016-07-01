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
 */
public enum GlLibMsg implements LogNachricht {

	/** Nicht für Log-Ausgaben vorgesehen. */
	Applikationsname,

	/** Nicht für Log-Ausgaben vorgesehen. */
	ApplikationsnameKurz,

	/** Parameter: 1) Name des Parameters, der ungültig ist. */
	IllegalArgumentExceptionNull,

	/** Parameter: 1) Name des Parameters, der ungültig ist. */
	IllegalArgumentExceptionGroesserNull,

	/** Parameter: 1) Name des Parameters, der ungültig ist. */
	IllegalArgumentExceptionGroesserGleichNull,

	/** Parameter: 1) Name des Parameters, der ungültig ist. */
	IllegalArgumentExceptionLeereListe,

	/** Parameter: keine. */
	IllegalArgumentExceptionBSplineOrdnung,

	/** Parameter: keine. */
	IllegalArgumentExceptionKeineGanglinienPrognose,

	/** Parameter: keine. */
	IllegalArgumentExceptionGanglinienUeberschneidenSich,

	/** Parameter: keine. */
	IllegalArgumentExceptionMin2Stuetzstellen,

	/** Parameter: keine. */
	IllegalArgumentExceptionUnterschiedlicheApproximation,

	/** Parameter: keine. */
	IllegalStateExceptionKeineApproximation,

	/** Parameter: keine. */
	IllegalStateExceptionKeinGanglinienSpeicher,

	/** Parameter: keine. */
	IllegalStateExceptionGanglinienSpeicherTypUndefiniert,

	/** Parameter: keine. */
	GlSpeicherServerExceptionTimeout,

	/** Parameter: 1) Exception. */
	ErrorAnmeldenSendenAnfrage(Debug.ERROR),

	/** Parameter: 1) PID des MQs, 2) Exception. */
	ErrorSerialisieren(Debug.ERROR),

	/** Parameter: 1) PID des MQs, 2) Exception. */
	ErrorDeserialisieren(Debug.ERROR),

	/** Parameter: keine. */
	WarningGanglinienSpeicherMehrdeutig(Debug.WARNING),

	/** Parameter: 1) Anfragedaten, 2) der MQ, 3) die Exception. */
	WarningAntwortKonnteNichtGesendetWerden(Debug.WARNING),

	/** Parameter: keine. */
	InfoGlProgBereit(Debug.INFO),

	/** Parameter: 1) der Ganglinienspeicher. */
	InfoGanglinienSpeicher(Debug.INFO),

	/** Paramater: 1) gewünschte Ordnung, 2) angepasste Ordnung. */
	InfoOrdnungGroesserAlsAnzahlStuetzstellen(Debug.INFO),

	/** Parameter: 1) der Listener. */
	FineGlProgNeuerListener(Debug.FINE),

	/** Parameter: 1) der Listener. */
	FineGlLibListenerEntfernt(Debug.FINE),

	/** Parameter: 1) Absenderzeichen. */
	FineGlProgAnfrageGesendet(Debug.FINE),

	/** Parameter: 1) das Ereignis. */
	FineGlProgAntwortVerteilt(Debug.FINE);

	/** Das Resource-Bundle. */
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(GlLibMsg.class.getCanonicalName());

	/** Die Eigenschaft {@code level}. */
	private final Level level;

	private GlLibMsg() {
		this(null);
	}

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
	@Override
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
	@Override
	public Level getLogLevel() {
		return level;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
