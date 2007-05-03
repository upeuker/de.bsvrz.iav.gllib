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
 * Wei�enfelser Stra�e 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

/**
 * Approximation einer Ganglinie mit Hilfe eines Cubic-Splines.
 * 
 * @author BitCtrl, Schumann
 * @version $Id: CubicSpline.java 31 2006-12-11 09:27:45Z Schumann $
 */
public class CubicSpline extends AbstractApproximation {

	/**
	 * {@inheritDoc}
	 */
	public Stuetzstelle getStuetzstelle(long zeitstempel) {
		Stuetzstelle[] s = getNaechsteStuetzstellen(zeitstempel);

		switch (s.length) {
		case 0:
			// Zeitstempel geh�rt nicht zu Ganglinie oder keine St�tzstellen
			return null;
		case 1:
			// St�tzstelle zum Zeitstempel ist in Ganglinie vorhanden
			return s[0];
		case 2:
			// St�tzstelle muss berechnet werden
			return new Stuetzstelle(zeitstempel,
					berechneStuetzstelle(zeitstempel));
		default:
			throw new IllegalStateException();
		}
	}

	/**
	 * Berechnet die St&uuml;tzstelle.
	 * 
	 * @param zeitstempel
	 *            Zeitstempel der gesuchten St&uuml;tzstelle
	 * @return Die gesuchte St&uuml;tzstelle
	 */
	private int berechneStuetzstelle(long zeitstempel) {
		Stuetzstelle [] grenzen;
		int a, b, c, d;
		
		grenzen = getNaechsteStuetzstellen(zeitstempel);
		
		return 0;
	}
}