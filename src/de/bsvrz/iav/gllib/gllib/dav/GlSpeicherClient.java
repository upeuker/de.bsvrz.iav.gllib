/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.3 Automatisches Lernen
 * Ganglinien
 * Copyright (C) 2011 BitCtrl Systems GmbH 
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientReceiverInterface;
import de.bsvrz.dav.daf.main.ClientSenderInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.DataNotSubscribedException;
import de.bsvrz.dav.daf.main.OneSubscriptionPerSendData;
import de.bsvrz.dav.daf.main.ReceiveOptions;
import de.bsvrz.dav.daf.main.ReceiverRole;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.SendSubscriptionNotConfirmed;
import de.bsvrz.dav.daf.main.SenderRole;
import de.bsvrz.dav.daf.main.config.Aspect;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.tmverkehrglobal.objekte.MessQuerschnittAllgemein;

/**
 * Synchroner Client zum Anfragen und Speichern von Ganglinien.
 * 
 * @author BitCtrl Systems GmbH, Thomas Thierfelder
 * 
 * @version $Id$
 */
public class GlSpeicherClient implements GlSpeicherClientInterface,
		ClientReceiverInterface, ClientSenderInterface {

	/** Kommando zum Abfragen und zum Speichern von Ganglinien. */
	private static final int REQ_GETSET = 0;

	/** Kommando zum Loaeschen aller Ganglinien an einem MQ. */
	private static final int REQ_DELETE = 1;

	/** Standard-Timeout fuer Antworten vom Ganglinien-Server. */
	private static final long TIMEOUT = 10L * 1000L;

	/** Pro Objektfactory ein statischer GL-Client. */
	private static final Map<ObjektFactory, GlSpeicherClient> davZuClientMap = new HashMap<ObjektFactory, GlSpeicherClient>();

	/** Anfrage-ID-Zaehler. */
	private static long anfrageId = 0;

	/** Datenverteiler-Verbindung. */
	private final ClientDavInterface dav;

	/** DAV-Objektfactory. */
	private final ObjektFactory objektFactory;

	/** Datenbeschreibung von Anfragen an den Ganglinien-Server. */
	private final DataDescription anfrageDD;

	/** Systemobjekt des Ganglinien-Servers. */
	private final SystemObject serverObj;

	/** Applikations-ID. */
	private final long appId;

	/** Indiziert, ob dieser Prozess gerade auf eine Antwort vom Server wartet. */
	private boolean inWait = false;

	/** Die letzte empfangene Antwort vom Server. */
	private ResultData lastResult = null;

	/** Sendererlaubnis zum Senden von Gl-Anfragen. */
	private boolean sendenErlaubt = false;

	/**
	 * Erfragt die statische Instanz dieser Klasse fuer die uebergebene
	 * DAV-Objektfactory.
	 * 
	 * @param objectFactory
	 *            DAV-Objektfactory.
	 * @return statische Instanz dieser Klasse fuer die uebergebene
	 *         DAV-Objektfactory.
	 * @throws OneSubscriptionPerSendData
	 *             wird weitergereicht.
	 */
	public static final synchronized GlSpeicherClientInterface getInstanz(
			final ObjektFactory objectFactory)
			throws OneSubscriptionPerSendData {
		GlSpeicherClient instanz = davZuClientMap.get(objectFactory);
		if (instanz == null) {
			instanz = new GlSpeicherClient(objectFactory);
		}
		return instanz;
	}

	/**
	 * Standardkonstruktor.
	 * 
	 * @param objectFactory
	 *            DAV-Objektfactory.
	 * @throws OneSubscriptionPerSendData
	 *             wird weitergereicht.
	 */
	private GlSpeicherClient(final ObjektFactory objectFactory)
			throws OneSubscriptionPerSendData {
		objektFactory = objectFactory;
		dav = objectFactory.getDav();
		serverObj = GlSpeicherUtil.getGanglinienSpeicherServerObjekt(dav);
		appId = dav.getLocalApplicationObject().getId();

		final AttributeGroup atgAnfrage = dav.getDataModel().getAttributeGroup(
				"atg.ganglinienSpeicherAnfrage");
		final Aspect aspAnfrage = dav.getDataModel().getAspect("asp.anfrage");
		assert atgAnfrage != null : "atg.ganglinienSpeicherAnfrage nicht konfiguriert";
		anfrageDD = new DataDescription(atgAnfrage, aspAnfrage);

		final AttributeGroup atgAntwort = dav.getDataModel().getAttributeGroup(
				"atg.ganglinienSpeicherAntwort");
		final Aspect aspAntwort = dav.getDataModel().getAspect("asp.antwort");
		assert atgAntwort != null : "atg.ganglinienSpeicherAntwort nicht konfiguriert";
		final DataDescription antwortDD = new DataDescription(atgAntwort,
				aspAntwort);

		dav.subscribeReceiver(this, serverObj, antwortDD,
				ReceiveOptions.normal(), ReceiverRole.receiver());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized List<GanglinieMQ> getGanglinien(
			final SystemObject mqSysObj) throws DataNotSubscribedException,
			SendSubscriptionNotConfirmed, GlSpeicherServerException,
			OneSubscriptionPerSendData {
		if (mqSysObj == null) {
			throw new NullPointerException(
					"Uebergebener Messquerschnitt darf nicht NULL sein.");
		}

		final Data anfrageData = dav.createData(anfrageDD.getAttributeGroup());
		anfrageData.getUnscaledValue("applikationsId").set(appId);
		anfrageData.getUnscaledValue("anfrageId").set(++anfrageId);
		anfrageData.getItem("Anfrage").getReferenceValue("Messquerschnitt")
				.setSystemObject(mqSysObj);
		anfrageData.getItem("Anfrage").getArray("EreignisTyp").setLength(0);
		anfrageData.getItem("Anfrage").getUnscaledValue("KommandoAnfrage")
				.set(REQ_GETSET);
		anfrageData.getItem("Anfrage").getArray("ZuSpeicherndeGanglinien")
				.setLength(0);

		final ResultData result = new ResultData(serverObj, anfrageDD,
				System.currentTimeMillis(), anfrageData);

		lastResult = null;
		inWait = true;

		try {
			dav.subscribeSender(this, serverObj, anfrageDD, SenderRole.sender());
			for (int i = 0; i < 50 && !sendenErlaubt; i++) {
				try {
					Thread.sleep(50L);
				} catch (final InterruptedException ex) {
					//
				}
			}
			dav.sendData(result);
		} catch (final RuntimeException ex) {
			//
		} finally {
			dav.unsubscribeSender(this, serverObj, anfrageDD);
		}

		while (inWait) {
			try {
				wait(TIMEOUT);
				inWait = false;
			} catch (final InterruptedException ex) {
				//
			}
		}

		final List<GanglinieMQ> ganglinien = new ArrayList<GanglinieMQ>();
		if (lastResult != null) {
			if (lastResult.getData().getItem("Antwort")
					.getArray("Fehlermeldung").getLength() > 0) {
				throw new GlSpeicherServerException(lastResult.getData()
						.getItem("Antwort").getArray("Fehlermeldung")
						.getItem(0).asTextValue().getText());
			}

			final Data.Array davGanglinien = lastResult.getData()
					.getItem("Antwort").getArray("GespeicherteGanglinien");
			for (int i = 0; i < davGanglinien.getLength(); i++) {
				ganglinien.add(GlSpeicherUtil.konvertiere(
						(MessQuerschnittAllgemein) objektFactory
								.getModellobjekt(mqSysObj), davGanglinien
								.getItem(i), objektFactory));
			}
		} else {
			throw new GlSpeicherServerException(
					"Timeout: Keine Rueckmeldung von Server erhalten.");
		}

		return ganglinien;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void setGanglinien(final SystemObject mqSysObj,
			final List<GanglinieMQ> ganglinien)
			throws DataNotSubscribedException, SendSubscriptionNotConfirmed,
			GlSpeicherServerException, OneSubscriptionPerSendData {
		if (mqSysObj == null) {
			throw new NullPointerException(
					"Uebergebener Messquerschnitt darf nicht NULL sein.");
		}

		final Data anfrageData = dav.createData(anfrageDD.getAttributeGroup());
		anfrageData.getUnscaledValue("applikationsId").set(appId);
		anfrageData.getUnscaledValue("anfrageId").set(++anfrageId);
		anfrageData.getItem("Anfrage").getReferenceValue("Messquerschnitt")
				.setSystemObject(mqSysObj);
		anfrageData.getItem("Anfrage").getArray("EreignisTyp").setLength(0);
		if (ganglinien == null) {
			anfrageData.getItem("Anfrage").getUnscaledValue("KommandoAnfrage")
					.set(REQ_DELETE);
			anfrageData.getItem("Anfrage").getArray("ZuSpeicherndeGanglinien")
					.setLength(0);
		} else {
			anfrageData.getItem("Anfrage").getUnscaledValue("KommandoAnfrage")
					.set(REQ_GETSET);
			anfrageData.getItem("Anfrage").getArray("ZuSpeicherndeGanglinien")
					.setLength(ganglinien.size());
			for (int i = 0; i < ganglinien.size(); i++) {
				GlSpeicherUtil.konvertiere(anfrageData.getItem("Anfrage")
						.getArray("ZuSpeicherndeGanglinien").getItem(i),
						ganglinien.get(i));
			}
		}

		final ResultData result = new ResultData(serverObj, anfrageDD,
				System.currentTimeMillis(), anfrageData);

		lastResult = null;
		inWait = true;

		try {
			dav.subscribeSender(this, serverObj, anfrageDD, SenderRole.sender());
			for (int i = 0; i < 50 && !sendenErlaubt; i++) {
				try {
					Thread.sleep(50L);
				} catch (final InterruptedException ex) {
					//
				}
			}
			dav.sendData(result);
		} catch (final RuntimeException ex) {
			//
		} finally {
			dav.unsubscribeSender(this, serverObj, anfrageDD);
		}

		while (inWait) {
			try {
				wait(TIMEOUT);
				inWait = false;
			} catch (final InterruptedException ex) {
				//
			}
		}

		if (lastResult != null) {
			if (lastResult.getData().getItem("Antwort")
					.getArray("Fehlermeldung").getLength() > 0) {
				throw new GlSpeicherServerException(lastResult.getData()
						.getItem("Antwort").getArray("Fehlermeldung")
						.getItem(0).asTextValue().getText());
			}
		} else {
			throw new GlSpeicherServerException(
					"Timeout: Keine Rueckmeldung von Server erhalten.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(final ResultData[] results) {
		if (results != null) {
			for (final ResultData result : results) {
				if (result != null && result.hasData()
						&& result.getData() != null) {
					final long appIdEmpfangen = result.getData()
							.getUnscaledValue("applikationsId").longValue();
					final long anfrageIdEmpfangen = result.getData()
							.getUnscaledValue("anfrageId").longValue();
					if (appIdEmpfangen == appId
							&& anfrageIdEmpfangen == anfrageId) {
						lastResult = result;
						inWait = false;
						synchronized (this) {
							notify();
						}
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dataRequest(final SystemObject arg0,
			final DataDescription arg1, final byte arg2) {
		sendenErlaubt = arg2 == ClientSenderInterface.START_SENDING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRequestSupported(final SystemObject arg0,
			final DataDescription arg1) {
		return true;
	}

}
