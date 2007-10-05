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

package de.bsvrz.iav.gllib.gllib.intern;

import java.util.List;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.EreignisTyp;

/**
 * Repr&auml;sentiert den Parameter f&uuml;r das automatische Ganglinienlernen
 * der pro Ereignistyp parametriert wird.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public interface GanglinienModellAutomatischesLernenEreignisParameter {

	/** Die PID der Parameterattributgruppe. */
	String ATG_PARAMETER = "atg.ganglinienModellAutomatischesLernenEreignis";

	/**
	 * Gibt eine Liste von Ereignistypen zur&uuml;ck, die beim Lernen nicht
	 * gleichzeitig anstehen dürfen.
	 * 
	 * @return Liste von Ereignistypen.
	 */
	List<EreignisTyp> getAusschlussliste();

	/**
	 * Gibt eine Liste von Ereignistypen zur&uuml;ck, auf die sich eine relative
	 * Ganglinie beziehen kann. Wird für absolute Ganglinien ignoriert.
	 * 
	 * @return eine Liste von Ereignistypen.
	 */
	List<EreignisTyp> getBezugsereignistypen();

	/**
	 * Gibt das zu verwendete Approximationsverfahren der zu genierenden
	 * Ganglinie fest.
	 * 
	 * @return eines der Verfahren
	 *         {@link de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#TYP_ABSOLUT},
	 *         {@link de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#TYP_ADDITIV} oder
	 *         {@link de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#TYP_MULTIPLIKATIV}.
	 */
	int getDarstellungsverfahren();

	/**
	 * Gibt den Typ der zu lernenden Ganglinien zur&uuml;ck.
	 * 
	 * @return einer der Werte
	 *         {@link de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#APPROX_BSPLINE},
	 *         {@link de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#APPROX_CUBICSPLINE},
	 *         {@link de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#APPROX_POLYLINE}
	 *         oder
	 *         {@link de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#APPROX_UNBESTIMMT}.
	 */
	int getGanglinienTyp();

	/**
	 * Gibt die Zeit nach dem Lernzeitraum zur&uuml;ck, der ebenfalls beim
	 * Lernen einbezogen werden soll.
	 * 
	 * @return zu ber&uuml;cksichtigender Zeitraum nach dem Lernzeitraum.
	 */
	long getMatchingIntervallNach();

	/**
	 * Gibt die Zeit vor dem Lernzeitraum zur&uuml;ck, der ebenfalls beim Lernen
	 * einbezogen werden soll.
	 * 
	 * @return zu ber&uuml;cksichtigender Zeitraum vor dem Lernzeitraum.
	 */
	long getMatchingIntervallVor();

	/**
	 * Gibt die zu verwendende Schrittweite beim Pattern-Matching zur&uuml;ck.
	 * 
	 * @return die zuverwende Schritte beim Pattern-Matching.
	 */
	long getMatchingSchrittweite();

	/**
	 * Gibt in Prozent den maximalen Abstand der Analyseganglinie zu einer
	 * historischen Ganglinie beim komplexen Abstandsverfahren zur&uuml;ck.
	 * 
	 * @return der maximaler Abstand in Prozent beim komplexen
	 *         Abstandsverfahren.
	 */
	int getMaxAbstand();

	/**
	 * Gibt die maximale Anzahl von Ganglinien pro Ereignistyp zur&uuml;ck.
	 * 
	 * @return die maximale Ganglinienanzahl pro Ereignistyp.
	 */
	long getMaxGanglinien();

	/**
	 * Gibt in Prozent den maximalen Abstand der Analyseganglinie zu einer
	 * historischen Ganglinie beim Pattern-Matching zur&uuml;ck.
	 * 
	 * @return der maximaler Abstand in Prozent beim Pattern-Matching.
	 */
	int getMaxMatchingFehler();

	/**
	 * Gibt das maximale Gewicht einer historischen Ganglinie beim Verschmelzen
	 * zur&uuml;ck.
	 * 
	 * @return das maximale Gewicht einer historischen Ganglinie.
	 */
	int getMaxWichtungsfaktor();

	/**
	 * Gibt die Schrittweite für das komplexe Abstandsverfahren zur&uuml;ck.
	 * 
	 * @return die Schrittweite f&uuml;r das Pattern-Matching.
	 */
	long getVergleichsSchrittweite();

	/**
	 * &Uuml;bernimmt die Werte aus einem Datum als inneren Zustand.
	 * 
	 * @param daten
	 *            ein Datum, welches dem Parameter entsprechen muss.
	 */
	void setDaten(Data daten);

}
