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

package de.bsvrz.iav.gllib.gllib.modell.parameter;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.Data.Array;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.DataModel;
import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatum;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractParameterDatensatz;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.MessQuerschnittAllgemein;

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

		/** Das Flag f&uuml;r die G&uuml;ltigkeit des Datensatzes. */
		private boolean valid;

		/** Die Eigenschaft {@code ganglinien}. */
		private List<GanglinieMQ> ganglinien;

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

			klon.valid = valid;
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
		 * @see de.bsvrz.sys.funclib.bitctrl.modell.Datum#isValid()
		 */
		public boolean isValid() {
			return valid;
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
	 * @see de.bsvrz.sys.funclib.bitctrl.modell.Datensatz#setDaten(de.bsvrz.dav.daf.main.ResultData)
	 */
	public void setDaten(ResultData result) {
		check(result);

		Daten datum = new Daten();
		if (result.hasData()) {
			Array feld;

			feld = result.getData().getArray("Ganglinie");
			for (int i = 0; i < feld.getLength(); i++) {
				GanglinieMQ g;

				g = new GanglinieMQ();
				g.setDatenVonGanglinie(feld.getItem(i));
				datum.add(g);
			}

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
		Data daten;
		Array feld;
		int i;

		daten = erzeugeSendeCache();

		feld = daten.getArray("Ganglinie");
		feld.setLength(datum.size());
		i = 0;
		for (GanglinieMQ g : datum) {
			g.getDatenFuerGanglinie(feld.getItem(i++));
		}

		return daten;
	}
}
