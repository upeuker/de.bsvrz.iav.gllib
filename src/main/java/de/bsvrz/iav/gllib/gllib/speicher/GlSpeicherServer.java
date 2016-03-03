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

import javax.persistence.EntityManagerFactory;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientReceiverInterface;
import de.bsvrz.dav.daf.main.ClientSenderInterface;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.OneSubscriptionPerSendData;
import de.bsvrz.dav.daf.main.ReceiveOptions;
import de.bsvrz.dav.daf.main.ReceiverRole;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.SenderRole;
import de.bsvrz.dav.daf.main.config.Aspect;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.SystemObject;

/**
 * Bearbeitet Lese- und Schreibeanfragen fuer Ganglinien als Server. Ist auf
 * <code>atg.ganglinienSpeicherAnfrage</code> und
 * <code>atg.ganglinienSpeicherAntwort</code> angemeldet.
 *
 * @author BitCtrl Systems GmbH, Thomas Thierfelder
 */
public class GlSpeicherServer
		implements ClientReceiverInterface, ClientSenderInterface {

	/** Globaler Zaehler zur Adressierung der einzelnen Worker-Threads. */
	private static int workerCount = 0;

	/** Verbindung zum Datenverteiler. */
	private final ClientDavInterface dav;

	/** {@link EntityManagerFactory}. */
	private final EntityManagerFactory emf;

	/** Datenbeschreibung fuer Serverantworten. */
	private final DataDescription antwortDataDescription;

	/** Anfrage-Worker. */
	private final AnfrageArbeiter[] arbeiter;

	/** Server-Systemobjekt. */
	private final SystemObject serverObj;

	/**
	 * Konstruktor.
	 *
	 * @param dav
	 *            Verbindung zum Datenverteiler.
	 * @param emf
	 *            {@link EntityManagerFactory}.
	 * @throws OneSubscriptionPerSendData
	 *             wird weitergereicht.
	 */
	public GlSpeicherServer(final ClientDavInterface dav,
			final EntityManagerFactory emf) throws OneSubscriptionPerSendData {
		this(dav, emf, 4);
	}

	/**
	 * Konstruktor.
	 *
	 * @param dav
	 *            Verbindung zum Datenverteiler.
	 * @param emf
	 *            {@link EntityManagerFactory}.
	 * @param anzahlWorker
	 *            Anzahl parallel arbeitender Threads.
	 * @throws OneSubscriptionPerSendData
	 *             wird weitergereicht.
	 */
	public GlSpeicherServer(final ClientDavInterface dav,
			final EntityManagerFactory emf, final int anzahlWorker)
					throws OneSubscriptionPerSendData {
		this.dav = dav;
		this.emf = emf;

		arbeiter = new AnfrageArbeiter[anzahlWorker];
		for (int i = 0; i < anzahlWorker; i++) {
			arbeiter[i] = new AnfrageArbeiter(this);
		}

		serverObj = GlSpeicherUtil.getGanglinienSpeicherServerObjekt(dav);

		final AttributeGroup atgS = dav.getDataModel()
				.getAttributeGroup("atg.ganglinienSpeicherAntwort"); //$NON-NLS-1$
		final Aspect aspS = dav.getDataModel().getAspect("asp.antwort"); //$NON-NLS-1$
		antwortDataDescription = new DataDescription(atgS, aspS);
		dav.subscribeSender(this, serverObj, antwortDataDescription,
				SenderRole.source());

		final AttributeGroup atgR = dav.getDataModel()
				.getAttributeGroup("atg.ganglinienSpeicherAnfrage"); //$NON-NLS-1$
		final Aspect aspR = dav.getDataModel().getAspect("asp.anfrage"); //$NON-NLS-1$
		final DataDescription ddR = new DataDescription(atgR, aspR);
		dav.subscribeReceiver(this, serverObj, ddR, ReceiveOptions.normal(),
				ReceiverRole.drain());
	}

	/**
	 * Erfragt die Datenbeschreibung von Serverantworten.
	 *
	 * @return die Datenbeschreibung von Serverantworten.
	 */
	public DataDescription getAntwortDataDescription() {
		return antwortDataDescription;
	}

	/**
	 * Erfragt die Datenverteiler-Verbindung.
	 *
	 * @return die Datenverteiler-Verbindung.
	 */
	public ClientDavInterface getDav() {
		return dav;
	}

	/**
	 * Erfragt die {@link EntityManagerFactory}.
	 *
	 * @return die {@link EntityManagerFactory}.
	 */
	public EntityManagerFactory getEmf() {
		return emf;
	}

	/**
	 * Erfragt das Server-Systemobjekt.
	 *
	 * @return das Server-Systemobjekt.
	 */
	public SystemObject getServerObj() {
		return serverObj;
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
					for (int i = 0; i <= arbeiter.length; i++) {
						workerCount = (workerCount + 1) % arbeiter.length;
						if (!arbeiter[workerCount].isBeschaeftigt()) {
							break;
						}
					}
					arbeiter[workerCount].weiseZu(result.getData());
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dataRequest(final SystemObject object,
			final DataDescription dataDescription, final byte state) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRequestSupported(final SystemObject object,
			final DataDescription dataDescription) {
		return false;
	}

}
