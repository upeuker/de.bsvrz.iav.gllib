/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.5 Funktionen Ganglinie
 * Copyright (C) 2011-2015 BitCtrl Systems GmbH
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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collection;
import java.util.Collections;

import com.bitctrl.Constants;
import com.bitctrl.util.jar.JarTools;
import com.bitctrl.util.logging.LoggerTools;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.config.ConfigurationAuthority;
import de.bsvrz.dav.daf.main.config.ConfigurationChangeException;
import de.bsvrz.dav.daf.main.config.DataModel;
import de.bsvrz.dav.daf.main.config.DynamicObject;
import de.bsvrz.dav.daf.main.config.MutableSet;
import de.bsvrz.dav.daf.main.config.ObjectTimeSpecification;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dav.daf.main.config.SystemObjectType;
import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;
import de.bsvrz.iav.gllib.gllib.modell.parameter.PdGanglinienModellAutomatischesLernenEreignis;
import de.bsvrz.sys.funclib.application.StandardApplication;
import de.bsvrz.sys.funclib.application.StandardApplicationRunner;
import de.bsvrz.sys.funclib.bitctrl.modell.AnmeldeException;
import de.bsvrz.sys.funclib.bitctrl.modell.DatensendeException;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.objekte.Ereignis;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.objekte.EreignisTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.objekte.Kalender;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.objekte.SystemKalenderEintrag;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.parameter.PdEreignisParameter;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.parameter.PdEreignisParameter.Daten.VerkehrlicheGueltigkeit;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.parameter.PdEreignisTypParameter;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.parameter.PdSystemKalenderEintrag;
import de.bsvrz.sys.funclib.commandLineArgs.ArgumentList;

/**
 * Initialisiert den Ereigniskalender. Es werden die Tage Montag bis Sonntag und
 * der Ostersonntag angelegt.
 *
 * @author BitCtrl Systems GmbH, Falko Schumann
 */
