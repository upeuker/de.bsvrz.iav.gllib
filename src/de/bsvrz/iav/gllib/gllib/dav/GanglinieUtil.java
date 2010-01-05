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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.bitctrl.Constants;
import com.bitctrl.util.Interval;

import de.bsvrz.sys.funclib.bitctrl.modell.att.Feld;
import de.bsvrz.sys.funclib.bitctrl.modell.att.Zeitstempel;
import de.bsvrz.sys.funclib.bitctrl.modell.fachmodellglobal.attribute.AttProzent;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AtlGanglinie;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AtlPrognoseGanglinie;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AtlPrognoseGanglinienAnfrage;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AtlStuetzstelle;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttAnzahlSekunden1Bis;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.parameter.PdGanglinie;
import de.bsvrz.sys.funclib.bitctrl.modell.tmverkehrglobal.objekte.MessQuerschnittAllgemein;
import de.bsvrz.sys.funclib.bitctrl.modell.util.KappichModellUtil;

/**
 * Enthält allgemeine Funktionen auf oder mit Datenverteilerganglinien.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public final class GanglinieUtil {

	public static List<AtlPrognoseGanglinienAnfrage> konvertiere(
			final Collection<GlProgAnfrage> anfragen) {
		final List<AtlPrognoseGanglinienAnfrage> result = new ArrayList<AtlPrognoseGanglinienAnfrage>();
		for (final GlProgAnfrage anfrage : anfragen) {
			// Allgemeine Anfragedaten
			final AtlPrognoseGanglinienAnfrage atl = new AtlPrognoseGanglinienAnfrage();
			atl.setMessquerschnitt(anfrage.getMessQuerschnitt());
			atl.setZeitpunktPrognoseBeginn(new Zeitstempel(anfrage
					.getPrognoseZeitraum().getStart()));
			atl.setZeitpunktPrognoseEnde(new Zeitstempel(anfrage
					.getPrognoseZeitraum().getEnd()));
			atl.setNurLangfristigeAuswahl(KappichModellUtil
					.konvertiereBool(anfrage.isNurLangfristigeAuswahl()));

			// Anfragedaten für zyklische Prognose
			atl.setZyklischePrognose(KappichModellUtil.konvertiereBool(anfrage
					.isZyklischePrognose()));
			atl.setUeberpruefungsintervall(new AttAnzahlSekunden1Bis(anfrage
					.getPruefIntervall()
					/ Constants.MILLIS_PER_SECOND));
			atl.setAktualisierungsintervall(new AttAnzahlSekunden1Bis(anfrage
					.getSendeIntervall()
					/ Constants.MILLIS_PER_SECOND));
			atl.setAktualisierungsschwelle(new AttProzent((byte) anfrage
					.getSchwelle()));

			result.add(atl);
		}
		return result;
	}

	public static List<GanglinieMQ> konvertiere(final PdGanglinie.Daten datum) {
		final List<GanglinieMQ> result = new ArrayList<GanglinieMQ>();
		for (final AtlGanglinie g : datum.getGanglinie()) {
			final GanglinieMQ ganglinie = new GanglinieMQ();
			ganglinie.setMessQuerschnitt((MessQuerschnittAllgemein) datum
					.dGetDatensatz().getSystemObjekt());
			ganglinie.setEreignisTyp(g.getEreignisTyp());
			ganglinie.setAnzahlVerschmelzungen(g.getAnzahlVerschmelzungen()
					.longValue());
			ganglinie.setApproximationsVerfahren(ApproximationsVerfahren
					.valueOf(g.getGanglinienVerfahren()));
			ganglinie.setBSplineOrdnung(g.getOrdnung().intValue());
			ganglinie.setLetzteVerschmelzung(g.getLetzteVerschmelzung()
					.getTime());
			ganglinie.setReferenz(0 != g.getReferenzganglinie().byteValue());
			for (final AtlStuetzstelle s : g.getStuetzstelle()) {
				ganglinie.put(s.getZeit().getTime(), new Messwerte(s.getQKfz()
						.getValue(), s.getQLkw().getValue(), s.getVPkw()
						.getValue(), s.getVLkw().getValue()));
			}

			result.add(ganglinie);
		}
		return result;
	}

	public static List<GanglinieMQ> konvertiere(
			final Feld<AtlPrognoseGanglinie> ganglinien) {
		final List<GanglinieMQ> result = new ArrayList<GanglinieMQ>();
		for (final AtlPrognoseGanglinie g : ganglinien) {
			final GanglinieMQ ganglinie = new GanglinieMQ();
			ganglinie.setMessQuerschnitt(g.getMessquerschnitt());
			ganglinie.setPrognoseZeitraum(new Interval(g
					.getZeitpunktPrognoseBeginn().getTime(), g
					.getZeitpunktPrognoseEnde().getTime()));

			for (final AtlStuetzstelle s : g.getStuetzstelle()) {
				ganglinie.put(s.getZeit().getTime(), new Messwerte(s.getQKfz()
						.getValue(), s.getQLkw().getValue(), s.getVPkw()
						.getValue(), s.getVLkw().getValue()));
			}

			ganglinie.setApproximationsVerfahren(ApproximationsVerfahren
					.valueOf(g.getGanglinienVerfahren()));
			ganglinie.setBSplineOrdnung(g.getOrdnung().intValue());

			result.add(ganglinie);
		}
		return result;
	}

	private GanglinieUtil() {
		// utility class
	}
}
