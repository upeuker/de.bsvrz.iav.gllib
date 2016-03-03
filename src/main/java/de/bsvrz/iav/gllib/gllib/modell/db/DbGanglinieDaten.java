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
 * Weiﬂenfelser Straﬂe 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib.modell.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.bitctrl.util.Timestamp;

import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;

/**
 * Ein Datum der (virtuellen) Attributgruppe <code>atg.ganglinie</code>, das in
 * der Gangliniendatenbank gespeichert wird.
 *
 * @author BitCtrl Systems GmbH, Thomas Thierfelder
 */
public class DbGanglinieDaten implements List<GanglinieMQ> {

	/** Ganglinienspezifische MQ-Daten aus der Datenbank. */
	private final DbMessQuerschnitt dbMq;

	/** Die Eigenschaft {@code ganglinien}. */
	private final List<GanglinieMQ> ganglinien = new ArrayList<GanglinieMQ>();

	/** Der Zeitstempel des Datums. */
	private long zeitstempel;

	/**
	 * Standardkonstruktor.
	 *
	 * @param dbMq
	 *            Ganglinienspezifische MQ-Daten aus der Datenbank.
	 */
	DbGanglinieDaten(final DbMessQuerschnitt dbMq) {
		this.dbMq = dbMq;
		for (final DbGanglinie dbGl : dbMq.getGanglinien()) {
			final GanglinieMQ dummy = dbGl.convert();
			if (dummy != null) {
				ganglinien.add(dummy);
			}
		}
	}

	/**
	 * Konstruktor fuer eine leere Ganglinie.
	 *
	 * @param mqPid
	 *            PID des MQ.
	 */
	public DbGanglinieDaten(final String mqPid) {
		dbMq = new DbMessQuerschnitt(mqPid);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GanglinieMQ set(final int index, final GanglinieMQ element) {
		return ganglinien.set(index, element);
	}

	/**
	 * Gibt den Zeitstempel des Datums als lesbaren Text zur&uuml;ck.
	 *
	 * @return der Zeitpunkt.
	 */
	public String getZeitpunkt() {
		return Timestamp.absoluteTime(zeitstempel);
	}

	/**
	 * Gibt den Zeitstempel des Datum zur&uuml;ck.
	 *
	 * @return den Zeitstempel oder 0, wenn er nicht bekannt ist.
	 */
	long getZeitstempel() {
		return zeitstempel;
	}

	/**
	 * Legt den Zeitstempel des Datums fest.
	 *
	 * @param zeitstempel
	 *            der neue Zeitstempel.
	 */
	public void setZeitstempel(final long zeitstempel) {
		this.zeitstempel = zeitstempel;
	}

	/**
	 * Erfragt das Datenbankobjekt, in dem die persistenten Daten dieses Objekts
	 * gespeichert werden sollen.<br>
	 * <b>Achtung:</b> Bei jedem Aufruf werden die aktuellen Daten dieses
	 * Objekts umgespeichert!
	 *
	 * @return das Datenbankobjekt, in dem die persistenten Daten dieses Objekts
	 *         gespeichert werden sollen.
	 */
	public DbMessQuerschnitt getDatenbankObjekt() {
		final ArrayList<DbGanglinie> dbGanglinien = new ArrayList<DbGanglinie>();
		for (final GanglinieMQ gl : ganglinien) {
			dbGanglinien.add(gl.convertToDbDatum());
		}
		dbMq.setGanglinien(dbGanglinien);
		return dbMq;
	}

	/**
	 * Erfragt die PID des assoziierten MQ.
	 *
	 * @return die PID des assoziierten MQ.
	 */
	public String getMqPid() {
		return dbMq.getMqPid();
	}

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
	public void add(final int index, final GanglinieMQ element) {
		ganglinien.add(index, element);
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
	public boolean addAll(final int index,
			final Collection<? extends GanglinieMQ> c) {
		return ganglinien.addAll(index, c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		ganglinien.clear();
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
	public List<GanglinieMQ> subList(final int fromIndex, final int toIndex) {
		return ganglinien.subList(fromIndex, toIndex);
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

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof DbGanglinieDaten) {
			DbGanglinieDaten datum;

			datum = (DbGanglinieDaten) obj;
			return ganglinien.equals(datum.ganglinien);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return ganglinien.hashCode();
	}

	@Override
	public String toString() {
		String s;

		s = getClass().getSimpleName() + "["; //$NON-NLS-1$
		s += "zeitpunkt=" + getZeitpunkt(); //$NON-NLS-1$
		s += ", ganglinien=" + ganglinien; //$NON-NLS-1$
		s += "]"; //$NON-NLS-1$

		return s;
	}

}
