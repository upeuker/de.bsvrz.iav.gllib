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
 * Wei�enfelser Stra�e 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Before;
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
 * Testet die Approximation einer Ganglinie mit Hilfe einer Polylinie.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class PolylineTest {

	/** Die Testganglinie. */
	private Ganglinie<Double> ganglinie;

	/**
	 * Testganglinie initialisieren.
	 */
	@Before
	public void setUp() {
		ganglinie = new Ganglinie<Double>();
		ganglinie.put(0L, 0.0);
		ganglinie.put(3000L, 30.0);
		ganglinie.put(4000L, 20.0);
		ganglinie.put(6000L, 40.0);
		ganglinie.put(9000L, 10.0);
	}

	/**
	 * Testet ob der Spline durch alle St�tzstellen durchgeht.
	 */
	@Test
	public void testGet() {
		Polyline polyline;
		long t;

		polyline = new Polyline();
		polyline.setStuetzstellen(ganglinie.getStuetzstellen());

		// Die existierenden St�tzstellen
		t = 0;
		assertEquals(ganglinie.getStuetzstellen().get(0), polyline.get(t));
		t = 3000;
		assertEquals(ganglinie.getStuetzstellen().get(1), polyline.get(t));
		t = 4000;
		assertEquals(ganglinie.getStuetzstellen().get(2), polyline.get(t));
		t = 6000;
		assertEquals(ganglinie.getStuetzstellen().get(3), polyline.get(t));
		t = 9000;
		assertEquals(ganglinie.getStuetzstellen().get(4), polyline.get(t));

		// Punkte zwischen den St�tzstellen
		t = 1000;
		assertEquals(new Stuetzstelle<Double>(t, 10.0), polyline.get(t));
		t = 2000;
		assertEquals(new Stuetzstelle<Double>(t, 20.0), polyline.get(t));
		t = 5000;
		assertEquals(new Stuetzstelle<Double>(t, 30.0), polyline.get(t));
		t = 7000;
		assertEquals(new Stuetzstelle<Double>(t, 30.0), polyline.get(t));
		t = 8000;
		assertEquals(new Stuetzstelle<Double>(t, 20.0), polyline.get(t));
	}

	/**
	 * Testet die Methode {@link Treppenfunktion#integral(Interval)}.
	 */
	@Test
	public void testIntegral() {
		Polyline polyline;
		Interval intervall;

		polyline = new Polyline();
		polyline.setStuetzstellen(ganglinie.getStuetzstellen());

		// Intervallgrenzen liegen auf St�tzstellen
		intervall = new Interval(0, 9000);
		assertEquals(205000.0, polyline.integral(intervall), 0.001);

		// Intervallgrenzen liegen nicht auf St�tzstellen
		intervall = new Interval(2000, 8000);
		assertEquals(170000.0, polyline.integral(intervall), 0.001);
	}

	/**
	 * Macht einen Performance-Test mit einer zuf�lligen Ganglinie mit
	 * Double-St�tzstellen. Das Ergebnis wird nur auf der Konsole ausgegeben.
	 */
	@Test
	public void performance() {
		final Ganglinie<Double> g;
		final Polyline polyline;
		final Calendar cal;
		final Interval intervall;
		long zeitstempel;
		int i;

		System.out
				.println("Starte Performancetest Polyline f�r einfache Ganglinie ...");

		cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		g = ZufallsganglinienFactory.getInstance().erzeugeGanglinie(
				Constants.MILLIS_PER_HOUR / 20);
		GanglinienOperationen.verschiebe(g, cal.getTimeInMillis());
		assertEquals("Die Anzahl der St�tzstellen muss stimmen.", 481, g.size());

		polyline = new Polyline();
		g.setApproximation(polyline);

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
					"Der Zeitstempel der berechneten St�tzstelle muss mit der Anfrage �bereinstimmen.",
					t, s.getZeitstempel());
			assertTrue("Der St�tzstellenwert darf nicht null sein.",
					s.getWert() != null);
			// System.out.println(s);
			++i;
		}
		zeitstempel = System.currentTimeMillis() - zeitstempel;
		System.out.println("Berechnung von " + i + " St�tzstellen in "
				+ Timestamp.relativeTime(zeitstempel));
	}

	/**
	 * Macht einen Performance-Test mit einer zuf�lligen
	 * Messquerschnittsfanglinie mit Double-St�tzstellen. Das Ergebnis wird nur
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
				.println("Starte Performancetest Polyline f�r Messquerschnittsganglinie ...");

		cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		g = ZufallsganglinienFactory.getInstance().erzeugeGanglinieMQ(null,
				Constants.MILLIS_PER_HOUR);
		GanglinienMQOperationen.verschiebe(g, cal.getTimeInMillis());
		assertEquals("Die Anzahl der St�tzstellen muss stimmen.", 25, g.size());

		g.setApproximationsVerfahren(ApproximationsVerfahren.Polyline);

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
					"Der Zeitstempel der berechneten St�tzstelle muss mit der Anfrage �bereinstimmen.",
					t, s.getZeitstempel());
			assertTrue("Der St�tzstellenwert QKfz darf nicht null sein.", s
					.getWert().getQKfz() != null);
			assertTrue("Der St�tzstellenwert QLkw darf nicht null sein.", s
					.getWert().getQLkw() != null);
			assertTrue("Der St�tzstellenwert VPkw darf nicht null sein.", s
					.getWert().getVPkw() != null);
			assertTrue("Der St�tzstellenwert VLkw darf nicht null sein.", s
					.getWert().getVLkw() != null);
			// System.out.println(s);
			++i;
		}
		zeitstempel = System.currentTimeMillis() - zeitstempel;
		System.out.println("Berechnung von " + i + " St�tzstellen in "
				+ Timestamp.relativeTime(zeitstempel));
	}

}
