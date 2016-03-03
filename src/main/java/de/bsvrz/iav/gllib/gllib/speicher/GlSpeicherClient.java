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
 * Weiﬂenfelser Straﬂe 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib.speicher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bitctrl.i18n.Messages;

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
import de.bsvrz.iav.gllib.gllib.GlLibMsg;
import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.objekte.MessQuerschnittAllgemein;

/**
 * Synchroner Client zum Anfragen und Speichern von Ganglinien.
 *
 * @author BitCtrl Systems GmbH, Thomas Thierfelder
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
	private final DataDescription anfrageDataDescription;

	/** Systemobjekt des Ganglinien-Servers. */
	private final SystemObject serverObj;

	/** Applikations-ID. */
	private final SystemObject app;

	/**
	 * Indiziert, ob dieser Prozess gerade auf eine Antwort vom Server wartet.
	 */
	private boolean inWait = false;

	/** Die letzte empfangene Antwort vom Server. */
	private ResultData lastResult = null;

	/** Sendererlaubnis zum Senden von Gl-Anfragen. */
	private boolean sendenErlaubt = false;

	/** Timeout. */
	private long timeout = TIMEOUT;

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
		dav = objectFactory.getVerbindung();
		serverObj = GlSpeicherUtil.getGanglinienSpeicherServerObjekt(dav);
		app = dav.getLocalApplicationObject();

		final AttributeGroup atgAnfrage = dav.getDataModel()
				.getAttributeGroup("atg.ganglinienSpeicherAnfrage"); //$NON-NLS-1$
		final Aspect aspAnfrage = dav.getDataModel().getAspect("asp.anfrage"); //$NON-NLS-1$
		assert atgAnfrage != null : "atg.ganglinienSpeicherAnfrage nicht konfiguriert"; //$NON-NLS-1$
		anfrageDataDescription = new DataDescription(atgAnfrage, aspAnfrage);

		final AttributeGroup atgAntwort = dav.getDataModel()
				.getAttributeGroup("atg.ganglinienSpeicherAntwort"); //$NON-NLS-1$
		final Aspect aspAntwort = dav.getDataModel().getAspect("asp.antwort"); //$NON-NLS-1$
		assert atgAntwort != null : "atg.ganglinienSpeicherAntwort nicht konfiguriert"; //$NON-NLS-1$
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
			throw new IllegalArgumentException(Messages
					.get(GlLibMsg.IllegalArgumentExceptionNull, "mqSysObj")); //$NON-NLS-1$

		}

		final Data anfrageData = dav
				.createData(anfrageDataDescription.getAttributeGroup());
		anfrageData.getReferenceValue("absenderId").setSystemObject(app); //$NON-NLS-1$
		anfrageData.getTextValue("AbsenderZeichen").setText( //$NON-NLS-1$
				new Long(++anfrageId).toString());
		anfrageData.getItem("Anfrage").getReferenceValue("Messquerschnitt") //$NON-NLS-1$ //$NON-NLS-2$
				.setSystemObject(mqSysObj);
		anfrageData.getItem("Anfrage").getArray("EreignisTyp").setLength(0); //$NON-NLS-1$//$NON-NLS-2$
		anfrageData.getItem("Anfrage").getUnscaledValue("KommandoAnfrage") //$NON-NLS-1$ //$NON-NLS-2$
				.set(REQ_GETSET);
		anfrageData.getItem("Anfrage").getArray("ZuSpeicherndeGanglinien") //$NON-NLS-1$//$NON-NLS-2$
				.setLength(0);

		final ResultData result = new ResultData(serverObj,
				anfrageDataDescription, System.currentTimeMillis(),
				anfrageData);

		lastResult = null;
		inWait = true;

		try {
			dav.subscribeSender(this, serverObj, anfrageDataDescription,
					SenderRole.sender());
			for (int i = 0; (i < 50) && !sendenErlaubt; i++) {
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
			dav.unsubscribeSender(this, serverObj, anfrageDataDescription);
		}

		while (inWait) {
			try {
				wait(timeout);
				inWait = false;
			} catch (final InterruptedException ex) {
				//
			}
		}

		final List<GanglinieMQ> ganglinien = new ArrayList<GanglinieMQ>();
		if (lastResult != null) {
			if (lastResult.getData().getItem("Antwort") //$NON-NLS-1$
					.getArray("Fehlermeldung").getLength() > 0) { //$NON-NLS-1$
				throw new GlSpeicherServerException(lastResult.getData()
						.getItem("Antwort").getArray("Fehlermeldung") //$NON-NLS-1$ //$NON-NLS-2$
						.getItem(0).asTextValue().getText());
			}

			final Data.Array davGanglinien = lastResult.getData()
					.getItem("Antwort").getArray("GespeicherteGanglinien"); //$NON-NLS-1$//$NON-NLS-2$
			for (int i = 0; i < davGanglinien.getLength(); i++) {
				ganglinien.add(GlSpeicherUtil.konvertiere(
						(MessQuerschnittAllgemein) objektFactory
								.getModellobjekt(mqSysObj),
						davGanglinien.getItem(i), objektFactory));
			}
		} else {
			throw new GlSpeicherServerException(
					GlLibMsg.GlSpeicherServerExceptionTimeout.toString());
		}

		return ganglinien;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void setGanglinien(final SystemObject mqSysObj,
			final List<GanglinieMQ> ganglinien)
					throws DataNotSubscribedException,
					SendSubscriptionNotConfirmed, GlSpeicherServerException,
					OneSubscriptionPerSendData {
		if (mqSysObj == null) {
			throw new IllegalArgumentException(Messages
					.get(GlLibMsg.IllegalArgumentExceptionNull, "mqSysObj")); //$NON-NLS-1$
		}

		final Data anfrageData = dav
				.createData(anfrageDataDescription.getAttributeGroup());
		anfrageData.getReferenceValue("absenderId").setSystemObject(app); //$NON-NLS-1$
		anfrageData.getTextValue("AbsenderZeichen").setText( //$NON-NLS-1$
				new Long(++anfrageId).toString());
		anfrageData.getItem("Anfrage").getReferenceValue("Messquerschnitt") //$NON-NLS-1$ //$NON-NLS-2$
				.setSystemObject(mqSysObj);
		anfrageData.getItem("Anfrage").getArray("EreignisTyp").setLength(0); //$NON-NLS-1$//$NON-NLS-2$
		if (ganglinien == null) {
			anfrageData.getItem("Anfrage").getUnscaledValue("KommandoAnfrage") //$NON-NLS-1$ //$NON-NLS-2$
					.set(REQ_DELETE);
			anfrageData.getItem("Anfrage").getArray("ZuSpeicherndeGanglinien") //$NON-NLS-1$//$NON-NLS-2$
					.setLength(0);
		} else {
			anfrageData.getItem("Anfrage").getUnscaledValue("KommandoAnfrage") //$NON-NLS-1$ //$NON-NLS-2$
					.set(REQ_GETSET);
			anfrageData.getItem("Anfrage").getArray("ZuSpeicherndeGanglinien") //$NON-NLS-1$//$NON-NLS-2$
					.setLength(ganglinien.size());
			for (int i = 0; i < ganglinien.size(); i++) {
				GlSpeicherUtil.konvertiere(
						anfrageData.getItem("Anfrage") //$NON-NLS-1$
								.getArray("ZuSpeicherndeGanglinien").getItem(i), //$NON-NLS-1$
						ganglinien.get(i));
			}
		}

		final ResultData result = new ResultData(serverObj,
				anfrageDataDescription, System.currentTimeMillis(),
				anfrageData);

		lastResult = null;
		inWait = true;

		try {
			dav.subscribeSender(this, serverObj, anfrageDataDescription,
					SenderRole.sender());
			for (int i = 0; (i < 50) && !sendenErlaubt; i++) {
				try {
					Thread.sleep(50L);
				} catch (final InterruptedException ex) {
					// wird ignoriert
				}
			}
			dav.sendData(result);
		} catch (final RuntimeException ex) {
			//
		} finally {
			dav.unsubscribeSender(this, serverObj, anfrageDataDescription);
		}

		while (inWait) {
			try {
				wait(timeout);
				inWait = false;
			} catch (final InterruptedException ex) {
				// wird ignoriert
			}
		}

		if (lastResult != null) {
			if (lastResult.getData().getItem("Antwort") //$NON-NLS-1$
					.getArray("Fehlermeldung").getLength() > 0) { //$NON-NLS-1$
				throw new GlSpeicherServerException(lastResult.getData()
						.getItem("Antwort").getArray("Fehlermeldung") //$NON-NLS-1$ //$NON-NLS-2$
						.getItem(0).asTextValue().getText());
			}
		} else {
			throw new GlSpeicherServerException(
					GlLibMsg.GlSpeicherServerExceptionTimeout.toString());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTimeout(final long timeoutInMillis) {
		timeout = timeoutInMillis;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTimeout() {
		return timeout;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(final ResultData[] results) {
		if (results != null) {
			for (final ResultData result : results) {
				if ((result != null) && result.hasData()
						&& (result.getData() != null)) {
					final SystemObject appEmpfangen = result.getData()
							.getReferenceValue("absenderId").getSystemObject(); //$NON-NLS-1$
					final String anfrageIdEmpfangen = result.getData()
							.getTextValue("AbsenderZeichen").getText(); //$NON-NLS-1$
					if (appEmpfangen.equals(app) && anfrageIdEmpfangen
							.equals(new Long(anfrageId).toString())) {
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
	public void dataRequest(final SystemObject arg0, final DataDescription arg1,
			final byte arg2) {
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
