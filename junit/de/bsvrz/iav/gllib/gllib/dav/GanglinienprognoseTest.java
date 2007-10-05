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

package de.bsvrz.iav.gllib.gllib.dav;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.sys.funclib.application.StandardApplication;
import de.bsvrz.sys.funclib.application.StandardApplicationRunner;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.MessQuerschnittAllgemein;
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

	/**
	 * Startet die Applikation.
	 * 
	 * @param args
	 *            die notwenidgen Datenverteilraufrufparameter.
	 */
	public static void main(String[] args) {
		StandardApplicationRunner.run(new GanglinienprognoseTest(), args);
	}

	/**
	 * Gibt die empfange Antwort aus.
	 * <p>
	 * {@inheritDoc}
	 */
	public void antwortEingetroffen(GlProgAntwortEvent e) {
		for (MessQuerschnittAllgemein mq : e.getMessquerschnitte()) {
			System.out.println(e.getPrognose(mq));
		}
		System.exit(0);
	}

	/**
	 * Stellt eine einzelne Anfrage f&uuml;r eine Prognoseganglinie.
	 * <p>
	 * {@inheritDoc}
	 */
	public void initialize(ClientDavInterface connection) throws Exception {
		Ganglinienprognose prognose;
		GlProgAnfrageNachricht anfrage;
		MessQuerschnittAllgemein mq;

		mq = (MessQuerschnittAllgemein) ObjektFactory.getInstanz()
				.getModellobjekt(
						connection.getDataModel().getObject("mq.a14.0001"));
		prognose = new Ganglinienprognose(connection);
		prognose.addAntwortListener(this);
		anfrage = new GlProgAnfrageNachricht(connection
				.getLocalApplicationObject(), "Mein Test");
		anfrage.add(new GlProgAnfrage(mq, new Intervall(1, 2 * 24 * 60 * 60
				* 1000), false));
		prognose.sendeAnfrage(anfrage);
	}

	/**
	 * Tut nichts.
	 * <p>
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unused")
	public void parseArguments(ArgumentList argumentList) throws Exception {
		// TODO Auto-generated method stub

	}

}
