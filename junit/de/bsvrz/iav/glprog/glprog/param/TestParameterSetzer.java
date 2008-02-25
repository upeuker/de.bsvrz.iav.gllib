/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.1 Ganglinienprognose
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

package de.bsvrz.iav.glprog.glprog.param;

import java.util.List;

import com.bitctrl.util.jar.JarTools;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;
import de.bsvrz.iav.gllib.gllib.modell.parameter.PdGanglinie;
import de.bsvrz.sys.funclib.application.StandardApplication;
import de.bsvrz.sys.funclib.application.StandardApplicationRunner;
import de.bsvrz.sys.funclib.bitctrl.modell.AnmeldeException;
import de.bsvrz.sys.funclib.bitctrl.modell.DatensendeException;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.SystemObjekt;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.objekte.EreignisTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.VerkehrsModellTypen;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.objekte.MessQuerschnittAllgemein;
import de.bsvrz.sys.funclib.commandLineArgs.ArgumentList;

/**
 * Setzt für alle Messquerschnitte den Parameter mit der Ganglinienliste. Es
 * wird je eine Ganglinie für jeden Wochentag angelegt.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public final class TestParameterSetzer implements StandardApplication {

	/**
	 * Startet die Applikation.
	 * 
	 * @param args
	 *            die Startparameter.
	 */
	public static void main(String[] args) {
		StandardApplicationRunner.run(new TestParameterSetzer(), args);
	}

	/**
	 * Initialisiert die Applikation.
	 */
	private TestParameterSetzer() {
		JarTools.printVersionInfo(getClass());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.sys.funclib.application.StandardApplication#initialize(de.bsvrz.dav.daf.main.ClientDavInterface)
	 */
	public void initialize(ClientDavInterface connection) {
		ObjektFactory factory;
		List<SystemObjekt> objekte;
		GanglinienFactory ganglinien;

		factory = ObjektFactory.getInstanz();
		factory.setVerbindung(connection);
		factory.registerStandardFactories();

		ganglinien = GanglinienFactory.getInstance();

		objekte = factory
				.bestimmeModellobjekte(VerkehrsModellTypen.MESSQUERSCHNITTALLGEMEIN
						.getPid());
		for (SystemObjekt so : objekte) {
			MessQuerschnittAllgemein mq;
			PdGanglinie param;
			PdGanglinie.Daten datum;

			mq = (MessQuerschnittAllgemein) so;
			param = mq.getParameterDatensatz(PdGanglinie.class);
			try {
				param.anmeldenSender();
				datum = param.erzeugeDatum();
				for (GanglinienFactory.Typ typ : GanglinienFactory.Typ.values()) {
					GanglinieMQ g;
					EreignisTyp ereignisTyp;

					ereignisTyp = (EreignisTyp) factory.bestimmeModellobjekte(
							typ.getPid()).get(0);
					g = ganglinien.erzeugeGanglinie(mq, 60 * 60 * 1000);
					g.setEreignisTyp(ereignisTyp);
					datum.add(g);
				}
				param.sendeDaten(datum, 60 * 1000);
				System.out.println("Ganglinien für " + mq + " gesendet.");
			} catch (AnmeldeException ex) {
				System.err
						.println("Kann mich nicht zum Senden der Ganglinien für "
								+ mq + " anmelden.");
			} catch (DatensendeException ex) {
				System.err.println("Kann Ganglinien für " + mq
						+ " nicht senden.");
			}

			param.abmeldenSender();
		}

		System.out.println("READY.");
		System.exit(0);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.sys.funclib.application.StandardApplication#parseArguments(de.bsvrz.sys.funclib.commandLineArgs.ArgumentList)
	 */
	public void parseArguments(ArgumentList argumentList) throws Exception {
		// TODO Auto-generated method stub

	}

}
