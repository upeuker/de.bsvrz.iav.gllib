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
import de.bsvrz.sys.funclib.application.StandardApplication;
import de.bsvrz.sys.funclib.application.StandardApplicationRunner;
import de.bsvrz.sys.funclib.bitctrl.modell.AnmeldeException;
import de.bsvrz.sys.funclib.bitctrl.modell.DatensendeException;
import de.bsvrz.sys.funclib.bitctrl.modell.DefaultObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.KonfigurationsDatum;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.att.RelativerZeitstempel;
import de.bsvrz.sys.funclib.bitctrl.modell.att.Zeitstempel;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.attribute.AtlVerkehrlicheGueltigkeit;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.attribute.AttEreignisTypPrioritaet;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.attribute.AttZeitBezug;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.konfigurationsdaten.KdEreignisEigenschaften;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.konfigurationsdaten.KdEreignisTypEigenschaften;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.objekte.Ereignis;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.objekte.EreignisTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.parameter.PdEreignisParameter;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.parameter.PdEreignisTypParameter;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.parameter.PdGanglinienModellAutomatischesLernenEreignis;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttAbstandsMass;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttAnzahlSekunden0Bis;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttAnzahlSekunden1Bis;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttGanglinienTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttGanglinienVerfahren;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttGanzzahl1Bis;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttWichtungsFaktor;
import de.bsvrz.sys.funclib.bitctrl.modell.tmsystemkalenderglobal.objekte.Kalender;
import de.bsvrz.sys.funclib.bitctrl.modell.tmsystemkalenderglobal.objekte.SystemKalenderEintrag;
import de.bsvrz.sys.funclib.bitctrl.modell.tmsystemkalenderglobal.parameter.PdSystemKalenderEintrag;
import de.bsvrz.sys.funclib.commandLineArgs.ArgumentList;
import de.bsvrz.sys.funclib.dynobj.DynObjektException;

/**
 * Initialisiert den Ereigniskalender. Es werden die Tage Montag bis Sonntag und
 * der Ostersonntag angelegt.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id: KalenderInitialisierer.java 8474 2008-04-18 16:44:39Z Schumann
 *          $
 */
