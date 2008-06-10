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

package de.bsvrz.iav.gllib.gllib.junit;

import java.util.List;

import com.bitctrl.Constants;
import com.bitctrl.util.jar.JarTools;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;
import de.bsvrz.iav.gllib.gllib.modell.parameter.PdGanglinie;
import de.bsvrz.iav.gllib.gllib.modell.parameter.PdGanglinienModellPrognose;
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
 * wird je eine Ganglinie für jeden Wochentag und Ostermontag angelegt.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public final class TestGanglinienParameterSetzer implements StandardApplication {

	/** Die PIDs der Standardereignistypen Montag bis Sonntag und Ostermontag. */
	private final String[] pidEreignistypen = new String[] {
			EreignisTyp.PRAEFIX_PID + "montag",
			EreignisTyp.PRAEFIX_PID + "dienstag",
			EreignisTyp.PRAEFIX_PID + "mittwoch",
			EreignisTyp.PRAEFIX_PID + "donnerstag",
			EreignisTyp.PRAEFIX_PID + "freitag",
			EreignisTyp.PRAEFIX_PID + "samstag",
			EreignisTyp.PRAEFIX_PID + "sonntag",
			EreignisTyp.PRAEFIX_PID + "ostersonntag" };

	/** Liste von PIDs von Messquerschnitten, die parametriert werden sollen. */
	private String[] objektPids;

	/**
	 * Startet die Applikation.
	 * 
	 * @param args
	 *            die Startparameter.
	 */
	public static void main(final String[] args) {
		StandardApplicationRunner.run(new TestGanglinienParameterSetzer(), args);
	}

	/**
	 * Initialisiert die Applikation.
	 */
	private TestGanglinienParameterSetzer() {
		JarTools.printVersionInfo(getClass());
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialize(final ClientDavInterface connection) {
		ObjektFactory factory;
		List<SystemObjekt> objekte;
		ZufallsganglinienFactory ganglinien;

		factory = ObjektFactory.getInstanz();
		factory.setVerbindung(connection);
		factory.registerStandardFactories();

		ganglinien = ZufallsganglinienFactory.getInstance();

		if (objektPids != null) {
			objekte = factory.bestimmeModellobjekte(objektPids);
		} else {
			objekte = factory.bestimmeModellobjekte(VerkehrsModellTypen.MESSQUERSCHNITTALLGEMEIN.getPid());
		}
		for (final SystemObjekt so : objekte) {
			final MessQuerschnittAllgemein mq;
			final PdGanglinie pdGanglinie;
			final PdGanglinie.Daten datumGanglinie;
			final PdGanglinienModellPrognose pdPrognose;
			final PdGanglinienModellPrognose.Daten datumPrognose;

			if (!(so instanceof MessQuerschnittAllgemein)) {
				throw new IllegalStateException(
						so
								+ " ist kein Messquerschnitt. Es können nur Messquerschnitte bearbeitet werden.");
			}
			mq = (MessQuerschnittAllgemein) so;

			pdGanglinie = mq.getParameterDatensatz(PdGanglinie.class);

			pdPrognose = mq.getParameterDatensatz(PdGanglinienModellPrognose.class);
			datumPrognose = pdPrognose.erzeugeDatum();
			datumPrognose.setAuswahlMethode(PdGanglinienModellPrognose.Daten.WAHRSCHEINLICHSTE_GANGLINIE);
			datumPrognose.setMatchingIntervall(10 * Constants.MILLIS_PER_MINUTE);
			datumPrognose.setMaxMatchingFehler(25);
			datumPrognose.setPatternMatchingHorizont(2 * Constants.MILLIS_PER_HOUR);
			datumPrognose.setPatternMatchingOffset(1 * Constants.MILLIS_PER_HOUR);

			try {
				pdGanglinie.anmeldenSender();
				datumGanglinie = pdGanglinie.erzeugeDatum();
				for (final String typ : pidEreignistypen) {
					GanglinieMQ g;
					EreignisTyp ereignisTyp;

					ereignisTyp = (EreignisTyp) factory.getModellobjekt(typ);
					g = ganglinien.erzeugeGanglinie(mq, 60 * 60 * 1000);
					g.setEreignisTyp(ereignisTyp);
					datumGanglinie.add(g);
				}
				pdGanglinie.sendeDaten(datumGanglinie, 60 * 1000);
				System.out.println("Ganglinien für " + mq + " gesendet.");

				pdPrognose.anmeldenSender();
				pdPrognose.sendeDaten(datumPrognose);
				System.out.println("Ganglinienprognoseparameter für " + mq
						+ " gesendet.");
			} catch (final AnmeldeException ex) {
				System.err.println("Kann mich nicht zum Senden der Ganglinien für "
						+ mq + " anmelden.");
			} catch (final DatensendeException ex) {
				System.err.println("Kann Ganglinien für " + mq
						+ " nicht senden.");
			}

			pdGanglinie.abmeldenSender();
		}

		System.out.println("READY.");
		System.exit(0);
	}

	/**
	 * Folgende Parameter werden unterstützt.
	 * <ul>
	 * <li><code>-objekte</code>: Die PIDs der Messquerschnitte die
	 * parametriert werden sollen. Mehrere PIDs können als kommagetrennte Liste
	 * angegeben werden (ohne Leerzeichen). Fehlt der Parameter, werden alle
	 * Messquerschnitte parametriert.</li>
	 * </ul>
	 * 
	 * {@inheritDoc}
	 */
	public void parseArguments(final ArgumentList argumentList)
			throws Exception {
		if (argumentList.hasArgument("-objekte")) {
			objektPids = argumentList.fetchArgument("-objekte=").asNonEmptyString().split(
					",");
		}
	}

}
