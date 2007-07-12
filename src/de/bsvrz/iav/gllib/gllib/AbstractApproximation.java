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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Implementiert nur die Property <code>Ganglinie</code> der Schnittstelle.
 * 
 * @author BitCtrl, Schumann
 * @version $Id$
 * @param <T>
 *            der Typ der approximierten Werte.
 */
public abstract class AbstractApproximation<T> implements Approximation<T> {

	/** Liste der verwendeten Stützstellen. */
	protected List<Stuetzstelle<T>> stuetzstellen;

	/**
	 * Bestimmt die Liste der verwendeten St&uuml;tzstellen. Die Liste
	 * entspricht der Ganglinie, abz&uuml;glich der undefinierten
	 * St&uuml;tzstellen.
	 * <p>
	 * {@inheritDoc}
	 */
	public void setStuetzstellen(Collection<Stuetzstelle<T>> menge) {
		stuetzstellen = new ArrayList<Stuetzstelle<T>>();
		for (Stuetzstelle<T> s : menge) {
			if (s.getWert() != null) {
				stuetzstellen.add(s);
			}
		}
		Collections.sort(stuetzstellen);
	}

	/**
	 * {@inheritDoc}
	 */
	public SortedSet<Stuetzstelle<T>> interpoliere(long intervallBreite) {
		if (intervallBreite <= 0) {
			throw new IllegalArgumentException(
					"Intervallbreite muss größer null sein.");
		}

		SortedSet<Stuetzstelle<T>> interpolation;
		long zeitstempel;

		interpolation = new TreeSet<Stuetzstelle<T>>();

		// Sonderfall: keine Stützstellen vorhanden
		if (stuetzstellen.size() == 0) {
			return interpolation;
		}

		// Stützstellen an den Intervallgrenzen bestimmen
		zeitstempel = stuetzstellen.get(0).getZeitstempel();
		while (zeitstempel < stuetzstellen.get(stuetzstellen.size() - 1)
				.getZeitstempel()) {
			interpolation.add(get(zeitstempel));
			zeitstempel += intervallBreite;
		}

		return interpolation;
	}

}
