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
 * Weißenfelser Straße 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib.dav;

import java.util.List;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dav.daf.main.config.SystemObjectType;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.tmereigniskalenderglobal.objekte.EreignisTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttGanglinienTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttGanglinienVerfahren;
import de.bsvrz.sys.funclib.debug.Debug;

/**
 * Utensilien.
 * 
 * @author BitCtrl Systems GmbH, Thomas Thierfelder
 * 
 * @version $Id$
 */
public class GlSpeicherUtil {

	/**
	 * Standardkonstruktor.
	 */
	private GlSpeicherUtil() {
		//
	}

	/**
	 * Erfragt das Systemobjekt, ueber das die Ganglinien abgefragt bzw.
	 * gespeichert werden koennen.
	 * 
	 * @param dav
	 *            Datenverteiler-Verbindung.
	 * @return das Systemobjekt, ueber das die Ganglinien abgefragt bzw.
	 *         gespeichert werden koennen.
	 */
	public static final SystemObject getGanglinienSpeicherServerObjekt(
			final ClientDavInterface dav) {
		SystemObject serverObj;

		final SystemObjectType glServerTyp = dav.getDataModel().getType(
				"typ.ganglinienSpeicher");

		if (glServerTyp != null) {
			final List<SystemObject> serverList = glServerTyp.getElements();
			if (serverList.size() >= 1) {
				serverObj = serverList.iterator().next();
				if (serverList.size() > 1) {
					Debug.getLogger()
							.warning(
									"Mehr als ein GanglinienSpeicher (typ.ganglinienSpeicher) definiert.");
				}
				Debug.getLogger().info(
						"Benutzte GanglinienSpeicher: " + serverObj);
			} else {
				throw new IllegalStateException(
						"Kein GanglinienSpeicher (typ.ganglinienSpeicher) definiert.");
			}
		} else {
			throw new IllegalStateException(
					"Objekttyp GanglinienSpeicher (typ.ganglinienSpeicher) nicht definiert.");
		}

		return serverObj;
	}

	/**
	 * 
	 * @param atlGanglinie
	 * @param objectFactory
	 * @return
	 */
	public static final GanglinieMQ konvertiere(final Data atlGanglinie,
			final ObjektFactory objectFactory) {
		final GanglinieMQ gl = new GanglinieMQ();

		gl.setEreignisTyp((EreignisTyp) objectFactory
				.getModellobjekt(atlGanglinie.getReferenceValue("EreignisTyp")
						.getSystemObject()));
		gl.setAnzahlVerschmelzungen(atlGanglinie.getUnscaledValue(
				"AnzahlVerschmelzungen").longValue());
		gl.setLetzteVerschmelzung(atlGanglinie.getTimeValue(
				"LetzteVerschmelzung").getMillis());
		gl.setTyp(GanglinienTyp.valueOf(AttGanglinienTyp
				.getZustand(atlGanglinie.getUnscaledValue("GanglinienTyp")
						.byteValue())));
		gl.setReferenz(atlGanglinie.getUnscaledValue("Referenzganglinie")
				.getText().equals("Ja"));
		gl.setApproximationsVerfahren(ApproximationsVerfahren
				.valueOf(AttGanglinienVerfahren.getZustand(atlGanglinie
						.getUnscaledValue("GanglinienVerfahren").byteValue())));
		gl.setBSplineOrdnung(atlGanglinie.getUnscaledValue("Ordnung")
				.intValue());

		final Data.Array stuetzstellen = atlGanglinie.getArray("Stützstelle");
		for (int i = 0; i < stuetzstellen.getLength(); i++) {
			final Data stuetzstelle = stuetzstellen.getItem(i);
			final Messwerte mw = new Messwerte(stuetzstelle.getScaledValue(
					"QKfz").doubleValue(), stuetzstelle.getScaledValue("QLkw")
					.doubleValue(), stuetzstelle.getScaledValue("VPkw")
					.doubleValue(), stuetzstelle.getScaledValue("VLkw")
					.doubleValue());
			gl.put(stuetzstelle.getTimeValue("Zeit").getMillis(), mw);
		}

		return gl;
	}

	public static final void konvertiere(final Data atlGanglinie,
			final GanglinieMQ gl) {
		atlGanglinie.getReferenceValue("EreignisTyp").setSystemObject(
				gl.getEreignisTyp().getSystemObject());
		atlGanglinie.getUnscaledValue("AnzahlVerschmelzungen").set(
				gl.getAnzahlVerschmelzungen());
		atlGanglinie.getTimeValue("LetzteVerschmelzung").setMillis(
				gl.getLetzteVerschmelzung());
		atlGanglinie.getUnscaledValue("GanglinienTyp").set(
				gl.getTyp().getTyp().intValue());
		atlGanglinie.getUnscaledValue("Referenzganglinie").setText(
				gl.isReferenz() ? "Ja" : "Nein");
		atlGanglinie.getUnscaledValue("GanglinienVerfahren").set(
				gl.getApproximationsVerfahren().getVerfahren().intValue());
		atlGanglinie.getUnscaledValue("Ordnung").set(gl.getBSplineOrdnung());

		final Data.Array stuetzstellen = atlGanglinie.getArray("Stützstelle");
		stuetzstellen.setLength(gl.size());
		for (int i = 0; i < stuetzstellen.getLength(); i++) {
			// stuetzstellen.getItem(i).getTimeValue("").setMillis();
			final Data stuetzstelle = stuetzstellen.getItem(i);
			final Messwerte mw = new Messwerte(stuetzstelle.getScaledValue(
					"QKfz").doubleValue(), stuetzstelle.getScaledValue("QLkw")
					.doubleValue(), stuetzstelle.getScaledValue("VPkw")
					.doubleValue(), stuetzstelle.getScaledValue("VLkw")
					.doubleValue());
			gl.put(stuetzstelle.getTimeValue("Zeit").getMillis(), mw);
		}
	}
}
