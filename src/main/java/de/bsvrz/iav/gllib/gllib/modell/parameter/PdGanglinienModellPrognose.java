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

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.DataModel;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatum;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractParameterDatensatz;
import de.bsvrz.sys.funclib.bitctrl.modell.Datum;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.objekte.MessQuerschnittAllgemein;

/**
 * Kapselt die Parameterattributgruppe {@code atg.ganglinienModellPrognose}.
 *
 * @author BitCtrl Systems GmbH, Falko Schumann
 */
public class PdGanglinienModellPrognose
		extends AbstractParameterDatensatz<PdGanglinienModellPrognose.Daten> {

	/**
	 * Kapselt die Daten des Datensatzes.
	 */
	public class Daten extends AbstractDatum {

		/** Auswahlmethode: Auswahl der Referenzganglinie. */
		public static final int REFERENZGANGLINIE = 1;

		/** Auswahlmethode: Auswahl der wahrscheinlichsten Ganglinie. */
		public static final int WAHRSCHEINLICHSTE_GANGLINIE = 2;

		/** Der aktuelle Datenstatus. */
		private Status datenStatus = Datum.Status.UNDEFINIERT;

		/** Die Auswahlmethode, wenn Pattern-Matching nicht geht. */
		private int auswahlMethode;

		/** Der Offset um den beim Pattern-Matching verschoben wird. */
		private long patternMatchingOffset;

		/** Die Schrittweite in der beim Pattern-Matching verschoben wird. */
		private long matchingIntervall;

		/** Der maximal erlaubte Fehler beim Pattern-Matching. */
		private int maxMatchingFehler;

		/** Der Horizont der mittelfristigen Prognose (Pattern-Matching). */
		private long patternMatchingHorizont;

		/**
		 * Initialisiert das Datum mit Standardwerten.
		 */
		public Daten() {
			datenStatus = Datum.Status.DATEN;
			auswahlMethode = WAHRSCHEINLICHSTE_GANGLINIE;
			matchingIntervall = 15 * 60 * 1000; // 15 Minuten
			maxMatchingFehler = 25;
			patternMatchingHorizont = 2 * 60 * 60 * 1000; // 2 Stunden
			patternMatchingOffset = 60 * 60 * 1000; // 1 Stunde
		}

		/**
		 * {@inheritDoc}
		 *
		 * @see de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatum#clone()
		 */
		@Override
		public Daten clone() {
			final Daten klon = new Daten();

			klon.datenStatus = datenStatus;
			klon.auswahlMethode = auswahlMethode;
			klon.matchingIntervall = matchingIntervall;
			klon.maxMatchingFehler = maxMatchingFehler;
			klon.patternMatchingHorizont = patternMatchingHorizont;
			klon.patternMatchingOffset = patternMatchingOffset;
			klon.setZeitstempel(getZeitstempel());

			return klon;
		}

		/**
		 * Gibt die Auswahlmethode zurück, die verwendet wird, wenn das
		 * Pattern-Matching kein Ergebnis liefert oder nicht verwendet werden
		 * soll.
		 *
		 * @return die Auswahlmethode für die Rückfallebene.
		 */
		public int getAuswahlMethode() {
			return auswahlMethode;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status getDatenStatus() {
			return datenStatus;
		}

		/**
		 * Gibt die Schrittweite zurück, in der beim Pattern-Matching verschoben
		 * wird.
		 *
		 * @return die Schrittweite.
		 */
		public long getMatchingIntervall() {
			return matchingIntervall;
		}

		/**
		 * Gibt den maximal erlaubten Fehler beim Pattern-Matching zurück.
		 *
		 * @return der maximale Fehler.
		 */
		public int getMaxMatchingFehler() {
			return maxMatchingFehler;
		}

		/**
		 * Gibt den Horizont der mittelfristigen Prognose (Pattern-Matching)
		 * zurück.
		 *
		 * @return der Horizont.
		 */
		public long getPatternMatchingHorizont() {
			return patternMatchingHorizont;
		}

		/**
		 * Gibt den Offset zurück, um den beim Pattern-Matching verschoben wird.
		 *
		 * @return der Offset.
		 */
		public long getPatternMatchingOffset() {
			return patternMatchingOffset;
		}

		/**
		 * Legt die Auswahlmethode fest, die verwendet wird, wenn das
		 * Pattern-Matching kein Ergebnis liefert oder nicht verwendet werden
		 * soll.
		 *
		 * @param auswahlMethode
		 *            die Auswahlmethode für die Rückfallebene.
		 */

		public void setAuswahlMethode(final int auswahlMethode) {
			this.auswahlMethode = auswahlMethode;
		}

		/**
		 * Legt die Schrittweite fest, in der beim Pattern-Matching verschoben
		 * wird.
		 *
		 * @param matchingIntervall
		 *            die Schrittweite.
		 */
		public void setMatchingIntervall(final long matchingIntervall) {
			this.matchingIntervall = matchingIntervall;
		}

		/**
		 * Legt den maximal erlaubten Fehler beim Pattern-Matching fest.
		 *
		 * @param maxMatchingFehler
		 *            der maximale Fehler.
		 */
		public void setMaxMatchingFehler(final int maxMatchingFehler) {
			this.maxMatchingFehler = maxMatchingFehler;
		}

		/**
		 * Legt den Horizont der mittelfristigen Prognose (Pattern-Matching)
		 * fest.
		 *
		 * @param patternMatchingHorizont
		 *            der Horizont.
		 */
		public void setPatternMatchingHorizont(
				final long patternMatchingHorizont) {
			this.patternMatchingHorizont = patternMatchingHorizont;
		}

		/**
		 * Legt den Offset fest, um den beim Pattern-Matching verschoben wird.
		 *
		 * @param patternMatchingOffset
		 *            das Offset.
		 */
		public void setPatternMatchingOffset(final long patternMatchingOffset) {
			this.patternMatchingOffset = patternMatchingOffset;
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
			s += ", auswahlMethode=" + auswahlMethode; //$NON-NLS-1$
			s += ", matchingIntervall=" + matchingIntervall; //$NON-NLS-1$
			s += ", maxMatchingFehler=" + maxMatchingFehler; //$NON-NLS-1$
			s += ", patternMatchingHorizont=" + patternMatchingHorizont; //$NON-NLS-1$
			s += ", patternMatchingOffset=" + patternMatchingOffset; //$NON-NLS-1$
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
	public static final String ATG_GANGLINIEN_MODELL_PROGNOSE = "atg.ganglinienModellPrognose"; //$NON-NLS-1$

	/** Die Attributgruppe kann von allen Instanzen gemeinsam genutzt werden. */
	private static AttributeGroup atg;

	/**
	 * Initialisiert den Parameter.
	 *
	 * @param mq
	 *            ein Messquerschnitt
	 */
	public PdGanglinienModellPrognose(final MessQuerschnittAllgemein mq) {
		super(mq);

		if (atg == null) {
			final DataModel modell = ObjektFactory.getInstanz().getVerbindung()
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

			datum.setAuswahlMethode(
					daten.getUnscaledValue("GLAuswahlMethode").intValue()); //$NON-NLS-1$
			datum.setPatternMatchingHorizont(daten
					.getUnscaledValue("GLPatternMatchingHorizont").longValue() //$NON-NLS-1$
					* MILLIS_PER_SECOND);
			datum.setMatchingIntervall(
					daten.getUnscaledValue("GLMatchingIntervall").longValue() //$NON-NLS-1$
							* MILLIS_PER_SECOND);
			datum.setPatternMatchingOffset(
					daten.getUnscaledValue("GLPatterMatchingOffset").longValue() //$NON-NLS-1$
							* MILLIS_PER_SECOND);
			datum.setMaxMatchingFehler(daten
					.getUnscaledValue("GLMaximalerMatchingFehler").intValue()); //$NON-NLS-1$
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

		daten.getUnscaledValue("GLAuswahlMethode").set( //$NON-NLS-1$
				datum.getAuswahlMethode());
		daten.getUnscaledValue("GLPatternMatchingHorizont").set( //$NON-NLS-1$
				datum.getPatternMatchingHorizont() / MILLIS_PER_SECOND);
		daten.getUnscaledValue("GLMatchingIntervall").set( //$NON-NLS-1$
				datum.getMatchingIntervall() / MILLIS_PER_SECOND);
		daten.getUnscaledValue("GLPatterMatchingOffset").set( //$NON-NLS-1$
				datum.getPatternMatchingOffset() / MILLIS_PER_SECOND);
		daten.getUnscaledValue("GLMaximalerMatchingFehler").set( //$NON-NLS-1$
				datum.getMaxMatchingFehler());

		return daten;
	}
}
