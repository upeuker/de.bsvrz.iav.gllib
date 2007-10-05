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
 * Weiﬂenfelser Straﬂe 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib.intern;

import de.bsvrz.dav.daf.main.Data;

/**
 * Repr&auml;sentiert die Prognoseparameter eines Messquerschnitts.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public interface GanglinienModellPrognoseParameter {

	/** Die PID der Parameterattributgruppe. */
	String ATG_GANGLINIEN_MODELL_PROGNOSE = "atg.ganglinienModellPrognose";

	/** Datenverteilerkonstante f&uuml;r den Zustand "Referenzganglinie". */
	int AUSWAHL_REFERENZ = 1;

	/**
	 * Datenverteilerkonstante f&uuml;r den Zustand "wahrscheinlichste
	 * Ganglinie".
	 */
	int AUSWAHL_WAHRSCHEINLICHKEIT = 2;

	/**
	 * Gibt die Ausweichverfahren zur&uuml;ck, falls das Pattern-Matching
	 * mangels Analysedaten nicht durchf&uuml;hrbar ist oder kein Ergebnis
	 * liefert.
	 * 
	 * @return Mˆgliche Angaben sind {@link #AUSWAHL_REFERENZ Referenzganglinie}
	 *         und
	 *         {@link #AUSWAHL_WAHRSCHEINLICHKEIT WahrscheinlichsteGanglinie}.
	 */
	int getAuswahlMethode();

	/**
	 * Gibt den Zeitraum zur&uuml;ck in dem, ausgehend vom aktuellen Zeitpunkt
	 * in die Vergangenheit zur&uuml;ck, Analysewerte in die Prognose
	 * einflieﬂen.
	 * 
	 * @return der zu ber&uuml;cksichtigende Zeitraum in Millisekunden der
	 *         Analysedaten in der Vergangenheit.
	 */
	long getMatchingIntervall();

	/**
	 * Gibt den maximalen Zeitraum zur&uuml;ck, in dem eine zyklischen Prognose
	 * neue Daten sendet.
	 * 
	 * @return der maximale Zeitraum einer zyklischen Prognose in Millisekunden.
	 */
	long getMaxDauerZyklischePrognose();

	/**
	 * Gibt den maximalen Abstand zweier Ganglinien beim Pattern-Matching
	 * zur&uuml;ck.
	 * 
	 * @return der maximale Abstand in Prozent.
	 */
	int getMaxMatchingFehler();

	/**
	 * Gibtdas Ende des mittelfristigen Prognosehorizont nach dem aktuellen
	 * Zeitpunkt zur&uuml;ck.
	 * 
	 * @return der mittelfristige Prognosehorizont in Millisekunden.
	 */
	long getPatternMatchingHorizont();

	/**
	 * Gibt das Intervall zur&uuml;ck, um den eine Ganglinien beim
	 * Pattern-Matching nach vorn und hinten verschoben werden.
	 * 
	 * @return der Offset beim Pattern-Matching in Millisekunden.
	 */
	long getPatternMatchingOffset();

	/**
	 * Setzt den Inhalt eines Datums als internen Zustand.
	 * 
	 * @param daten
	 *            ein Datum, welches dem Parameter entsprechen muss.
	 */
	void setDaten(Data daten);

}
