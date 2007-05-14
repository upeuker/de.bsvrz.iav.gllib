package de.bsvrz.iav.gllib.gllib;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestBSpline {

	private Ganglinie ganglinie;

	@Before
	public void setUp() {
		ganglinie = new Ganglinie();
		ganglinie.set(new Stuetzstelle(0, 0));
		ganglinie.set(new Stuetzstelle(30, 30));
		ganglinie.set(new Stuetzstelle(40, 20));
		ganglinie.set(new Stuetzstelle(60, 40));
		ganglinie.set(new Stuetzstelle(90, 10));
	}

	@Test
	public void testGet() {
		Ganglinie g;
		BSpline spline;
		int k;

		g = new Ganglinie();
		for (int i = 0; i < 1000; i++) {
			long x = (long) (Math.random() * 1000);
			int y = (int) (Math.random() * 1000);
			g.set(x, y);
		}

		k = 4;
		spline = new BSpline(g, k);

		// Infos ausgeben
		System.err.println("B-Spline mit Ordnung " + k + " mit "
				+ g.anzahlStuetzstellen() + " Stützstellen:");
		System.err.println(spline.ganglinie);

		// Rechnen
		for (long t = g.getIntervall().start; t <= g.getIntervall().ende; t++) {
			Stuetzstelle s = spline.getAnders(t);
			assertEquals(t, s.zeitstempel);
		}
	}

	@Test
	public void testGetEndlosschleife() {
		Ganglinie g;
		BSpline spline;
		int k;

		g = new Ganglinie();
		g.set(0, 0);
		g.set(300, 300);
		g.set(400, 200);
		g.set(600, 400);
		g.set(900, 100);

		k = 1;
		spline = new BSpline(g, k);

		// Infos ausgeben
		System.err.println("B-Spline mit " + g.anzahlStuetzstellen()
				+ " Stützstellen und Ordnung " + k + ":");
		System.err.println(spline.ganglinie);

		// Rechnen
		for (long t = g.getIntervall().start; t <= g.getIntervall().ende; t += 1) {
			assertEquals(t, spline.getAnders(t).zeitstempel);
		}
	}

}
