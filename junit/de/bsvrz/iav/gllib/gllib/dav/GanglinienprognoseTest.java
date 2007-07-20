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

package de.bsvrz.iav.gllib.gllib.dav;

import stauma.dav.clientside.ClientDavInterface;
import stauma.dav.configuration.interfaces.SystemObject;
import sys.funclib.ArgumentList;
import sys.funclib.application.StandardApplication;
import sys.funclib.application.StandardApplicationRunner;

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
	 * Stellt eine einzelne Anfrage f&uuml;r eine Prognoseganglinie.
	 * <p>
	 * {@inheritDoc}
	 */
	public void initialize(ClientDavInterface connection) throws Exception {
		Ganglinienprognose prognose;
		AnfrageNachricht anfrage;
		SystemObject mq;

		mq = connection.getDataModel().getObject("mq.a14.0001");
		prognose = new Ganglinienprognose(connection);
		prognose.addAntwortListener(this);
		anfrage = new AnfrageNachricht(connection.getLocalApplicationObject(),
				"Mein Test");
		anfrage.add(new Anfrage(mq, 1, 1, false));
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

	/**
	 * Gibt die empfange Antwort aus.
	 * <p>
	 * {@inheritDoc}
	 */
	public void antwortEingetroffen(AntwortEvent e) {
		for (SystemObject mq : e.getMessquerschnitte()) {
			System.out.println(e.getPrognose(mq));
		}
		System.exit(0);
	}

}
