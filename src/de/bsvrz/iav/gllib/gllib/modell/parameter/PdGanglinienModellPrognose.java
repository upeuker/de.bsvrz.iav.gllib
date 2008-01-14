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

package de.bsvrz.iav.gllib.gllib.modell.parameter;

import static de.bsvrz.sys.funclib.bitctrl.util.Konstanten.MILLIS_PER_SEKUNDE;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.DataModel;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatum;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractParameterDatensatz;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.MessQuerschnittAllgemein;

/**
 * Kapselt die Parameterattributgruppe {@code atg.ganglinienModellPrognose}.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class PdGanglinienModellPrognose extends
		AbstractParameterDatensatz<PdGanglinienModellPrognose.Daten> {

	/**
	 * Kapselt die Daten des Datensatzes.
	 */
	public class Daten extends AbstractDatum {

		/** Auswahlmethode: Auswahl der Referenzganglinie. */
		public static final int REFERENZGANGLINIE = 1;

		/** Auswahlmethode: Auswahl der wahrscheinlichsten Ganglinie. */
		public static final int WAHRSCHEINLICHSTE_GANGLINIE = 2;

		/** Das Flag f&uuml;r die G&uuml;ltigkeit des Datensatzes. */
		private boolean valid;

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
		 * {@inheritDoc}
		 * 
		 * @see de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatum#clone()
		 */
		@Override
		public Daten clone() {
			Daten klon = new Daten();

			klon.valid = valid;
			klon.auswahlMethode = auswahlMethode;
			klon.matchingIntervall = matchingIntervall;
			klon.maxDauerZyklischePrognose = maxDauerZyklischePrognose;
			klon.maxMatchingFehler = maxMatchingFehler;
			klon.patternMatchingHorizont = patternMatchingHorizont;
			klon.patternMatchingOffset = patternMatchingOffset;
			klon.setZeitstempel(getZeitstempel());

			return klon;
		}

		/**
		 * Gibt den Wert der Eigenschaft {@code auswahlMethode} wieder.
		 * 
		 * @return {@code auswahlMethode}.
		 */
		public int getAuswahlMethode() {
			return auswahlMethode;
		}

		/**
		 * Gibt den Wert der Eigenschaft {@code matchingIntervall} wieder.
		 * 
		 * @return {@code matchingIntervall}.
		 */
		public long getMatchingIntervall() {
			return matchingIntervall;
		}

		/**
		 * Gibt den Wert der Eigenschaft {@code maxDauerZyklischePrognose}
		 * wieder.
		 * 
		 * @return {@code maxDauerZyklischePrognose}.
		 */
		public long getMaxDauerZyklischePrognose() {
			return maxDauerZyklischePrognose;
		}

		/**
		 * Gibt den Wert der Eigenschaft {@code maxMatchingFehler} wieder.
		 * 
		 * @return {@code maxMatchingFehler}.
		 */
		public int getMaxMatchingFehler() {
			return maxMatchingFehler;
		}

		/**
		 * Gibt den Wert der Eigenschaft {@code patternMatchingHorizont} wieder.
		 * 
		 * @return {@code patternMatchingHorizont}.
		 */
		public long getPatternMatchingHorizont() {
			return patternMatchingHorizont;
		}

		/**
		 * Gibt den Wert der Eigenschaft {@code patternMatchingOffset} wieder.
		 * 
		 * @return {@code patternMatchingOffset}.
		 */
		public long getPatternMatchingOffset() {
			return patternMatchingOffset;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see de.bsvrz.sys.funclib.bitctrl.modell.Datum#isValid()
		 */
		public boolean isValid() {
			return valid;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code auswahlMethode} fest.
		 * 
		 * @param auswahlMethode
		 *            der neue Wert von {@code auswahlMethode}.
		 */
		public void setAuswahlMethode(int auswahlMethode) {
			this.auswahlMethode = auswahlMethode;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code matchingIntervall} fest.
		 * 
		 * @param matchingIntervall
		 *            der neue Wert von {@code matchingIntervall}.
		 */
		public void setMatchingIntervall(long matchingIntervall) {
			this.matchingIntervall = matchingIntervall;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code maxDauerZyklischePrognose} fest.
		 * 
		 * @param maxDauerZyklischePrognose
		 *            der neue Wert von {@code maxDauerZyklischePrognose}.
		 */
		public void setMaxDauerZyklischePrognose(long maxDauerZyklischePrognose) {
			this.maxDauerZyklischePrognose = maxDauerZyklischePrognose;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code maxMatchingFehler} fest.
		 * 
		 * @param maxMatchingFehler
		 *            der neue Wert von {@code maxMatchingFehler}.
		 */
		public void setMaxMatchingFehler(int maxMatchingFehler) {
			this.maxMatchingFehler = maxMatchingFehler;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code patternMatchingHorizont} fest.
		 * 
		 * @param patternMatchingHorizont
		 *            der neue Wert von {@code patternMatchingHorizont}.
		 */
		public void setPatternMatchingHorizont(long patternMatchingHorizont) {
			this.patternMatchingHorizont = patternMatchingHorizont;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code patternMatchingOffset} fest.
		 * 
		 * @param patternMatchingOffset
		 *            der neue Wert von {@code patternMatchingOffset}.
		 */
		public void setPatternMatchingOffset(long patternMatchingOffset) {
			this.patternMatchingOffset = patternMatchingOffset;
		}

		/**
		 * Setzt das Flag {@code valid} des Datum.
		 * 
		 * @param valid
		 *            der neue Wert des Flags.
		 */
		protected void setValid(boolean valid) {
			this.valid = valid;
		}

	}

	/** Die PID der Attributgruppe. */
	public static final String ATG_GANGLINIEN_MODELL_PROGNOSE = "atg.ganglinienModellPrognose";

	/** Die Attributgruppe kann von allen Instanzen gemeinsam genutzt werden. */
	private static AttributeGroup atg;

	/**
	 * Initialisiert den Parameter.
	 * 
	 * @param mq
	 *            ein Messquerschnitt
	 */
	public PdGanglinienModellPrognose(MessQuerschnittAllgemein mq) {
		super(mq);

		if (atg == null) {
			DataModel modell = ObjektFactory.getInstanz().getVerbindung()
					.getDataModel();
			atg = modell.getAttributeGroup(ATG_GANGLINIEN_MODELL_PROGNOSE);
			assert atg != null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.sys.funclib.bitctrl.modell.Datensatz#erzeugeDatum()
	 */
	public Daten erzeugeDatum() {
		return new Daten();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.sys.funclib.bitctrl.modell.Datensatz#getAttributGruppe()
	 */
	public AttributeGroup getAttributGruppe() {
		return atg;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.sys.funclib.bitctrl.modell.Datensatz#setDaten(de.bsvrz.dav.daf.main.ResultData)
	 */
	public void setDaten(ResultData result) {
		check(result);

		Daten datum = new Daten();
		if (result.hasData()) {
			Data daten = result.getData();

			datum.setAuswahlMethode(daten.getUnscaledValue("GLAuswahlMethode")
					.intValue());
			datum.setPatternMatchingHorizont(daten.getUnscaledValue(
					"GLPatternMatchingHorizont").longValue()
					* MILLIS_PER_SEKUNDE);
			datum.setMatchingIntervall(daten.getUnscaledValue(
					"GLMatchingIntervall").longValue()
					* MILLIS_PER_SEKUNDE);
			datum.setPatternMatchingOffset(daten.getUnscaledValue(
					"GLPatternMatchingOffset").longValue()
					* MILLIS_PER_SEKUNDE);
			datum.setMaxMatchingFehler(daten.getUnscaledValue(
					"GLMaxMatchingFehler").intValue());
			datum.setMaxDauerZyklischePrognose(daten.getUnscaledValue(
					"GLMaxDauerZyklischePrognose").longValue()
					* MILLIS_PER_SEKUNDE);

			datum.setValid(true);
		} else {
			datum.setValid(false);
		}

		datum.setZeitstempel(result.getDataTime());
		setDatum(result.getDataDescription().getAspect(), datum);
		fireDatensatzAktualisiert(result.getDataDescription().getAspect(),
				datum.clone());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatensatz#konvertiere(de.bsvrz.sys.funclib.bitctrl.modell.Datum)
	 */
	@Override
	protected Data konvertiere(Daten datum) {
		Data daten = erzeugeSendeCache();

		daten.getUnscaledValue("GLAuswahlMethode").set(
				datum.getAuswahlMethode());
		daten.getUnscaledValue("GLPatternMatchingHorizont").set(
				datum.getPatternMatchingHorizont() / MILLIS_PER_SEKUNDE);
		daten.getUnscaledValue("GLMatchingIntervall").set(
				datum.getMatchingIntervall() / MILLIS_PER_SEKUNDE);
		daten.getUnscaledValue("GLPatternMatchingOffset").set(
				datum.getPatternMatchingOffset() / MILLIS_PER_SEKUNDE);
		daten.getUnscaledValue("GLMaximalerMatchingFehler").set(
				datum.getMaxMatchingFehler());
		daten.getUnscaledValue("GLMaximaleDauerZyklischePrognose").set(
				datum.getMaxDauerZyklischePrognose() / MILLIS_PER_SEKUNDE);

		return daten;
	}
}
