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

package de.bsvrz.iav.gllib.gllib;

import static com.bitctrl.Constants.MILLIS_PER_HOUR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.bitctrl.Constants;
import com.bitctrl.util.Interval;
import com.bitctrl.util.Timestamp;

import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;
import de.bsvrz.iav.gllib.gllib.dav.GanglinienMQOperationen;
import de.bsvrz.iav.gllib.gllib.dav.Messwerte;
import de.bsvrz.iav.gllib.gllib.junit.ZufallsganglinienFactory;
import de.bsvrz.sys.funclib.bitctrl.math.RationaleZahl;

/**
 * Testet die Approximation einer Ganglinie mit Hilfe eines Cubic-Splines.
 *
 * @author BitCtrl Systems GmbH, Schumann
 */
@SuppressWarnings("nls")
public class CubicSplineTest {

	/** Die Testganglinie. */
	private Ganglinie<Double> ganglinie;

	/**
	 * Testganglinie initialisieren.
	 */
	@Before
	public void setUp() {
		ganglinie = new Ganglinie<Double>();
		ganglinie.put(0L, 0.0);
		ganglinie.put(3L * MILLIS_PER_HOUR, 3000.0);
		ganglinie.put(4L * MILLIS_PER_HOUR, 2000.0);
		ganglinie.put(6L * MILLIS_PER_HOUR, 4000.0);
		ganglinie.put(9L * MILLIS_PER_HOUR, 1000.0);
		ganglinie.setApproximation(new Polyline());
	}

