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

package de.bsvrz.iav.gllib.gllib.modell.onlinedaten;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import com.bitctrl.util.Interval;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.Data.Array;
import de.bsvrz.dav.daf.main.ResultData;
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
 */
public class OdPrognoseGanglinienAntwort
		extends AbstractOnlineDatensatz<OdPrognoseGanglinienAntwort.Daten> {

	/**
	 * Die vorhandenen Aspekte des Datensatzes.
	 */
	public enum Aspekte implements Aspekt {

		/** Der Aspekt {@code asp.antwort}. */
		Antwort("asp.antwort"); //$NON-NLS-1$

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
		@Override
		public Aspect getAspekt() {
			return aspekt;
		}

		/**
		 * {@inheritDoc}
		 *
		 * @see de.bsvrz.sys.funclib.bitctrl.modell.Aspekt#getName()
		 */
		@Override
		public String getName() {
			return aspekt.getNameOrPidOrId();
		}

	}

	/**
	 * Kapselt die Daten des Datensatzes.
	 */
	public static class Daten extends AbstractDatum
			implements List<GanglinieMQ> {

		/**
		 * Eine beliebige Zeichenkette die der Absender frei eingetragen kann.
		 */
		private String absenderZeichen;

		/** Hash zum einfachen auffinden der passenden Ganglinie. */
		private final List<GanglinieMQ> ganglinien = new ArrayList<GanglinieMQ>();

		/** Der aktuelle Datenstatus. */
		private Status datenStatus = Datum.Status.UNDEFINIERT;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean add(final GanglinieMQ o) {
			return ganglinien.add(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean addAll(final Collection<? extends GanglinieMQ> c) {
			return ganglinien.addAll(c);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			ganglinien.clear();

		}

		/**
		 * Es wird eine flache Kopie zurückgegeben.
		 *
		 * {@inheritDoc}
		 */
		@Override
		public Daten clone() {
			final Daten klon = new Daten();

			klon.setZeitstempel(getZeitstempel());
			klon.datenStatus = datenStatus;
			klon.absenderZeichen = absenderZeichen;
			klon.ganglinien.addAll(ganglinien);

			return klon;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean contains(final Object o) {
			return ganglinien.contains(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean containsAll(final Collection<?> c) {
			return ganglinien.containsAll(c);
		}

		/**
		 * Gibt das Zeichen des Absenders zurück. Der Text wurde bei der Anfrage
		 * in die Anfragenachricht eingetragen und von der Ganglinienprognose in
		 * die Antwort kopiert. Somit kann die anfragende Applikation mehrere
		 * Anfragen unterscheiden.
		 *
		 * @return das Absenderzeichen.
		 */
		public String getAbsenderZeichen() {
			return absenderZeichen;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status getDatenStatus() {
			return datenStatus;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEmpty() {
			return ganglinien.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GanglinieMQ> iterator() {
			return ganglinien.iterator();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean remove(final Object o) {
			return ganglinien.remove(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean removeAll(final Collection<?> c) {
			return ganglinien.removeAll(c);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
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
		protected void setDatenStatus(final Status datenStatus) {
			this.datenStatus = datenStatus;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return ganglinien.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object[] toArray() {
			return ganglinien.toArray();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <T> T[] toArray(final T[] a) {
			return ganglinien.toArray(a);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			String s = getClass().getName() + "["; //$NON-NLS-1$

			s += "zeitpunkt=" + getZeitpunkt(); //$NON-NLS-1$
			s += ", datenStatus=" + getDatenStatus(); //$NON-NLS-1$
			s += ", absenderZeichen=" + absenderZeichen; //$NON-NLS-1$
			s += ", ganglinien=" + ganglinien; //$NON-NLS-1$

			return s + "]"; //$NON-NLS-1$
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final GanglinieMQ element) {
			ganglinien.add(index, element);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean addAll(final int index,
				final Collection<? extends GanglinieMQ> c) {
			return ganglinien.addAll(c);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GanglinieMQ get(final int index) {
			return ganglinien.get(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int indexOf(final Object o) {
			return ganglinien.indexOf(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int lastIndexOf(final Object o) {
			return ganglinien.lastIndexOf(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListIterator<GanglinieMQ> listIterator() {
			return ganglinien.listIterator();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListIterator<GanglinieMQ> listIterator(final int index) {
			return ganglinien.listIterator(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GanglinieMQ remove(final int index) {
			return ganglinien.remove(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GanglinieMQ set(final int index, final GanglinieMQ element) {
			return ganglinien.set(index, element);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<GanglinieMQ> subList(final int fromIndex,
				final int toIndex) {
			return ganglinien.subList(fromIndex, toIndex);
		}

	}

	/** Die PID der Attributgruppe. */
	public static final String ATG_PROGNOSE_GANGLINIEN_ANTWORT = "atg.prognoseGanglinienAntwort"; //$NON-NLS-1$

	/** Die Attributgruppe kann von allen Instanzen gemeinsam genutzt werden. */
	private static AttributeGroup atg;

	/**
	 * Erzeugt eine Anfrage an die Ganglinienprognose.
	 *
	 * @param app
	 *            die Applikation <em>Ganglinienprognose</em> an die die Anfrage
	 *            gestellt wird.
	 */
	public OdPrognoseGanglinienAntwort(final SystemObjekt app) {
		super(app);

		if (atg == null) {
			final DataModel modell = ObjektFactory.getInstanz().getVerbindung()
					.getDataModel();
			atg = modell.getAttributeGroup(ATG_PROGNOSE_GANGLINIEN_ANTWORT);
			assert atg != null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Daten erzeugeDatum() {
		return new Daten();
	}

	/**
	 * {@inheritDoc}
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
	 */
	@Override
	public AttributeGroup getAttributGruppe() {
		return atg;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Data konvertiere(final Daten datum) {
		final Data daten = erzeugeSendeCache();
		final Array ganglinien;

		daten.getTextValue("AbsenderZeichen").setText( //$NON-NLS-1$
				datum.getAbsenderZeichen());

		ganglinien = daten.getArray("PrognoseGanglinie"); //$NON-NLS-1$
		ganglinien.setLength(datum.size());
		for (int i = 0; i < datum.size(); ++i) {
			final Array stuetzstellen;
			final List<Stuetzstelle<Messwerte>> liste;
			final GanglinieMQ g;

			g = datum.get(i);

			// Allgemeines
			ganglinien.getItem(i).getReferenceValue("Messquerschnitt") //$NON-NLS-1$
					.setSystemObject(g.getMessQuerschnitt().getSystemObject());
			ganglinien.getItem(i).getTimeValue("ZeitpunktPrognoseBeginn") //$NON-NLS-1$
					.setMillis(g.getIntervall().getStart());
			ganglinien.getItem(i).getTimeValue("ZeitpunktPrognoseEnde") //$NON-NLS-1$
					.setMillis(g.getIntervall().getEnd());

			// Verfahren
			ganglinien.getItem(i).getUnscaledValue("GanglinienVerfahren").set( //$NON-NLS-1$
					g.getApproximationDaK());
			ganglinien.getItem(i).getUnscaledValue("Ordnung").set( //$NON-NLS-1$
					g.getBSplineOrdnung());

			// Stützstellen
			liste = g.getStuetzstellen();
			stuetzstellen = ganglinien.getItem(i).getArray("Stützstelle"); //$NON-NLS-1$
			stuetzstellen.setLength(liste.size());
			for (int j = 0; j < liste.size(); j++) {
				Stuetzstelle<Messwerte> s;

				s = liste.get(j);
				stuetzstellen.getItem(j).getTimeValue("Zeit").setMillis( //$NON-NLS-1$
						s.getZeitstempel());
				if (s.getWert().getQKfz() != null) {
					stuetzstellen.getItem(j).getScaledValue("QKfz").set( //$NON-NLS-1$
							s.getWert().getQKfz());
				} else {
					stuetzstellen.getItem(j).getScaledValue("QKfz").set( //$NON-NLS-1$
							Messwerte.UNDEFINIERT);
				}
				if (s.getWert().getQLkw() != null) {
					stuetzstellen.getItem(j).getScaledValue("QLkw").set( //$NON-NLS-1$
							s.getWert().getQLkw());
				} else {
					stuetzstellen.getItem(j).getScaledValue("QLkw").set( //$NON-NLS-1$
							Messwerte.UNDEFINIERT);
				}
				if (s.getWert().getVPkw() != null) {
					stuetzstellen.getItem(j).getScaledValue("VPkw").set( //$NON-NLS-1$
							s.getWert().getVPkw());
				} else {
					stuetzstellen.getItem(j).getScaledValue("VPkw").set( //$NON-NLS-1$
							Messwerte.UNDEFINIERT);
				}
				if (s.getWert().getVLkw() != null) {
					stuetzstellen.getItem(j).getScaledValue("VLkw").set( //$NON-NLS-1$
							s.getWert().getVLkw());
				} else {
					stuetzstellen.getItem(j).getScaledValue("VLkw").set( //$NON-NLS-1$
							Messwerte.UNDEFINIERT);
				}
			}
		}

		return daten;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDaten(final ResultData result) {
		check(result);

		final Daten datum = new Daten();
		if (result.hasData()) {
			Array ganglinien;
			final Data daten = result.getData();
			Interval prognoseZeitraum;

			datum.setAbsenderZeichen(daten.getTextValue("AbsenderZeichen") //$NON-NLS-1$
					.getText());
			ganglinien = daten.getArray("PrognoseGanglinie"); //$NON-NLS-1$
			for (int i = 0; i < ganglinien.getLength(); ++i) {
				GanglinieMQ g;
				Array feld;

				g = new GanglinieMQ();

				g.setMessQuerschnitt(
						(MessQuerschnittAllgemein) ObjektFactory.getInstanz()
								.getModellobjekt(ganglinien.getItem(i)
										.getReferenceValue("Messquerschnitt") //$NON-NLS-1$
										.getSystemObject()));

				// Verfahren
				g.setApproximationDaK(ganglinien.getItem(i)
						.getUnscaledValue("GanglinienVerfahren").intValue()); //$NON-NLS-1$
				g.setBSplineOrdnung((byte) ganglinien.getItem(i)
						.getUnscaledValue("Ordnung").longValue()); //$NON-NLS-1$

				// Prognosezeitraum
				prognoseZeitraum = new Interval(ganglinien.getItem(i)
						.getTimeValue("ZeitpunktPrognoseBeginn").getMillis(), //$NON-NLS-1$
						ganglinien.getItem(i)
								.getTimeValue("ZeitpunktPrognoseEnde") //$NON-NLS-1$
								.getMillis());
				g.setPrognoseZeitraum(prognoseZeitraum);

				// Stützstellen
				feld = ganglinien.getItem(i).getArray("Stützstelle"); //$NON-NLS-1$
				for (int j = 0; j < feld.getLength(); j++) {
					long zeitstempel;
					Double qKfz0, qLkw0, vPkw0, vLkw0;

					zeitstempel = feld.getItem(j).getTimeValue("Zeit") //$NON-NLS-1$
							.getMillis();
					if (feld.getItem(j).getScaledValue("QKfz") //$NON-NLS-1$
							.doubleValue() == Messwerte.UNDEFINIERT) {
						qKfz0 = null;
					} else {
						qKfz0 = feld.getItem(j).getScaledValue("QKfz") //$NON-NLS-1$
								.doubleValue();
					}
					if (feld.getItem(j).getScaledValue("QLkw") //$NON-NLS-1$
							.doubleValue() == Messwerte.UNDEFINIERT) {
						qLkw0 = null;
					} else {
						qLkw0 = feld.getItem(j).getScaledValue("QLkw") //$NON-NLS-1$
								.doubleValue();
					}
					if (feld.getItem(j).getScaledValue("VPkw") //$NON-NLS-1$
							.doubleValue() == Messwerte.UNDEFINIERT) {
						vPkw0 = null;
					} else {
						vPkw0 = feld.getItem(j).getScaledValue("VPkw") //$NON-NLS-1$
								.doubleValue();
					}
					if (feld.getItem(j).getScaledValue("VLkw") //$NON-NLS-1$
							.doubleValue() == Messwerte.UNDEFINIERT) {
						vLkw0 = null;
					} else {
						vLkw0 = feld.getItem(j).getScaledValue("VLkw") //$NON-NLS-1$
								.doubleValue();
					}
					g.put(zeitstempel,
							new Messwerte(qKfz0, qLkw0, vPkw0, vLkw0));
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
