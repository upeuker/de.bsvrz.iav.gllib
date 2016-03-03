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

package de.bsvrz.iav.gllib.gllib.speicher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dav.daf.main.config.SystemObjectType;
import de.bsvrz.iav.gllib.gllib.GlLibMsg;
import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;
import de.bsvrz.iav.gllib.gllib.dav.Messwerte;
import de.bsvrz.iav.gllib.gllib.modell.db.DbMessQuerschnitt;
import de.bsvrz.sys.funclib.bitctrl.daf.BetriebsmeldungDaten;
import de.bsvrz.sys.funclib.bitctrl.daf.LogTools;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.objekte.EreignisTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.objekte.MessQuerschnittAllgemein;
import de.bsvrz.sys.funclib.debug.Debug;

/**
 * Utensilien.
 *
 * @author BitCtrl Systems GmbH, Thomas Thierfelder
 */
public class GlSpeicherUtil {

	private static final Debug log = Debug.getLogger();

	private GlSpeicherUtil() {
		// utility class
	}

	/**
	 * Erfragt das Systemobjekt (vom Typ <code>typ.ganglinienSpeicher</code>),
	 * ueber das die Ganglinien abgefragt bzw. gespeichert werden koennen.
	 *
	 * @param dav
	 *            Datenverteiler-Verbindung.
	 * @return das Systemobjekt, ueber das die Ganglinien abgefragt bzw.
	 *         gespeichert werden koennen.
	 */
	public static final SystemObject getGanglinienSpeicherServerObjekt(
			final ClientDavInterface dav) {
		SystemObject serverObj;

		final SystemObjectType glServerTyp = dav.getDataModel()
				.getType("typ.ganglinienSpeicher"); //$NON-NLS-1$

		if (glServerTyp != null) {
			final List<SystemObject> serverList = glServerTyp.getElements();
			if (serverList.size() >= 1) {
				serverObj = serverList.iterator().next();
				if (serverList.size() > 1) {
					LogTools.log(log,
							GlLibMsg.WarningGanglinienSpeicherMehrdeutig);
				}

				LogTools.log(log, new BetriebsmeldungDaten(serverObj),
						GlLibMsg.InfoGanglinienSpeicher, serverObj);
			} else {
				throw new IllegalStateException(
						GlLibMsg.IllegalStateExceptionKeinGanglinienSpeicher
								.toString());
			}
		} else {
			throw new IllegalStateException(
					GlLibMsg.IllegalStateExceptionGanglinienSpeicherTypUndefiniert
							.toString());
		}

		return serverObj;
	}

	/**
	 * Erfragt die {@link EntityManagerFactory} fuer die Ganglinien-Datenbank.
	 *
	 * @param dbIp
	 *            Datenbank-Server-IP.
	 * @param dbPort
	 *            Datenbank-Server-Port.
	 * @param dbUser
	 *            Datenbank-Nutzer.
	 * @param dbPass
	 *            Datenbank-Nutzer-Passwort.
	 * @param verzeichnis
	 *            Datenbank-Verzeichnis (das Verzeichnis, in dem die Datenbank
	 *            liegt bzw. angelegt werden soll).
	 * @return die {@link EntityManagerFactory} fuer die Ganglinien-Datenbank.
	 */
	public static final EntityManagerFactory getGlSpeicherEmf(final String dbIp,
			final String dbPort, final String dbUser, final String dbPass,
			final String verzeichnis) {

		final Map<String, String> params = new HashMap<String, String>();
		params.put("javax.persistence.jdbc.url", //$NON-NLS-1$
				"jdbc:derby://" + dbIp + ":" + dbPort //$NON-NLS-1$ //$NON-NLS-2$
						+ "/" + verzeichnis + ";create=true"); //$NON-NLS-1$//$NON-NLS-2$
		params.put("javax.persistence.jdbc.user", dbUser); //$NON-NLS-1$
		params.put("javax.persistence.jdbc.password", dbPass); //$NON-NLS-1$
		final EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("glSpeicher", params); //$NON-NLS-1$
		final EntityManager em = emf.createEntityManager();
		em.find(DbMessQuerschnitt.class, "aa"); //$NON-NLS-1$
		em.clear();
		em.close();

		return emf;
	}

