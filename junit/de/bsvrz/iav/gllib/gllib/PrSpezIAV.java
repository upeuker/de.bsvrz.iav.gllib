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

	/** Protokolllogger. */
	private final Logger logger;

	/**
	 * Protokollierung initialisieren.
	 */
	public PrSpezIAV() {
		Handler handler;

		logger = Logger.getLogger(getClass().getCanonicalName());
		try {
			handler = new StreamHandler(new FileOutputStream("Testprotokoll.txt"),
					new SimpleFormatter());
			handler.setLevel(Level.ALL);
			logger.addHandler(handler);

			handler = new StreamHandler(new FileOutputStream("Testprotokoll.xml"),
					new XMLFormatter());
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
	 * 
	 */
	@Test
	public void testfall6() {
		Ganglinie g, erg;
		Intervall i;

		logger.config("Tesfall 6: Cut-Operation");

		g = new Ganglinie();
		g.setStuetzstelle(5, 35);
		g.setStuetzstelle(15, 20);
		g.setStuetzstelle(20, 30);
		g.setStuetzstelle(35, 10);
		g.setStuetzstelle(50, 25);
		g.setStuetzstelle(65, 20);
		g.setStuetzstelle(75, 30);
		g.setStuetzstelle(80, 20);
		logger.info("Verwende Ganglinie: " + g);

		i = new Intervall(20, 70);
		logger.info("Schneide Bereich aus: " + i);
		g = Ganglinie.auschneiden(g, i);
		logger.info("Neue Ganglinie: " + g);
		
		erg = new Ganglinie();
		erg.setStuetzstelle(20, 30);
		erg.setStuetzstelle(35, 10);
		erg.setStuetzstelle(50, 25);
		erg.setStuetzstelle(65, 20);
		erg.setStuetzstelle(70, 25);
		
		assertEquals(erg, g);
		logger.info("Testfall bestanden.");
	}

}
