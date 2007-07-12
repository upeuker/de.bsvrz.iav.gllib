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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import de.bsvrz.iav.gllib.gllib.events.GanglinienListener;

/**
 * Implementiert nur die Property <code>Ganglinie</code> der Schnittstelle.
 * 
 * @author BitCtrl, Schumann
 * @version $Id$
 */
public abstract class AbstractApproximation implements Approximation,
		GanglinienListener {

	/** Die der Approximation zugrunde liegende Ganglinie. */
	protected final Ganglinie ganglinie;

	/** Liste der verwendeten Stützstellen. */
	protected Stuetzstelle[] stuetzstellen;
	
	/**
	 * Konstruiert eine Approximation, indem der Verweis auf die zu
	 * approximierende Ganglinie gesichert wird.
	 * 
	 * @param ganglinie
	 *            Die zu approximierende Ganglinie
	 */
	protected AbstractApproximation(Ganglinie ganglinie) {
		if (ganglinie == null) {
			throw new NullPointerException(
					"Die Ganglinie darf nicht null sein.");
		}
		this.ganglinie = ganglinie;
		ganglinie.addGanglinienListener(this);
	}

	/**
	 * Bestimmt die Liste der verwendeten St&uuml;tzstellen. Die Liste
	 * entspricht der Ganglinie, abz&uuml;glich der undefinierten
	 * St&uuml;tzstellen.
	 */
	protected void bestimmeStuetzstellen() {
		List<Stuetzstelle> liste;

		liste = new ArrayList<Stuetzstelle>();
		for (Stuetzstelle s : ganglinie.getStuetzstellen()) {
			if (s.getWert() != null) {
				liste.add(s);
			}
		}
		stuetzstellen = liste.toArray(new Stuetzstelle[0]);
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
			interpolation.add(get(zeitstempel));
			zeitstempel += intervallBreite;
		}

		return interpolation;
	}

}