	/**
	 * Erfragt die {@link EntityManagerFactory} fuer die Ganglinien-Datenbank
	 * mit den Default-Einstellungen (localhost, 1527, derby, derby, gldb).
	 *
	 * @return die {@link EntityManagerFactory} fuer die Ganglinien-Datenbank
	 *         mit den Default-Einstellungen (localhost, 1527, derby, derby,
	 *         gldb).
	 */
	public static final EntityManagerFactory getDefaultGlSpeicherEmf() {
		return getGlSpeicherEmf("localhost", "1527", "derby", "derby", "gldb"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$
	}

	/**
	 * Konvertiert das uebergebene DAV-Datum (<code>atl.ganglinie</code>) in das
	 * interne Format.
	 *
	 * @param mqa
	 *            der Messquerschnitt zu dem das Gangliniendatum gehoert.
	 * @param davAtlGanglinie
	 *            ein DAV-Datum <code>atl.ganglinie</code>.
	 * @param objectFactory
	 *            die DAV-Objektfactory.
	 * @return das uebergebene DAV-Datum (<code>atl.ganglinie</code>) im
	 *         internen Format.
	 */
	public static final GanglinieMQ konvertiere(
			final MessQuerschnittAllgemein mqa, final Data davAtlGanglinie,
			final ObjektFactory objectFactory) {
		final GanglinieMQ gl = new GanglinieMQ();

		gl.setMessQuerschnitt(mqa);
		gl.setEreignisTyp(
				(EreignisTyp) objectFactory.getModellobjekt(davAtlGanglinie
						.getReferenceValue("EreignisTyp").getSystemObject())); //$NON-NLS-1$
		gl.setAnzahlVerschmelzungen(davAtlGanglinie
				.getUnscaledValue("AnzahlVerschmelzungen").longValue()); //$NON-NLS-1$
		gl.setLetzteVerschmelzung(davAtlGanglinie
				.getTimeValue("LetzteVerschmelzung").getMillis()); //$NON-NLS-1$
		gl.setTyp(
				davAtlGanglinie.getUnscaledValue("GanglinienTyp").byteValue()); //$NON-NLS-1$
		gl.setReferenz(davAtlGanglinie.getTextValue("Referenzganglinie") //$NON-NLS-1$
				.getText().equals("Ja")); //$NON-NLS-1$
		gl.setApproximationDaK(davAtlGanglinie
				.getUnscaledValue("GanglinienVerfahren").byteValue()); //$NON-NLS-1$
		gl.setBSplineOrdnung(davAtlGanglinie.getUnscaledValue("Ordnung") //$NON-NLS-1$
				.intValue());

		final Data.Array stuetzstellen = davAtlGanglinie
				.getArray("Stützstelle"); //$NON-NLS-1$
		for (int i = 0; i < stuetzstellen.getLength(); i++) {
			final Data stuetzstelle = stuetzstellen.getItem(i);
			final Messwerte mw = new Messwerte(
					stuetzstelle.getScaledValue("QKfz").doubleValue(), //$NON-NLS-1$
					stuetzstelle.getScaledValue("QLkw") //$NON-NLS-1$
							.doubleValue(),
					stuetzstelle.getScaledValue("VPkw") //$NON-NLS-1$
							.doubleValue(),
					stuetzstelle.getScaledValue("VLkw") //$NON-NLS-1$
							.doubleValue());
			gl.put(stuetzstelle.getTimeValue("Zeit").getMillis(), mw); //$NON-NLS-1$
		}

		return gl;
	}

	/**
	 * Konvertiert die uebergebene Ganglinie im internen Format in das
	 * uebergebene DAV-Datum.
	 *
	 * @param davAtlGanglinie
	 *            ein DAV-Datum <code>atl.ganglinie</code>.
	 * @param ganglinieIntern
	 *            eine Ganglinie im internen Format.
	 */
	public static final void konvertiere(final Data davAtlGanglinie,
			final GanglinieMQ ganglinieIntern) {
		davAtlGanglinie.getReferenceValue("EreignisTyp").setSystemObject( //$NON-NLS-1$
				ganglinieIntern.getEreignisTyp().getSystemObject());
		davAtlGanglinie.getUnscaledValue("AnzahlVerschmelzungen").set( //$NON-NLS-1$
				ganglinieIntern.getAnzahlVerschmelzungen());
		davAtlGanglinie.getTimeValue("LetzteVerschmelzung").setMillis( //$NON-NLS-1$
				ganglinieIntern.getLetzteVerschmelzung());
		davAtlGanglinie.getUnscaledValue("GanglinienTyp").set( //$NON-NLS-1$
				ganglinieIntern.getTyp());
		davAtlGanglinie.getUnscaledValue("Referenzganglinie").setText( //$NON-NLS-1$
				ganglinieIntern.isReferenz() ? "Ja" : "Nein"); //$NON-NLS-1$ //$NON-NLS-2$
		davAtlGanglinie.getUnscaledValue("GanglinienVerfahren").set( //$NON-NLS-1$
				ganglinieIntern.getApproximationDaK());
		davAtlGanglinie.getUnscaledValue("Ordnung").set( //$NON-NLS-1$
				ganglinieIntern.getBSplineOrdnung());

		final Data.Array stuetzstellen = davAtlGanglinie
				.getArray("Stützstelle"); //$NON-NLS-1$
		stuetzstellen.setLength(ganglinieIntern.size());

		final List<Long> zeitStempelList = new ArrayList<Long>();
		zeitStempelList.addAll(ganglinieIntern.keySet());
		Collections.sort(zeitStempelList);
		for (int i = 0; i < zeitStempelList.size(); i++) {
			final Long zeitStempel = zeitStempelList.get(i);
			final Messwerte mw = ganglinieIntern.get(zeitStempel);

			stuetzstellen.getItem(i).getTimeValue("Zeit") //$NON-NLS-1$
					.setMillis(zeitStempel);

			if (mw.getQKfz() != null) {
				stuetzstellen.getItem(i).getScaledValue("QKfz") //$NON-NLS-1$
						.set(mw.getQKfz());
			} else {
				stuetzstellen.getItem(i).getScaledValue("QKfz") //$NON-NLS-1$
						.set(Messwerte.UNDEFINIERT);
			}
			if (mw.getQLkw() != null) {
				stuetzstellen.getItem(i).getScaledValue("QLkw") //$NON-NLS-1$
						.set(mw.getQLkw());
			} else {
				stuetzstellen.getItem(i).getScaledValue("QLkw") //$NON-NLS-1$
						.set(Messwerte.UNDEFINIERT);
			}
			if (mw.getVPkw() != null) {
				stuetzstellen.getItem(i).getScaledValue("VPkw") //$NON-NLS-1$
						.set(mw.getVPkw());
			} else {
				stuetzstellen.getItem(i).getScaledValue("VPkw") //$NON-NLS-1$
						.set(Messwerte.UNDEFINIERT);
			}
			if (mw.getVLkw() != null) {
				stuetzstellen.getItem(i).getScaledValue("VLkw") //$NON-NLS-1$
						.set(mw.getVLkw());
			} else {
				stuetzstellen.getItem(i).getScaledValue("VLkw") //$NON-NLS-1$
						.set(Messwerte.UNDEFINIERT);
			}
		}
	}

}