public final class KalenderInitialisierer implements StandardApplication,
		UncaughtExceptionHandler {

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

	private ObjektFactory factory;

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

		factory = DefaultObjektFactory.getInstanz();

		factory.setDav(connection);
		// TODO Vermutlich nicht mehr notwendig?
		// factory.registerStandardFactories();

		kalender = (Kalender) factory.getModellobjekt(connection
				.getLocalConfigurationAuthority());

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
	 * @throws DynObjektException
	 *             Fehler beim Anlegen eines dynamischen Objekts
	 */
	private void anlegenEreignis(final String ereignisName, final int prioritaet)
			throws ConfigurationChangeException, AnmeldeException,
			DatensendeException, DynObjektException {
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

		ske = factory.createDynamischesObjekt(SystemKalenderEintrag.class,
				ereignisName + " (Systemkalendereintrag)", "ske."
						+ ereignisName.toLowerCase(),
				new KonfigurationsDatum[0]);
		kalender.getSystemKalenderEintraege().add(ske);

		skeParam = ske.getPdSystemKalenderEintrag();
		skeParam.anmeldenSender();
		skeDatum = skeParam.createDatum();
		skeDatum.setDefinition("ske" + ereignisName + ":=" + ereignisName);
		skeParam.sendeDatum(skeDatum);
		System.out.println("Systemkalendereintrag " + ereignisName
				+ " angelegt.");

		final KdEreignisTypEigenschaften.Daten typEigenschaften = new KdEreignisTypEigenschaften.Daten(
				new KdEreignisTypEigenschaften(null, factory),
				KdEreignisTypEigenschaften.Aspekte.Eigenschaften);

		typ = factory.createDynamischesObjekt(EreignisTyp.class, ereignisName
				+ " (Ereignistyp)",
				"ereignisTyp." + ereignisName.toLowerCase(),
				new KonfigurationsDatum[] { typEigenschaften });
		kalender.getEreignisTypen().add(typ);
		typParam = typ.getPdEreignisTypParameter();
		typParam.anmeldenSender();
		typDatum = typParam.createDatum();
		typDatum.setEreignisTypPrioritaet(new AttEreignisTypPrioritaet(
				(long) prioritaet));
		typParam.sendeDatum(typDatum);
		lernParam = typ.getPdGanglinienModellAutomatischesLernenEreignis();
		lernParam.anmeldenSender();
		lernDatum = lernParam.createDatum();
		lernDatum
				.setAlgDarstellungsverfahren(AttGanglinienVerfahren.ZUSTAND_3_POLYLINE_VERFAHREN_LINEARE_INTERPOLATION_);
		lernDatum.setAlgGanglinienTyp(AttGanglinienTyp.ZUSTAND_0_ABSOLUT);
		lernDatum.setAlgMatchingIntervallNach(new AttAnzahlSekunden0Bis(
				15 * Constants.MILLIS_PER_MINUTE));
		lernDatum.setAlgMatchingIntervallVor(new AttAnzahlSekunden0Bis(
				15 * Constants.MILLIS_PER_MINUTE));
		lernDatum.setAlgMatchingSchrittweite(new AttAnzahlSekunden1Bis(
				Constants.MILLIS_PER_MINUTE));
		lernDatum.setAlgMaxAbstand(new AttAbstandsMass((byte) 75));
		lernDatum.setAlgMaxGanglinien(new AttGanzzahl1Bis((long) 10));
		lernDatum.setAlgMaxMatchingFehler(new AttAbstandsMass((byte) 75));
		lernDatum.setAlgMaxWichtungsfaktor(new AttWichtungsFaktor((short) 3));
		lernDatum.setAlgVergleichsSchrittweite(new AttAnzahlSekunden1Bis(
				15 * Constants.MILLIS_PER_MINUTE));
		lernParam.sendeDatum(lernDatum);
		System.out.println("Ereignistyp " + ereignisName + " angelegt.");

		final KdEreignisEigenschaften.Daten eigenschaften = new KdEreignisEigenschaften.Daten(
				new KdEreignisEigenschaften(null, factory),
				KdEreignisEigenschaften.Aspekte.Eigenschaften);
		eigenschaften.setEreignisTypReferenz(typ);

		erg = factory.createDynamischesObjekt(Ereignis.class, ereignisName,
				"ereignis." + ereignisName.toLowerCase(),
				new KonfigurationsDatum[] { eigenschaften });

		kalender.getEreignisse().add(erg);
		final AtlVerkehrlicheGueltigkeit vg = new AtlVerkehrlicheGueltigkeit();
		vg.setZeitDauerAnfangIntervall(new RelativerZeitstempel(0));
		vg.setZeitBezugAnfangIntervall(AttZeitBezug.ZUSTAND_0_VORANFANG);
		vg.setZeitDauerEndeIntervall(new RelativerZeitstempel(0));
		vg.setZeitBezugEndeIntervall(AttZeitBezug.ZUSTAND_3_NACHENDE);

		ergParam = erg.getPdEreignisParameter();
		ergParam.anmeldenSender();

		ergDatum = ergParam.createDatum();
		ergDatum.setSystemKalenderEintragReferenz(ske);
		ergDatum.getVerkehrlicheGueltigkeit().add(vg);
		ergDatum.setBeginnZeitlicheGueltigkeit(new Zeitstempel());
		ergDatum.setEndeZeitlicheGueltigkeit(new Zeitstempel());
		ergDatum.setQuelle("Initialisierung");
		ergParam.sendeDatum(ergDatum);
		System.out.println("Ereignis " + ereignisName + " angelegt.");
	}

	/**
	 * Lösche alle Objekt die angelegt werden.
	 * 
	 * @throws ConfigurationChangeException
	 *             wenn das Löschen nicht möglich war.
	 */
	private void reset() throws ConfigurationChangeException {
		DataModel modell;
		SystemObjectType typ;
		Collection<SystemObject> objekte;
		ConfigurationAuthority aoe;
		MutableSet menge;
		ClientDavInterface verbindung;

		verbindung = DefaultObjektFactory.getInstanz().getDav();
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
		System.err.println("Der Thread " + t
				+ " hat sich unerwartet beendet:\n"
				+ LoggerTools.getStackTrace(e));
	}

}
