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

package de.bsvrz.iav.gllib.gllib.modell;

import static de.bsvrz.sys.funclib.bitctrl.util.Konstanten.MILLIS_PER_SEKUNDE;

import java.util.ArrayList;
import java.util.List;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.Data.ReferenceArray;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.DataModel;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatum;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractParameterDatensatz;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.EreignisTyp;

/**
 * Kapselt die Parameterattributgruppe
 * {@code atg.ganglinienModellAutomatischesLernenEreignis}.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class PdGanglinienModellAutomatischesLernenEreignis extends
		AbstractParameterDatensatz<PdGanglinienModellAutomatischesLernenEreignis.Daten> {

	/**
	 * Kapselt die Daten des Datensatzes.
	 */
	public class Daten extends AbstractDatum {

		/** Das Flag f&uuml;r die G&uuml;ltigkeit des Datensatzes. */
		private boolean valid;

		/** Die Liste der Ausschlussereignistypen. */
		private final List<EreignisTyp> ausschlussliste = new ArrayList<EreignisTyp>();

		/** Die Liste der Bezugsereignistypen (nur f&uuml;r relative Ganglinien). */
		private final List<EreignisTyp> bezugsereignistypen = new ArrayList<EreignisTyp>();

		/**
		 * Das Darstellungsverfahren der Ganglinie.
		 * 
		 * @see de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#APPROX_BSPLINE
		 * @see de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#APPROX_CUBICSPLINE
		 * @see de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#APPROX_POLYLINE
		 * @see de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#APPROX_UNBESTIMMT
		 */
		private int darstellungsverfahren;

		/**
		 * Der Typ der Ganglinie.
		 * 
		 * @see de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#TYP_ABSOLUT
		 * @see de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#TYP_ADDITIV
		 * @see de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#TYP_MULTIPLIKATIV
		 */
		private int ganglinienTyp;

		/** Abstand der St&uuml;tzstellen in generierten Ganglinien. */
		private long stuetzstellenAbstand;

		/**
		 * Das Intervall nach der Ganglinie, welches beim Pattern-Matching
		 * einbezogen wird.
		 */
		private long matchingIntervallNach;

		/**
		 * Das Intervall vor der Ganglinie, welches beim Pattern-Matching
		 * einbezogen wird.
		 */
		private long matchingIntervallVor;

		/** Die Schrittweite die beim Pattern-Matching benutzt wird. */
		private long matchingSchrittweite;

		/** Der maximale Abstand, bei dem Ganglinien verschmolzen werden. */
		private int maxAbstand;

		/** Die maximale Anzahl von Ganglinien f&uuml;r den Ereignistyp. */
		private long maxGanglinien;

		/** Der maximal erlaubte Fehler beim Pattern-Matching. */
		private int maxMatchingFehler;

		/**
		 * Der maximale Wichtungsfaktor der historischen Ganglinie beim
		 * Verschmelzen.
		 */
		private int maxWichtungsfaktor;

		/**
		 * Die Schrittweite beim Vergleichen mittels komplexer
		 * Abstandsberechnugn.
		 */
		private long vergleichsSchrittweite;

		/**
		 * {@inheritDoc}
		 * 
		 * @see de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatum#clone()
		 */
		@Override
		public Daten clone() {
			Daten klon = new Daten();

			klon.valid = valid;
			klon.ausschlussliste.addAll(ausschlussliste);
			klon.bezugsereignistypen.addAll(bezugsereignistypen);
			klon.darstellungsverfahren = darstellungsverfahren;
			klon.ganglinienTyp = ganglinienTyp;
			klon.matchingIntervallNach = matchingIntervallNach;
			klon.matchingIntervallVor = matchingIntervallVor;
			klon.matchingSchrittweite = matchingSchrittweite;
			klon.maxAbstand = maxAbstand;
			klon.maxGanglinien = maxGanglinien;
			klon.maxMatchingFehler = maxMatchingFehler;
			klon.maxWichtungsfaktor = maxWichtungsfaktor;
			klon.stuetzstellenAbstand = stuetzstellenAbstand;
			klon.vergleichsSchrittweite = vergleichsSchrittweite;
			klon.setZeitstempel(getZeitstempel());

			return klon;
		}

		/**
		 * Gibt den Wert der Eigenschaft {@code ausschlussliste} wieder.
		 * 
		 * @return {@code ausschlussliste}.
		 */
		public List<EreignisTyp> getAusschlussliste() {
			return ausschlussliste;
		}

		/**
		 * Gibt den Wert der Eigenschaft {@code bezugsereignistypen} wieder.
		 * 
		 * @return {@code bezugsereignistypen}.
		 */
		public List<EreignisTyp> getBezugsereignistypen() {
			return bezugsereignistypen;
		}

		/**
		 * Gibt den Wert der Eigenschaft {@code darstellungsverfahren} wieder.
		 * 
		 * @return {@code darstellungsverfahren}.
		 */
		public int getDarstellungsverfahren() {
			return darstellungsverfahren;
		}

		/**
		 * Gibt den Wert der Eigenschaft {@code ganglinienTyp} wieder.
		 * 
		 * @return {@code ganglinienTyp}.
		 */
		public int getGanglinienTyp() {
			return ganglinienTyp;
		}

		/**
		 * Gibt den Wert der Eigenschaft {@code matchingIntervallNach} wieder.
		 * 
		 * @return {@code matchingIntervallNach}.
		 */
		public long getMatchingIntervallNach() {
			return matchingIntervallNach;
		}

		/**
		 * Gibt den Wert der Eigenschaft {@code matchingIntervallVor} wieder.
		 * 
		 * @return {@code matchingIntervallVor}.
		 */
		public long getMatchingIntervallVor() {
			return matchingIntervallVor;
		}

		/**
		 * Gibt den Wert der Eigenschaft {@code matchingSchrittweite} wieder.
		 * 
		 * @return {@code matchingSchrittweite}.
		 */
		public long getMatchingSchrittweite() {
			return matchingSchrittweite;
		}

		/**
		 * Gibt den Wert der Eigenschaft {@code maxAbstand} wieder.
		 * 
		 * @return {@code maxAbstand}.
		 */
		public int getMaxAbstand() {
			return maxAbstand;
		}

		/**
		 * Gibt den Wert der Eigenschaft {@code maxGanglinien} wieder.
		 * 
		 * @return {@code maxGanglinien}.
		 */
		public long getMaxGanglinien() {
			return maxGanglinien;
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
		 * Gibt den Wert der Eigenschaft {@code maxWichtungsfaktor} wieder.
		 * 
		 * @return {@code maxWichtungsfaktor}.
		 */
		public int getMaxWichtungsfaktor() {
			return maxWichtungsfaktor;
		}

		/**
		 * Gibt den Wert der Eigenschaft {@code stuetzstellenAbstand} wieder.
		 * 
		 * @return {@code stuetzstellenAbstand}.
		 */
		public long getStuetzstellenAbstand() {
			return stuetzstellenAbstand;
		}

		/**
		 * Gibt den Wert der Eigenschaft {@code vergleichsSchrittweite} wieder.
		 * 
		 * @return {@code vergleichsSchrittweite}.
		 */
		public long getVergleichsSchrittweite() {
			return vergleichsSchrittweite;
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
		 * Legt den Wert der Eigenschaft {@code darstellungsverfahren} fest.
		 * 
		 * @param darstellungsverfahren
		 *            der neue Wert von {@code darstellungsverfahren}.
		 */
		public void setDarstellungsverfahren(int darstellungsverfahren) {
			this.darstellungsverfahren = darstellungsverfahren;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code ganglinienTyp} fest.
		 * 
		 * @param ganglinienTyp
		 *            der neue Wert von {@code ganglinienTyp}.
		 */
		public void setGanglinienTyp(int ganglinienTyp) {
			this.ganglinienTyp = ganglinienTyp;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code matchingIntervallNach} fest.
		 * 
		 * @param matchingIntervallNach
		 *            der neue Wert von {@code matchingIntervallNach}.
		 */
		public void setMatchingIntervallNach(long matchingIntervallNach) {
			this.matchingIntervallNach = matchingIntervallNach;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code matchingIntervallVor} fest.
		 * 
		 * @param matchingIntervallVor
		 *            der neue Wert von {@code matchingIntervallVor}.
		 */
		public void setMatchingIntervallVor(long matchingIntervallVor) {
			this.matchingIntervallVor = matchingIntervallVor;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code matchingSchrittweite} fest.
		 * 
		 * @param matchingSchrittweite
		 *            der neue Wert von {@code matchingSchrittweite}.
		 */
		public void setMatchingSchrittweite(long matchingSchrittweite) {
			this.matchingSchrittweite = matchingSchrittweite;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code maxAbstand} fest.
		 * 
		 * @param maxAbstand
		 *            der neue Wert von {@code maxAbstand}.
		 */
		public void setMaxAbstand(int maxAbstand) {
			this.maxAbstand = maxAbstand;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code maxGanglinien} fest.
		 * 
		 * @param maxGanglinien
		 *            der neue Wert von {@code maxGanglinien}.
		 */
		public void setMaxGanglinien(long maxGanglinien) {
			this.maxGanglinien = maxGanglinien;
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
		 * Legt den Wert der Eigenschaft {@code maxWichtungsfaktor} fest.
		 * 
		 * @param maxWichtungsfaktor
		 *            der neue Wert von {@code maxWichtungsfaktor}.
		 */
		public void setMaxWichtungsfaktor(int maxWichtungsfaktor) {
			this.maxWichtungsfaktor = maxWichtungsfaktor;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code stuetzstellenAbstand} fest.
		 * 
		 * @param stuetzstellenAbstand
		 *            der neue Wert von {@code stuetzstellenAbstand}.
		 */
		public void setStuetzstellenAbstand(long stuetzstellenAbstand) {
			this.stuetzstellenAbstand = stuetzstellenAbstand;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code vergleichsSchrittweite} fest.
		 * 
		 * @param vergleichsSchrittweite
		 *            der neue Wert von {@code vergleichsSchrittweite}.
		 */
		public void setVergleichsSchrittweite(long vergleichsSchrittweite) {
			this.vergleichsSchrittweite = vergleichsSchrittweite;
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
	public static final String ATG_GANGLINIEN_MODELL_AUTOMATISCHES_LERNEN_EREIGNIS = "atg.ganglinienModellAutomatischesLernenEreignis";

	/** Die Attributgruppe kann von allen Instanzen gemeinsam genutzt werden. */
	private static AttributeGroup atg;

	/**
	 * Initialisiert den Parameter.
	 * 
	 * @param ereignisTyp
	 *            ein Ereignistyp.
	 */
	public PdGanglinienModellAutomatischesLernenEreignis(EreignisTyp ereignisTyp) {
		super(ereignisTyp);

		if (atg == null) {
			DataModel modell = ObjektFactory.getInstanz().getVerbindung()
					.getDataModel();
			atg = modell
					.getAttributeGroup(ATG_GANGLINIEN_MODELL_AUTOMATISCHES_LERNEN_EREIGNIS);
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
			ReferenceArray feld;

			feld = daten.getReferenceArray("AlgAusschlussliste");
			for (int i = 0; i < feld.getLength(); i++) {
				datum.getAusschlussliste().add(
						(EreignisTyp) ObjektFactory.getInstanz()
								.getModellobjekt(feld.getSystemObject(i)));
			}

			feld = daten.getReferenceArray("AlgBezugsereignistypen");
			for (int i = 0; i < feld.getLength(); i++) {
				datum.getBezugsereignistypen().add(
						(EreignisTyp) ObjektFactory.getInstanz()
								.getModellobjekt(feld.getSystemObject(i)));
			}

			datum.setDarstellungsverfahren(daten.getUnscaledValue(
					"AlgDarstellungsverfahren").intValue());
			datum.setGanglinienTyp(daten.getUnscaledValue("AlgGanglinienTyp")
					.intValue());
			datum.setMatchingIntervallNach(daten.getUnscaledValue(
					"AlgMatchingIntervallNach").longValue()
					* MILLIS_PER_SEKUNDE);
			datum.setMatchingIntervallVor(daten.getUnscaledValue(
					"AlgMatchingIntervallVor").longValue()
					* MILLIS_PER_SEKUNDE);
			datum.setMatchingSchrittweite(daten.getUnscaledValue(
					"AlgMatchingSchrittweite").longValue()
					* MILLIS_PER_SEKUNDE);
			datum.setMaxAbstand(daten.getUnscaledValue("AlgMaxAbstand")
					.intValue());
			datum.setMaxGanglinien(daten.getUnscaledValue("AlgMaxGanglinien")
					.longValue());
			datum.setMaxMatchingFehler(daten.getUnscaledValue(
					"AlgMaxMatchingFehler").intValue());
			datum.setMaxWichtungsfaktor(daten.getUnscaledValue(
					"AlgMaxWichtungsfaktor").intValue());
			datum.setVergleichsSchrittweite(daten.getUnscaledValue(
					"AlgVergleichsSchrittweite").longValue()
					* MILLIS_PER_SEKUNDE);
			datum.setStuetzstellenAbstand(daten.getUnscaledValue(
					"AlgSt¸tzstellenAbstand").longValue());

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
		ReferenceArray feld;

		feld = daten.getReferenceArray("AlgAusschlussliste");
		feld.setLength(datum.getAusschlussliste().size());
		for (int i = 0; i < datum.getAusschlussliste().size(); i++) {
			feld.getReferenceValue(i).setSystemObject(
					datum.getAusschlussliste().get(i).getSystemObject());
		}

		feld = daten.getReferenceArray("AlgBezugsereignistypen");
		feld.setLength(datum.getBezugsereignistypen().size());
		for (int i = 0; i < feld.getLength(); i++) {
			feld.getReferenceValue(i).setSystemObject(
					datum.getBezugsereignistypen().get(i).getSystemObject());
		}

		daten.getUnscaledValue("AlgDarstellungsverfahren").set(
				datum.getDarstellungsverfahren());
		daten.getUnscaledValue("AlgGanglinienTyp")
				.set(datum.getGanglinienTyp());
		daten.getUnscaledValue("AlgMatchingIntervallNach").set(
				datum.getMatchingIntervallNach() / MILLIS_PER_SEKUNDE);
		daten.getUnscaledValue("AlgMatchingIntervallVor").set(
				datum.getMatchingIntervallVor() / MILLIS_PER_SEKUNDE);
		daten.getUnscaledValue("AlgMatchingSchrittweite").set(
				datum.getMatchingSchrittweite() / MILLIS_PER_SEKUNDE);
		daten.getUnscaledValue("AlgMaxAbstand").set(datum.getMaxAbstand());
		daten.getUnscaledValue("AlgMaxGanglinien")
				.set(datum.getMaxGanglinien());
		daten.getUnscaledValue("AlgMaxMatchingFehler").set(
				datum.getMaxMatchingFehler());
		daten.getUnscaledValue("AlgMaxWichtungsfaktor").set(
				datum.getMaxWichtungsfaktor());
		daten.getUnscaledValue("AlgVergleichsSchrittweite").set(
				datum.getVergleichsSchrittweite() / MILLIS_PER_SEKUNDE);
		daten.getUnscaledValue("AlgSt¸tzstellenAbstand").set(
				datum.getStuetzstellenAbstand());

		return daten;
	}

}
