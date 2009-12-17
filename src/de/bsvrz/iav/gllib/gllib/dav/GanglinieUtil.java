package de.bsvrz.iav.gllib.gllib.dav;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.bitctrl.util.Interval;

import de.bsvrz.sys.funclib.bitctrl.modell.att.Feld;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AtlGanglinie;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AtlPrognoseGanglinie;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AtlPrognoseGanglinienAnfrage;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AtlStuetzstelle;
import de.bsvrz.sys.funclib.bitctrl.modell.tmverkehrglobal.objekte.MessQuerschnittAllgemein;

public final class GanglinieUtil {

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

			ganglinie
					.setApproximationDaK(g.getGanglinienVerfahren().getValue());
			ganglinie.setBSplineOrdnung(g.getOrdnung().intValue());

			result.add(ganglinie);
		}
		return result;
	}

	public static List<GanglinieMQ> konvertiere(
			final Feld<AtlGanglinie> ganglinien,
			final MessQuerschnittAllgemein mq) {
		final List<GanglinieMQ> result = new ArrayList<GanglinieMQ>();
		for (final AtlGanglinie g : ganglinien) {
			final GanglinieMQ ganglinie = new GanglinieMQ();
			ganglinie.setMessQuerschnitt(mq);
			ganglinie.setEreignisTyp(g.getEreignisTyp());
			ganglinie.setAnzahlVerschmelzungen(g.getAnzahlVerschmelzungen()
					.longValue());
			ganglinie
					.setApproximationDaK(g.getGanglinienVerfahren().getValue());
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

	public static List<AtlPrognoseGanglinienAnfrage> konvertiere(
			final Collection<GlProgAnfrage> anfragen) {
		final List<AtlPrognoseGanglinienAnfrage> result = new ArrayList<AtlPrognoseGanglinienAnfrage>();

		return result;
	}

	private GanglinieUtil() {
		// utility class
	}
}
