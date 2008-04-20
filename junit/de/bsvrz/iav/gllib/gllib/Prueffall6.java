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
 * Wei�enfelser Stra�e 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

import static com.bitctrl.Constants.MILLIS_PER_MINUTE;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.bitctrl.util.Interval;

/**
 * F�hrt den Pr�ffall 6 "Cut-Operation" laut Pr�fspezifikation aus.
 * <p>
 * Aus einer Ganglinie wird ein Teilintervall rausgeschnitten. Auf der linken
 * Seit liegt eine St�tzstelle direkt auf der Intervallgrenze und auf der
 * rechten Seite nicht. AUf der rechten Seite muss der Wert interpoliert werden.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class Prueffall6 {

	/**
	 * Aus einer Ganglinie im Intervall 5 bis 80 Minuten wird das Teilintervall
	 * 20 bis 70 herausgeschnitten. F�r die linke Intervallseite (20) existiert
	 * eine St�tzstelle in der Ganglinie. F�r die rechte Seite des
	 * Teilintervalls (70) existiert keine St�tzstelle.
	 * <p>
	 * Im Ergebnis umfasst die herausgeschnitte Ganglinie das gew�nschte
	 * Intervall. Die St�tzstelle am linken Rand wird aus der Originalganglinie
	 * �bernommen. Die St�tzstelle an der rechten Seite wird interpoliert.
	 */
	@Test
	public void testfall6() {
		Ganglinie<Double> g, erg;
		Interval i;

		g = new Ganglinie<Double>();
		g.put(5 * MILLIS_PER_MINUTE, 35.0);
		g.put(15 * MILLIS_PER_MINUTE, 20.0);
		g.put(20 * MILLIS_PER_MINUTE, 30.0);
		g.put(35 * MILLIS_PER_MINUTE, 10.0);
		g.put(50 * MILLIS_PER_MINUTE, 25.0);
		g.put(65 * MILLIS_PER_MINUTE, 20.0);
		g.put(75 * MILLIS_PER_MINUTE, 30.0);
		g.put(80 * MILLIS_PER_MINUTE, 20.0);

		i = new Interval(20 * MILLIS_PER_MINUTE, 70 * MILLIS_PER_MINUTE);
		g = GanglinienOperationen.auschneiden(g, i);

		erg = new Ganglinie<Double>();
		erg.put(20 * MILLIS_PER_MINUTE, 30.0);
		erg.put(35 * MILLIS_PER_MINUTE, 10.0);
		erg.put(50 * MILLIS_PER_MINUTE, 25.0);
		erg.put(65 * MILLIS_PER_MINUTE, 20.0);
		erg.put(70 * MILLIS_PER_MINUTE, 25.0);

		assertEquals(erg.getStuetzstellen(), g.getStuetzstellen());
	}
}
