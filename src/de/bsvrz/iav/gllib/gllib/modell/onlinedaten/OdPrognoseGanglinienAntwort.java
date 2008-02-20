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

package de.bsvrz.iav.gllib.gllib.modell.onlinedaten;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.bitctrl.util.Interval;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.Data.Array;
import de.bsvrz.dav.daf.main.config.Aspect;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.DataModel;
import de.bsvrz.iav.gllib.gllib.Stuetzstelle;
import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;
import de.bsvrz.iav.gllib.gllib.dav.Messwerte;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatum;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractOnlineDatensatz;
import de.bsvrz.sys.funclib.bitctrl.modell.Aspekt;
import de.bsvrz.sys.funclib.bitctrl.modell.Datum;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.SystemObjekt;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.objekte.MessQuerschnittAllgemein;

/**
 * Kapselt die Onlineattributgruppe {@code atg.prognoseGanglinienAntwort}.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class OdPrognoseGanglinienAntwort extends
		AbstractOnlineDatensatz<OdPrognoseGanglinienAntwort.Daten> {

	/**
	 * Die vorhandenen Aspekte des Datensatzes.
	 */
	public enum Aspekte implements Aspekt {

		/** Der Aspekt {@code asp.antwort}. */
		Antwort("asp.antwort");

		/** Der Aspekt, den das enum kapselt. */
		private final Aspect aspekt;

		/**
		 * Erzeugt aus der PID den Aspekt.
		 * 
		 * @param pid
		 *            die PID eines Aspekts.
		 */
		private Aspekte(final String pid) {
			DataModel modell = ObjektFactory.getInstanz().getVerbindung()
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
			Collection<GanglinieMQ> {

		/** Eine beliebige Zeichenkette die der Absender frei eingetragen kann. */
		private String absenderZeichen;

		/** Hash zum einfachen auffinden der passenden Ganglinie. */
		private final List<GanglinieMQ> ganglinien = new ArrayList<GanglinieMQ>();

		/** Der aktuelle Datenstatus. */
		private Status datenStatus = Datum.Status.UNDEFINIERT;

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Collection#add(java.lang.Object)
		 */
		public boolean add(final GanglinieMQ o) {
			return ganglinien.add(o);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Collection#addAll(java.util.Collection)
		 */
		public boolean addAll(final Collection<? extends GanglinieMQ> c) {
			return ganglinien.addAll(c);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Collection#clear()
		 */
		public void clear() {
			ganglinien.clear();

		}

		/**
		 * Es wird eine flache Kopie zur&uuml;ckgegeben.
		 * 
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#clone()
		 */
		@Override
		public Daten clone() {
			Daten klon = new Daten();

			klon.setZeitstempel(getZeitstempel());
			klon.datenStatus = datenStatus;
			klon.absenderZeichen = absenderZeichen;
			klon.ganglinien.addAll(ganglinien);

			return klon;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Collection#contains(java.lang.Object)
		 */
		public boolean contains(final Object o) {
			return ganglinien.contains(o);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Collection#containsAll(java.util.Collection)
		 */
		public boolean containsAll(final Collection<?> c) {
			return ganglinien.containsAll(c);
		}

		/**
		 * Gibt das Zeichen des Absenders zur&uuml;ck. Der Text wurde bei der
		 * Anfrage in die Anfragenachricht eingetragen und von der
		 * Ganglinienprognose in die Antwort kopiert. Somit kann die anfragende
		 * Applikation mehrere Anfragen unterscheiden.
		 * 
		 * @return das Absenderzeichen.
		 */
		public String getAbsenderZeichen() {
			return absenderZeichen;
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
		 * {@inheritDoc}
		 * 
		 * @see java.util.Collection#isEmpty()
		 */
		public boolean isEmpty() {
			return ganglinien.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Collection#iterator()
		 */
		public Iterator<GanglinieMQ> iterator() {
			return ganglinien.iterator();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Collection#remove(java.lang.Object)
		 */
		public boolean remove(final Object o) {
			return ganglinien.remove(o);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Collection#removeAll(java.util.Collection)
		 */
		public boolean removeAll(final Collection<?> c) {
			return ganglinien.removeAll(c);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Collection#retainAll(java.util.Collection)
		 */
		public boolean retainAll(final Collection<?> c) {
			return ganglinien.retainAll(c);
		}

		/**
		 * Setzt das Absenderzeichen. In der Regel wird dieses lediglich aus der
		 * Anfrage in die Antwort kopiert.
		 * <p>
		 * <em>Hinweis:</em> Diese Methode ist nicht Teil der öffentlichen API
		 * und sollte nicht außerhalb der Ganglinie-API verwendet werden.
		 * 
		 * @param absenderZeichen
		 *            ein beliebiger Text.
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
		protected void setDatenStatus(Status datenStatus) {
			this.datenStatus = datenStatus;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Collection#size()
		 */
		public int size() {
			return ganglinien.size();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Collection#toArray()
		 */
		public Object[] toArray() {
			return ganglinien.toArray();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.Collection#toArray(T[])
		 */
		public <T> T[] toArray(final T[] a) {
			return ganglinien.toArray(a);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			String s = getClass().getName() + "[";

			s += "zeitpunkt=" + getZeitpunkt();
			s += ", valid=" + isValid();
			s += ", absenderZeichen=" + absenderZeichen;
			s += ", ganglinien=" + ganglinien;

			return s + "]";
		}

	}

	/** Die PID der Attributgruppe. */
	public static final String ATG_PROGNOSE_GANGLINIEN_ANTWORT = "atg.prognoseGanglinienAntwort";

	/** Die Attributgruppe kann von allen Instanzen gemeinsam genutzt werden. */
	private static AttributeGroup atg;

	/**
	 * Erzeugt eine Anfrage an die Ganglinienprognose.
	 * 
	 * @param app
	 *            die Applikation <em>Ganglinienprognose</em> an die die
	 *            Anfrage gestellt wird.
	 */
	public OdPrognoseGanglinienAntwort(final SystemObjekt app) {
		super(app);

		if (atg == null) {
			DataModel modell = ObjektFactory.getInstanz().getVerbindung()
					.getDataModel();
			atg = modell.getAttributeGroup(ATG_PROGNOSE_GANGLINIEN_ANTWORT);
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
		Set<Aspect> aspekte = new HashSet<Aspect>();
		for (Aspekt a : Aspekte.values()) {
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
		Data daten = erzeugeSendeCache();

		Array ganglinien;
		int i;

		daten.getTextValue("AbsenderZeichen").setText(
				datum.getAbsenderZeichen());

		ganglinien = daten.getArray("PrognoseGanglinie");
		ganglinien.setLength(datum.size());
		i = 0;
		for (GanglinieMQ g : datum) {
			Array stuetzstellen;
			List<Stuetzstelle<Messwerte>> liste;

			// Allgemeines
			ganglinien.getItem(i).getReferenceValue("Messquerschnitt")
					.setSystemObject(g.getMessQuerschnitt().getSystemObject());
			ganglinien.getItem(i).getTimeValue("ZeitpunktPrognoseBeginn")
					.setMillis(g.getIntervall().getStart());
			ganglinien.getItem(i).getTimeValue("ZeitpunktPrognoseEnde")
					.setMillis(g.getIntervall().getEnd());

			// Verfahren
			ganglinien.getItem(i).getUnscaledValue("GanglinienVerfahren").set(
					g.getApproximationDaK());
			ganglinien.getItem(i).getUnscaledValue("Ordnung").set(
					g.getBSplineOrdnung());

			// Stützstellen
			liste = g.getStuetzstellen();
			stuetzstellen = ganglinien.getItem(i).getArray("Stützstelle");
			stuetzstellen.setLength(liste.size());
			for (int j = 0; j < liste.size(); j++) {
				Stuetzstelle<Messwerte> s;

				s = liste.get(j);
				stuetzstellen.getItem(j).getTimeValue("Zeit").setMillis(
						s.getZeitstempel());
				if (s.getWert().getQKfz() != null) {
					stuetzstellen.getItem(j).getScaledValue("QKfz").set(
							s.getWert().getQKfz());
				} else {
					stuetzstellen.getItem(j).getScaledValue("QKfz").set(
							Messwerte.UNDEFINIERT);
				}
				if (s.getWert().getQLkw() != null) {
					stuetzstellen.getItem(j).getScaledValue("QLkw").set(
							s.getWert().getQLkw());
				} else {
					stuetzstellen.getItem(j).getScaledValue("QLkw").set(
							Messwerte.UNDEFINIERT);
				}
				if (s.getWert().getVPkw() != null) {
					stuetzstellen.getItem(j).getScaledValue("VPkw").set(
							s.getWert().getVPkw());
				} else {
					stuetzstellen.getItem(j).getScaledValue("VPkw").set(
							Messwerte.UNDEFINIERT);
				}
				if (s.getWert().getVLkw() != null) {
					stuetzstellen.getItem(j).getScaledValue("VLkw").set(
							s.getWert().getVLkw());
				} else {
					stuetzstellen.getItem(j).getScaledValue("VLkw").set(
							Messwerte.UNDEFINIERT);
				}
			}
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

		Daten datum = new Daten();
		if (result.hasData()) {
			Array ganglinien;
			Data daten = result.getData();
			Interval prognoseZeitraum;

			datum.setAbsenderZeichen(daten.getTextValue("AbsenderZeichen")
					.getText());

			datum.clear();
			ganglinien = daten.getArray("PrognoseGanglinie");
			for (int i = 0; i < ganglinien.getLength(); i++) {
				GanglinieMQ g;
				Array feld;

				g = new GanglinieMQ();

				g.setMessQuerschnitt((MessQuerschnittAllgemein) ObjektFactory
						.getInstanz().getModellobjekt(
								ganglinien.getItem(i).getReferenceValue(
										"Messquerschnitt").getSystemObject()));

				// Verfahren
				g.setApproximationDaK(ganglinien.getItem(i).getUnscaledValue(
						"GanglinienVerfahren").intValue());
				g.setBSplineOrdnung((byte) ganglinien.getItem(i)
						.getUnscaledValue("Ordnung").longValue());

				// Prognosezeitraum
				prognoseZeitraum = new Interval(ganglinien.getItem(i)
						.getTimeValue("ZeitpunktPrognoseBeginn").getMillis(),
						ganglinien.getItem(i).getTimeValue(
								"ZeitpunktPrognoseEnde").getMillis());
				g.setPrognoseZeitraum(prognoseZeitraum);

				// Stützstellen
				feld = ganglinien.getItem(i).getArray("Stützstelle");
				for (int j = 0; j < feld.getLength(); j++) {
					long zeitstempel;
					Double qKfz0, qLkw0, vPkw0, vLkw0;

					zeitstempel = feld.getItem(j).getTimeValue("Zeit")
							.getMillis();
					if (feld.getItem(j).getScaledValue("QKfz").doubleValue() == Messwerte.UNDEFINIERT) {
						qKfz0 = null;
					} else {
						qKfz0 = feld.getItem(j).getScaledValue("QKfz")
								.doubleValue();
					}
					if (feld.getItem(j).getScaledValue("QLkw").doubleValue() == Messwerte.UNDEFINIERT) {
						qLkw0 = null;
					} else {
						qLkw0 = feld.getItem(j).getScaledValue("QLkw")
								.doubleValue();
					}
					if (feld.getItem(j).getScaledValue("VPkw").doubleValue() == Messwerte.UNDEFINIERT) {
						vPkw0 = null;
					} else {
						vPkw0 = feld.getItem(j).getScaledValue("VPkw")
								.doubleValue();
					}
					if (feld.getItem(j).getScaledValue("VLkw").doubleValue() == Messwerte.UNDEFINIERT) {
						vLkw0 = null;
					} else {
						vLkw0 = feld.getItem(j).getScaledValue("VLkw")
								.doubleValue();
					}
					g.setStuetzstelle(zeitstempel, new Messwerte(qKfz0, qLkw0,
							vPkw0, vLkw0));
				}

				datum.add(g);
			}
		}

		datum.setDatenStatus(Datum.Status.getStatus(result.getDataState()));
		datum.setZeitstempel(result.getDataTime());
		setDatum(result.getDataDescription().getAspect(), datum);
		fireDatensatzAktualisiert(result.getDataDescription().getAspect(),
				datum.clone());
	}

}
