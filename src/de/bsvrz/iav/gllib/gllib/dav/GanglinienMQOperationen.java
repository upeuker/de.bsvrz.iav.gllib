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

package de.bsvrz.iav.gllib.gllib.dav;

import java.util.List;

import de.bsvrz.iav.gllib.gllib.Polyline;
import de.bsvrz.iav.gllib.gllib.Stuetzstelle;
import de.bsvrz.sys.funclib.bitctrl.util.Intervall;

/**
 * Helferklasse mit den Operationen auf Ganglinien, deren St&uuml;tzstellen
 * Gleitkommazahlen sind.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class GanglinienMQOperationen {

	/**
	 * Konstruktor verstecken.
	 */
	protected GanglinienMQOperationen() {
		// nichts
	}

	/**
	 * Schneidet ein Intervall aus einer Ganglinie heraus. Existieren keine
	 * St&uuml;tzstellen in den Intervallgrenzen, werden an diesen Stellen
	 * mittels Approximation durch Polyline St&uuml;tzstellen hinzugef&uuml;gt.
	 * 
	 * @param g
	 *            Eine Ganglinie
	 * @param i
	 *            Auszuschneidendes Intervall
	 * @return Der Intervallausschnitt
	 */
	public static GanglinieMQ auschneiden(GanglinieMQ g, Intervall i) {
		GanglinieMQ a;
		List<Stuetzstelle<Messwerte>> teilintervall;

		// Stützstellen gegebenenfalls ergänzen
		teilintervall = g.getStuetzstellen();
		a = new GanglinieMQ(teilintervall);
		a.setApproximation(new Polyline());

		if (!a.existsStuetzstelle(i.start)) {
			a.setStuetzstelle(a.getStuetzstelle(i.start));
		}

		if (!a.existsStuetzstelle(i.ende)) {
			a.setStuetzstelle(a.getStuetzstelle(i.ende));
		}

		// Intervall ausschneiden
		teilintervall = g.getStuetzstellen(i);
		a = new GanglinieMQ(teilintervall);
		a.setApproximation(g.getApproximation());
		
		return a;

	}

}
