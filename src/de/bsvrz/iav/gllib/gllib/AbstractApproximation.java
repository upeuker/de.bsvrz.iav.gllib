/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.5 Funktionen Ganglinie
 * Copyright (C) 2007 BitCtrl Systems GmbH 
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact Information:
 * BitCtrl Systems GmbH
 * Weißenfelser Straße 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

import java.util.SortedSet;
import java.util.TreeSet;

import de.bsvrz.sys.funclib.bitctrl.i18n.Messages;

/**
 * Implementiert nur die Property <code>Ganglinie</code> der Schnittstelle.
 * 
 * @author BitCtrl, Schumann
 * @version $Id$
 */
public abstract class AbstractApproximation implements Approximation {

	/** Die der Approximation zugrunde liegende Ganglinie. */
	protected Ganglinie ganglinie;

	/**
	 * {@inheritDoc}
	 */
	public void setGanglinie(Ganglinie ganglinie) {
		this.ganglinie = ganglinie;
	}

	/**
	 * {@inheritDoc}
	 */
	public SortedSet<Stuetzstelle> getInterpolation(int anzahlIntervalle) {
		if (anzahlIntervalle < 0) {
			Messages.get(GlLibMessages.BadCount, anzahlIntervalle);
		}

		SortedSet<Stuetzstelle> interpolation = new TreeSet<Stuetzstelle>();
		long start;
		long intervall;
		Stuetzstelle s;
		long zeitstempel;

		// Sonderfall: keine Stützstellen vorhanden
		if (ganglinie.size() == 0) {
			return interpolation;
		}

		start = ganglinie.first().zeitstempel;
		intervall = ganglinie.last().zeitstempel - start / anzahlIntervalle;

		for (int i = 0; i <= anzahlIntervalle; i++) {
			zeitstempel = start + i * intervall;
			s = new Stuetzstelle(zeitstempel, getStuetzstelle(zeitstempel).wert);
			interpolation.add(s);
			zeitstempel += intervall;
		}

		return interpolation;
	}

	/**
	 * Gibt, falls vorhanden, die n&auml;chste St&uuml;tzstelle vor dem
	 * Zeitstempel zur&uuml;ck.
	 * 
	 * @param zeitstempel
	 *            Ein Zeitstempel
	 * @return St&uuml;tzstelle oder {@code null}, falls keine existiert
	 */
	protected Stuetzstelle naechsteStuetzstelleDavor(long zeitstempel) {
		Stuetzstelle s;
		SortedSet<Stuetzstelle> kopf;

		s = new Stuetzstelle(zeitstempel);
		kopf = ganglinie.headSet(s);

		if (kopf.isEmpty()) {
			return null;
		}

		return kopf.last();
	}

	/**
	 * Gibt, falls vorhanden, die n&auml;chste St&uuml;tzstelle nach dem
	 * Zeitstempel zur&uuml;ck.
	 * 
	 * @param zeitstempel
	 *            Ein Zeitstempel
	 * @return St&uuml;tzstelle oder {@code null}, falls keine existiert
	 */
	protected Stuetzstelle naechsteStuetzstelleDanach(long zeitstempel) {
		Stuetzstelle s;
		SortedSet<Stuetzstelle> kopf;

		s = new Stuetzstelle(zeitstempel + 1); // +1 wegen >= von tailSet()
		kopf = ganglinie.tailSet(s);

		if (kopf.isEmpty()) {
			return null;
		}

		return kopf.first();
	}

}
