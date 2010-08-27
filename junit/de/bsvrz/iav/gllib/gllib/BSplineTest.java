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

package de.bsvrz.iav.gllib.gllib;

import static com.bitctrl.Constants.MILLIS_PER_HOUR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import com.bitctrl.Constants;
import com.bitctrl.util.Interval;
import com.bitctrl.util.Timestamp;

import de.bsvrz.iav.gllib.gllib.dav.ApproximationsVerfahren;
import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;
import de.bsvrz.iav.gllib.gllib.dav.GanglinienMQOperationen;
import de.bsvrz.iav.gllib.gllib.dav.Messwerte;
import de.bsvrz.iav.gllib.gllib.junit.ZufallsganglinienFactory;

/**
 * Testet die Approximation einer Ganglinie mit Hilfe eines B-Spline.
 * 
 * @todo B-Spline nachrechnen
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class BSplineTest {

	/**
	 * Prüft das Verhalten des B-Spline bei (zu) wenigen Stützstellen.
	 */
	@Test
	public void testAnzahlStuetzstelle() {
		List<Stuetzstelle<Double>> stuetzstellen;
		BSpline spline;

		stuetzstellen = new ArrayList<Stuetzstelle<Double>>();

		// Nur eine Stützstelle. Hier kann kein B-Spline bestimmt werden, also
		// gibt es keine gültigen Stützstellen.
		stuetzstellen
				.add(new Stuetzstelle<Double>(100 * MILLIS_PER_HOUR, 30.0));
		spline = new BSpline();
		spline.setStuetzstellen(stuetzstellen);
		spline.setOrdnung((byte) 1);
		spline.initialisiere();
		assertEquals(new Stuetzstelle<Double>(0, null), spline.get(0));
		assertEquals(new Stuetzstelle<Double>(100 * MILLIS_PER_HOUR, null),
				spline.get(100 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(120 * MILLIS_PER_HOUR, null),
				spline.get(120 * MILLIS_PER_HOUR));

		// Nur zwei Stützstellen
		stuetzstellen
				.add(new Stuetzstelle<Double>(200 * MILLIS_PER_HOUR, 80.0));
		spline = new BSpline();
		spline.setStuetzstellen(stuetzstellen);
		spline.setOrdnung((byte) 2);
		spline.initialisiere();
		assertEquals(new Stuetzstelle<Double>(0, null), spline.get(0));
		assertEquals(new Stuetzstelle<Double>(100 * MILLIS_PER_HOUR, 30.0),
				spline.get(100 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(200 * MILLIS_PER_HOUR, 80.0),
				spline.get(200 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(320 * MILLIS_PER_HOUR, null),
				spline.get(320 * MILLIS_PER_HOUR));

		// Nur drei Stützstellen
		stuetzstellen
				.add(new Stuetzstelle<Double>(300 * MILLIS_PER_HOUR, 40.0));
		spline = new BSpline();
		spline.setStuetzstellen(stuetzstellen);
		spline.setOrdnung((byte) 3);
		spline.initialisiere();
		assertEquals(new Stuetzstelle<Double>(0, null), spline.get(0));
		assertEquals(new Stuetzstelle<Double>(100 * MILLIS_PER_HOUR, 30.0),
				spline.get(100 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(200 * MILLIS_PER_HOUR, 57.5),
				spline.get(200 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(300 * MILLIS_PER_HOUR, 40.0),
				spline.get(300 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(320 * MILLIS_PER_HOUR, null),
				spline.get(320 * MILLIS_PER_HOUR));

		// Nur drei Stützstellen, mit Ordnung 5
		spline = new BSpline();
		spline.setStuetzstellen(stuetzstellen);
		spline.initialisiere();
		assertEquals(new Stuetzstelle<Double>(0, null), spline.get(0));
		assertEquals(new Stuetzstelle<Double>(100 * MILLIS_PER_HOUR, 30.0),
				spline.get(100 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(200 * MILLIS_PER_HOUR, 57.5),
				spline.get(200 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(300 * MILLIS_PER_HOUR, 40.0),
				spline.get(300 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(320 * MILLIS_PER_HOUR, null),
				spline.get(320 * MILLIS_PER_HOUR));
	}

	/**
	 * Prüft ob bei Anfrage einer Stützstelle auch der richtige Zeitstempel
	 * kommt. Beim B-Spline ist dies nicht trivial. Dieser Test arbeitet mit
	 * einer vorgegebenen Ganglinie.
	 */
	@Test
	public void testGetA() {
		Ganglinie<Double> g;
		BSpline spline;

		g = new Ganglinie<Double>();
		g.put(0L, 0.0);
		g.put(3L * MILLIS_PER_HOUR, 300.0);
		g.put(4L * MILLIS_PER_HOUR, 200.0);
		g.put(6L * MILLIS_PER_HOUR, 400.0);
		g.put(9L * MILLIS_PER_HOUR, 100.0);

		spline = new BSpline();
		spline.setStuetzstellen(g.getStuetzstellen());

		// Rechnen
		for (byte k = 1; k <= g.size() && k <= 10; k++) {
			spline.setOrdnung(k);
			spline.initialisiere();

			for (long t = g.getIntervall().getStart(); t <= g.getIntervall()
					.getEnd(); t += 60 * 1000) {
				assertEquals(t, spline.get(t).getZeitstempel());
			}
		}
	}

	/**
	 * Macht einen Performance-Test mit einer zufälligen Ganglinie mit
	 * Double-Stützstellen. Das Ergebnis wird nur auf der Konsole ausgegeben.
	 */
	@Test
	public void performance() {
		final Ganglinie<Double> g;
		final BSpline bspline;
		final Calendar cal;
		final Interval intervall;
		long zeitstempel;
		int i;

		System.out
				.println("Starte Performancetest B-Spline für einfache Ganglinie ...");

		cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		g = ZufallsganglinienFactory.getInstance().erzeugeGanglinie(
				Constants.MILLIS_PER_HOUR / 20);
		GanglinienOperationen.verschiebe(g, cal.getTimeInMillis());
		assertEquals("Die Anzahl der Stützstellen muss stimmen.", 481, g.size());

		bspline = new BSpline(5);
		g.setApproximation(bspline);

		zeitstempel = System.currentTimeMillis();
		i = 0;
		intervall = new Interval(cal.getTimeInMillis(), cal.getTimeInMillis()
				+ Constants.MILLIS_PER_DAY);
		for (long t = intervall.getStart(); t <= intervall.getEnd(); t += Constants.MILLIS_PER_MINUTE) {
			// for (long t = 0; t <= Constants.MILLIS_PER_DAY; t +=
			// Constants.MILLIS_PER_MINUTE) {
			Stuetzstelle<Double> s;

			s = g.getStuetzstelle(t);
			assertEquals(
					"Der Zeitstempel der berechneten Stützstelle muss mit der Anfrage übereinstimmen.",
					t, s.getZeitstempel());
			assertTrue("Der Stützstellenwert darf nicht null sein.", s
					.getWert() != null);
			// System.out.println(s);
			++i;
		}
		zeitstempel = System.currentTimeMillis() - zeitstempel;
		System.out.println("Berechnung von " + i + " Stützstellen in "
				+ Timestamp.relativeTime(zeitstempel));
	}

	/**
	 * Macht einen Performance-Test mit einer zufälligen
	 * Messquerschnittsganglinie mit Double-Stützstellen. Das Ergebnis wird nur
	 * auf der Konsole ausgegeben.
	 */
	@Test
	public void performanceMQ() {
		final GanglinieMQ g;
		final Calendar cal;
		final Interval intervall;
		long zeitstempel;
		int i;

		System.out
				.println("Starte Performancetest B-Spline für Messquerschnittsganglinie ...");

		cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		g = ZufallsganglinienFactory.getInstance().erzeugeGanglinie(null,
				Constants.MILLIS_PER_HOUR);
		GanglinienMQOperationen.verschiebe(g, cal.getTimeInMillis());
		assertEquals("Die Anzahl der Stützstellen muss stimmen.", 25, g.size());

		g.setApproximationsVerfahren(ApproximationsVerfahren.BSpline);
		g.setBSplineOrdnung(5);

		zeitstempel = System.currentTimeMillis();
		i = 0;
		intervall = new Interval(cal.getTimeInMillis(), cal.getTimeInMillis()
				+ Constants.MILLIS_PER_DAY);
		for (long t = intervall.getStart(); t <= intervall.getEnd(); t += Constants.MILLIS_PER_MINUTE) {
			// for (long t = 0; t <= Constants.MILLIS_PER_DAY; t +=
			// Constants.MILLIS_PER_MINUTE) {
			Stuetzstelle<Messwerte> s;

			s = g.getStuetzstelle(t);
			assertEquals(
					"Der Zeitstempel der berechneten Stützstelle muss mit der Anfrage übereinstimmen.",
					t, s.getZeitstempel());
			assertTrue("Der Stützstellenwert QKfz darf nicht null sein.", s
					.getWert().getQKfz() != null);
			assertTrue("Der Stützstellenwert QLkw darf nicht null sein.", s
					.getWert().getQLkw() != null);
			assertTrue("Der Stützstellenwert VPkw darf nicht null sein.", s
					.getWert().getVPkw() != null);
			assertTrue("Der Stützstellenwert VLkw darf nicht null sein.", s
					.getWert().getVLkw() != null);
			// System.out.println(s);
			++i;
		}
		zeitstempel = System.currentTimeMillis() - zeitstempel;
		System.out.println("Berechnung von " + i + " Stützstellen in "
				+ Timestamp.relativeTime(zeitstempel));
	}

}
