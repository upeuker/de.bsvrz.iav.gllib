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

package de.bsvrz.iav.gllib.gllib.intern;

import java.util.ArrayList;
import java.util.List;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.Data.ReferenceArray;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.EreignisTyp;

/**
 * Standardimplementierung der Schnittstelle.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class GanglinienModellAutomatischesLernenEreignisParameterImpl implements
		GanglinienModellAutomatischesLernenEreignisParameter {

	private final List<EreignisTyp> ausschlussliste;
	private final List<EreignisTyp> bezugsereignistypen;
	private int darstellungsverfahren;
	private int ganglinienTyp;
	private long matchingIntervallNach;
	private long matchingIntervallVor;
	private long matchingSchrittweite;
	private int maxAbstand;
	private long maxGanglinien;
	private int maxMatchingFehler;
	private int maxWichtungsfaktor;
	private long vergleichsSchrittweite;

	public GanglinienModellAutomatischesLernenEreignisParameterImpl() {
		ausschlussliste = new ArrayList<EreignisTyp>();
		bezugsereignistypen = new ArrayList<EreignisTyp>();
	}

	/**
	 * Gibt den Wert der Eigenschaft ausschlussliste wieder.
	 * 
	 * @return the ausschlussliste
	 */
	public List<EreignisTyp> getAusschlussliste() {
		return ausschlussliste;
	}

	/**
	 * Gibt den Wert der Eigenschaft bezugsereignistypen wieder.
	 * 
	 * @return the bezugsereignistypen
	 */
	public List<EreignisTyp> getBezugsereignistypen() {
		return bezugsereignistypen;
	}

	/**
	 * Gibt den Wert der Eigenschaft darstellungsverfahren wieder.
	 * 
	 * @return the darstellungsverfahren
	 */
	public int getDarstellungsverfahren() {
		return darstellungsverfahren;
	}

	/**
	 * Gibt den Wert der Eigenschaft ganglinienTyp wieder.
	 * 
	 * @return the ganglinienTyp
	 */
	public int getGanglinienTyp() {
		return ganglinienTyp;
	}

	/**
	 * Gibt den Wert der Eigenschaft matchingIntervallNach wieder.
	 * 
	 * @return the matchingIntervallNach
	 */
	public long getMatchingIntervallNach() {
		return matchingIntervallNach;
	}

	/**
	 * Gibt den Wert der Eigenschaft matchingIntervallVor wieder.
	 * 
	 * @return the matchingIntervallVor
	 */
	public long getMatchingIntervallVor() {
		return matchingIntervallVor;
	}

	/**
	 * Gibt den Wert der Eigenschaft matchingSchrittweite wieder.
	 * 
	 * @return the matchingSchrittweite
	 */
	public long getMatchingSchrittweite() {
		return matchingSchrittweite;
	}

	/**
	 * Gibt den Wert der Eigenschaft maxAbstand wieder.
	 * 
	 * @return the maxAbstand
	 */
	public int getMaxAbstand() {
		return maxAbstand;
	}

	/**
	 * Gibt den Wert der Eigenschaft maxGanglinien wieder.
	 * 
	 * @return the maxGanglinien
	 */
	public long getMaxGanglinien() {
		return maxGanglinien;
	}

	/**
	 * Gibt den Wert der Eigenschaft maxMatchingFehler wieder.
	 * 
	 * @return the maxMatchingFehler
	 */
	public int getMaxMatchingFehler() {
		return maxMatchingFehler;
	}

	/**
	 * Gibt den Wert der Eigenschaft maxWichtungsfaktor wieder.
	 * 
	 * @return the maxWichtungsfaktor
	 */
	public int getMaxWichtungsfaktor() {
		return maxWichtungsfaktor;
	}

	/**
	 * Gibt den Wert der Eigenschaft vergleichsSchrittweite wieder.
	 * 
	 * @return the vergleichsSchrittweite
	 */
	public long getVergleichsSchrittweite() {
		return vergleichsSchrittweite;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.iav.gllib.gllib.intern.GanglinienModellAutomatischesLernenEreignisParameter#setDaten(de.bsvrz.dav.daf.main.Data)
	 */
	public void setDaten(Data daten) {
		ReferenceArray feld;

		ausschlussliste.clear();
		feld = daten.getReferenceArray("AlgAusschlussliste");
		for (int i = 0; i < feld.getLength(); i++) {
			ausschlussliste.add((EreignisTyp) ObjektFactory
					.getModellobjekt(feld.getSystemObject(i)));
		}

		bezugsereignistypen.clear();
		feld = daten.getReferenceArray("AlgBezugsereignistypen");
		for (int i = 0; i < feld.getLength(); i++) {
			bezugsereignistypen.add((EreignisTyp) ObjektFactory
					.getModellobjekt(feld.getSystemObject(i)));
		}

		darstellungsverfahren = daten.getUnscaledValue(
				"AlgDarstellungsverfahren").intValue();
		ganglinienTyp = daten.getUnscaledValue("AlgGanglinienTyp").intValue();
		matchingIntervallNach = daten.getUnscaledValue(
				"AlgMatchingIntervallNach").longValue() * 1000;
		matchingIntervallVor = daten
				.getUnscaledValue("AlgMatchingIntervallVor").longValue() * 1000;
		matchingSchrittweite = daten
				.getUnscaledValue("AlgMatchingSchrittweite").longValue() * 1000;
		maxAbstand = daten.getUnscaledValue("AlgMaxAbstand").intValue();
		maxGanglinien = daten.getUnscaledValue("AlgMaxGanglinien").longValue();
		maxMatchingFehler = daten.getUnscaledValue("AlgMaxMatchingFehler")
				.intValue();
		maxWichtungsfaktor = daten.getUnscaledValue("AlgMaxWichtungsfaktor")
				.intValue();
		vergleichsSchrittweite = daten.getUnscaledValue(
				"AlgVergleichsSchrittweite").longValue() * 1000;
	}

}
