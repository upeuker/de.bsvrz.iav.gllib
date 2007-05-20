package de.bsvrz.iav.gllib.gllib.util;

import static org.junit.Assert.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;

public class TestSortierteListe {

	SortierteListe<Integer> liste;

	/**
	 * Initialisiert die Testdaten vor jedem Test
	 */
	@Before
	public void setUp() {
		liste = new SortierteListe<Integer>();

		liste.add(6);
		liste.add(2);
		liste.add(8);
		liste.add(1);
		liste.add(0);
		liste.add(3);
		liste.add(7);
		liste.add(5);
		liste.add(4);
		liste.add(9);
	}

	/**
	 * Testet die Abfrage der Gr&ouml;&szlig;e der Datenstruktur. Die
	 * Gr&ouml;&szlig; betrifft nur die Anzahl der Elemente, nicht den
	 * Speicherverbrauch des internen Buffers.
	 * 
	 */
	@Test
	public void testSize() {
		assertEquals(10, liste.size());
	}

	/**
	 * Testet die Korrektheit des Mergesort-Algorithmus.
	 */
	@Test
	public void testMergesort() {
		Integer[] list;
		List<Integer> sorted;

		list = new Integer[] { 3, 9, 6, 8, 0, 1, 4, 7, 5, 2 };
		sorted = Arrays.asList(list);
		sorted = SortierteListe.mergesort(sorted);
		for (int i = 1; i < sorted.size(); i++) {
			if (sorted.get(i - 1) > sorted.get(i)) {
				fail(sorted.get(i - 1) + " > " + sorted.get(i));
			}
		}

		list = new Integer[] { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
		sorted = Arrays.asList(list);
		sorted = SortierteListe.mergesort(sorted);
		for (int i = 1; i < sorted.size(); i++) {
			if (sorted.get(i - 1) > sorted.get(i)) {
				fail(sorted.get(i - 1) + " > " + sorted.get(i));
			}
		}

		list = new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		sorted = Arrays.asList(list);
		sorted = SortierteListe.mergesort(sorted);

		for (int i = 1; i < sorted.size(); i++) {
			if (sorted.get(i - 1) > sorted.get(i)) {
				fail(sorted.get(i - 1) + " > " + sorted.get(i));
			}
		}
	}

	/**
	 * Testet den Standardkonstruktor.
	 */
	@Test
	public void testSortierteListe() {
		SortierteListe<Integer> leereListe;

		leereListe = new SortierteListe<Integer>();
		assertEquals(0, leereListe.size());
	}

	/**
	 * Testet den Konstruktor mit Angabe der Buffergr&ouml;&szlig;e.
	 */
	@Test
	public void testSortierteListeInt() {
		SortierteListe<Integer> leereListe;

		leereListe = new SortierteListe<Integer>();
		assertEquals(0, leereListe.size());
	}

	/**
	 * Testet den Kopierkonstruktor.
	 */
	@Test
	public void testSortierteListeCollectionOfQextendsE() {
		Integer[] list;
		List<Integer> sorted;

		list = new Integer[] { 3, 9, 6, 8, 0, 1, 4, 7, 5, 2 };
		sorted = Arrays.asList(list);
		sorted = new SortierteListe<Integer>(sorted);
		assertEquals(10, sorted.size());
		for (int i = 1; i < sorted.size(); i++) {
			if (sorted.get(i - 1) > sorted.get(i)) {
				fail(sorted.get(i - 1) + " > " + sorted.get(i));
			}
		}
	}

	@Test
	public void testEnsureCapacity() {
		assertEquals(10, liste.size());

		liste.ensureCapacity(10);
		assertEquals(10, liste.size());

		liste.ensureCapacity(20);
		assertEquals(10, liste.size());

		liste.ensureCapacity(5);
		assertEquals(10, liste.size());
	}

	@Test
	public void testAddE() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveInt() {
		fail("Not yet implemented");
	}

	/**
	 * Testet die Angabe der Klasse mit der Vergleichsmethode.
	 */
	@Test
	public void testComparator() {
		assertEquals(null, liste.comparator());
	}

	/**
	 * Testet die Frage nach dem ersten Element der Liste.
	 */
	@Test
	public void testFirst() {
		assertEquals(0, liste.first());
	}

	/**
	 * Testet das Abschneiden des kopfes der Liste.
	 */
	@Test
	public void testHeadSet() {
		SortedSet<Integer> teilmenge;

		teilmenge = liste.headSet(5);
		assertEquals(5, teilmenge.size());
		assertTrue(teilmenge.contains(0));
		assertTrue(teilmenge.contains(1));
		assertTrue(teilmenge.contains(2));
		assertTrue(teilmenge.contains(3));
		assertTrue(teilmenge.contains(4));
	}

	/**
	 * Testet die Frage nach dem letztem Element der Liste.
	 */
	@Test
	public void testLast() {
		assertEquals(9, liste.last());
	}

	/**
	 * Testet das Herausschneiden von Teillisten.
	 */
	@Test
	public void testSubSet() {
		SortedSet<Integer> teilmenge;

		teilmenge = liste.subSet(4, 7);
		assertEquals(3, teilmenge.size());
		assertTrue(teilmenge.contains(4));
		assertTrue(teilmenge.contains(5));
		assertTrue(teilmenge.contains(6));
	}

	/**
	 * Testet das Abschneiden des Schwanzes der Liste.
	 */
	@Test
	public void testTailSet() {
		SortedSet<Integer> teilmenge;

		teilmenge = liste.tailSet(5);
		assertEquals(5, teilmenge.size());
		assertTrue(teilmenge.contains(5));
		assertTrue(teilmenge.contains(6));
		assertTrue(teilmenge.contains(7));
		assertTrue(teilmenge.contains(8));
		assertTrue(teilmenge.contains(9));

	}

	@Test
	public void testEquals() {
		fail("Not yet implemented");
	}

	/**
	 * Testet den Iterator der Liste und gleichzeitig, ob die Liste sortiert
	 * ist.
	 */
	@Test
	public void testIterator() {
		Integer i, j;
		Iterator<Integer> iterator;
		int zaehler;

		iterator = liste.iterator();
		zaehler = 0;
		i = j = null;
		while (iterator.hasNext()) {
			i = iterator.next();
			if (zaehler > 0) {
				if (i.compareTo(j) > 0) {
					fail(i + " > " + j);
				}
			}
			j = i;
			i = iterator.next();
		}
	}

	/**
	 * Testet das leeren der Liste.
	 */
	@Test
	public void testClear() {
		liste.clear();
		assertTrue(liste.isEmpty());
		assertEquals(0, liste.size());
	}

	/**
	 * Muss immer eine Exception werfen.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testSet() {
		liste.set(4, 5);
	}

	/**
	 * Muss immer eine Exception werfen.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testAddIntE() {
		liste.add(3, 10);
	}

	/**
	 * Testet die Suche nach einem bestimmten Element.
	 */
	@Test
	public void testIndexOf() {
		assertEquals(5, liste.indexOf(5));
	}

	/**
	 * Testet die Suche nach einem bestimmten Element.
	 */
	@Test
	public void testLastIndexOf() {
		assertEquals(5, liste.indexOf(5));
	}

	/**
	 * Muss immer eine Exception werfen.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testAddAllIntCollectionOfQextendsE() {
		List<Integer> neu = new ArrayList<Integer>();

		neu.add(11);
		neu.add(12);
		neu.add(13);
		neu.add(14);
		neu.add(15);
		liste.addAll(3, neu);
	}

	@Test
	public void testListIterator() {
		fail("Not yet implemented");
	}

	@Test
	public void testListIteratorInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testSubList() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveRange() {
		fail("Not yet implemented");
	}

	/**
	 * Obligatorischer Test der Print-Ausgabe.
	 */
	@Test
	public void testToString() {
		System.out.println("Liste: " + liste);
	}

	/**
	 * Testet die Methode zum Test auf eine leere Liste.
	 */
	@Test
	public void testIsEmpty() {
		assertFalse(liste.isEmpty());

		liste.clear();
		assertTrue(liste.isEmpty());
	}

	/**
	 * Testet die Methode zum Auffinden eines Objekts der Liste.
	 */
	@Test
	public void testContains() {
		assertTrue(liste.contains(0));
		assertTrue(liste.contains(1));
		assertTrue(liste.contains(2));
		assertTrue(liste.contains(3));
		assertTrue(liste.contains(4));
		assertTrue(liste.contains(5));
		assertTrue(liste.contains(6));
		assertTrue(liste.contains(7));
		assertTrue(liste.contains(8));
		assertTrue(liste.contains(9));

		assertFalse(liste.contains(-2));
		assertFalse(liste.contains(-1));
		assertFalse(liste.contains(10));
		assertFalse(liste.contains(11));
	}

	@Test
	public void testToArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testToArrayTArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveObject() {
		assertEquals(10, liste.size());

		liste.remove(Integer.valueOf(5));
		liste.remove(Integer.valueOf(3));
		assertEquals(8, liste.size());
		assertTrue(liste.contains(0));
		assertTrue(liste.contains(1));
		assertTrue(liste.contains(2));
		assertFalse(liste.contains(3));
		assertTrue(liste.contains(4));
		assertFalse(liste.contains(5));
		assertTrue(liste.contains(6));
		assertTrue(liste.contains(7));
		assertTrue(liste.contains(8));
		assertTrue(liste.contains(9));
	}

	@Test
	public void testContainsAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddAllCollectionOfQextendsE() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testRetainAll() {
		fail("Not yet implemented");
	}

}
