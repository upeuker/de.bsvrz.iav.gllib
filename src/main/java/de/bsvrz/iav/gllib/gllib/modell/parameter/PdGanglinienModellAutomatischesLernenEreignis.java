/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.5 Funktionen Ganglinie
 * Copyright (C) 2011-2015 BitCtrl Systems GmbH
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

package de.bsvrz.iav.gllib.gllib.modell.parameter;

import static com.bitctrl.Constants.MILLIS_PER_SECOND;

import java.util.ArrayList;
import java.util.List;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.Data.ReferenceArray;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.DataModel;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatum;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractParameterDatensatz;
import de.bsvrz.sys.funclib.bitctrl.modell.Datum;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.objekte.EreignisTyp;

/**
 * Kapselt die Parameterattributgruppe
 * {@code atg.ganglinienModellAutomatischesLernenEreignis}.
 *
 * @author BitCtrl Systems GmbH, Falko Schumann
 */
public class PdGanglinienModellAutomatischesLernenEreignis extends
		AbstractParameterDatensatz<PdGanglinienModellAutomatischesLernenEreignis.Daten> {

	/**
	 * Kapselt die Daten des Datensatzes.
	 */
	public class Daten extends AbstractDatum {

		/** Der aktuelle Datenstatus. */
		private Status datenStatus;

		/** Die Liste der Ausschlussereignistypen. */
		private final List<EreignisTyp> ausschlussliste = new ArrayList<EreignisTyp>();

		/** Die Liste der Bezugsereignistypen (nur für relative Ganglinien). */
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

		/** Die maximale Anzahl von Ganglinien für den Ereignistyp. */
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
		 * Abstandsberechnung.
		 */
		private long vergleichsSchrittweite;

		/**
		 * {@inheritDoc}
		 *
		 * @see de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatum#clone()
		 */
		@Override
		public Daten clone() {
			final Daten klon = new Daten();

			klon.datenStatus = datenStatus;
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
			klon.vergleichsSchrittweite = vergleichsSchrittweite;
			klon.setZeitstempel(getZeitstempel());

			return klon;
		}

		/**
		 * Gibt die Liste der Ausschlussereignistypen wieder.
		 *
		 * @return {@code ausschlussliste}.
		 */
		public List<EreignisTyp> getAusschlussliste() {
			assert ausschlussliste != null;
			return ausschlussliste;
		}

		/**
		 * Gibt die Liste der Bezugsereignistypen (nur für relative Ganglinien)
		 * wieder.
		 *
		 * @return {@code bezugsereignistypen}.
		 */
		public List<EreignisTyp> getBezugsereignistypen() {
			assert bezugsereignistypen != null;
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
		 * {@inheritDoc}.<br>
		 *
		 * @see de.bsvrz.sys.funclib.bitctrl.modell.Datum#getDatenStatus()
		 */
		@Override
		public Status getDatenStatus() {
			return datenStatus;
		}

		/**
		 * Gibt den Wert der Eigenschaft {@code ganglinienTyp} wieder.
		 *
		 * @return {@code ganglinienTyp}.
		 * @see de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#TYP_ABSOLUT
		 * @see de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#TYP_ADDITIV
		 * @see de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#TYP_MULTIPLIKATIV
		 */
		public int getGanglinienTyp() {
			return ganglinienTyp;
		}

		/**
		 * Gibt das Intervall nach der Ganglinie, welches beim Pattern-Matching
		 * einbezogen wird wieder.
		 *
		 * @return {@code matchingIntervallNach}.
		 */
		public long getMatchingIntervallNach() {
			assert matchingIntervallNach >= 0;
			return matchingIntervallNach;
		}

		/**
		 * Gibt das Intervall vor der Ganglinie, welches beim Pattern-Matching
		 * einbezogen wird wieder.
		 *
		 * @return {@code matchingIntervallVor}.
		 */
		public long getMatchingIntervallVor() {
			assert matchingIntervallVor >= 0;
			return matchingIntervallVor;
		}

		/**
		 * Gibt den Wert der Eigenschaft {@code matchingSchrittweite} wieder.
		 *
		 * @return {@code matchingSchrittweite}.
		 */
		public long getMatchingSchrittweite() {
			assert matchingSchrittweite > 0;
			return matchingSchrittweite;
		}

		/**
		 * Gibt den maximalen Abstand, bei dem Ganglinien verschmolzen werden,
		 * wieder.
		 *
		 * @return {@code maxAbstand} in Prozent.
		 */
		public int getMaxAbstand() {
			assert maxAbstand > 0;
			return maxAbstand;
		}

		/**
		 * Gibt die maximale Anzahl von Ganglinien für den Ereignistyp wieder.
		 *
		 * @return {@code maxGanglinien}.
		 */
		public long getMaxGanglinien() {
			assert maxGanglinien > 0;
			return maxGanglinien;
		}

		/**
		 * Gibt den maximal erlaubten Fehler beim Pattern-Matching wieder.
		 *
		 * @return {@code maxMatchingFehler} in Prozent.
		 */
		public int getMaxMatchingFehler() {
			assert maxMatchingFehler > 0;
			return maxMatchingFehler;
		}

		/**
		 * Gibt den maximalen Wichtungsfaktor der historischen Ganglinie beim
		 * Verschmelzen wieder.
		 *
		 * @return {@code maxWichtungsfaktor}.
		 */
		public int getMaxWichtungsfaktor() {
			assert maxWichtungsfaktor > 0;
			return maxWichtungsfaktor;
		}

		/**
		 * Gibt die Schrittweite beim Vergleichen mittels komplexer
		 * Abstandsberechnung wieder.
		 *
		 * @return {@code vergleichsSchrittweite}.
		 */
		public long getVergleichsSchrittweite() {
			assert vergleichsSchrittweite > 0;
			return vergleichsSchrittweite;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code darstellungsverfahren} fest.
		 *
		 * @param darstellungsverfahren
		 *            der neue Wert von {@code darstellungsverfahren}.
		 */
		public void setDarstellungsverfahren(final int darstellungsverfahren) {
			this.darstellungsverfahren = darstellungsverfahren;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code ganglinienTyp} fest.
		 *
		 * @param ganglinienTyp
		 *            der neue Wert von {@code ganglinienTyp}.
		 * @see de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#TYP_ABSOLUT
		 * @see de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#TYP_ADDITIV
		 * @see de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ#TYP_MULTIPLIKATIV
		 */
		public void setGanglinienTyp(final int ganglinienTyp) {
			this.ganglinienTyp = ganglinienTyp;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code matchingIntervallNach} fest.
		 *
		 * @param matchingIntervallNach
		 *            der neue Wert von {@code matchingIntervallNach}.
		 */
		public void setMatchingIntervallNach(final long matchingIntervallNach) {
			this.matchingIntervallNach = matchingIntervallNach;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code matchingIntervallVor} fest.
		 *
		 * @param matchingIntervallVor
		 *            der neue Wert von {@code matchingIntervallVor}.
		 */
		public void setMatchingIntervallVor(final long matchingIntervallVor) {
			this.matchingIntervallVor = matchingIntervallVor;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code matchingSchrittweite} fest.
		 *
		 * @param matchingSchrittweite
		 *            der neue Wert von {@code matchingSchrittweite}.
		 */
		public void setMatchingSchrittweite(final long matchingSchrittweite) {
			this.matchingSchrittweite = matchingSchrittweite;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code maxAbstand} fest.
		 *
		 * @param maxAbstand
		 *            der neue Wert von {@code maxAbstand}.
		 */
		public void setMaxAbstand(final int maxAbstand) {
			this.maxAbstand = maxAbstand;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code maxGanglinien} fest.
		 *
		 * @param maxGanglinien
		 *            der neue Wert von {@code maxGanglinien}.
		 */
		public void setMaxGanglinien(final long maxGanglinien) {
			this.maxGanglinien = maxGanglinien;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code maxMatchingFehler} fest.
		 *
		 * @param maxMatchingFehler
		 *            der neue Wert von {@code maxMatchingFehler}.
		 */
		public void setMaxMatchingFehler(final int maxMatchingFehler) {
			this.maxMatchingFehler = maxMatchingFehler;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code maxWichtungsfaktor} fest.
		 *
		 * @param maxWichtungsfaktor
		 *            der neue Wert von {@code maxWichtungsfaktor}.
		 */
		public void setMaxWichtungsfaktor(final int maxWichtungsfaktor) {
			this.maxWichtungsfaktor = maxWichtungsfaktor;
		}

		/**
		 * Legt den Wert der Eigenschaft {@code vergleichsSchrittweite} fest.
		 *
		 * @param vergleichsSchrittweite
		 *            der neue Wert von {@code vergleichsSchrittweite}.
		 */
		public void setVergleichsSchrittweite(
				final long vergleichsSchrittweite) {
			this.vergleichsSchrittweite = vergleichsSchrittweite;
		}

		/**
		 * {@inheritDoc}
		 *
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			String s;

			s = getClass().getSimpleName() + "["; //$NON-NLS-1$
			s += "zeitpunkt=" + getZeitpunkt(); //$NON-NLS-1$
			s += ", datenStatus=" + getDatenStatus(); //$NON-NLS-1$
			s += ", darstellungsverfahren=" + darstellungsverfahren; //$NON-NLS-1$
			s += ", ganglinienTyp=" + ganglinienTyp; //$NON-NLS-1$
			s += ", matchingIntervallNach=" + matchingIntervallNach; //$NON-NLS-1$
			s += ", matchingIntervallVor=" + matchingIntervallVor; //$NON-NLS-1$
			s += ", matchingSchrittweite=" + matchingSchrittweite; //$NON-NLS-1$
			s += ", maxAbstand=" + maxAbstand; //$NON-NLS-1$
			s += ", maxGanglinien=" + maxGanglinien; //$NON-NLS-1$
			s += ", maxMatchingFehler=" + maxMatchingFehler; //$NON-NLS-1$
			s += ", maxWichtungsfaktor=" + maxWichtungsfaktor; //$NON-NLS-1$
			s += ", vergleichsSchrittweite=" + vergleichsSchrittweite; //$NON-NLS-1$
			s += ", bezugsereignistypen=" + bezugsereignistypen; //$NON-NLS-1$
			s += ", ausschlussliste=" + ausschlussliste; //$NON-NLS-1$
			s += "]"; //$NON-NLS-1$

			return s;
		}

		/**
		 * setzt den aktuellen Datenstatus.
		 *
		 * @param datenStatus
		 *            der neue Status
		 */
		protected void setDatenStatus(final Status datenStatus) {
			this.datenStatus = datenStatus;
		}
	}

	/** Die PID der Attributgruppe. */
	public static final String ATG_GANGLINIEN_MODELL_AUTOMATISCHES_LERNEN_EREIGNIS = "atg.ganglinienModellAutomatischesLernenEreignis"; //$NON-NLS-1$

	/** Die Attributgruppe kann von allen Instanzen gemeinsam genutzt werden. */
	private static AttributeGroup atg;

	/**
	 * Initialisiert den Parameter.
	 *
	 * @param ereignisTyp
	 *            ein Ereignistyp.
	 */
	public PdGanglinienModellAutomatischesLernenEreignis(
			final EreignisTyp ereignisTyp) {
		super(ereignisTyp);

		if (atg == null) {
			final DataModel modell = ObjektFactory.getInstanz().getVerbindung()
					.getDataModel();
			atg = modell.getAttributeGroup(
					ATG_GANGLINIEN_MODELL_AUTOMATISCHES_LERNEN_EREIGNIS);
			assert atg != null;
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see de.bsvrz.sys.funclib.bitctrl.modell.Datensatz#erzeugeDatum()
	 */
	@Override
	public Daten erzeugeDatum() {
		return new Daten();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see de.bsvrz.sys.funclib.bitctrl.modell.Datensatz#getAttributGruppe()
	 */
	@Override
	public AttributeGroup getAttributGruppe() {
		return atg;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see de.bsvrz.sys.funclib.bitctrl.modell.Datensatz#setDaten(de.bsvrz.dav.daf.main.ResultData)
	 */
	@Override
	public void setDaten(final ResultData result) {
		check(result);

		final Daten datum = new Daten();
		if (result.hasData()) {
			final Data daten = result.getData();
			ReferenceArray feld;

			feld = daten.getReferenceArray("AlgAusschlussliste"); //$NON-NLS-1$
			for (int i = 0; i < feld.getLength(); i++) {
				datum.getAusschlussliste().add((EreignisTyp) ObjektFactory
						.getInstanz().getModellobjekt(feld.getSystemObject(i)));
			}

			feld = daten.getReferenceArray("AlgBezugsereignistypen"); //$NON-NLS-1$
			for (int i = 0; i < feld.getLength(); i++) {
				datum.getBezugsereignistypen().add((EreignisTyp) ObjektFactory
						.getInstanz().getModellobjekt(feld.getSystemObject(i)));
			}

			datum.setDarstellungsverfahren(daten
					.getUnscaledValue("AlgDarstellungsverfahren").intValue()); //$NON-NLS-1$
			datum.setGanglinienTyp(daten.getUnscaledValue("AlgGanglinienTyp") //$NON-NLS-1$
					.intValue());
			datum.setMatchingIntervallNach(daten
					.getUnscaledValue("AlgMatchingIntervallNach").longValue() //$NON-NLS-1$
					* MILLIS_PER_SECOND);
			datum.setMatchingIntervallVor(daten
					.getUnscaledValue("AlgMatchingIntervallVor").longValue() //$NON-NLS-1$
					* MILLIS_PER_SECOND);
			datum.setMatchingSchrittweite(daten
					.getUnscaledValue("AlgMatchingSchrittweite").longValue() //$NON-NLS-1$
					* MILLIS_PER_SECOND);
			datum.setMaxAbstand(daten.getUnscaledValue("AlgMaxAbstand") //$NON-NLS-1$
					.intValue());
			datum.setMaxGanglinien(daten.getUnscaledValue("AlgMaxGanglinien") //$NON-NLS-1$
					.longValue());
			datum.setMaxMatchingFehler(
					daten.getUnscaledValue("AlgMaxMatchingFehler").intValue()); //$NON-NLS-1$
			datum.setMaxWichtungsfaktor(
					daten.getUnscaledValue("AlgMaxWichtungsfaktor").intValue()); //$NON-NLS-1$
			datum.setVergleichsSchrittweite(daten
					.getUnscaledValue("AlgVergleichsSchrittweite").longValue() //$NON-NLS-1$
					* MILLIS_PER_SECOND);
		}

		datum.setDatenStatus(Datum.Status.getStatus(result.getDataState()));
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
	protected Data konvertiere(final Daten datum) {
		final Data daten = erzeugeSendeCache();
		ReferenceArray feld;

		feld = daten.getReferenceArray("AlgAusschlussliste"); //$NON-NLS-1$
		feld.setLength(datum.getAusschlussliste().size());
		for (int i = 0; i < datum.getAusschlussliste().size(); i++) {
			feld.getReferenceValue(i).setSystemObject(
					datum.getAusschlussliste().get(i).getSystemObject());
		}

		feld = daten.getReferenceArray("AlgBezugsereignistypen"); //$NON-NLS-1$
		feld.setLength(datum.getBezugsereignistypen().size());
		for (int i = 0; i < feld.getLength(); i++) {
			feld.getReferenceValue(i).setSystemObject(
					datum.getBezugsereignistypen().get(i).getSystemObject());
		}

		daten.getUnscaledValue("AlgDarstellungsverfahren").set( //$NON-NLS-1$
				datum.getDarstellungsverfahren());
		daten.getUnscaledValue("AlgGanglinienTyp") //$NON-NLS-1$
				.set(datum.getGanglinienTyp());
		daten.getUnscaledValue("AlgMatchingIntervallNach").set( //$NON-NLS-1$
				datum.getMatchingIntervallNach() / MILLIS_PER_SECOND);
		daten.getUnscaledValue("AlgMatchingIntervallVor").set( //$NON-NLS-1$
				datum.getMatchingIntervallVor() / MILLIS_PER_SECOND);
		daten.getUnscaledValue("AlgMatchingSchrittweite").set( //$NON-NLS-1$
				datum.getMatchingSchrittweite() / MILLIS_PER_SECOND);
		daten.getUnscaledValue("AlgMaxAbstand").set(datum.getMaxAbstand()); //$NON-NLS-1$
		daten.getUnscaledValue("AlgMaxGanglinien") //$NON-NLS-1$
				.set(datum.getMaxGanglinien());
		daten.getUnscaledValue("AlgMaxMatchingFehler").set( //$NON-NLS-1$
				datum.getMaxMatchingFehler());
		daten.getUnscaledValue("AlgMaxWichtungsfaktor").set( //$NON-NLS-1$
				datum.getMaxWichtungsfaktor());
		daten.getUnscaledValue("AlgVergleichsSchrittweite").set( //$NON-NLS-1$
				datum.getVergleichsSchrittweite() / MILLIS_PER_SECOND);

		return daten;
	}

}
