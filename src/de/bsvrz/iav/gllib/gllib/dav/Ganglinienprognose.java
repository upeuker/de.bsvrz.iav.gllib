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

import java.util.Collection;

import javax.swing.event.EventListenerList;

import de.bsvrz.dav.daf.main.config.Aspect;
import de.bsvrz.dav.daf.main.config.ClientApplication;
import de.bsvrz.iav.gllib.gllib.modell.GanglinienobjektFactory;
import de.bsvrz.iav.gllib.gllib.modell.onlinedaten.OdPrognoseGanglinienAnfrage;
import de.bsvrz.iav.gllib.gllib.modell.onlinedaten.OdPrognoseGanglinienAntwort;
import de.bsvrz.sys.funclib.bitctrl.modell.AnmeldeException;
import de.bsvrz.sys.funclib.bitctrl.modell.DatensatzUpdateEvent;
import de.bsvrz.sys.funclib.bitctrl.modell.DatensatzUpdateListener;
import de.bsvrz.sys.funclib.bitctrl.modell.DatensendeException;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.ganglinien.objekte.ApplikationGanglinienPrognose;
import de.bsvrz.sys.funclib.bitctrl.modell.systemmodellglobal.objekte.Applikation;
import de.bsvrz.sys.funclib.debug.Debug;

