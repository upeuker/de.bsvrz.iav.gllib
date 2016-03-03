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

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.DataNotSubscribedException;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.SendSubscriptionNotConfirmed;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.iav.gllib.gllib.GlLibMsg;
import de.bsvrz.sys.funclib.bitctrl.daf.BetriebsmeldungDaten;
import de.bsvrz.sys.funclib.bitctrl.daf.LogTools;
import de.bsvrz.sys.funclib.debug.Debug;

/**
 * Prozess in dem Anfragen sequenziell abgearbeitet werden.
 *
 * @author BitCtrl Systems GmbH, Thomas Thierfelder
 */
public class AnfrageArbeiter extends Thread {

	/** Kennzeichen fuer Lese- bzw. Schreibeanfrage */
	private static final int REQ_LESEN_SCHREIBEN = 0;

	/** Kennzeichen fuer Anfrage zum Loeschen aller Ganglinien an einem MQ. */
	private static final int REQ_LOESCHEN = 1;

	private final Debug log = Debug.getLogger();

	/** Verbdindung zum GL-Speicher-Server. */
	private final GlSpeicherServer server;

	/** FIFO-Puffer, in dem die anstehenden Anfragen gespeichert werden. */
	private final FifoBuffer<Data> anfrageDataPuffer = new FifoBuffer<Data>();

	/** Helfer. */
	private final DbGanglinienHelper helper;

	/** Indiziert, ob dieser Prozess gerade arbeitet. */
	private boolean busy = false;

	/**
	 * Standardkonstruktor.
	 *
	 * @param server
	 *            der Gl-Speicher-Server.
	 */
	AnfrageArbeiter(final GlSpeicherServer server) {
		this.server = server;
		helper = new DbGanglinienHelper(server.getDav(), server.getEmf());
		start();
	}

	/**
	 * Arbeitet eine Ganglinienabfrage ab und gibt die Antwort darauf zurueck.
	 *
	 * @param anfrageData
	 *            eine Ganglinienanfrage.
	 * @return die Antwort.
	 */
	private ResultData work(final Data anfrageData) {
		final Data antwortData = server.getDav().createData(
				server.getAntwortDataDescription().getAttributeGroup());

		antwortData.getReferenceValue("absenderId").setSystemObject( //$NON-NLS-1$
				anfrageData.getReferenceValue("absenderId").getSystemObject()); //$NON-NLS-1$
		antwortData.getTextValue("AbsenderZeichen").setText( //$NON-NLS-1$
				anfrageData.getTextValue("AbsenderZeichen").getText()); //$NON-NLS-1$
		antwortData.getItem("Antwort") //$NON-NLS-1$
				.getReferenceValue("Messquerschnitt") //$NON-NLS-1$
				.setSystemObject(anfrageData.getItem("Anfrage") //$NON-NLS-1$
						.getReferenceValue("Messquerschnitt") //$NON-NLS-1$
						.getSystemObject());

		switch (anfrageData.getItem("Anfrage") //$NON-NLS-1$
				.getUnscaledValue("KommandoAnfrage").intValue()) { //$NON-NLS-1$
		case REQ_LESEN_SCHREIBEN:
			workGetSet(anfrageData, antwortData);
			break;
		case REQ_LOESCHEN:
			workDelete(anfrageData, antwortData);
			break;
		}

		return new ResultData(server.getServerObj(),
				server.getAntwortDataDescription(), System.currentTimeMillis(),
				antwortData);
	}

