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

		System.err.println("B-Spline:");
		spline = new BSpline(ganglinie, 2);
		for (int i = 0; i < 10; i += 1) {
			System.err.println(spline.get(i));
		}
	}

	@Test
	public void testB() {
		fail("Not yet implemented");
	}

}
