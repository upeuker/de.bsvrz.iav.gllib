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
		BSpline spline;
		int k;

		k = 1;
		spline = new BSpline(ganglinie, k);
		System.err.println("B-Spline mit Ordnung " + k + ":");
		System.err.println(spline.ganglinie);
		for (int i = 0; i < 100; i += 10) {
			System.err.println("Wert[" + i + "] = " + spline.get(i));
		}
	}

	@Test
	public void testGewicht() {
		BSpline spline;
		int k;
		long[] intervalle;

		k = 1;
		spline = new BSpline(ganglinie, k);
		intervalle = spline.getInterpolationsintervalle();
		System.err.println("B-Spline mit Ordnung " + k + ":");
		System.err.println(spline.ganglinie);
		for (int i = 0; i < intervalle.length; i++) {
			System.err.print(intervalle[i]);
			if (i < intervalle.length - 1) {
				System.err.print(", ");
			}
		}
		System.err.println();
		for (int i = 0; i < 100; i += 10) {
			System.err.println("i = " + i);
			for (int j = 0; j < spline.ganglinie.anzahlStuetzstellen(); j++) {
				System.err.println("Gewicht für j=" + j + " ist "
						+ spline.gewicht(j, k, i));
			}
		}
	}

}
