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
 * Weißenfelser Straße 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib.dav;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.event.EventListenerList;

import stauma.dav.clientside.ClientDavInterface;
import stauma.dav.clientside.ClientReceiverInterface;
import stauma.dav.clientside.ClientSenderInterface;
import stauma.dav.clientside.Data;
import stauma.dav.clientside.DataDescription;
import stauma.dav.clientside.DataNotSubscribedException;
import stauma.dav.clientside.ReceiveOptions;
import stauma.dav.clientside.ReceiverRole;
import stauma.dav.clientside.ResultData;
import stauma.dav.clientside.SenderRole;
import stauma.dav.common.OneSubscriptionPerSendData;
import stauma.dav.common.SendSubscriptionNotConfirmed;
import stauma.dav.configuration.interfaces.Aspect;
import stauma.dav.configuration.interfaces.AttributeGroup;
import stauma.dav.configuration.interfaces.DataModel;
import stauma.dav.configuration.interfaces.SystemObject;
import sys.funclib.debug.Debug;

/**
 * Mit dieser Klasse k&ouml;nnen Applikation bequem Anfragen an die
 * Ganglinienprognose stellen, ohne sich mit dem Datenkatalog
 * auseinanderzusetzen m&uuml;ssen.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class Ganglinienprognose {

	/**
	 * Wickelt die Kommunikation mit dem Datenverteiler ab. L&auml;ft als
	 * eigenst&auml;ndiger Thread.
	 * 
	 * @author BitCtrl Systems GmbH, Schumann
	 * @version $Id$
	 */
	private class Kommunikation implements ClientSenderInterface,
			ClientReceiverInterface {

		/** Der Logger. */
		private final Debug logger;

		/** Die zu verwendende Datenverteilerverbindung. */
		private final ClientDavInterface verbindung;

		/** Cached die gestellte Anfragen. */
		private final List<AnfrageNachricht> anfragen;

		/** Das Systemobjekt, an dass die Anfragen geschickt werden. */
		private final SystemObject soPrognose;

		/** Datenbeschreibung, mit der Anfragen gestellt werden. */
		private final DataDescription dbsAnfrage;

		/** Datenbeschreibung, mit der Antworten empfangen werden. */
		private final DataDescription dbsAntwort;

		/** D&uuml;rfen Anfragen gesendet werden? */
		private boolean sendenErlaubt;

		/**
		 * Initialisiert die Kommunikationsverbindung.
		 * 
		 * @param verbindung
		 *            die f&uuml;r Anfragen zu verwendende
		 *            Datenverteilerverbindung.
		 */
		Kommunikation(ClientDavInterface verbindung) {
			DataModel modell;
			AttributeGroup atg;
			Aspect asp;

			this.verbindung = verbindung;
			anfragen = new ArrayList<AnfrageNachricht>();
			logger = Debug.getLogger();

			modell = verbindung.getDataModel();

			soPrognose = modell.getConfigurationAuthority();
			atg = modell.getAttributeGroup("atg.prognoseGanglinienAnfrage");
			asp = modell.getAspect("asp.anfrage");
			dbsAnfrage = new DataDescription(atg, asp);

			atg = modell.getAttributeGroup("atg.prognoseGanglinienAntwort");
			asp = modell.getAspect("asp.antwort");
			dbsAntwort = new DataDescription(atg, asp);

			try {
				verbindung.subscribeSender(this, soPrognose, dbsAnfrage,
						SenderRole.sender());
			} catch (OneSubscriptionPerSendData ex) {
				throw new IllegalStateException(ex.getLocalizedMessage());
			}
			logger.info("Ganglinienprognose bereit.");
		}

		/**
		 * Sendet eine Anfrage an die Ganglinienprognose.
		 * 
		 * @param anfrage
		 *            die Nachricht mit den Anfragen.
		 */
		public void sendeAnfrage(AnfrageNachricht anfrage) {
			anfragen.add(anfrage);
			sendeAnfragen();
		}

		/**
		 * Sendet alle gecachten Anfragen, solange es erlaubt. Erfolgreich
		 * gesendete Anfragen werden aus dem Cache entfernt.
		 */
		private void sendeAnfragen() {
			ListIterator<AnfrageNachricht> iterator;

			iterator = anfragen.listIterator();
			while (sendenErlaubt && iterator.hasNext()) {
				Data daten;
				ResultData datensatz;
				SystemObject so;
				AnfrageNachricht anfrage;

				anfrage = iterator.next();

				// Anfrage senden
				daten = verbindung.createData(dbsAnfrage.getAttributeGroup());
				anfrage.setDaten(daten);
				datensatz = new ResultData(soPrognose, dbsAnfrage, System
						.currentTimeMillis(), daten);
				try {
					verbindung.sendData(datensatz);
					iterator.remove(); // Anfrage erfolgreich gesendet
				} catch (DataNotSubscribedException e) {
					sendenErlaubt = false;
					continue;
				} catch (SendSubscriptionNotConfirmed e) {
					sendenErlaubt = false;
					continue;
				}

				logger.fine("Anfrage gesendet: " + anfrage);

				// Als Empfänger der Antwort anmelden
				so = anfrage.getAbsender();
				verbindung.subscribeReceiver(this, so, dbsAntwort,
						ReceiveOptions.normal(), ReceiverRole.receiver());
				logger.fine("Als Empfänger der Antwort angemeldet für " + so);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void dataRequest(SystemObject object,
				DataDescription dataDescription, byte state) {
			if (object.equals(soPrognose) && dataDescription.equals(dbsAnfrage)
					&& state == ClientSenderInterface.START_SENDING) {
				sendenErlaubt = true;
				sendeAnfragen();
			} else {
				sendenErlaubt = false;
			}
		}

		/**
		 * Sendesteuerung wird verwendet.
		 * <p>
		 * {@inheritDoc}
		 */
		public boolean isRequestSupported(SystemObject object,
				DataDescription dataDescription) {
			if (object.equals(soPrognose) && dataDescription.equals(dbsAnfrage)) {
				return true;
			}
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		public void update(ResultData[] results) {
			for (ResultData datensatz : results) {
				if (datensatz.getDataDescription().equals(dbsAntwort)
						&& datensatz.hasData()) {
					logger.fine("Antwort erhalten: "
							+ fireAntwort(datensatz.getData()));
				}
			}
		}

	}

	/** Angemeldete Listener. */
	private final EventListenerList listeners;

	/** Die Kommunikationsinstanz. */
	private final Kommunikation kommunikation;

	/**
	 * Initialisiert den inneren Zustand.
	 * 
	 * @param verbindung
	 *            die f&uuml;r Anfragen zu verwendende Datenverteilerverbindung.
	 */
	public Ganglinienprognose(ClientDavInterface verbindung) {
		kommunikation = new Kommunikation(verbindung);
		listeners = new EventListenerList();
	}

	/**
	 * Registriert einen Listener.
	 * 
	 * @param listener
	 *            Der neue Listener
	 */
	public void addAntwortListener(AntwortListener listener) {
		listeners.add(AntwortListener.class, listener);
	}

	/**
	 * Entfernt einen Listener wieder aus der Liste registrierter Listener.
	 * 
	 * @param listener
	 *            Listener der abgemeldet werden soll
	 */
	public void removeAntwortListener(AntwortListener listener) {
		listeners.remove(AntwortListener.class, listener);
	}

	/**
	 * Sendet eine Anfrage an die Ganglinienprognose. Die anfragende Applikation
	 * wird &uuml;ber ein Event &uuml;ber die eingetroffene Antwort informiert.
	 * 
	 * @param anfrage
	 *            die Nachricht mit den Anfragen.
	 */
	public void sendeAnfrage(AnfrageNachricht anfrage) {
		kommunikation.sendeAnfrage(anfrage);
	}

	/**
	 * Informiert alle registrierten Listener &uuml;ber eine Antwort.
	 * 
	 * @param daten
	 *            ein Datum mit der Antwort auf eine Prognoseanfrage.
	 * @return das verschickte Event.
	 */
	protected synchronized AntwortEvent fireAntwort(Data daten) {
		AntwortEvent e = new AntwortEvent(this);
		e.setDaten(daten);

		for (AntwortListener l : listeners.getListeners(AntwortListener.class)) {
			l.antwortEingetroffen(e);
		}
		return e;
	}

}
