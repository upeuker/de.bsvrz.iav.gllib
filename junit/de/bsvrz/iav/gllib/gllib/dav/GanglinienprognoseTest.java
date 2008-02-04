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

package de.bsvrz.iav.gllib.gllib.dav;

import java.util.ArrayList;
import java.util.List;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.sys.funclib.application.StandardApplication;
import de.bsvrz.sys.funclib.application.StandardApplicationRunner;
import de.bsvrz.sys.funclib.bitctrl.modell.DatensendeException;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.VerkehrsobjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.objekte.MessQuerschnittAllgemein;
import de.bsvrz.sys.funclib.bitctrl.util.Intervall;
import de.bsvrz.sys.funclib.commandLineArgs.ArgumentList;

/**
 * Testprogramm f&uuml;r Anfragen an die Ganglinienprognose.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class GanglinienprognoseTest implements StandardApplication,
		GlProgAntwortListener {

	/** Die PID des Messquerschnitts der für den Test verwendet wird. */
	private static String pidMQ;

	/**
	 * Startet die Applikation.
	 * 
	 * @param args
	 *            die notwendigen Datenverteilraufrufparameter und {@code -mq}
	 *            womit der Testmessquerschnitt festgelegt wird.
	 */
	public static void main(String[] args) {
		StandardApplicationRunner.run(new GanglinienprognoseTest(), args);
	}

	/**
	 * Gibt die empfange Antwort aus.
	 * 
	 * {@inheritDoc}
	 */
	public void antwortEingetroffen(GlProgAntwortEvent e) {
		for (GanglinieMQ g : e.getGanglinien()) {
			System.out.println(g);
		}
		System.exit(0);
	}

	/**
	 * Stellt eine einzelne Anfrage f&uuml;r eine Prognoseganglinie.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.sys.funclib.application.StandardApplication#initialize(de.bsvrz.dav.daf.main.ClientDavInterface)
	 */
	public void initialize(ClientDavInterface connection) {
		Ganglinienprognose prognose;
		List<GlProgAnfrage> anfragen;
		MessQuerschnittAllgemein mq;

		ObjektFactory.getInstanz().registerFactory(new VerkehrsobjektFactory());
		ObjektFactory.getInstanz().setVerbindung(connection);

		mq = (MessQuerschnittAllgemein) ObjektFactory.getInstanz()
				.getModellobjekt(connection.getDataModel().getObject(pidMQ));
		prognose = Ganglinienprognose.getInstanz();
		prognose.addAntwortListener(this);
		anfragen = new ArrayList<GlProgAnfrage>();
		anfragen.add(new GlProgAnfrage(mq, new Intervall(1, 2 * 24 * 60 * 60
				* 1000), false));
		try {
			prognose.sendeAnfrage("Test GlLib", anfragen);
		} catch (DatensendeException ex) {
			System.err.println("Anfrage konnte gesendet werden: "
					+ ex.getLocalizedMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.sys.funclib.application.StandardApplication#parseArguments(de.bsvrz.sys.funclib.commandLineArgs.ArgumentList)
	 */
	public void parseArguments(ArgumentList argumentList) {
		pidMQ = argumentList.fetchArgument("-mq=").asNonEmptyString();
	}

}
