package de.bsvrz.iav.gllib.gllib;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestCubicpline {

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
	public void testGet() throws Exception {
		CubicSpline spline;

		System.out.println("Cubic Spline:");
		spline = new CubicSpline(ganglinie);
		for (int i = 10; i < 100; i += 10) {
			System.out.println(spline.get(i));
		}
	}
}
