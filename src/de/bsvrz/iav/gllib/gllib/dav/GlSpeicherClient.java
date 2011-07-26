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

/**
 * Synchroner Client zum Anfragen und Speichern von Ganglinien.
 * 
 * @author BitCtrl Systems GmbH, Thomas Thierfelder
 * 
 * @version $Id$
 */
public class GlSpeicherClient implements GlSpeicherClientInterface,
		ClientReceiverInterface, ClientSenderInterface {

	private static final int REQ_GETSET = 0;

	private static final int REQ_DELETE = 1;

	private static final long TIMEOUT = 10L * 1000L;

	private static final Map<ObjektFactory, GlSpeicherClient> davZuClientMap = new HashMap<ObjektFactory, GlSpeicherClient>();

	private static long anfrageId = 0;

	private final ClientDavInterface dav;

	private final ObjektFactory objektFactory;

	private final DataDescription anfrageDD;

	private final SystemObject serverObj;

	private final long appId;

	private boolean inWait = false;

	private ResultData lastResult = null;

	/**
	 * 
	 * @param dav
	 * @return
	 * @throws OneSubscriptionPerSendData
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
	 * 
	 * @param dav
	 * @throws OneSubscriptionPerSendData
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
		dav.subscribeSender(this, serverObj, anfrageDD, SenderRole.sender());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized List<GanglinieMQ> getGanglinien(
			final SystemObject mqSysObj) throws DataNotSubscribedException,
			SendSubscriptionNotConfirmed, GlSpeicherServerException {
		if (mqSysObj == null) {
			throw new NullPointerException(
					"Uebergebener Messquerschnitt darf nicht NULL sein.");
		}

		final Data anfrageData = dav.createData(anfrageDD.getAttributeGroup());
		anfrageData.getUnscaledValue("applikationsId").set(appId);
		anfrageData.getUnscaledValue("anfrageId").set(++anfrageId);
		anfrageData.getItem("Anfrage").getReferenceValue("Messquerschnitt")
				.setSystemObject(mqSysObj);
		anfrageData.getArray("EreignisTyp").setLength(0);
		anfrageData.getUnscaledValue("KommandoAnfrage").set(REQ_GETSET);
		anfrageData.getArray("ZuSpeicherndeGanglinien").setLength(0);

		final ResultData result = new ResultData(serverObj, anfrageDD,
				System.currentTimeMillis(), anfrageData);

		lastResult = null;
		inWait = true;

		dav.sendData(result);

		while (inWait) {
			try {
				wait(TIMEOUT);
			} catch (final InterruptedException ex) {
				ex.printStackTrace();
			}
		}

		final List<GanglinieMQ> ganglinien = new ArrayList<GanglinieMQ>();
		if (lastResult != null) {
			if (lastResult.getData().getArray("Fehlermeldung").getLength() > 0) {
				throw new GlSpeicherServerException(lastResult.getData()
						.getArray("Fehlermeldung").getItem(0).asTextValue()
						.getText());
			}

			final Data.Array davGanglinien = lastResult.getData().getArray(
					"GespeicherteGanglinien");
			for (int i = 0; i < davGanglinien.getLength(); i++) {
				ganglinien.add(GlSpeicherUtil.konvertiere(
						davGanglinien.getItem(i), objektFactory));
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
			GlSpeicherServerException {
		if (mqSysObj == null) {
			throw new NullPointerException(
					"Uebergebener Messquerschnitt darf nicht NULL sein.");
		}

		final Data anfrageData = dav.createData(anfrageDD.getAttributeGroup());
		anfrageData.getUnscaledValue("applikationsId").set(appId);
		anfrageData.getUnscaledValue("anfrageId").set(++anfrageId);
		anfrageData.getItem("Anfrage").getReferenceValue("Messquerschnitt")
				.setSystemObject(mqSysObj);
		anfrageData.getArray("EreignisTyp").setLength(0);
		if (ganglinien == null) {
			anfrageData.getUnscaledValue("KommandoAnfrage").set(REQ_DELETE);
			anfrageData.getArray("ZuSpeicherndeGanglinien").setLength(0);
		} else {
			anfrageData.getUnscaledValue("KommandoAnfrage").set(REQ_GETSET);
			anfrageData.getArray("ZuSpeicherndeGanglinien").setLength(
					ganglinien.size());
			for (int i = 0; i < ganglinien.size(); i++) {
				GlSpeicherUtil.konvertiere(
						anfrageData.getArray("ZuSpeicherndeGanglinien")
								.getItem(i), ganglinien.get(i));
			}
		}

		final ResultData result = new ResultData(serverObj, anfrageDD,
				System.currentTimeMillis(), anfrageData);

		lastResult = null;
		inWait = true;

		dav.sendData(result);

		while (inWait) {
			try {
				wait(TIMEOUT);
			} catch (final InterruptedException ex) {
				ex.printStackTrace();
			}
		}

		if (lastResult != null) {
			if (lastResult.getData().getArray("Fehlermeldung").getLength() > 0) {
				throw new GlSpeicherServerException(lastResult.getData()
						.getArray("Fehlermeldung").getItem(0).asTextValue()
						.getText());
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
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRequestSupported(final SystemObject arg0,
			final DataDescription arg1) {
		return false;
	}

}
