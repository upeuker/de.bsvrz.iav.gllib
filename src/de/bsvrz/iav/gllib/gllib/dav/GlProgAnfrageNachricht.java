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
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.Data.Array;
import de.bsvrz.dav.daf.main.config.ClientApplication;
import de.bsvrz.dav.daf.main.config.SystemObject;

/**
 * Repr&auml;sentuiert eine Anfrage an die Ganglinienprognose.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class GlProgAnfrageNachricht {

	/** Die anfragende Applikation. */
	protected ClientApplication absender;

	/** Eine beliebige Zeichenkette die der Absender frei eingetragen kann. */
	protected String absenderZeichen;

	/** Liste der Anfragen in dieser Nachricht. */
	protected final Map<SystemObject, GlProgAnfrage> anfragen;

	/**
	 * Konstruktor f&uuml;r Vererbung.
	 */
	protected GlProgAnfrageNachricht() {
		anfragen = new HashMap<SystemObject, GlProgAnfrage>();
	}

	/**
	 * Konstruiert aus dem Datensatz ein Anfragenachrichtobjekt.
	 * 
	 * @param absender
	 *            die Applikation, die die Anfrage stellt.
	 * @param absenderZeichen
	 *            ein beliebiger Text, den die Applikation zum unterscheiden der
	 *            Antworten benutzen kann.
	 * 
	 */
	public GlProgAnfrageNachricht(ClientApplication absender,
			String absenderZeichen) {
		this();
		this.absender = absender;
		this.absenderZeichen = absenderZeichen;
	}

	/**
	 * F&uuml;gt der Anfragenachricht eine Prognoseanfrage hinzu.
	 * 
	 * @param anfrage
	 *            eine Anfrage.
	 */
	public void add(GlProgAnfrage anfrage) {
		anfragen.put(anfrage.getMq(), anfrage);
	}

	/**
	 * Gibt die anfragende Applikation zur&uuml;ck.
	 * 
	 * @return Die Applikation als Systemobjekt
	 */
	public ClientApplication getAbsender() {
		return absender;
	}

	/**
	 * Gibt die Zeichenkette zur&uuml;ck, die der Absender in der Nachricht frei
	 * eintragen darf.
	 * 
	 * @return Eine Zeichenkette
	 */
	public String getAbsenderZeichen() {
		return absenderZeichen;
	}

	/**
	 * Gibt die Anzahl der Anfragen dieser Nachricht zur&uuml;ck.
	 * 
	 * @return Anfragenanzahl
	 */
	public int getAnzahlAnfragen() {
		return anfragen.size();
	}

	/**
	 * Gibt eine bestimmte Anfrage zur&uuml;ck.
	 * 
	 * @param index
	 *            Index der gesuchten Anfrage
	 * @return Die Anfrage zum Index
	 */
	public GlProgAnfrage getAnfrage(int index) {
		return anfragen.get(index);
	}

	/**
	 * Gibt die Menge der Messquerschnitte zur&uuml;ck, f&uuml;r die Ganglinien
	 * prognostiziert wurden.
	 * 
	 * @return eine Menge von Messquerschnitten.
	 */
	public Collection<SystemObject> getMessquerschnitte() {
		return anfragen.keySet();
	}

	/**
	 * Gibt die prognostizierte Ganglinie zu einem Messquerschnitt zur&uuml;ck.
	 * 
	 * @param mq
	 *            ein Messquerschnitt.
	 * @return die Prognoseganglinie des Messquerschnitts.
	 */
	public GlProgAnfrage getAnfrage(SystemObject mq) {
		GlProgAnfrage anfrage;

		anfrage = anfragen.get(mq);
		if (anfrage == null) {
			throw new NoSuchElementException(
					"Für den Messquerschnitt wurde keine Prognoseganglinie angefragt.");
		}
		return anfrage;
	}

	/**
	 * Baut aus den Informationen der Anfragen einen Datensatz.
	 * <p>
	 * Hinweis: Das Ergebnis wird auch im Parameter abgelegt!
	 * 
	 * @param daten
	 *            ein Datum, welches eine (leere) Anfragenachricht darstellt.
	 * @return das ausgef&uuml;llte Datum.
	 */
	protected Data getDaten(Data daten) {
		Array feld;
		int i;

		daten.getReferenceValue("absenderId").setSystemObject(absender);
		daten.getTextValue("AbsenderZeichen").setText(absenderZeichen);

		feld = daten.getArray("PrognoseGanglinienAnfrage");
		feld.setLength(anfragen.size());
		i = 0;
		for (GlProgAnfrage anfrage : anfragen.values()) {
			anfrage.getDaten(feld.getItem(i));
		}

		return daten;
	}

	/**
	 * &Uuml;bernimmt die Informationen aus dem Datum als inneren Zustand.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der öffentlichen API und
	 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
	 * 
	 * @param daten
	 *            ein Datum, welches eine Anfrage darstellt.
	 */
	public void setDaten(Data daten) {
		Array feld;

		absender = (ClientApplication) daten.getReferenceValue("absenderId")
				.getSystemObject();
		absenderZeichen = daten.getTextValue("AbsenderZeichen").getText();

		feld = daten.getArray("PrognoseGanglinienAnfrage");
		for (int i = 0; i < feld.getLength(); i++) {
			GlProgAnfrage anfrage;

			anfrage = new GlProgAnfrage();
			anfrage.setDaten(feld.getItem(i));
			anfragen.put(anfrage.getMq(), anfrage);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Anfrage von " + absender + " mit Absenderzeichen: \""
				+ absenderZeichen + "\"";
	}

}
