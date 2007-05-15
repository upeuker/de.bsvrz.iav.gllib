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
		if (ganglinie == null) {
			throw new NullPointerException(
					"Die Ganglinie darf nicht null sein.");
		}
		this.ganglinie = ganglinie;
	}

	/**
	 * {@inheritDoc}
	 */
	public SortedSet<Stuetzstelle> interpoliere(long intervallBreite) {
		if (intervallBreite <= 0) {
			throw new IllegalArgumentException(
					"Intervallbreite muss größer null sein.");
		}

		SortedSet<Stuetzstelle> interpolation = new TreeSet<Stuetzstelle>();
		long zeitstempel;

		// Sonderfall: keine Stützstellen vorhanden
		if (ganglinie.getStuetzstellen().size() == 0) {
			return interpolation;
		}

		// Stützstellen an den Intervallgrenzen bestimmen
		zeitstempel = ganglinie.getIntervall().start;
		while (zeitstempel < ganglinie.getIntervall().ende) {
			try {
				interpolation.add(get(zeitstempel));
			} catch (UndefiniertException e) {
				// einfach ignorieren
			}
			zeitstempel += intervallBreite;
		}

		return interpolation;
	}

}
