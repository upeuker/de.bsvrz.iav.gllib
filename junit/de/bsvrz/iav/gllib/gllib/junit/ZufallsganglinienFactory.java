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

import de.bsvrz.iav.gllib.gllib.Stuetzstelle;
import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;
import de.bsvrz.iav.gllib.gllib.dav.Messwerte;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.objekte.MessQuerschnittAllgemein;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.onlinedaten.OdVerkehrsDatenKurzZeitMq;
import de.bsvrz.sys.funclib.bitctrl.test.zufallsdaten.verkehr.VerkehrsDatenKurzZeitMqGenerator;

/**
 * Erzeugt für bestimmte Ereignistypen je eine Ganglinie.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id: ZufallsganglinienFactory.java 8374 2008-04-16 18:24:43Z
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
	public GanglinieMQ erzeugeGanglinie(final MessQuerschnittAllgemein mq,
			final long abstand) {
		GanglinieMQ g;
		long t;
		VerkehrsDatenKurzZeitMqGenerator generator;

		g = new GanglinieMQ();
		g.setMessQuerschnitt(mq);
		g.setLetzteVerschmelzung(System.currentTimeMillis());

		t = 0;
		generator = new VerkehrsDatenKurzZeitMqGenerator();
		while (t <= 24 * 60 * 60 * 1000) {
			OdVerkehrsDatenKurzZeitMq.Daten datum;
			Messwerte messwerte;
			Number qKfz, qLkw, vPkw, vLkw;

			datum = generator.generiere();
			qKfz = datum.getWert(OdVerkehrsDatenKurzZeitMq.Daten.Werte.QKfz
					.name());
			qLkw = datum.getWert(OdVerkehrsDatenKurzZeitMq.Daten.Werte.QLkw
					.name());
			vPkw = datum.getWert(OdVerkehrsDatenKurzZeitMq.Daten.Werte.VPkw
					.name());
			vLkw = datum.getWert(OdVerkehrsDatenKurzZeitMq.Daten.Werte.VLkw
					.name());
			messwerte = new Messwerte(qKfz != null ? qKfz.doubleValue() : null,
					qLkw != null ? qLkw.doubleValue() : null,
					vPkw != null ? vPkw.doubleValue() : null,
					vLkw != null ? vLkw.doubleValue() : null);
			g.setStuetzstelle(new Stuetzstelle<Messwerte>(t, messwerte));

			t += abstand;
		}

		return g;
	}

}
