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

import de.bsvrz.iav.gllib.gllib.GlLibMsg;
import de.bsvrz.sys.funclib.bitctrl.daf.LogTools;
import de.bsvrz.sys.funclib.bitctrl.modell.AnmeldeException;
import de.bsvrz.sys.funclib.bitctrl.modell.Aspekt;
import de.bsvrz.sys.funclib.bitctrl.modell.DatensatzUpdateEvent;
import de.bsvrz.sys.funclib.bitctrl.modell.DatensatzUpdateListener;
import de.bsvrz.sys.funclib.bitctrl.modell.DatensendeException;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.OnlineDatensatz;
import de.bsvrz.sys.funclib.bitctrl.modell.kappich.KappichModellUtil;
import de.bsvrz.sys.funclib.bitctrl.modell.systemmodellglobal.objekte.Applikation;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.objekte.ApplikationGanglinienPrognose;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.onlinedaten.OdPrognoseGanglinienAnfrage;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.onlinedaten.OdPrognoseGanglinienAntwort;
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
 * <pre>
 * <code>
 * prognose = new Ganglinienprognose(objektFactory);
 * prognose.addAntwortListener(this);
 * anfragen = new ArrayList&lt;GlProgAnfrage&gt;();
 * anfragen.add(new GlProgAnfrage(mq, intervall, false));
 * prognose.sendeAnfrage(&quot;Meine Anfrage&quot;, anfragen);
 * </code>
 * </pre>
 * 
 * Die anfragende Klasse muss die Schnittstelle {@link GlProgAntwortListener}
 * implementieren, mit der die Antwort auf die Anfrage empfangen wird.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public final class Ganglinienprognose implements DatensatzUpdateListener {

	/** Der Logger. */
	private final Debug log;

	/** Angemeldete Listener. */
	private final EventListenerList listeners;

	/** Der Anfragedatensatz. */
	private final OdPrognoseGanglinienAnfrage odAnfrage;

	/** Der Aspekt zum Senden der Anfrage. */
	private final Aspekt aspAnfrage;

	private final ObjektFactory factory;

	/**
	 * Initialisiert die Ganglinienprognose.
	 * 
	 * @param factory
	 *            die Objekt Factory die für die Prognose verwendet werden soll.
	 */
	public Ganglinienprognose(final ObjektFactory factory) {
		this.factory = factory;

		OdPrognoseGanglinienAntwort odAntwort;
		Aspekt aspAntwort;
		ApplikationGanglinienPrognose glProg;
		Applikation klient;

		listeners = new EventListenerList();
		log = Debug.getLogger();

		glProg = (ApplikationGanglinienPrognose) factory
				.getModellobjekt(factory.getDav()
						.getLocalConfigurationAuthority());
		aspAnfrage = OdPrognoseGanglinienAnfrage.Aspekte.Anfrage;
		odAnfrage = glProg.getOdPrognoseGanglinienAnfrage();

		klient = KappichModellUtil.getApplikation(factory);
		aspAntwort = OdPrognoseGanglinienAntwort.Aspekte.Antwort;
		odAntwort = klient.getOdPrognoseGanglinienAntwort();
		odAntwort.setSenke(aspAntwort, true);
		odAntwort.addUpdateListener(aspAntwort, this);

		try {
			odAnfrage.anmeldenSender(aspAnfrage);
		} catch (final AnmeldeException ex) {
			LogTools.log(log, GlLibMsg.ErrorAnmeldenSendenAnfrage, ex);
		}

		LogTools.log(log, GlLibMsg.InfoGlProgBereit);
	}

	/**
	 * Registriert einen Listener.
	 * 
	 * @param listener
	 *            Der neue Listener
	 */
	public void addAntwortListener(final GlProgAntwortListener listener) {
		listeners.add(GlProgAntwortListener.class, listener);
		LogTools.log(log, GlLibMsg.FineGlProgNeuerListener, listener);
	}

	/**
	 * Fragt, ob die Ganglinienprognose Anfragen entgegennimmt. Wenn die
	 * Sendesteuerung noch nicht geantwortet hat, wartet die Methode maximal 30
	 * Sekunden. Hat die Sendesteuerung schon geantwortet, entsteht keine
	 * Verzögerung.
	 * 
	 * @return {@code true}, wenn der Kalender verwendet werden kann.
	 */
	public boolean isBereit() {
		for (int i = 0; i < 300; i++) {
			if (odAnfrage.getStatusSendesteuerung(aspAnfrage) != null) {
				break;
			}
			factory.getDav().sleep(100);
		}
		return odAnfrage.getStatusSendesteuerung(aspAnfrage) == OnlineDatensatz.Status.START;
	}

	public void datensatzAktualisiert(final DatensatzUpdateEvent event) {
		assert event.getDatum() instanceof OdPrognoseGanglinienAntwort.Daten;

		OdPrognoseGanglinienAntwort.Daten datum;

		datum = (OdPrognoseGanglinienAntwort.Daten) event.getDatum();
		if (datum.dContainsDaten()) {
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
		LogTools.log(log, GlLibMsg.FineGlLibListenerEntfernt, listener);
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
		final OdPrognoseGanglinienAnfrage.Daten datum = odAnfrage.createDatum();
		datum.setAbsenderId(KappichModellUtil.getApplikation(factory));
		datum.setAbsenderZeichen(absenderZeichen);
		datum.getPrognoseGanglinienAnfrage().addAll(
				GanglinieUtil.konvertiere(anfragen));
		odAnfrage.sendeDatum(aspAnfrage, datum);

		LogTools.log(log, GlLibMsg.FineGlProgAnfrageGesendet, absenderZeichen);
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

		LogTools.log(log, GlLibMsg.FineGlProgAntwortVerteilt, e);
	}

}
