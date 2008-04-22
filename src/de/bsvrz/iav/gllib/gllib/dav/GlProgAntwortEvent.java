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

package de.bsvrz.iav.gllib.gllib.dav;

import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import de.bsvrz.iav.gllib.gllib.modell.onlinedaten.OdPrognoseGanglinienAntwort;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.objekte.MessQuerschnittAllgemein;

/**
 * Repräsentiert eine Antwortnachricht der Ganglinienprognose. Enthalten sind
 * für alle angefragten Messquerschnitte die prognostizuierten Ganglinien.
 * <p>
 * Hinweis: Dieses Event ist nicht Serialisierbar, da enthaltene Objekt nicht
 * serialisierbar sind.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class GlProgAntwortEvent extends EventObject {

	/** Die Eigenschaft {@code serialVersionUID}. */
	private static final long serialVersionUID = 1L;

	/** Eine beliebige Zeichenkette die der Absender frei eingetragen kann. */
	private final String absenderZeichen;

	/** Hash zum einfachen auffinden der passenden Ganglinie. */
	private final Map<MessQuerschnittAllgemein, GanglinieMQ> prognosen;

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
		prognosen = new HashMap<MessQuerschnittAllgemein, GanglinieMQ>();
		for (final GanglinieMQ g : datum) {
			prognosen.put(g.getMessQuerschnitt(), g);
		}
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
	 * <em>Hinweis:</em> Es werden nicht alle Eigenschaften der Ganglinie in
	 * der Prognoseganglinie gesetzt. Gesetzt werden nur die Stütztstellen, das
	 * Approximationsverfahren und der Messquerschnitt.
	 * 
	 * @return eine Menge von Ganglinien.
	 */
	public Collection<GanglinieMQ> getGanglinien() {
		return prognosen.values();
	}

	/**
	 * Gibt die Menge der Messquerschnitte zurück, für die Ganglinien
	 * prognostiziert wurden.
	 * 
	 * @return eine Menge von Messquerschnitten.
	 */
	public Set<MessQuerschnittAllgemein> getMessquerschnitte() {
		return prognosen.keySet();
	}

	/**
	 * Gibt die prognostizierte Ganglinie zu einem Messquerschnitt zurück.
	 * <p>
	 * <em>Hinweis:</em> Es werden nicht alle Eigenschaften der Ganglinie in
	 * der Prognoseganglinie gesetzt. Gesetzt werden nur die Stütztstellen, das
	 * Approximationsverfahren und der Messquerschnitt.
	 * 
	 * @param mq
	 *            ein Messquerschnitt.
	 * @return die Prognoseganglinie des Messquerschnitts.
	 */
	public GanglinieMQ getPrognose(final MessQuerschnittAllgemein mq) {
		GanglinieMQ ganglinie;

		ganglinie = prognosen.get(mq);
		if (ganglinie == null) {
			throw new NoSuchElementException(
					"Für den Messquerschnitt wurde keine Prognoseganglinie angefragt.");
		}
		return ganglinie;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String s = getClass().getName() + "[";

		s += "source=" + source;
		s += ", absenderZeichen" + absenderZeichen;
		s += ", prognosen=" + prognosen;

		return s + "]";
	}

}
