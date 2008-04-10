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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.Data.Array;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.DataModel;
import de.bsvrz.iav.gllib.gllib.Stuetzstelle;
import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;
import de.bsvrz.iav.gllib.gllib.dav.Messwerte;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatum;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractParameterDatensatz;
import de.bsvrz.sys.funclib.bitctrl.modell.Datum;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.objekte.EreignisTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.objekte.MessQuerschnittAllgemein;

/**
 * Kapselt die Parameterattributgruppe {@code atg.ganglinie}.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class PdGanglinie extends AbstractParameterDatensatz<PdGanglinie.Daten> {

	/**
	 * Kapselt die Daten des Datensatzes.
	 */
	public class Daten extends AbstractDatum implements List<GanglinieMQ> {

		/** Die Eigenschaft {@code ganglinien}. */
		private final List<GanglinieMQ> ganglinien = new ArrayList<GanglinieMQ>();

		/** Der aktuelle Datenstatus. */
		private Status datenStatus = Datum.Status.UNDEFINIERT;

		/**
		 * {@inheritDoc}
		 */
		public boolean add(final GanglinieMQ o) {
			return ganglinien.add(o);
		}

		/**
		 * {@inheritDoc}
		 */
		public void add(final int index, final GanglinieMQ element) {
			ganglinien.add(index, element);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean addAll(final Collection<? extends GanglinieMQ> c) {
			return ganglinien.addAll(c);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean addAll(final int index,
				final Collection<? extends GanglinieMQ> c) {
			return ganglinien.addAll(index, c);
		}

		/**
		 * {@inheritDoc}
		 */
		public void clear() {
			ganglinien.clear();
		}

		/**
		 * Erzeugt eine flache Kopie.
		 * 
		 * {@inheritDoc}
		 */
		@Override
		public Daten clone() {
			final Daten klon = new Daten();

			klon.datenStatus = datenStatus;
			klon.ganglinien.addAll(ganglinien);
			klon.setZeitstempel(getZeitstempel());

			return klon;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean contains(final Object o) {
			return ganglinien.contains(o);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean containsAll(final Collection<?> c) {
			return ganglinien.containsAll(c);
		}

		/**
		 * {@inheritDoc}
		 */
		public GanglinieMQ get(final int index) {
			return ganglinien.get(index);
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
		public int indexOf(final Object o) {
			return ganglinien.indexOf(o);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isEmpty() {
			return ganglinien.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		public Iterator<GanglinieMQ> iterator() {
			return ganglinien.iterator();
		}

		/**
		 * {@inheritDoc}
		 */
		public int lastIndexOf(final Object o) {
			return ganglinien.lastIndexOf(o);
		}

		/**
		 * {@inheritDoc}
		 */
		public ListIterator<GanglinieMQ> listIterator() {
			return ganglinien.listIterator();
		}

		/**
		 * {@inheritDoc}
		 */
		public ListIterator<GanglinieMQ> listIterator(final int index) {
			return ganglinien.listIterator(index);
		}

		/**
		 * {@inheritDoc}
		 */
		public GanglinieMQ remove(final int index) {
			return ganglinien.remove(index);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean remove(final Object o) {
			return ganglinien.remove(o);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean removeAll(final Collection<?> c) {
			return ganglinien.removeAll(c);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean retainAll(final Collection<?> c) {
			return ganglinien.retainAll(c);
		}

		/**
		 * {@inheritDoc}
		 */
		public GanglinieMQ set(final int index, final GanglinieMQ element) {
			return ganglinien.set(index, element);
		}

		/**
		 * {@inheritDoc}
		 */
		public int size() {
			return ganglinien.size();
		}

		/**
		 * {@inheritDoc}
		 */
		public List<GanglinieMQ> subList(final int fromIndex, final int toIndex) {
			return ganglinien.subList(fromIndex, toIndex);
		}

		/**
		 * {@inheritDoc}
		 */
		public Object[] toArray() {
			return ganglinien.toArray();
		}

		/**
		 * {@inheritDoc}
		 */
		public <T> T[] toArray(final T[] a) {
			return ganglinien.toArray(a);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj instanceof PdGanglinie.Daten) {
				PdGanglinie.Daten datum;

				datum = (PdGanglinie.Daten) obj;
				return ganglinien.equals(datum.ganglinien);
			}
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			String s;

			s = getClass().getSimpleName() + "[";
			s += "zeitpunkt=" + getZeitpunkt();
			s += ", datenStatus=" + getDatenStatus();
			s += ", ganglinien=" + ganglinien;
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
	public static final String ATG_GANGLINIE = "atg.ganglinie";

	/** Die Attributgruppe kann von allen Instanzen gemeinsam genutzt werden. */
	private static AttributeGroup atg;

	/**
	 * Initialisiert den Parameter.
	 * 
	 * @param mq
	 *            ein Messquerschnitt
	 */
	public PdGanglinie(final MessQuerschnittAllgemein mq) {
		super(mq);

		if (atg == null) {
			final DataModel modell = ObjektFactory.getInstanz().getVerbindung()
					.getDataModel();
			atg = modell.getAttributeGroup(ATG_GANGLINIE);
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
			Array ganglinien;

			ganglinien = result.getData().getArray("Ganglinie");
			for (int i = 0; i < ganglinien.getLength(); i++) {
				GanglinieMQ g;
				Array stuetzstellen;
				Data daten;

				g = new GanglinieMQ();
				g.setMessQuerschnitt((MessQuerschnittAllgemein) getObjekt());
				daten = ganglinien.getItem(i);

				g.setEreignisTyp((EreignisTyp) ObjektFactory.getInstanz()
						.getModellobjekt(
								daten.getReferenceValue("EreignisTyp")
										.getSystemObject()));
				g.setAnzahlVerschmelzungen(daten.getUnscaledValue(
						"AnzahlVerschmelzungen").longValue());
				g.setLetzteVerschmelzung(daten.getTimeValue(
						"LetzteVerschmelzung").getMillis());
				g.setTyp(daten.getUnscaledValue("GanglinienTyp").intValue());

				if (daten.getUnscaledValue("Referenzganglinie").intValue() == 1) {
					g.setReferenz(true);
				} else {
					g.setReferenz(false);
				}

				g.setApproximationDaK(daten.getUnscaledValue(
						"GanglinienVerfahren").intValue());
				g.setBSplineOrdnung((byte) daten.getUnscaledValue("Ordnung")
						.longValue());

				stuetzstellen = daten.getArray("Stützstelle");
				for (int j = 0; j < stuetzstellen.getLength(); j++) {
					long zeitstempel;
					Double qKfz0, qLkw0, vPkw0, vLkw0;

					zeitstempel = stuetzstellen.getItem(j).getTimeValue("Zeit")
							.getMillis();
					qKfz0 = stuetzstellen.getItem(j).getScaledValue("QKfz")
							.doubleValue();
					if (qKfz0 == Messwerte.UNDEFINIERT) {
						qKfz0 = null;
					}
					qLkw0 = stuetzstellen.getItem(j).getScaledValue("QLkw")
							.doubleValue();
					if (qLkw0 == Messwerte.UNDEFINIERT) {
						qLkw0 = null;
					}
					vPkw0 = stuetzstellen.getItem(j).getScaledValue("VPkw")
							.doubleValue();
					if (vPkw0 == Messwerte.UNDEFINIERT) {
						vPkw0 = null;
					}
					vLkw0 = stuetzstellen.getItem(j).getScaledValue("VLkw")
							.doubleValue();
					if (vLkw0 == Messwerte.UNDEFINIERT) {
						vLkw0 = null;
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

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatensatz#konvertiere(de.bsvrz.sys.funclib.bitctrl.modell.Datum)
	 */
	@Override
	protected Data konvertiere(final Daten datum) {
		Data daten;
		Array ganglinien;
		int i;

		daten = erzeugeSendeCache();

		ganglinien = daten.getArray("Ganglinie");
		ganglinien.setLength(datum.size());
		i = 0;
		for (final GanglinieMQ g : datum) {
			Array stuetzstellen;

			ganglinien.getItem(i).getReferenceValue("EreignisTyp")
					.setSystemObject(g.getEreignisTyp().getSystemObject());
			ganglinien.getItem(i).getUnscaledValue("AnzahlVerschmelzungen")
					.set(g.getAnzahlVerschmelzungen());
			ganglinien.getItem(i).getTimeValue("LetzteVerschmelzung")
					.setMillis(g.getLetzteVerschmelzung());
			ganglinien.getItem(i).getUnscaledValue("GanglinienTyp").set(
					g.getTyp());

			if (g.isReferenz()) {
				ganglinien.getItem(i).getUnscaledValue("Referenzganglinie")
						.setText("Ja");
			} else {
				ganglinien.getItem(i).getUnscaledValue("Referenzganglinie")
						.setText("Nein");
			}

			ganglinien.getItem(i).getUnscaledValue("GanglinienVerfahren").set(
					g.getApproximationDaK());
			ganglinien.getItem(i).getUnscaledValue("Ordnung").set(
					g.getBSplineOrdnung());

			stuetzstellen = ganglinien.getItem(i).getArray("Stützstelle");
			final List<Stuetzstelle<Messwerte>> liste = g.getStuetzstellen();
			int j = 0;
			stuetzstellen.setLength(liste.size());
			for (final Stuetzstelle<Messwerte> s : liste) {
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

				if (s.getWert().getVLkw() != null) {
					stuetzstellen.getItem(j).getScaledValue("VLkw").set(
							s.getWert().getVLkw());
				} else {
					stuetzstellen.getItem(j).getScaledValue("VLkw").set(
							Messwerte.UNDEFINIERT);
				}

				if (s.getWert().getVPkw() != null) {
					stuetzstellen.getItem(j).getScaledValue("VPkw").set(
							s.getWert().getVPkw());
				} else {
					stuetzstellen.getItem(j).getScaledValue("VPkw").set(
							Messwerte.UNDEFINIERT);
				}

				j++;
			}

			i++;
		}

		return daten;
	}
}
