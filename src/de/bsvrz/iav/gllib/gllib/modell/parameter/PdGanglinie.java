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
import de.bsvrz.iav.gllib.gllib.BSpline;
import de.bsvrz.iav.gllib.gllib.CubicSpline;
import de.bsvrz.iav.gllib.gllib.Polyline;
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
		 * 
		 * @see java.util.List#add(java.lang.Object)
		 */
		public boolean add(GanglinieMQ o) {
			return ganglinien.add(o);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#add(int, java.lang.Object)
		 */
		public void add(int index, GanglinieMQ element) {
			ganglinien.add(index, element);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#addAll(java.util.Collection)
		 */
		public boolean addAll(Collection<? extends GanglinieMQ> c) {
			return ganglinien.addAll(c);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#addAll(int, java.util.Collection)
		 */
		public boolean addAll(int index, Collection<? extends GanglinieMQ> c) {
			return ganglinien.addAll(index, c);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#clear()
		 */
		public void clear() {
			ganglinien.clear();
		}

		/**
		 * Erzeugt eine flache Kopie.
		 * 
		 * {@inheritDoc}
		 * 
		 * @see de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatum#clone()
		 */
		@Override
		public Daten clone() {
			Daten klon = new Daten();

			klon.datenStatus = datenStatus;
			klon.ganglinien.addAll(ganglinien);
			klon.setZeitstempel(getZeitstempel());

			return klon;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#contains(java.lang.Object)
		 */
		public boolean contains(Object o) {
			return ganglinien.contains(o);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#containsAll(java.util.Collection)
		 */
		public boolean containsAll(Collection<?> c) {
			return ganglinien.containsAll(c);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#get(int)
		 */
		public GanglinieMQ get(int index) {
			return ganglinien.get(index);
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
		 * @see java.util.List#indexOf(java.lang.Object)
		 */
		public int indexOf(Object o) {
			return ganglinien.indexOf(o);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#isEmpty()
		 */
		public boolean isEmpty() {
			return ganglinien.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#iterator()
		 */
		public Iterator<GanglinieMQ> iterator() {
			return ganglinien.iterator();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#lastIndexOf(java.lang.Object)
		 */
		public int lastIndexOf(Object o) {
			return ganglinien.lastIndexOf(o);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#listIterator()
		 */
		public ListIterator<GanglinieMQ> listIterator() {
			return ganglinien.listIterator();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#listIterator(int)
		 */
		public ListIterator<GanglinieMQ> listIterator(int index) {
			return ganglinien.listIterator(index);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#remove(int)
		 */
		public GanglinieMQ remove(int index) {
			return ganglinien.remove(index);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#remove(java.lang.Object)
		 */
		public boolean remove(Object o) {
			return ganglinien.remove(o);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#removeAll(java.util.Collection)
		 */
		public boolean removeAll(Collection<?> c) {
			return ganglinien.removeAll(c);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#retainAll(java.util.Collection)
		 */
		public boolean retainAll(Collection<?> c) {
			return ganglinien.retainAll(c);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#set(int, java.lang.Object)
		 */
		public GanglinieMQ set(int index, GanglinieMQ element) {
			return ganglinien.set(index, element);
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
		 * @see java.util.List#size()
		 */
		public int size() {
			return ganglinien.size();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#subList(int, int)
		 */
		public List<GanglinieMQ> subList(int fromIndex, int toIndex) {
			return ganglinien.subList(fromIndex, toIndex);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#toArray()
		 */
		public Object[] toArray() {
			return ganglinien.toArray();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.util.List#toArray(T[])
		 */
		public <T> T[] toArray(T[] a) {
			return ganglinien.toArray(a);
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
	public PdGanglinie(MessQuerschnittAllgemein mq) {
		super(mq);

		if (atg == null) {
			DataModel modell = ObjektFactory.getInstanz().getVerbindung()
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
	 * @see de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatensatz#konvertiere(de.bsvrz.sys.funclib.bitctrl.modell.Datum)
	 */
	@Override
	protected Data konvertiere(Daten datum) {
		Data daten;
		Array ganglinien;
		int i;

		daten = erzeugeSendeCache();

		ganglinien = daten.getArray("Ganglinie");
		ganglinien.setLength(datum.size());
		i = 0;
		for (GanglinieMQ g : datum) {
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
			List<Stuetzstelle<Messwerte>> liste = g.getStuetzstellen();
			int j = 0;
			stuetzstellen.setLength(liste.size());
			for (Stuetzstelle<Messwerte> s : liste) {
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

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.sys.funclib.bitctrl.modell.Datensatz#setDaten(de.bsvrz.dav.daf.main.ResultData)
	 */
	public void setDaten(ResultData result) {
		check(result);

		Daten datum = new Daten();
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

				switch (daten.getUnscaledValue("GanglinienVerfahren")
						.intValue()) {
				case GanglinieMQ.APPROX_BSPLINE:
					g.setApproximation(new BSpline());
					g.setBSplineOrdnung((byte) daten
							.getUnscaledValue("Ordnung").longValue());
					break;
				case GanglinieMQ.APPROX_CUBICSPLINE:
					g.setApproximation(new CubicSpline());
					break;
				case GanglinieMQ.APPROX_POLYLINE:
					g.setApproximation(new Polyline());
					break;
				default:
					g.setApproximation(new BSpline());
					g.setBSplineOrdnung((byte) 5);
				}

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
					g.setStuetzstelle(zeitstempel, new Messwerte(qKfz0, qLkw0,
							vPkw0, vLkw0));
				}

				datum.add(g);
			}
		}

		datum.setDatenStatus(Datum.Status.getStatus(result.getDataState()
				.getCode()));
		datum.setZeitstempel(result.getDataTime());
		setDatum(result.getDataDescription().getAspect(), datum);
		fireDatensatzAktualisiert(result.getDataDescription().getAspect(),
				datum.clone());
	}
}
