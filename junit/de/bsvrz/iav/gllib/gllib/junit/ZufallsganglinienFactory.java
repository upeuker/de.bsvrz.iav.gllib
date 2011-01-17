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

package de.bsvrz.iav.gllib.gllib.junit;

import java.util.Random;

import com.bitctrl.Constants;

import de.bsvrz.iav.gllib.gllib.Ganglinie;
import de.bsvrz.iav.gllib.gllib.Stuetzstelle;
import de.bsvrz.sys.funclib.bitctrl.modell.att.RelativerZeitstempel;
import de.bsvrz.sys.funclib.bitctrl.modell.att.Zeitstempel;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AtlGanglinie;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AtlStuetzstelle;
import de.bsvrz.sys.funclib.bitctrl.modell.tmganglinienglobal.attribute.AttStuetzStellenWert;
import de.bsvrz.sys.funclib.bitctrl.modell.tmverkehrglobal.objekte.MessQuerschnittAllgemein;
import de.bsvrz.sys.funclib.bitctrl.modell.tmverkehrglobal.onlinedaten.OdVerkehrsDatenKurzZeitMq;
import de.bsvrz.sys.funclib.bitctrl.modell.util.test.VerkehrsDatenKurzZeitMqGenerator;

/**
 * Erzeugt für bestimmte Ereignistypen je eine Ganglinie.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id: ZufallsganglinienFactory.java 8526 2008-04-21 15:31:50Z
 *          Schumann $
 */
public final class ZufallsganglinienFactory {

	/** Die Eigenschaft {@code singleton}. */
	private static ZufallsganglinienFactory singleton;

	/**
	 * Gibt das Singleton der Factory zurück.
	 * 
	 * @return die Ganglinienfactory.
	 */
	public static ZufallsganglinienFactory getInstance() {
		if (singleton == null) {
			singleton = new ZufallsganglinienFactory();
		}
		return singleton;
	}

	/**
	 * Konstruktor verstecken.
	 */
	private ZufallsganglinienFactory() {
		// nix
	}

	/**
	 * Erzeugt eine Ganglinien mit zufälligen Stützstellen.
	 * <p>
	 * <em>Hinweis:</em> Die Ganglinie wird keinem Ereignistyp zugeordnet.
	 * Dieser muss nachgetragen werden, bevor die Ganglinien an den
	 * Datenverteiler gesendet werden kann.
	 * 
	 * @param mq
	 *            der Messquerschnitt der Ganglinie.
	 * @param abstand
	 *            der gewünschte Abstand Stützstellen in Millisekunden.
	 * @return die generierte Ganglinie.
	 */
	public AtlGanglinie erzeugeGanglinie(final MessQuerschnittAllgemein mq,
			final long abstand) {
		AtlGanglinie g;
		long t;
		VerkehrsDatenKurzZeitMqGenerator generator;

		g = new AtlGanglinie();
		// g.setMessQuerschnitt(mq);
		g.setLetzteVerschmelzung(new Zeitstempel(System.currentTimeMillis()));

		t = 0;
		generator = new VerkehrsDatenKurzZeitMqGenerator();
		while (t <= 24 * 60 * 60 * 1000) {
			OdVerkehrsDatenKurzZeitMq.Daten datum;
			// final Messwerte messwerte;
			// final Number qKfz, qLkw, vPkw, vLkw;

			datum = generator.generiere();
			final AtlStuetzstelle stuetzstelle = new AtlStuetzstelle();
			stuetzstelle.setQKfz(new AttStuetzStellenWert(datum.getQKfz()
					.getWert().doubleValue()));
			stuetzstelle.setQLkw(new AttStuetzStellenWert(datum.getQLkw()
					.getWert().doubleValue()));
			stuetzstelle.setVPkw(new AttStuetzStellenWert(datum.getVPkw()
					.getWert().doubleValue()));
			stuetzstelle.setVLkw(new AttStuetzStellenWert(datum.getVLkw()
					.getWert().doubleValue()));
			stuetzstelle.setZeit(new RelativerZeitstempel(t));
			// qKfz = datum.getQKfz().getWert();
			// qLkw = datum.getQLkw().getWert();
			// vPkw = datum.getVPkw().getWert();
			// vLkw = datum.getVLkw().getWert();
			// messwerte = new Messwerte(qKfz != null ? qKfz.doubleValue() :
			// null,
			// qLkw != null ? qLkw.doubleValue() : null,
			// vPkw != null ? vPkw.doubleValue() : null,
			// vLkw != null ? vLkw.doubleValue() : null);
			// g.getStuetzstelle().add((new Stuetzstelle<Messwerte>(t,
			// messwerte));
			g.getStuetzstelle().add(stuetzstelle);

			t += abstand;
		}

		return g;
	}

	/**
	 * Erzeugt eine Ganglinien mit zufälligen Stützstellen.
	 * 
	 * @param abstand
	 *            der gewünschte Abstand Stützstellen in Millisekunden.
	 * @return die generierte Ganglinie.
	 */
	public Ganglinie<Double> erzeugeGanglinie(final long abstand) {
		Ganglinie<Double> g;
		Random generator;

		g = new Ganglinie<Double>();

		generator = new Random();
		for (long t = 0; t <= Constants.MILLIS_PER_DAY; t += abstand) {
			Double wert;

			wert = generator.nextDouble() * 1000;
			g.setStuetzstelle(new Stuetzstelle<Double>(t, wert));
		}

		return g;
	}
}
