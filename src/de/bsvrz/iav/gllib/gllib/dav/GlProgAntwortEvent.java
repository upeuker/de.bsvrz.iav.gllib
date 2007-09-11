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

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.Data.Array;
import de.bsvrz.dav.daf.main.config.ClientApplication;
import de.bsvrz.dav.daf.main.config.SystemObject;

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
@SuppressWarnings("serial")
public class GlProgAntwortEvent extends EventObject {

	/** Die anfragende Applikation. */
	protected final ClientApplication anfrager;

	/** Eine beliebige Zeichenkette die der Absender frei eingetragen kann. */
	protected String absenderZeichen;

	/** Hash zum einfachen auffinden der passenden Ganglinie. */
	protected final Map<SystemObject, GanglinieMQ> prognosen;

	/**
	 * Initialisiert interne Felder.
	 * 
	 * @param quelle
	 *            die Quelle des Events.
	 * @param anfrager
	 *            die anfragende Applikation.
	 */
	public GlProgAntwortEvent(Object quelle, ClientApplication anfrager) {
		super(quelle);
		this.anfrager = anfrager;
		prognosen = new HashMap<SystemObject, GanglinieMQ>();
	}

	/**
	 * Gibt die anfragende Applikation zur&uuml;ck.
	 * 
	 * @return Referenz auf die anfragende Applikation.
	 */
	public ClientApplication getAnfrager() {
		return anfrager;
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
	public Collection<SystemObject> getMessquerschnitte() {
		return prognosen.keySet();
	}

	/**
	 * Gibt die prognostizierte Ganglinie zu einem Messquerschnitt zur&uuml;ck.
	 * 
	 * @param mq
	 *            ein Messquerschnitt.
	 * @return die Prognoseganglinie des Messquerschnitts.
	 */
	public GanglinieMQ getPrognose(SystemObject mq) {
		GanglinieMQ ganglinie;

		ganglinie = prognosen.get(mq);
		if (ganglinie == null) {
			throw new NoSuchElementException(
					"Für den Messquerschnitt wurde keine Prognoseganglinie angefragt.");
		}
		return ganglinie;
	}

	/**
	 * Extrahiert die enthaltenen Informationen aus den &uuml;bergebenen Daten.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der öffentlichen API und
	 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
	 * 
	 * @param daten
	 *            die auszulesenden Daten.
	 */
	public void setDaten(Data daten) {
		Array feld;

		absenderZeichen = daten.getTextValue("AbsenderZeichen").getText();

		feld = daten.getArray("PrognoseGanglinie");
		for (int i = 0; i < feld.getLength(); i++) {
			GanglinieMQ g;

			g = new GanglinieMQ();
			g.setDatenVonPrognoseGanglinie(feld.getItem(i));
			prognosen.put(g.getMessQuerschnitt(), g);
		}
	}

	/**
	 * Setzt das Absenderzeichen. In der Regel wird dieses lediglich aus der
	 * Anfrage in die Antwort kopiert.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der öffentlichen API und
	 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
	 * 
	 * @param absenderZeichen
	 *            ein beliebiger Text.
	 */
	public void setAbsenderZeichen(String absenderZeichen) {
		this.absenderZeichen = absenderZeichen;
	}

	/**
	 * F&uuml;gt der Antwort eine Prognoseganglinie hinzu.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der öffentlichen API und
	 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
	 * 
	 * @param ganglinie
	 *            eine Prognoseganglinie.
	 */
	public void addGanglinie(GanglinieMQ ganglinie) {
		prognosen.put(ganglinie.getMessQuerschnitt(), ganglinie);
	}

	/**
	 * Baut aus den Informationen der Antwort einen Datensatz. Das Ergebnis wird
	 * im Parameter abgelegt!
	 * 
	 * @param daten
	 *            ein Datum, welches eine Antwortnachricht darstellt.
	 */
	public void getDaten(Data daten) {
		Array feld;
		int i;

		daten.getTextValue("AbsenderZeichen").setText(absenderZeichen);
		feld = daten.getArray("PrognoseGanglinie");
		feld.setLength(prognosen.size());
		i = 0;
		for (GanglinieMQ g : prognosen.values()) {
			g.getDaten(feld.getItem(i));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getClass().getName() + "=" + source + ", " + absenderZeichen;
	}

}
