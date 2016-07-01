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

package de.bsvrz.iav.gllib.gllib.speicher;

import java.util.Vector;

/**
 * Generischer thread-safe FiFo-Puffer.
 *
 * @param <T>
 *            Der Typ der gepufferten Daten.
 * 
 * @author BitCtrl Systems GmbH, Thomas Thierfelder
 */
public class FifoBuffer<T> {

	/** Puffergroesse. */
	private int size = 0;

	/** Datenvektor. */
	private final Vector<T> fifo = new Vector<T>();

	/**
	 * Schreibt ein Element in den Puffer.
	 *
	 * @param obj
	 *            ein Element.
	 */
	public synchronized void add(final T obj) {
		fifo.addElement(obj);
		size++;
		notify();
	}

	/**
	 * Liest das erste Element aus dem Puffer und loescht es. Haelt solange, bis
	 * mindestens ein Element im Puffer steht.
	 *
	 * @return das erste in den Puffer geschriebene Element.
	 */
	public synchronized T get() {
		return getIntern(-1);
	}

	/**
	 * Liest das erste Element aus dem Puffer und loescht es. Haelt solange, bis
	 * mindestens ein Element im Puffer steht (bzw. Timeout).
	 *
	 * @param timeout
	 *            Timeout in Millisekunden.
	 * @return das erste in den Puffer geschriebene Element oder
	 *         <code>null</code>, wenn der Puffer leer ist und das Timeout
	 *         erreicht wurde.
	 */
	public synchronized T get(final int timeout) {
		return getIntern(timeout);
	}

	/**
	 * Erfragt die Anzahl der im Puffer befindlichen Elemente.
	 *
	 * @return die Anzahl der im Puffer befindlichen Elemente.
	 */
	public final int size() {
		return size;
	}

	private synchronized T getIntern(final int timeout) {
		T obj = null;
		if (size == 0) {
			try {
				if (timeout == -1) {
					wait();
				} else {
					wait(timeout);
				}
			} catch (final InterruptedException e) {
				// kann ignoriert werden
			}
		}
		if (size != 0) {
			obj = fifo.elementAt(0);
			fifo.removeElementAt(0);
			size--;
			notify();
		}
		return obj;
	}

	/**
	 * Schliest den Puffer.
	 */
	public void close() {
		synchronized (this) {
			notifyAll();
		}
	}

}