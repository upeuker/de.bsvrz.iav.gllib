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
 * Weiﬂenfelser Straﬂe 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

import static com.bitctrl.Constants.MILLIS_PER_MINUTE;
import static org.junit.Assert.assertEquals;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import com.bitctrl.util.Interval;

/**
 * F&uuml;hrt die Testf&auml;lle nach der Pr&uuml;fspezifikation aus.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class Prueffall6 {

	/** Protokolllogger. */
	private final Logger logger;

	/**
	 * Protokollierung initialisieren.
	 */
	public Prueffall6() {
		logger = Logger.getLogger(getClass().getCanonicalName());
		logger.setLevel(Level.ALL);
	}

	/**
	 * F&uuml;hrt den Testfall 6 "Cut-Operation" aus.
	 */
	@Test
	public void testfall6() {
		Ganglinie g, erg;
		Interval i;

		logger.config("Tesfall 6: Cut-Operation");

		g = new Ganglinie();
		g.setStuetzstelle(5 * MILLIS_PER_MINUTE, 35.0);
		g.setStuetzstelle(15 * MILLIS_PER_MINUTE, 20.0);
		g.setStuetzstelle(20 * MILLIS_PER_MINUTE, 30.0);
		g.setStuetzstelle(35 * MILLIS_PER_MINUTE, 10.0);
		g.setStuetzstelle(50 * MILLIS_PER_MINUTE, 25.0);
		g.setStuetzstelle(65 * MILLIS_PER_MINUTE, 20.0);
		g.setStuetzstelle(75 * MILLIS_PER_MINUTE, 30.0);
		g.setStuetzstelle(80 * MILLIS_PER_MINUTE, 20.0);
		logger.info("Verwende Ganglinie: " + g);

		i = new Interval(20 * MILLIS_PER_MINUTE, 70 * MILLIS_PER_MINUTE);
		logger.info("Schneide Bereich aus: " + i);
		g = GanglinienOperationen.auschneiden(g, i);
		logger.info("Neue Ganglinie: " + g);

		erg = new Ganglinie();
		erg.setStuetzstelle(20 * MILLIS_PER_MINUTE, 30.0);
		erg.setStuetzstelle(35 * MILLIS_PER_MINUTE, 10.0);
		erg.setStuetzstelle(50 * MILLIS_PER_MINUTE, 25.0);
		erg.setStuetzstelle(65 * MILLIS_PER_MINUTE, 20.0);
		erg.setStuetzstelle(70 * MILLIS_PER_MINUTE, 25.0);

		assertEquals(erg.getStuetzstellen(), g.getStuetzstellen());
		logger.info("Testfall bestanden.");
	}
}
