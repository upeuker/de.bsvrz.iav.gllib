package de.bsvrz.iav.gllib.gllib;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Testet relevante Funktionen der Klasse
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class GanglinieTest {

	/** Eine Testst&uuml;tzstelle */
	private Stuetzstelle s1;

	/** Eine Testst&uuml;tzstelle */
	private Stuetzstelle s2;

	/** Eine Testst&uuml;tzstelle */
	private Stuetzstelle s3;

	/** Eine Testst&uuml;tzstelle */
	private Stuetzstelle s4;

	/** Eine Testst&uuml;tzstelle */
	private Stuetzstelle s5;

	/** Eine Testganglinie */
	private Ganglinie ganglinie;

	/**
	 * Initialisiert die Testganglinie vor jedem Test
	 */
	@Before
	public void beforeTest() {
		ganglinie = new Ganglinie();

		s1 = new Stuetzstelle(10, 34);
		s2 = new Stuetzstelle(20, 23);
		s3 = new Stuetzstelle(30, 54);
		s4 = new Stuetzstelle(40, 22);
		s5 = new Stuetzstelle(50, 43);

		ganglinie.add(s4);
		ganglinie.add(s2);
		ganglinie.add(s5);
		ganglinie.add(s1);
		ganglinie.add(s3);
	}

	/**
	 * Testet die korrekte Bestimmung des Intervalls der
	 * St&uuml;tzstellenzeitstempel
	 */
	@Test
	public void testGetIntervall() {
		Intervall intervall;
		Ganglinie g;

		intervall = ganglinie.getIntervall();
		assertEquals(10L, intervall.start);
		assertEquals(50L, intervall.ende);

		// Sonderfall, eine Ganglinie ohne Stützstellen
		g = new Ganglinie();
		intervall = g.getIntervall();
		assertNull(intervall);
	}

	/**
	 * Pr&uuml;ft ob konkrete Zeitstempel innerhalb der Ganglinien liegen
	 */
	@Test
	public void testContains() {
		Ganglinie g;

		// davor
		assertFalse(ganglinie.contains(5));

		// dazwischen, auf Stützstelle
		assertTrue(ganglinie.contains(20));

		// dazwischen, nicht auf Stützstelle
		assertTrue(ganglinie.contains(33));

		// dahinter
		assertFalse(ganglinie.contains(55));

		// Sonderfall, eine Ganglinie ohne Stützstellen
		g = new Ganglinie();
		assertFalse(g.contains(30));
	}

	/**
	 * Testet die Suche nach St&uuml;tzstellen
	 */
	@Test
	public void testGetStuetzstelle() {
		Stuetzstelle s;
		
		// existierende Stützstelle
		s = ganglinie.getStuetzstelle(30);
		assertEquals(s3, s);
		
		// innerhalb der Ganglinie, aber keine Stützstelle
		s = ganglinie.getStuetzstelle(35);
		assertEquals(s4, s);
		
		// außerhalb der Ganglinie
		s = ganglinie.getStuetzstelle(70);
		assertNull(s);
	}

	/**
	 * Testet pro forma die toString()-Methode
	 */
	@Test
	public void testToString() {
		System.out.println(ganglinie);
	}

}
