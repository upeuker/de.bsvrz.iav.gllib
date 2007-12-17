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

package de.bsvrz.iav.gllib.gllib.modell;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.ResultData;
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
public class PdGanglinienModellAutomatischesLernen
		extends
		AbstractParameterDatensatz<PdGanglinienModellAutomatischesLernen.Daten> {

	/**
	 * Kapselt die Daten des Datensatzes.
	 */
	public class Daten extends AbstractDatum {

		/** Das Flag f&uuml;r die G&uuml;ltigkeit des Datensatzes. */
		private boolean valid;

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
		 * Gibt an, in welchen Zeitabständen der Lernvorgang gestartet werden
		 * soll.
		 * 
		 * @return das aktuelle Aktualisierungsintervall.
		 */
		public long getAktualisierungsintervall() {
			return aktualisierungsintervall;
		}

		/**
		 * Gibt an, in welchen Zeitabständen der Lernvorgang gestartet werden
		 * soll.
		 * 
		 * @param aktualisierungsintervall
		 *            das neue Aktualisierungsintervall.
		 */
		public void setAktualisierungsintervall(long aktualisierungsintervall) {
			this.aktualisierungsintervall = aktualisierungsintervall;
		}

		/**
		 * Mindestalter der Analysewerte, die im automatischen Lernen
		 * verarbeitet werden.
		 * 
		 * @return das aktuelle Datenmindestalter.
		 */
		public long getDatenMindestalter() {
			return datenMindestalter;
		}

		/**
		 * Mindestalter der Analysewerte, die im automatischen Lernen
		 * verarbeitet werden.
		 * 
		 * @param datenMindestalter
		 *            das neue Datenmindestalter.
		 */
		public void setDatenMindestalter(long datenMindestalter) {
			this.datenMindestalter = datenMindestalter;
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
		 * Maximaler Wichtungsfaktor der historischen Ganglinien bei der
		 * Verschmelzung mit Analyseganglinien.
		 * 
		 * @param maximalWichtung
		 *            die neue maximale Wichtung.
		 */
		public void setMaximalWichtung(int maximalWichtung) {
			this.maximalWichtung = maximalWichtung;
		}

		/**
		 * Wenn dieses Abstandsmaß beim zyklischen Archivieren und Vergleichen
		 * von Ganglinien überschritten wird, wird eine Meldung erzeugt.
		 * 
		 * @return der aktuelle maximale Vergleichsabstand.
		 */
		public int getMaxVergleichsAbstand() {
			return maxVergleichsAbstand;
		}

		/**
		 * Wenn dieses Abstandsmaß beim zyklischen Archivieren und Vergleichen
		 * von Ganglinien überschritten wird, wird eine Meldung erzeugt.
		 * 
		 * @param maxVergleichsAbstand
		 *            der neue maximale Vergleichsabstand.
		 */
		public void setMaxVergleichsAbstand(int maxVergleichsAbstand) {
			this.maxVergleichsAbstand = maxVergleichsAbstand;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatum#clone()
		 */
		@Override
		public Daten clone() {
			Daten klon = new Daten();

			klon.aktualisierungsintervall = aktualisierungsintervall;
			klon.datenMindestalter = datenMindestalter;
			klon.maximalWichtung = maximalWichtung;
			klon.maxVergleichsAbstand = maxVergleichsAbstand;
			klon.valid = valid;
			klon.setZeitstempel(getZeitstempel());

			return klon;
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
		 * Setzt das Flag {@code valid} des Datum.
		 * 
		 * @param valid
		 *            der neue Wert des Flags.
		 */
		protected void setValid(boolean valid) {
			this.valid = valid;
		}

		@Override
		public String toString() {
			String s = getClass().getSimpleName() + "[";

			s += "zeitpunkt=" + getZeitpunkt();
			s += ", aktualisierungsintervall=" + aktualisierungsintervall;
			s += ", datenMindestalter=" + datenMindestalter;
			s += ", maximalWichtung=" + maximalWichtung;
			s += ", maxVergleichsAbstand=" + maxVergleichsAbstand;

			return s + "]";
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
	public PdGanglinienModellAutomatischesLernen(EreignisTyp ereignisTyp) {
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

			datum.setAktualisierungsintervall(daten.getUnscaledValue(
					"AlgAktualisierungsintervall").longValue());
			datum.setDatenMindestalter(daten.getUnscaledValue(
					"AlgDatenMindestalter").longValue());
			datum.setMaximalWichtung(daten.getUnscaledValue(
					"AlgMaximalWichtung").intValue());
			datum.setMaxVergleichsAbstand(daten.getUnscaledValue(
					"AlgMaxVergleichsAbstand").intValue());

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
