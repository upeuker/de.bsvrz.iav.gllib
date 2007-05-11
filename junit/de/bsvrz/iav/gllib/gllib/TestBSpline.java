package de.bsvrz.iav.gllib.gllib;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.bsvrz.iav.gllib.gllib.math.RationaleZahl;

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
		BSpline spline;
		int k;

		k = 4;
		spline = new BSpline(ganglinie, k);

		// Infos ausgeben
		System.err.println("B-Spline mit Ordnung " + k + ":");
		System.err.println(spline.ganglinie);

		// Rechnen
		int i = 2;
		Stuetzstelle s = spline.get(i);
		System.err.println("Wert[" + i + "] = " + s);
	}

}
