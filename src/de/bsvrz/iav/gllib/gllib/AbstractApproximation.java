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
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public abstract class AbstractApproximation implements Approximation {

	/** Liste der verwendeten Stützstellen. */
	private final List<Stuetzstelle<Double>> stuetzstellen;

	/**
	 * Initialisiert die St&uuml;tzstellenliste.
	 */
	protected AbstractApproximation() {
		stuetzstellen = new ArrayList<Stuetzstelle<Double>>();
	}

	/**
	 * Wirft immer eine {@link java.lang.CloneNotSupportedException}.
	 * <p>
	 * {@inheritDoc}
	 * 
	 * @throws CloneNotSupportedException
	 */
	@Override
	public Approximation clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * {@inheritDoc}
	 */
	public SortedSet<Stuetzstelle<Double>> interpoliere(long intervallBreite) {
		if (intervallBreite <= 0) {
			throw new IllegalArgumentException(
					"Intervallbreite muss größer null sein.");
		}

		SortedSet<Stuetzstelle<Double>> interpolation;
		long zeitstempel;

		interpolation = new TreeSet<Stuetzstelle<Double>>();

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

	/**
	 * Bestimmt die Liste der verwendeten St&uuml;tzstellen. Die Liste
	 * entspricht der Ganglinie, abz&uuml;glich der undefinierten
	 * St&uuml;tzstellen.
	 * <p>
	 * {@inheritDoc}
	 */
	public void setStuetzstellen(Collection<Stuetzstelle<Double>> menge) {
		stuetzstellen.clear();
		for (Stuetzstelle<Double> s : menge) {
			if (s.getWert() != null) {
				stuetzstellen.add(s);
			}
		}
		Collections.sort(stuetzstellen);
	}

	/**
	 * Gibt die Anzahl der St&uuml;tzstellen zur&uuml;ck.
	 * 
	 * @return die St&uuml;tzstellenanzahl.
	 */
	protected int anzahl() {
		return stuetzstellen.size();
	}

	/**
	 * Gibt die St&uuml;tzstelle mit dem angegebenen Index zur&uml;ck.
	 * 
	 * @param index
	 *            ein g&uuml;ltiger Index.
	 * @return die St&uuml;tzstelle zum Index.
	 */
	protected Stuetzstelle<Double> get(int index) {
		return stuetzstellen.get(index);
	}

}
