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

import static de.bsvrz.iav.gllib.gllib.intern.GLKonstanten.MILLIS_PER_SEKUNDE;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.MessQuerschnittAllgemein;

/**
 * Repr&auml;sentiert einen allgemeinen Messquerschnitt mit Parameter der
 * Ganglinienprognose.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class GlMessQuerschnittAllgemein extends MessQuerschnittAllgemein
		implements GanglinienModellPrognoseParameter {

	/** Die Auswahlmethode, wenn Pattern-Matching nicht geht. */
	private int auswahlMethode;

	/** Intervall in die Vergangenheit, was ber&uuml;cksichtigt wird. */
	private long matchingIntervall;

	/** Maximale Dauer einer zyklischen Prognose. */
	private long maxDauerZyklischePrognose;

	/** Maximaler Fehler beim Pattern-Matching. */
	private int maxMatchingFehler;

	/** Der Zeitraum der mittelfristigen Prognose. */
	private long patternMatchingHorizont;

	/** Offset um den beim Pattern-Matching verschoben wird. */
	private long patternMatchingOffset;

	/**
	 * Ruft den Superkonstroktur auf.
	 * 
	 * @param obj
	 *            ein Systemobjekt, welches vom Typ MessQuerschnittAllgemein
	 *            sein muss.
	 */
	public GlMessQuerschnittAllgemein(SystemObject obj) {
		super(obj);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getAuswahlMethode() {
		return auswahlMethode;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getMatchingIntervall() {
		return matchingIntervall;
	}

	/**
	 * {@inheritDoc}
	 */

	public long getMaxDauerZyklischePrognose() {
		return maxDauerZyklischePrognose;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getMaxMatchingFehler() {
		return maxMatchingFehler;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getPatternMatchingHorizont() {
		return patternMatchingHorizont;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getPatternMatchingOffset() {
		return patternMatchingOffset;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDaten(Data daten) {
		if (!daten.getName().equals(ATG_GANGLINIEN_MODELL_PROGNOSE)) {
			throw new IllegalArgumentException(
					"Das Datum hat den falschen Typ.");
		}

		auswahlMethode = daten.getUnscaledValue("GLAuswahlMethode").intValue();
		patternMatchingHorizont = daten.getUnscaledValue(
				"GLPatternMatchingHorizont").longValue()
				* MILLIS_PER_SEKUNDE;
		matchingIntervall = daten.getUnscaledValue("GLMatchingIntervall")
				.longValue()
				* MILLIS_PER_SEKUNDE;
		patternMatchingOffset = daten.getUnscaledValue(
				"GLPatternMatchingOffset").longValue()
				* MILLIS_PER_SEKUNDE;
		maxMatchingFehler = daten.getUnscaledValue("GLMaxMatchingFehler")
				.intValue();
		maxDauerZyklischePrognose = daten.getUnscaledValue(
				"GLMaxDauerZyklischePrognose").longValue()
				* MILLIS_PER_SEKUNDE;
	}

}
