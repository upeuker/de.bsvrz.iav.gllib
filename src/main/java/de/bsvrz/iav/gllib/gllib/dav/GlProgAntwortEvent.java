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

package de.bsvrz.iav.gllib.gllib.dav;

import java.util.Collections;
import java.util.EventObject;
import java.util.List;

import de.bsvrz.iav.gllib.gllib.modell.onlinedaten.OdPrognoseGanglinienAntwort;

/**
 * Repräsentiert eine Antwortnachricht der Ganglinienprognose. Enthalten sind
 * für alle angefragten Messquerschnitte die prognostizuierten Ganglinien.
 * <p>
 * Hinweis: Dieses Event ist nicht Serialisierbar, da enthaltene Objekt nicht
 * serialisierbar sind.
 *
 * @author BitCtrl Systems GmbH, Schumann
 */
public class GlProgAntwortEvent extends EventObject {

	/** Die ID für die Serialisierung. */
	private static final long serialVersionUID = 1L;

	/** Eine beliebige Zeichenkette die der Absender frei eingetragen kann. */
	private final String absenderZeichen;

	/** Die Liste der Prognoseganglinien in der Antwort. */
	private final List<GanglinieMQ> prognosen;

	/**
	 * Initialisiert interne Felder.
	 *
	 * @param quelle
	 *            die Quelle des Events.
	 * @param datum
	 *            die anfragende Applikation.
	 */
	GlProgAntwortEvent(final Object quelle,
			final OdPrognoseGanglinienAntwort.Daten datum) {
		super(quelle);
		absenderZeichen = datum.getAbsenderZeichen();
		prognosen = Collections.unmodifiableList(datum);
	}

	/**
	 * Gibt das Zeichen des Absenders zurück. Der Text wurde bei der Anfrage in
	 * die Anfragenachricht eingetragen und von der Ganglinienprognose in die
	 * Antwort kopiert. Somit kann die anfragende Applikation mehrere Anfragen
	 * unterscheiden.
	 *
	 * @return das Absenderzeichen.
	 */
	public String getAbsenderZeichen() {
		return absenderZeichen;
	}

	/**
	 * Gibt die Menge der prognostizierten Ganglinien zurück.
	 * <p>
	 * <em>Hinweis:</em> Es werden nicht alle Eigenschaften der Ganglinie in der
	 * Prognoseganglinie gesetzt. Gesetzt werden nur die Stütztstellen, das
	 * Approximationsverfahren und der Messquerschnitt.
	 *
	 * @return eine Menge von Ganglinien.
	 */
	public List<GanglinieMQ> getGanglinien() {
		return prognosen;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String s = getClass().getName() + "["; //$NON-NLS-1$

		s += "source=" + source; //$NON-NLS-1$
		s += ", absenderZeichen" + absenderZeichen; //$NON-NLS-1$
		s += ", prognosen=" + prognosen; //$NON-NLS-1$

		return s + "]"; //$NON-NLS-1$
	}

}
