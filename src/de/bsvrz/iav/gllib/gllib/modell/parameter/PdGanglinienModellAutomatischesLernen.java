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

package de.bsvrz.iav.gllib.gllib.modell.parameter;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.DataModel;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatum;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractParameterDatensatz;
import de.bsvrz.sys.funclib.bitctrl.modell.Datum;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.ganglinien.objekte.ApplikationGanglinienPrognose;

/**
 * Kapselt die Parameterattributgruppe
 * {@code atg.ganglinienModellAutomatischesLernenEreignis}.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class PdGanglinienModellAutomatischesLernen extends
		AbstractParameterDatensatz<PdGanglinienModellAutomatischesLernen.Daten> {

	/**
	 * Kapselt die Daten des Datensatzes.
	 */
	public class Daten extends AbstractDatum {

		/** Der aktuelle Datenstatus. */
		private Status datenStatus = Datum.Status.UNDEFINIERT;

		/**
		 * Gibt an, in welchen Zeitabständen der Lernvorgang gestartet werden
		 * soll.
		 */
		private long aktualisierungsintervall;

		/**
		 * Mindestalter der Analysewerte, die im automatischen Lernen
		 * verarbeitet werden.
		 */
		private long datenMindestalter;

		/**
		 * Maximaler Wichtungsfaktor der historischen Ganglinien bei der
		 * Verschmelzung mit Analyseganglinien.
		 */
		private int maximalWichtung;

		/**
		 * Wenn dieses Abstandsmaß beim zyklischen Archivieren und Vergleichen
		 * von Ganglinien überschritten wird, wird eine Meldung erzeugt.
		 */
		private int maxVergleichsAbstand;

		/**
		 * {@inheritDoc}
		 * 
		 * @see de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatum#clone()
		 */
		@Override
		public Daten clone() {
			final Daten klon = new Daten();

			klon.aktualisierungsintervall = aktualisierungsintervall;
			klon.datenMindestalter = datenMindestalter;
			klon.maximalWichtung = maximalWichtung;
			klon.maxVergleichsAbstand = maxVergleichsAbstand;
			klon.datenStatus = datenStatus;
			klon.setZeitstempel(getZeitstempel());

			return klon;
		}

		/**
		 * Gibt an, in welchen Zeitabständen der Lernvorgang gestartet werden
		 * soll.
		 * 
		 * @return das aktuelle Aktualisierungsintervall in Tagen.
		 */
		public long getAktualisierungsintervall() {
			return aktualisierungsintervall;
		}

		/**
		 * Mindestalter der Analysewerte, die im automatischen Lernen
		 * verarbeitet werden.
		 * 
		 * @return das aktuelle Datenmindestalter in Tagen.
		 */
		public long getDatenMindestalter() {
			return datenMindestalter;
		}

		/**
		 * {@inheritDoc}.<br>
		 * 
		 * @see de.bsvrz.sys.funclib.bitctrl.modell.Datum#getDatenStatus()
		 */
		public Status getDatenStatus() {
			return datenStatus;
		}

		/**
		 * Maximaler Wichtungsfaktor der historischen Ganglinien bei der
		 * Verschmelzung mit Analyseganglinien.
		 * 
		 * @return die aktuelle maximale Wichtung.
		 */
		public int getMaximalWichtung() {
			return maximalWichtung;
		}

		/**
		 * Wenn dieses Abstandsmaß beim zyklischen Archivieren und Vergleichen
		 * von Ganglinien überschritten wird, wird eine Meldung erzeugt.
		 * 
		 * @return der aktuelle maximale Vergleichsabstand in Prozent.
		 */
		public int getMaxVergleichsAbstand() {
			return maxVergleichsAbstand;
		}

		/**
		 * Gibt an, in welchen Zeitabständen der Lernvorgang gestartet werden
		 * soll.
		 * 
		 * @param aktualisierungsintervall
		 *            das neue Aktualisierungsintervall in Tagen.
		 */
		public void setAktualisierungsintervall(
				final long aktualisierungsintervall) {
			this.aktualisierungsintervall = aktualisierungsintervall;
		}

		/**
		 * Mindestalter der Analysewerte, die im automatischen Lernen
		 * verarbeitet werden.
		 * 
		 * @param datenMindestalter
		 *            das neue Datenmindestalter in Tagen.
		 */
		public void setDatenMindestalter(final long datenMindestalter) {
			this.datenMindestalter = datenMindestalter;
		}

		/**
		 * Maximaler Wichtungsfaktor der historischen Ganglinien bei der
		 * Verschmelzung mit Analyseganglinien. Das Gewicht muss größer 0 sein.
		 * 
		 * @param maximalWichtung
		 *            die neue maximale Wichtung.
		 */
		public void setMaximalWichtung(final int maximalWichtung) {
			this.maximalWichtung = maximalWichtung;
		}

		/**
		 * Wenn dieses Abstandsmaß beim zyklischen Archivieren und Vergleichen
		 * von Ganglinien überschritten wird, wird eine Meldung erzeugt. Der
		 * Wert muss größer als 1 sein. Werte über 100 Prozent sind erlaubt.
		 * 
		 * @param maxVergleichsAbstand
		 *            der neue maximale Vergleichsabstand in Prozent.
		 */
		public void setMaxVergleichsAbstand(final int maxVergleichsAbstand) {
			this.maxVergleichsAbstand = maxVergleichsAbstand;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			String s;

			s = getClass().getSimpleName() + "[";
			s += "zeitpunkt=" + getZeitpunkt();
			s += ", datenStatus=" + getDatenStatus();
			s += ", aktualisierungsintervall=" + aktualisierungsintervall;
			s += ", datenMindestalter=" + datenMindestalter;
			s += ", maximalWichtung=" + maximalWichtung;
			s += ", maxVergleichsAbstand=" + maxVergleichsAbstand;
			s += "]";

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
	public static final String ATG_GANGLINIEN_MODELL_AUTOMATISCHES_LERNEN = "atg.ganglinienModellAutomatischesLernen";

	/** Die Attributgruppe kann von allen Instanzen gemeinsam genutzt werden. */
	private static AttributeGroup atg;

	/**
	 * Initialisiert den Parameter.
	 * 
	 * @param appGanglinie
	 *            die Ganglinienapplikation, in der Regel die autarke
	 *            Organisationseinheit.
	 */
	public PdGanglinienModellAutomatischesLernen(
			final ApplikationGanglinienPrognose appGanglinie) {
		super(appGanglinie);

		if (atg == null) {
			final DataModel modell = ObjektFactory.getInstanz().getVerbindung()
					.getDataModel();
			atg = modell
					.getAttributeGroup(ATG_GANGLINIEN_MODELL_AUTOMATISCHES_LERNEN);
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
	public void setDaten(final ResultData result) {
		check(result);

		final Daten datum = new Daten();
		if (result.hasData()) {
			final Data daten = result.getData();

			datum.setAktualisierungsintervall(daten.getUnscaledValue(
					"AlgAktualisierungsintervall").longValue());
			datum.setDatenMindestalter(daten.getUnscaledValue(
					"AlgDatenMindestalter").longValue());
			datum.setMaximalWichtung(daten.getUnscaledValue(
					"AlgMaximalWichtung").intValue());
			datum.setMaxVergleichsAbstand(daten.getUnscaledValue(
					"AlgMaxVergleichsAbstand").intValue());
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

		daten.getUnscaledValue("AlgAktualisierungsintervall").set(
				datum.getAktualisierungsintervall());
		daten.getUnscaledValue("AlgDatenMindestalter").set(
				datum.getDatenMindestalter());
		daten.getUnscaledValue("AlgMaximalWichtung").set(
				datum.getMaximalWichtung());
		daten.getUnscaledValue("AlgMaxVergleichsAbstand").set(
				datum.getMaxVergleichsAbstand());

		return daten;
	}

}