	/**
	 * Prüft das Verhalten des Cubic-Spline bei (zu) wenigen Stützstellen.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void testAnzahlStuetzstelle() {
		List<Stuetzstelle<Double>> stuetzstellen;
		CubicSpline spline;

		stuetzstellen = new ArrayList<Stuetzstelle<Double>>();
		spline = new CubicSpline();

		// Nur eine Stützstelle
		stuetzstellen
				.add(new Stuetzstelle<Double>(100 * MILLIS_PER_HOUR, 30.0));
		spline.setStuetzstellen(stuetzstellen);
		spline.initialisiere();
		assertEquals(new Stuetzstelle<Double>(0, null), spline.get(0));
		assertEquals(new Stuetzstelle<Double>(100 * MILLIS_PER_HOUR, 30.0),
				spline.get(100 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(120 * MILLIS_PER_HOUR, null),
				spline.get(120 * MILLIS_PER_HOUR));

		// Nur zwei Stützstellen
		stuetzstellen
				.add(new Stuetzstelle<Double>(200 * MILLIS_PER_HOUR, 80.0));
		spline.setStuetzstellen(stuetzstellen);
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
		spline.setStuetzstellen(stuetzstellen);
		spline.initialisiere();
		assertEquals(new Stuetzstelle<Double>(0, null), spline.get(0));
		assertEquals(new Stuetzstelle<Double>(100 * MILLIS_PER_HOUR, 30.0),
				spline.get(100 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(200 * MILLIS_PER_HOUR, 80.0),
				spline.get(200 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(300 * MILLIS_PER_HOUR, 40.0),
				spline.get(300 * MILLIS_PER_HOUR));
		assertEquals(new Stuetzstelle<Double>(320 * MILLIS_PER_HOUR, null),
				spline.get(320 * MILLIS_PER_HOUR));
	}

	/**
	 * Testet ob der Spline durch ausgewählte vorher berechnete Punkte
	 * durchgeht.
	 */
	@Test
	public void testGet() {
		CubicSpline spline;
		long t;

		spline = new CubicSpline();
		spline.setStuetzstellen(ganglinie.getStuetzstellen());
		spline.initialisiere();

		t = 0;
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));

		t = 1 * MILLIS_PER_HOUR;
		assertEquals(new RationaleZahl(1201000, 657).doubleValue(),
				(Object) spline.get(t).getWert());

		t = 2 * MILLIS_PER_HOUR;
		assertEquals(new RationaleZahl(1994000, 657).doubleValue(),
				(Object) spline.get(t).getWert());

		t = 3 * MILLIS_PER_HOUR;
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));

		t = 4 * MILLIS_PER_HOUR;
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));

		t = 5 * MILLIS_PER_HOUR;
		assertEquals(new RationaleZahl(198500, 73).doubleValue(),
				(Object) spline.get(t).getWert());

		t = 6 * MILLIS_PER_HOUR;
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));

		t = 7 * MILLIS_PER_HOUR;
		assertEquals(new RationaleZahl(2621000, 657).doubleValue(),
				(Object) spline.get(t).getWert());

		t = 8 * MILLIS_PER_HOUR;
		assertEquals(new RationaleZahl(1834000, 657).doubleValue(),
				(Object) spline.get(t).getWert());

		t = 9 * MILLIS_PER_HOUR;
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));
	}

	/**
	 * Testet ob der Spline durch alle Stützstellen durchgeht.
	 */
	@Test
	public void testGetStuetzstellen() {
		CubicSpline spline;
		long t;

		spline = new CubicSpline();
		spline.setStuetzstellen(ganglinie.getStuetzstellen());
		spline.initialisiere();

		t = 0;
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));
		t = 3 * MILLIS_PER_HOUR;
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));
		t = 4 * MILLIS_PER_HOUR;
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));
		t = 6 * MILLIS_PER_HOUR;
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));
		t = 9 * MILLIS_PER_HOUR;
		assertEquals(ganglinie.getStuetzstelle(t), spline.get(t));
	}

	/**
	 * Macht einen Performance-Test mit einer zufälligen Ganglinie mit
	 * Double-Stützstellen. Das Ergebnis wird nur auf der Konsole ausgegeben.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void performance() {
		final Ganglinie<Double> g;
		final CubicSpline spline;
		final Calendar cal;
		final Interval intervall;
		long zeitstempel;
		int i;

		System.out.println(
				"Starte Performancetest Cubic-Spline für einfache Ganglinie ...");

		cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		g = ZufallsganglinienFactory
				.erzeugeGanglinie(Constants.MILLIS_PER_HOUR / 20);
		GanglinienOperationen.verschiebe(g, cal.getTimeInMillis());
		assertEquals("Die Anzahl der Stützstellen muss stimmen.", 481,
				g.size());

		spline = new CubicSpline();
		g.setApproximation(spline);

		zeitstempel = System.currentTimeMillis();
		i = 0;
		intervall = new Interval(cal.getTimeInMillis(),
				cal.getTimeInMillis() + Constants.MILLIS_PER_DAY);
		for (long t = intervall.getStart(); t <= intervall
				.getEnd(); t += Constants.MILLIS_PER_MINUTE) {
			// for (long t = 0; t <= Constants.MILLIS_PER_DAY; t +=
			// Constants.MILLIS_PER_MINUTE) {
			Stuetzstelle<Double> s;

			s = g.getStuetzstelle(t);
			assertEquals(
					"Der Zeitstempel der berechneten Stützstelle muss mit der Anfrage übereinstimmen.",
					t, s.getZeitstempel());
			assertTrue("Der Stützstellenwert darf nicht null sein.",
					s.getWert() != null);
			// System.out.println(s);
			++i;
		}
		zeitstempel = System.currentTimeMillis() - zeitstempel;
		System.out.println("Berechnung von " + i + " Stützstellen in "
				+ Timestamp.relativeTime(zeitstempel));
	}

	/**
	 * Macht einen Performance-Test mit einer zufälligen
	 * Messquerschnittsfanglinie mit Double-Stützstellen. Das Ergebnis wird nur
	 * auf der Konsole ausgegeben.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void performanceMQ() {
		final GanglinieMQ g;
		final Calendar cal;
		final Interval intervall;
		long zeitstempel;
		int i;

		System.out.println(
				"Starte Performancetest Cubic-Spline für Messquerschnittsganglinie ...");

		cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		g = ZufallsganglinienFactory.erzeugeGanglinie(null,
				Constants.MILLIS_PER_HOUR);
		GanglinienMQOperationen.verschiebe(g, cal.getTimeInMillis());
		assertEquals("Die Anzahl der Stützstellen muss stimmen.", 25, g.size());

		g.setApproximationDaK(GanglinieMQ.APPROX_CUBICSPLINE);

		zeitstempel = System.currentTimeMillis();
		i = 0;
		intervall = new Interval(cal.getTimeInMillis(),
				cal.getTimeInMillis() + Constants.MILLIS_PER_DAY);
		for (long t = intervall.getStart(); t <= intervall
				.getEnd(); t += Constants.MILLIS_PER_MINUTE) {
			// for (long t = 0; t <= Constants.MILLIS_PER_DAY; t +=
			// Constants.MILLIS_PER_MINUTE) {
			Stuetzstelle<Messwerte> s;

			s = g.getStuetzstelle(t);
			assertEquals(
					"Der Zeitstempel der berechneten Stützstelle muss mit der Anfrage übereinstimmen.",
					t, s.getZeitstempel());
			assertTrue("Der Stützstellenwert QKfz darf nicht null sein.",
					s.getWert().getQKfz() != null);
			assertTrue("Der Stützstellenwert QLkw darf nicht null sein.",
					s.getWert().getQLkw() != null);
			assertTrue("Der Stützstellenwert VPkw darf nicht null sein.",
					s.getWert().getVPkw() != null);
			assertTrue("Der Stützstellenwert VLkw darf nicht null sein.",
					s.getWert().getVLkw() != null);
			// System.out.println(s);
			++i;
		}
		zeitstempel = System.currentTimeMillis() - zeitstempel;
		System.out.println("Berechnung von " + i + " Stützstellen in "
				+ Timestamp.relativeTime(zeitstempel));
	}

}
