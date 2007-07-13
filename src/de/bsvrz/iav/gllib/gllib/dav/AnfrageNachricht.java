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

package de.bsvrz.iav.gllib.gllib.dav;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import stauma.dav.clientside.Data;
import stauma.dav.clientside.Data.Array;
import stauma.dav.configuration.interfaces.ClientApplication;

/**
 * Repr&auml;sentuiert eine Anfrage an die Ganglinienprognose.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class AnfrageNachricht {

	/** Die anfragende Applikation. */
	private final ClientApplication absender;

	/** Eine beliebige Zeichenkette die der Absender frei eingetragen kann. */
	private final String absenderZeichen;

	/** Liste der Anfragen in dieser Nachricht. */
	private final List<Anfrage> anfragen;

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
	public AnfrageNachricht(ClientApplication absender, String absenderZeichen) {
		this.absender = absender;
		this.absenderZeichen = absenderZeichen;
		anfragen = new ArrayList<Anfrage>();
	}

	/**
	 * F&uuml;gt der Anfragenachricht eine Prognoseanfrage hinzu.
	 * 
	 * @param anfrage
	 *            eine Anfrage.
	 */
	public void add(Anfrage anfrage) {
		anfragen.add(anfrage);
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
	public Anfrage getAnfrage(int index) {
		return anfragen.get(index);
	}

	/**
	 * Gibt einen Iterator &uuml;ber die Anfragen zur&uuml;ck.
	 * 
	 * @return Anfrageniterator
	 */
	public Iterator<Anfrage> getAnfragenIterator() {
		return anfragen.listIterator();
	}

	/**
	 * Baut aus den Informationen der Anfragen einen Datensatz. Das Ergebnis
	 * wird im Parameter abgelegt!
	 * 
	 * @param daten
	 *            ein Datum, welches eine Anfragenachricht darstellt.
	 */
	void setDaten(Data daten) {
		Array feld;

		daten.getReferenceValue("absenderId").setSystemObject(absender);
		daten.getTextValue("AbsenderZeichen").setText(absenderZeichen);

		feld = daten.getArray("PrognoseGanglinienAnfrage");
		feld.setLength(anfragen.size());
		for (int i = 0; i < anfragen.size(); i++) {
			anfragen.get(i).getDaten(feld.getItem(i));
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return absender + ", " + absenderZeichen;
	}

}