	/**
	 * Erfragt bzw. setzt (speichert) Ganglinien eines MQ.
	 *
	 * @param anfrageData
	 *            die Anfrage.
	 * @param antwortData
	 *            die Antwort (wird hier manipuliert).
	 */
	private void workGetSet(final Data anfrageData, final Data antwortData) {
		final String mqPid = anfrageData.getItem("Anfrage") //$NON-NLS-1$
				.getReferenceValue("Messquerschnitt").getSystemObjectPid(); //$NON-NLS-1$

		final Data.Array zuSpeicherndeGanglinien = anfrageData
				.getItem("Anfrage") //$NON-NLS-1$
				.getArray("ZuSpeicherndeGanglinien"); //$NON-NLS-1$
		antwortData.getItem("Antwort") //$NON-NLS-1$
				.getReferenceValue("Messquerschnitt") //$NON-NLS-1$
				.setSystemObject(anfrageData.getItem("Anfrage") //$NON-NLS-1$
						.getReferenceValue("Messquerschnitt") //$NON-NLS-1$
						.getSystemObject());
		String err = null;
		if (zuSpeicherndeGanglinien.getLength() == 0) {
			final Data.Array ganglinien = antwortData.getItem("Antwort") //$NON-NLS-1$
					.getArray(
							"GespeicherteGanglinien");
			err = helper.read(mqPid, ganglinien);
		} else {
			antwortData.getItem("Antwort").getArray("GespeicherteGanglinien") //$NON-NLS-1$ //$NON-NLS-2$
					.setLength(0);
			err = helper.write(mqPid, zuSpeicherndeGanglinien);
		}

		if (err != null) {
			antwortData.getItem("Antwort").getArray("Fehlermeldung") //$NON-NLS-1$//$NON-NLS-2$
					.setLength(1);
			antwortData.getItem("Antwort").getArray("Fehlermeldung").getItem(0) //$NON-NLS-1$ //$NON-NLS-2$
					.asTextValue().setText(err);
		} else {
			antwortData.getItem("Antwort").getArray("Fehlermeldung") //$NON-NLS-1$//$NON-NLS-2$
					.setLength(0);
		}
	}

	/**
	 * Loescht alle Ganglinien an einem MQ.
	 *
	 * @param anfrageData
	 *            die Anfrage.
	 * @param antwortData
	 *            die Antwort (wird hier manipuliert).
	 */
	private void workDelete(final Data anfrageData, final Data antwortData) {
		final String mqPid = anfrageData.getReferenceValue("Messquerschnitt") //$NON-NLS-1$
				.getSystemObjectPid();
		antwortData.getItem("Antwort") //$NON-NLS-1$
				.getReferenceValue("Messquerschnitt") //$NON-NLS-1$
				.setSystemObject(anfrageData.getItem("Anfrage") //$NON-NLS-1$
						.getReferenceValue("Messquerschnitt") //$NON-NLS-1$
						.getSystemObject());
		final String err = helper.delete(mqPid);
		if (err != null) {
			antwortData.getItem("Antwort").getArray("Fehlermeldung") //$NON-NLS-1$ //$NON-NLS-2$
					.setLength(1);
			antwortData.getItem("Antwort").getArray("Fehlermeldung").getItem(0) //$NON-NLS-1$ //$NON-NLS-2$
					.asTextValue().setText(err);
		} else {
			antwortData.getItem("Antwort").getArray("Fehlermeldung") //$NON-NLS-1$ //$NON-NLS-2$
					.setLength(0);
		}
		antwortData.getItem("Antwort").getArray("GespeicherteGanglinien") //$NON-NLS-1$ //$NON-NLS-2$
				.setLength(0);
	}

	/**
	 * Erfragt, ob dieser Prozess gerade beschaeftigt ist.
	 *
	 * @return ob dieser Prozess gerade beschaeftigt ist.
	 */
	final boolean isBeschaeftigt() {
		return busy;
	}

	/**
	 * Weist diesem Prozess eine neue Anfrage zu.
	 *
	 * @param anfrageData
	 *            eine Ganglinienanfrage.
	 */
	final void weiseZu(final Data anfrageData) {
		anfrageDataPuffer.add(anfrageData);
	}

	@Override
	public void run() {
		while (true) {
			final Data anfrageData = anfrageDataPuffer.get();
			busy = true;
			final ResultData result = work(anfrageData);
			try {
				server.getDav().sendData(result);
			} catch (final DataNotSubscribedException ex) {
				final SystemObject mq = anfrageData.getItem("Anfrage") //$NON-NLS-1$
						.getReferenceValue("Messquerschnitt") //$NON-NLS-1$
						.getSystemObject();
				LogTools.log(log, new BetriebsmeldungDaten(mq),
						GlLibMsg.WarningAntwortKonnteNichtGesendetWerden,
						anfrageData, mq, ex);
			} catch (final SendSubscriptionNotConfirmed ex) {
				final SystemObject mq = anfrageData.getItem("Anfrage") //$NON-NLS-1$
						.getReferenceValue("Messquerschnitt") //$NON-NLS-1$
						.getSystemObject();
				LogTools.log(log, new BetriebsmeldungDaten(mq),
						GlLibMsg.WarningAntwortKonnteNichtGesendetWerden,
						anfrageData, mq, ex);
			}
			if (anfrageDataPuffer.size() > 0) {
				continue;
			}
			busy = false;
		}
	}

}
