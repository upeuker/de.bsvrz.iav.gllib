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

import de.bsvrz.sys.funclib.bitctrl.i18n.Messages;

/**
 * Approximation einer Ganglinie mit Hilfe eines B-Splines beliebiger Ordung.
 * 
 * @author BitCtrl, Schumann
 * @version $Id$
 */
public class BSpline extends AbstractApproximation {

	/** Die Ordnung des B-Splines. */
	private int ordnung;

	/**
	 * Erstellt einen B-Spline der Ordung 5.
	 * 
	 * @see AbstractApproximation
	 */
	public BSpline() {
		setOrdnung(5);
	}

	/**
	 * Erstellt einen B-Spline beliebiger Ordnung.
	 * 
	 * @see AbstractApproximation
	 * @param ordnung
	 *            Ordnung
	 */
	public BSpline(int ordnung) {
		setOrdnung(ordnung);
	}

	/**
	 * {@inheritDoc}
	 */
	public Stuetzstelle get(long zeitstempel) {
		// TODO Automatisch erstellter Methoden-Stub
		return null;
	}

	/**
	 * Gibt die Ordgung des B-Splines zur&uuml;ck.
	 * 
	 * @return Ordnung
	 */
	public int getOrdnung() {
		return ordnung;
	}

	/**
	 * Legt die Ordnung des B-Splines fest.
	 * 
	 * @param ordnung
	 *            Ordnung
	 */
	public void setOrdnung(int ordnung) {
		assert ordnung > 0 : Messages.get(GlLibMessages.BadBSplineDegree,
				ordnung);

		this.ordnung = ordnung;
	}

}