/**
 * Mit dieser Klasse können Applikation bequem Anfragen an die
 * Ganglinienprognose stellen, ohne sich mit dem Datenkatalog
 * auseinanderzusetzen müssen.
 * <p>
 * <em>Hinweis:</em> Die Ganglinienprognose benutzt die
 * {@link de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory}. Die Factory muss
 * mit der Datenverteilerverbindung initialisiert sein.
 * <p>
 * Ein vereinfachtes Beispiel der Anwendung:
 * 
 * <pre><code>
 * ObjektFactory.getInstanz().setVerbindung(vernindung);
 * prognose = Ganglinienprognose.getInstanz();
 * prognose.addAntwortListener(this);
 * anfragen = new ArrayList&lt;GlProgAnfrage&gt;();
 * anfragen.add(new GlProgAnfrage(mq, intervall, false));
 * prognose.sendeAnfrage(&quot;Meine Anfrage&quot;, anfragen);
 * </code></pre>
 * 
 * Die anfragende Klasse muss die Schnittstelle {@link GlProgAntwortListener}
 * implementieren, mit der die Antwort auf die Anfrage empfangen wird.
 * 
 * @see de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory#setVerbindung(de.bsvrz.dav.daf.main.ClientDavInterface)
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public final class Ganglinienprognose implements DatensatzUpdateListener {

	/** Das Singleton. */
	private static Ganglinienprognose singleton;

	/**
	 * Gibt eine Ganglinienprognose als Singleton zurück.
	 * 
	 * @return die Ganglinienprognose als Singleton.
	 */
	public static Ganglinienprognose getInstanz() {
		if (singleton == null) {
			singleton = new Ganglinienprognose();
		}
		return singleton;
	}

	/** Der Logger. */
	private final Debug log;

	/** Angemeldete Listener. */
	private final EventListenerList listeners;

	/** Der Anfragedatensatz. */
	private final OdPrognoseGanglinienAnfrage odAnfrage;

	/** Der Aspekt zum Senden der Anfrage. */
	private final Aspect aspAnfrage;

	/**
	 * Initialisiert den inneren Zustand.
	 */
	private Ganglinienprognose() {
		ObjektFactory factory;
		OdPrognoseGanglinienAntwort odAntwort;
		Aspect aspAntwort;
		ApplikationGanglinienPrognose glProg;
		Applikation klient;

		listeners = new EventListenerList();
		log = Debug.getLogger();

		factory = ObjektFactory.getInstanz();
		factory.registerStandardFactories();
		factory.registerFactory(new GanglinienobjektFactory());

		glProg = (ApplikationGanglinienPrognose) factory
				.getModellobjekt(factory.getVerbindung()
						.getLocalConfigurationAuthority());
		aspAnfrage = OdPrognoseGanglinienAnfrage.Aspekte.Anfrage.getAspekt();
		odAnfrage = glProg
				.getOnlineDatensatz(OdPrognoseGanglinienAnfrage.class);

		klient = (Applikation) factory.getModellobjekt(factory.getVerbindung()
				.getLocalApplicationObject());
		aspAntwort = OdPrognoseGanglinienAntwort.Aspekte.Antwort.getAspekt();
		odAntwort = klient
				.getOnlineDatensatz(OdPrognoseGanglinienAntwort.class);
		odAntwort.setSenke(aspAntwort, true);
		odAntwort.addUpdateListener(aspAntwort, this);

		try {
			odAnfrage.anmeldenSender(aspAnfrage);
		} catch (final AnmeldeException ex) {
			log
					.error(
							"Anmeldung zum Senden von Anfragen an die Ganglinienprognose konnte nicht durchgeführt werden",
							ex);
		}

		log.info("Schnittstelle zur Ganglinienprognose bereit.");
	}

	/**
	 * Registriert einen Listener.
	 * 
	 * @param listener
	 *            Der neue Listener
	 */
	public void addAntwortListener(final GlProgAntwortListener listener) {
		listeners.add(GlProgAntwortListener.class, listener);
		log.fine("Neuer Listener für Prognoseantworten angemeldet", listener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.bsvrz.sys.funclib.bitctrl.modell.DatensatzUpdateListener#datensatzAktualisiert(de.bsvrz.sys.funclib.bitctrl.modell.DatensatzUpdateEvent)
	 */
	public void datensatzAktualisiert(final DatensatzUpdateEvent event) {
		assert event.getDatum() instanceof OdPrognoseGanglinienAntwort.Daten;

		OdPrognoseGanglinienAntwort.Daten datum;

		datum = (OdPrognoseGanglinienAntwort.Daten) event.getDatum();
		if (datum.isValid()) {
			fireAntwort(datum);
		}
	}

	/**
	 * Entfernt einen Listener wieder aus der Liste registrierter Listener.
	 * 
	 * @param listener
	 *            Listener der abgemeldet werden soll
	 */
	public void removeAntwortListener(final GlProgAntwortListener listener) {
		listeners.remove(GlProgAntwortListener.class, listener);
		log.fine("Listener für Prognoseantworten abgemeldet", listener);
	}

	/**
	 * Sendet eine Anfrage an die Ganglinienprognose. Die anfragende Applikation
	 * wird über ein Event über die eingetroffene Antwort informiert.
	 * 
	 * @param absenderZeichen
	 *            ein beliebiger Text.
	 * @param anfragen
	 *            die Anfragen.
	 * @throws DatensendeException
	 *             wenn beim Senden ein Fehler passiert ist.
	 */
	public void sendeAnfrage(final String absenderZeichen,
			final Collection<GlProgAnfrage> anfragen)
			throws DatensendeException {
		OdPrognoseGanglinienAnfrage.Daten datum;
		ObjektFactory factory;
		ClientApplication klient;

		factory = ObjektFactory.getInstanz();
		klient = factory.getVerbindung().getLocalApplicationObject();

		datum = odAnfrage.erzeugeDatum();
		datum.setAbsender((Applikation) factory.getModellobjekt(klient));
		datum.setAbsenderZeichen(absenderZeichen);
		datum.addAll(anfragen);
		odAnfrage.sendeDaten(aspAnfrage, datum);

		log.fine("Anfrage \"" + absenderZeichen + "\" wurde gesendet");
	}

	/**
	 * Informiert alle registrierten Listener über eine Antwort.
	 * 
	 * @param datum
	 *            ein Datum mit der Antwort auf eine Prognoseanfrage.
	 */
	protected synchronized void fireAntwort(
			final OdPrognoseGanglinienAntwort.Daten datum) {
		final GlProgAntwortEvent e = new GlProgAntwortEvent(this, datum);

		for (final GlProgAntwortListener l : listeners
				.getListeners(GlProgAntwortListener.class)) {
			l.antwortEingetroffen(e);
		}

		log.fine("Prognoseantwort wurde verteilt: " + e);
	}

}
