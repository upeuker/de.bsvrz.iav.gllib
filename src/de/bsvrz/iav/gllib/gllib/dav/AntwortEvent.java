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

import stauma.dav.clientside.Data;
import stauma.dav.clientside.Data.Array;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.MessQuerschnitt;

/**
 * Repr&auml;sentiert eine Antwortnachricht der Ganglinienprognose. Enthalten
 * sind f&uuml;r alle angefragten Messquerschnitte die prognostizuierten
 * Ganglinien.
 * <p>
 * Hinweis: Dieses Event ist nicht Serialisierbar, da enthaltene Objekt nicht
 * serialisierbar sind.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class AntwortEvent extends EventObject {

	/** Eine beliebige Zeichenkette die der Absender frei eingetragen kann. */
	private String absenderZeichen;

	/** Hash zum einfachen auffinden der passenden Ganglinie. */
	private final Map<MessQuerschnitt, GanglinieMQ> prognosen;

	/**
	 * Initialisiert interne Felder.
	 * 
	 * @param quelle
	 *            die Quelle des Events.
	 */
	public AntwortEvent(Object quelle) {
		super(quelle);
		prognosen = new HashMap<MessQuerschnitt, GanglinieMQ>();
	}

	/**
	 * Gibt das Zeichen des Absenders zur&uuml;ck. Der Text wurde bei der
	 * Anfrage in die Anfragenachricht eingetragen und von der
	 * Ganglinienprognose in die Antwort kopiert. Somit kann die anfragende
	 * Applikation mehrere Anfragen unterscheiden.
	 * 
	 * @return das Absenderzeichen.
	 */
	public String getAbsenderZeichen() {
		return absenderZeichen;
	}

	/**
	 * Gibt die Menge der Messquerschnitte zur&uuml;ck, f&uuml;r die Ganglinien
	 * prognostiziert wurden.
	 * 
	 * @return eine Menge von Messquerschnitten.
	 */
	public Collection<MessQuerschnitt> getMessquerschnitte() {
		return prognosen.keySet();
	}

	/**
	 * Gibt die prognostizierte Ganglinie zu einem Messquerschnitt zur&uuml;ck.
	 * 
	 * @param mq
	 *            ein Messquerschnitt.
	 * @return die Prognoseganglinie des Messquerschnitts.
	 */
	public GanglinieMQ getPrognose(MessQuerschnitt mq) {
		GanglinieMQ g;

		g = prognosen.get(mq);
		if (g == null) {
			throw new NoSuchElementException(
					"Für den Messquerschnitt wurde keine Prognoseganglinie angefragt.");
		}
		return g;
	}

	/**
	 * Extrahiert die enthaltenen Informationen aus den &uuml;bergebenen Daten.
	 * 
	 * @param daten
	 *            die auszulesenden Daten.
	 */
	void setDaten(Data daten) {
		Array feld;

		absenderZeichen = daten.getTextValue("AbsenderZeichen").getText();

		feld = daten.getArray("PrognoseGanglinie");
		for (int i = 0; i < feld.getLength(); i++) {
			GanglinieMQ g;

			g = new GanglinieMQ();
			g.setDaten(feld.getItem(i));
			prognosen.put(g.getMessQuerschnitt(), g);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "=" + source + ", "
				+ absenderZeichen;
	}

}
