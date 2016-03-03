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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import com.bitctrl.Constants;
import com.bitctrl.util.Interval;
import com.bitctrl.util.Timestamp;

import de.bsvrz.iav.gllib.gllib.junit.ZufallsganglinienFactory;

/**
 * Testet die Approximation einer Ganglinie mit Hilfe einer Treppenfunktion.
 *
 * @author BitCtrl Systems GmbH, Schumann
 */
@SuppressWarnings("nls")
public class TreppenfunktionTest {

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
	 * Testet ob der Spline durch alle Stützstellen durchgeht.
	 */
	@Test
	public void testGet() {
		Treppenfunktion treppe;
		long t;

		treppe = new Treppenfunktion();
		treppe.setStuetzstellen(ganglinie.getStuetzstellen());

		// Die existierenden Stützstellen
		t = 0;
		assertEquals(ganglinie.getStuetzstellen().get(0), treppe.get(t));
		t = 3000;
		assertEquals(ganglinie.getStuetzstellen().get(1), treppe.get(t));
		t = 4000;
		assertEquals(ganglinie.getStuetzstellen().get(2), treppe.get(t));
		t = 6000;
		assertEquals(ganglinie.getStuetzstellen().get(3), treppe.get(t));
		t = 9000;
		assertEquals(ganglinie.getStuetzstellen().get(4), treppe.get(t));

		// Punkte zwischen den Stützstellen
		t = 1000;
		assertEquals(new Stuetzstelle<Double>(t, 0.0), treppe.get(t));
		t = 2000;
		assertEquals(new Stuetzstelle<Double>(t, 0.0), treppe.get(t));
		t = 5000;
		assertEquals(new Stuetzstelle<Double>(t, 20.0), treppe.get(t));
		t = 7000;
		assertEquals(new Stuetzstelle<Double>(t, 40.0), treppe.get(t));
		t = 8000;
		assertEquals(new Stuetzstelle<Double>(t, 40.0), treppe.get(t));
	}

	/**
	 * Testet die Methode {@link Treppenfunktion#integral(Interval)}.
	 */
	@Test
	public void testIntegral() {
		Treppenfunktion treppe;
		Interval intervall;

		treppe = new Treppenfunktion();
		treppe.setStuetzstellen(ganglinie.getStuetzstellen());

		// Intervallgrenzen liegen auf Stützstellen
		intervall = new Interval(0, 9000);
		assertEquals(190000.0, treppe.integral(intervall), 0.1);

		// Intervallgrenzen liegen nicht auf Stützstellen
		intervall = new Interval(3500, 8500);
		assertEquals(170000.0, treppe.integral(intervall), 0.1);
	}

	/**
	 * Macht einen Performance-Test mit einer zufälligen Ganglinie mit
	 * Double-Stützstellen. Das Ergebnis wird nur auf der Konsole ausgegeben.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void performance() {
		final Ganglinie<Double> g;
		final Treppenfunktion treppe;
		final Calendar cal;
		final Interval intervall;
		long zeitstempel;
		int i;

		System.out.println(
				"Starte Performancetest Treppenfunktion für einfache Ganglinie ...");

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

		treppe = new Treppenfunktion();
		g.setApproximation(treppe);

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

}
