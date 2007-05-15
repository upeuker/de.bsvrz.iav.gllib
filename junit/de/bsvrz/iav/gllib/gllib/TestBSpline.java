package de.bsvrz.iav.gllib.gllib;

import static org.junit.Assert.assertEquals;

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
	public void testGetA() {
		Ganglinie g;
		BSpline spline;

		g = new Ganglinie();
		g.set(0, 0);
		g.set(300, 300);
		g.set(400, 200);
		g.set(600, 400);
		g.set(900, 100);

		spline = new BSpline(g);

		// Rechnen
		// for (long t = 0; t <= 900; t += 1) {
		for (int k = 1; k <= g.anzahlStuetzstellen(); k++) {
			spline.setOrdnung(k);
			for (long t = g.getIntervall().start; t <= g.getIntervall().ende; t += 1) {
				assertEquals(t, spline.get(t).zeitstempel);
			}
		}
	}

	@Test
	public void testGetB() {
		Ganglinie g;
		BSpline spline;

		g = new Ganglinie();
		for (int i = 0; i < 100; i++) {
			long x = (long) (Math.random() * 1000);
			int y = (int) (Math.random() * 1000);
			g.set(x, y);
		}

		spline = new BSpline(g);

		// Rechnen
		for (int k = 1; k < 10; k++) {
			spline.setOrdnung(k);
			for (long t = g.getIntervall().start; t <= g.getIntervall().ende; t += 1) {
				assertEquals(t, spline.get(t).zeitstempel);
			}
		}
	}

}
