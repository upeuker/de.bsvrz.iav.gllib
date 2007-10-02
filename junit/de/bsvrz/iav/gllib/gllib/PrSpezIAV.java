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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.util.logging.XMLFormatter;

import org.junit.Test;

import de.bsvrz.sys.funclib.bitctrl.util.Intervall;

/**
 * F&uuml;hrt die Testf&auml;lle nach der Pr&uuml;fspezifikation aus.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class PrSpezIAV {

	/** Anzahl Millisekunden pro Minute. */
	private static final long MILLIS_PER_MINUTE = 60 * 1000;

	/** Protokolllogger. */
	private final Logger logger;

	/**
	 * Protokollierung initialisieren.
	 */
	public PrSpezIAV() {
		Handler handler;

		logger = Logger.getLogger(getClass().getCanonicalName());
		try {
			handler = new StreamHandler(new FileOutputStream(
					"Testprotokoll.txt"), new SimpleFormatter());
			handler.setLevel(Level.ALL);
			logger.addHandler(handler);

			handler = new StreamHandler(new FileOutputStream(
					"Testprotokoll.xml"), new XMLFormatter());
			handler.setLevel(Level.ALL);
			logger.addHandler(handler);

			handler = new StreamHandler(System.out, new SimpleFormatter());
			handler.setLevel(Level.ALL);
			logger.addHandler(handler);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}

		logger.setLevel(Level.ALL);
	}

	/**
	 * F&uuml;hrt den Testfall 6 "Cut-Operation" aus.
	 */
	@Test
	public void testfall6() {
		Ganglinie g, erg;
		Intervall i;

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

		i = new Intervall(20 * MILLIS_PER_MINUTE, 70 * MILLIS_PER_MINUTE);
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

	/**
	 * F&uuml;hrt Teile des Testfalls 8 "Automatisches Lernen" aus.
	 * <p>
	 * Getestet wird das Verschmelzen zweier Ganglinien. Da das Verschmelzen von
	 * Messquerschnittsganglinien auf dem Verschmelzen von einfachen Ganglinien
	 * beruht, wird nur letzteres getestet.
	 */
	@Test
	public void testfall8() {
		Ganglinie g1, g2, g;
		List<Stuetzstelle<Double>> stuetzstellen;
		final int gewicht;

		logger.config("Tesfall 8: Automatisches Lernen (Verschmelzen)");

		g1 = new Ganglinie();
		g1.setStuetzstelle(10 * MILLIS_PER_MINUTE, 10.0);
		g1.setStuetzstelle(20 * MILLIS_PER_MINUTE, 40.0);
		g1.setStuetzstelle(30 * MILLIS_PER_MINUTE, 10.0);
		g1.setStuetzstelle(40 * MILLIS_PER_MINUTE, 60.0);
		g1.setStuetzstelle(50 * MILLIS_PER_MINUTE, 20.0);
		logger.info("Ganglinie zum Verschmelzen: " + g1);

		g2 = new Ganglinie();
		g2.setStuetzstelle(10 * MILLIS_PER_MINUTE, 30.0);
		g2.setStuetzstelle(20 * MILLIS_PER_MINUTE, 10.0);
		g2.setStuetzstelle(30 * MILLIS_PER_MINUTE, 20.0);
		g2.setStuetzstelle(40 * MILLIS_PER_MINUTE, 20.0);
		g2.setStuetzstelle(50 * MILLIS_PER_MINUTE, 40.0);
		gewicht = 3;
		logger.info("Ursprungsganglinie mit Gewicht " + gewicht + ": " + g2);

		g = GanglinienOperationen.verschmelze(g1, g2, gewicht);
		logger.info("Neue Ganglinie: " + g);
		stuetzstellen = g.getStuetzstellen();
		assertEquals(25, stuetzstellen.get(0).getWert());
		assertEquals(17.5, stuetzstellen.get(1).getWert());
		assertEquals(17.5, stuetzstellen.get(2).getWert());
		assertEquals(30, stuetzstellen.get(3).getWert());
		assertEquals(35, stuetzstellen.get(4).getWert());
		logger.info("Testfall bestanden.");
	}
}