@SuppressWarnings("nls")
public final class KalenderInitialisierer
		implements StandardApplication, UncaughtExceptionHandler {

	/**
	 * Startet die Applikation.
	 * <p>
	 * Neben den Datenverteilerparametern kennt die Applikation noch:
	 * <ul>
	 * <li><code>-reset</code> ist der Parameter angegeben, werden <em>alle</em>
	 * Ereignisse, Ereignistypen und Systemkalendereinträge gelöscht, bevor die
	 * Standardereignisse angelgt werden.</li>
	 * </ul>
	 *
	 * @param args
	 *            die Startparameter.
	 */
	public static void main(final String[] args) {
		StandardApplicationRunner.run(new KalenderInitialisierer(), args);
	}

	/** Die Eigenschaft {@code reset}. */
	private boolean reset = false;

	/** Die Eigenschaft {@code kalender}. */
	private Kalender kalender;

	/**
	 * Initialisiert die Applikation.
	 */
	private KalenderInitialisierer() {
		JarTools.printVersionInfo(getClass());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see de.bsvrz.sys.funclib.application.StandardApplication#initialize(de.bsvrz.dav.daf.main.ClientDavInterface)
	 */
	@Override
	public void initialize(final ClientDavInterface connection) {
		ObjektFactory factory;

		factory = ObjektFactory.getInstanz();
		factory.setVerbindung(connection);
		factory.registerStandardFactories();

		kalender = (Kalender) factory
				.getModellobjekt(connection.getLocalConfigurationAuthority());

		try {
			if (reset) {
				reset();
			}

			anlegenEreignis("Montag", 10);
			anlegenEreignis("Dienstag", 10);
			anlegenEreignis("Mittwoch", 10);
			anlegenEreignis("Donnerstag", 10);
			anlegenEreignis("Freitag", 10);
			anlegenEreignis("Samstag", 15);
			anlegenEreignis("Sonntag", 30);
			anlegenEreignis("Ostersonntag", 100);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("READY.");
		System.exit(0);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see de.bsvrz.sys.funclib.application.StandardApplication#parseArguments(de.bsvrz.sys.funclib.commandLineArgs.ArgumentList)
	 */
	@Override
	public void parseArguments(final ArgumentList argumentList)
			throws Exception {
		Thread.setDefaultUncaughtExceptionHandler(this);
		if (argumentList.hasArgument("-reset")) {
			argumentList.fetchArgument("-reset=");
			reset = true;
		}
	}

	/**
	 * Legt ein Ereignis an, welches auf einem vordefinierten
	 * Systemkalendereintrag basiert. Es werden alle notwendigen Objekte
	 * angelegt: der Systemkalendereintrag, der Ereignistyp und das Ereignis.
	 *
	 * @param ereignisName
	 *            der Name des Ereignisses.
	 * @param prioritaet
	 *            die Priorität des dazugehörigen Ereignistyps.
	 * @throws ConfigurationChangeException
	 *             wenn ein Objekt der Konfiguration nicht hinzugefügt werden
	 *             konnte.
	 * @throws AnmeldeException
	 *             wenn die Anmeldung zum Senden eines Parameters nicht möglich
	 *             ist.
	 * @throws DatensendeException
	 *             wenn ein Parameter nicht gesendet werden kann.
	 */
	private void anlegenEreignis(final String ereignisName,
			final int prioritaet) throws ConfigurationChangeException,
					AnmeldeException, DatensendeException {
		SystemKalenderEintrag ske;
		EreignisTyp typ;
		Ereignis erg;
		PdSystemKalenderEintrag skeParam;
		PdSystemKalenderEintrag.Daten skeDatum;
		PdEreignisTypParameter typParam;
		PdEreignisTypParameter.Daten typDatum;
		PdEreignisParameter ergParam;
		PdEreignisParameter.Daten ergDatum;
		PdGanglinienModellAutomatischesLernenEreignis lernParam;
		PdGanglinienModellAutomatischesLernenEreignis.Daten lernDatum;
		VerkehrlicheGueltigkeit vg;

		ske = SystemKalenderEintrag.anlegen("ske." + ereignisName.toLowerCase(),
				ereignisName + " (Systemkalendereintrag)");
		kalender.add(ske);
		skeParam = ske.getParameterDatensatz(PdSystemKalenderEintrag.class);
		skeParam.anmeldenSender();
		skeDatum = skeParam.erzeugeDatum();
		skeDatum.setDefinition("ske" + ereignisName + ":=" + ereignisName);
		skeParam.sendeDaten(skeDatum);
		System.out.println(
				"Systemkalendereintrag " + ereignisName + " angelegt.");

		typ = EreignisTyp.anlegen("ereignisTyp." + ereignisName.toLowerCase(),
				ereignisName + " (Ereignistyp)");
		kalender.add(typ);
		typParam = typ.getParameterDatensatz(PdEreignisTypParameter.class);
		typParam.anmeldenSender();
		typDatum = typParam.erzeugeDatum();
		typDatum.setPrioritaet(prioritaet);
		typParam.sendeDaten(typDatum);
		lernParam = typ.getParameterDatensatz(
				PdGanglinienModellAutomatischesLernenEreignis.class);
		lernParam.anmeldenSender();
		lernDatum = lernParam.erzeugeDatum();
		lernDatum.setDarstellungsverfahren(GanglinieMQ.APPROX_POLYLINE);
		lernDatum.setGanglinienTyp(GanglinieMQ.TYP_ABSOLUT);
		lernDatum.setMatchingIntervallNach(15 * Constants.MILLIS_PER_MINUTE);
		lernDatum.setMatchingIntervallVor(15 * Constants.MILLIS_PER_MINUTE);
		lernDatum.setMatchingSchrittweite(Constants.MILLIS_PER_MINUTE);
		lernDatum.setMaxAbstand(75);
		lernDatum.setMaxGanglinien(10);
		lernDatum.setMaxMatchingFehler(75);
		lernDatum.setMaxWichtungsfaktor(3);
		lernDatum.setVergleichsSchrittweite(15 * Constants.MILLIS_PER_MINUTE);
		lernParam.sendeDaten(lernDatum);
		System.out.println("Ereignistyp " + ereignisName + " angelegt.");

		erg = Ereignis.anlegen("ereignis." + ereignisName.toLowerCase(),
				ereignisName, "", typ);
		kalender.add(erg);
		vg = new VerkehrlicheGueltigkeit();
		vg.setDauerAnfang(0);
		vg.setBezugAnfang(VerkehrlicheGueltigkeit.VOR_ANFANG);
		vg.setDauerEnde(0);
		vg.setBezugEnde(VerkehrlicheGueltigkeit.NACH_ENDE);
		ergParam = erg.getParameterDatensatz(PdEreignisParameter.class);
		ergParam.anmeldenSender();
		ergDatum = ergParam.erzeugeDatum();
		ergDatum.setSystemKalenderEintrag(ske);
		ergDatum.getVerkehrlicheGueltigkeit().add(vg);
		ergDatum.setQuelle("Initialisierung");
		ergParam.sendeDaten(ergDatum);
		System.out.println("Ereignis " + ereignisName + " angelegt.");
	}

	/**
	 * Lösche alle Objekt die angelegt werden.
	 *
	 * @throws ConfigurationChangeException
	 *             wenn das Löschen nicht möglich war.
	 */
	private static void reset() throws ConfigurationChangeException {
		DataModel modell;
		SystemObjectType typ;
		Collection<SystemObject> objekte;
		ConfigurationAuthority aoe;
		MutableSet menge;
		ClientDavInterface verbindung;

		verbindung = ObjektFactory.getInstanz().getVerbindung();
		aoe = verbindung.getLocalConfigurationAuthority();
		modell = verbindung.getDataModel();

		menge = aoe.getMutableSet("Ereignisse");
		for (final SystemObject so : menge.getElements()) {
			System.out.println("Entferne aus Menge " + so);
			menge.remove(so);
		}

		menge = aoe.getMutableSet("EreignisTypen");
		for (final SystemObject so : menge.getElements()) {
			System.out.println("Entferne aus Menge " + so);
			menge.remove(so);
		}

		menge = aoe.getMutableSet("SystemKalenderEinträge");
		for (final SystemObject so : menge.getElements()) {
			System.out.println("Entferne aus Menge " + so);
			menge.remove(so);
		}

		typ = modell.getType("typ.ereignisTyp");
		objekte = modell.getObjects(null, Collections.singleton(typ),
				ObjectTimeSpecification.valid());
		for (final SystemObject so : objekte) {
			DynamicObject dyn;

			dyn = (DynamicObject) so;
			System.out.println("Invalidiere " + dyn);
			dyn.invalidate();
		}

		typ = modell.getType("typ.ereignis");
		objekte = modell.getObjects(null, Collections.singleton(typ),
				ObjectTimeSpecification.valid());
		for (final SystemObject so : objekte) {
			DynamicObject dyn;

			dyn = (DynamicObject) so;
			System.out.println("Invalidiere " + dyn);
			dyn.invalidate();
		}

		typ = modell.getType("typ.systemKalenderEintrag");
		objekte = modell.getObjects(null, Collections.singleton(typ),
				ObjectTimeSpecification.valid());
		for (final SystemObject so : objekte) {
			DynamicObject dyn;

			dyn = (DynamicObject) so;
			System.out.println("Invalidiere " + dyn);
			dyn.invalidate();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		System.err.println("Der Thread " + t + " hat sich unerwartet beendet:\n"
				+ LoggerTools.getStackTrace(e));
	}

}
