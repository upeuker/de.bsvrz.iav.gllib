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
 * Wei�enfelser Stra�e 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib.modell.onlinedaten;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.Data.Array;
import de.bsvrz.dav.daf.main.config.Aspect;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.DataModel;
import de.bsvrz.iav.gllib.gllib.dav.GlProgAnfrage;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatum;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractOnlineDatensatz;
import de.bsvrz.sys.funclib.bitctrl.modell.Aspekt;
import de.bsvrz.sys.funclib.bitctrl.modell.Datum;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.SystemObjekt;
import de.bsvrz.sys.funclib.bitctrl.modell.systemmodellglobal.objekte.Applikation;

/**
 * Kapselt die Onlineattributgruppe {@code atg.prognoseGanglinienAnfrage}.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class OdPrognoseGanglinienAnfrage extends
		AbstractOnlineDatensatz<OdPrognoseGanglinienAnfrage.Daten> {

	/**
	 * Die vorhandenen Aspekte des Datensatzes.
	 */
	public enum Aspekte implements Aspekt {

		/** Der Aspekt {@code asp.anfrage}. */
		Anfrage("asp.anfrage");

		/** Der Aspekt, den das enum kapselt. */
		private final Aspect aspekt;

		/**
		 * Erzeugt aus der PID den Aspekt.
		 * 
		 * @param pid
		 *            die PID eines Aspekts.
		 */
		private Aspekte(final String pid) {
			final DataModel modell = ObjektFactory.getInstanz().getVerbindung()
					.getDataModel();
			aspekt = modell.getAspect(pid);
			assert aspekt != null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see de.bsvrz.sys.funclib.bitctrl.modell.Aspekt#getAspekt()
		 */
		public Aspect getAspekt() {
			return aspekt;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see de.bsvrz.sys.funclib.bitctrl.modell.Aspekt#getName()
		 */
		public String getName() {
			return aspekt.getNameOrPidOrId();
		}

	}

	/**
	 * Kapselt die Daten des Datensatzes.
	 */
	public static class Daten extends AbstractDatum implements
			Collection<GlProgAnfrage> {

		/** Die anfragende Applikation. */
		private Applikation absender;

		/** Eine beliebige Zeichenkette die der Absender frei eingetragen kann. */
		private String absenderZeichen;

		/** Liste der Anfragen in dieser Nachricht. */
		private final List<GlProgAnfrage> anfragen = new ArrayList<GlProgAnfrage>();

		/** Der aktuelle Datenstatus. */
		private Status datenStatus = Datum.Status.UNDEFINIERT;

		/**
		 * {@inheritDoc}
		 */
		public boolean add(final GlProgAnfrage o) {
			return anfragen.add(o);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean addAll(final Collection<? extends GlProgAnfrage> c) {
			return anfragen.addAll(c);
		}

		/**
		 * {@inheritDoc}
		 */
		public void clear() {
			anfragen.clear();

		}

		/**
		 * Es wird eine flache Kopie zur�ckgegeben.
		 * 
		 * {@inheritDoc}
		 */
		@Override
		public Daten clone() {
			final Daten klon = new Daten();

			klon.setZeitstempel(getZeitstempel());
			klon.datenStatus = datenStatus;
			klon.absender = absender;
			klon.absenderZeichen = absenderZeichen;
			klon.anfragen.addAll(anfragen);

			return klon;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean contains(final Object o) {
			return anfragen.contains(o);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean containsAll(final Collection<?> c) {
			return anfragen.containsAll(c);
		}

		/**
		 * Gibt die anfragende Applikation zur�ck.
		 * 
		 * @return die Applikation.
		 */
		public Applikation getAbsender() {
			return absender;
		}

		/**
		 * Gibt die Zeichenkette zur�ck, die der Absender in der Nachricht frei
		 * eintragen darf.
		 * 
		 * @return Eine Zeichenkette
		 */
		public String getAbsenderZeichen() {
			return absenderZeichen;
		}

		/**
		 * {@inheritDoc}
		 */
		public Status getDatenStatus() {
			return datenStatus;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isEmpty() {
			return anfragen.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		public Iterator<GlProgAnfrage> iterator() {
			return anfragen.iterator();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean remove(final Object o) {
			return anfragen.remove(o);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean removeAll(final Collection<?> c) {
			return anfragen.removeAll(c);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean retainAll(final Collection<?> c) {
			return anfragen.retainAll(c);
		}

		/**
		 * Legt die anfragende Applikation fest.
		 * 
		 * @param absender
		 *            die anfragende Applikation.
		 */
		public void setAbsender(final Applikation absender) {
			this.absender = absender;
		}

		/**
		 * Legt die Zeichenkette fest, die der Absender in der Nachricht frei
		 * eintragen darf.
		 * 
		 * @param absenderZeichen
		 *            die Zeichenkette.
		 */
		public void setAbsenderZeichen(final String absenderZeichen) {
			this.absenderZeichen = absenderZeichen;
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

		/**
		 * {@inheritDoc}
		 */
		public int size() {
			return anfragen.size();
		}

		/**
		 * {@inheritDoc}
		 */
		public Object[] toArray() {
			return anfragen.toArray();
		}

		/**
		 * {@inheritDoc}
		 */
		public <T> T[] toArray(final T[] a) {
			return anfragen.toArray(a);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			String s = getClass().getName() + "[";

			s += "zeitpunkt=" + getZeitpunkt();
			s += ", valid=" + isValid();
			s += ", absender=" + absender;
			s += ", absenderZeichen=" + absenderZeichen;
			s += ", Anzahl der Teilanfragen=" + anfragen.size();

			return s + "]";
		}

	}

	/** Die PID der Attributgruppe. */
	public static final String ATG_PROGNOSE_GANGLINIEN_ANFRAGE = "atg.prognoseGanglinienAnfrage";

	/** Die Attributgruppe kann von allen Instanzen gemeinsam genutzt werden. */
	private static AttributeGroup atg;

	/**
	 * Erzeugt eine Anfrage an die Ganglinienprognose.
	 * 
	 * @param app
	 *            die Applikation <em>Ganglinienprognose</em> an die die
	 *            Anfrage gestellt wird.
	 */
	public OdPrognoseGanglinienAnfrage(final SystemObjekt app) {
		super(app);

		if (atg == null) {
			final DataModel modell = ObjektFactory.getInstanz().getVerbindung()
					.getDataModel();
			atg = modell.getAttributeGroup(ATG_PROGNOSE_GANGLINIEN_ANFRAGE);
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
	 * @see de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatensatz#getAspekte()
	 */
	@Override
	public Collection<Aspect> getAspekte() {
		final Set<Aspect> aspekte = new HashSet<Aspect>();
		for (final Aspekt a : Aspekte.values()) {
			aspekte.add(a.getAspekt());
		}
		return aspekte;
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
	 * @see de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatensatz#konvertiere(de.bsvrz.sys.funclib.bitctrl.modell.Datum)
	 */
	@Override
	protected Data konvertiere(final Daten datum) {
		final Data daten = erzeugeSendeCache();

		Array feld;
		int i;

		daten.getReferenceValue("absenderId").setSystemObject(
				datum.getAbsender().getSystemObject());
		daten.getTextValue("AbsenderZeichen").setText(
				datum.getAbsenderZeichen());

		feld = daten.getArray("PrognoseGanglinienAnfrage");
		feld.setLength(datum.size());
		i = 0;
		for (final GlProgAnfrage anfrage : datum) {
			anfrage.getDaten(feld.getItem(i++));
		}

		return daten;
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
			Array feld;
			final Data daten = result.getData();
			final ObjektFactory factory = ObjektFactory.getInstanz();

			datum.setAbsender((Applikation) factory.getModellobjekt(daten
					.getReferenceValue("absenderId").getSystemObject()));
			datum.setAbsenderZeichen(daten.getTextValue("AbsenderZeichen")
					.getText());
			datum.clear();
			feld = daten.getArray("PrognoseGanglinienAnfrage");
			for (int i = 0; i < feld.getLength(); i++) {
				GlProgAnfrage anfrage;

				anfrage = new GlProgAnfrage();
				anfrage.setDaten(feld.getItem(i));
				datum.add(anfrage);
			}
		}

		datum.setDatenStatus(Datum.Status.getStatus(result.getDataState()));
		datum.setZeitstempel(result.getDataTime());
		setDatum(result.getDataDescription().getAspect(), datum);
		fireDatensatzAktualisiert(result.getDataDescription().getAspect(),
				datum.clone());
	}

}
