/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.5 Funktionen Ganglinie
 * Copyright (C) 2007 BitCtrl Systems GmbH 
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact Information:
 * BitCtrl Systems GmbH
 * Weiﬂenfelser Straﬂe 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib.math;

/**
 * Enth&auml;t verschiedene Algorithmen zur L&ouml;sung von linearen
 * Gleichunssystemen.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public final class Gauss {

	/**
	 * Konstruktor verstecken, da es nur statische Methoden gibt.
	 * 
	 */
	private Gauss() {
		// nix
	}

	/**
	 * F&uuml;hrt die LR-Zerlegung einer Matrix durch. Die beiden Matrizen L und
	 * R stehen im Ergebnis in einer Matrix, wobei die diagonalen Elemente der
	 * Matrix L die immer gleich 1 sind, nicht gespeichert werden.
	 * 
	 * @param a
	 *            Eine quadratische Matrix
	 * @return Das Ergebnis der LR-Zerlegung
	 */
	public static Matrix bestimmeLRZerlegung(Matrix a) {
		if (a.anzahlZeilen() != a.anzahlSpalten()) {
			throw new IllegalArgumentException(
					"Die Anzahl der Zeilen und Spalten der Matrix sind nicht identisch.");
		}

		if (a.get(0, 0).equals(new RationaleZahl(0))) {
			throw new IllegalArgumentException(
					"Das Element in der ersten Zeile und ersten Spalte der Matrix darf nicht 0 sein.");
		}

		Matrix lr;

		lr = new Matrix(a);

		for (int j = 0; j < lr.anzahlSpalten(); j++) {
			// Nullen in der aktuellen Spalte erzeugen
			for (int i = j + 1; i < lr.anzahlZeilen(); i++) {
				Vektor v0, neueZeile;
				RationaleZahl z, n, f;

				// Erste Matrixzeile
				v0 = lr.getZeilenvektor(j);

				// Faktor bestimmen
				z = lr.get(i, j);
				n = v0.get(j);
				f = RationaleZahl.dividiere(z, n);

				// Erste Zeile mit Faktor multiplizieren und von aktueller Zeile
				// subtrahieren
				v0 = Vektor.multipliziere(v0, f);
				neueZeile = lr.getZeilenvektor(i);
				neueZeile.set(j, f);
				for (int k = j + 1; k < neueZeile.anzahlKomponenten(); k++) {
					neueZeile.set(k, RationaleZahl.subtrahiere(
							neueZeile.get(k), v0.get(k)));
				}
				lr.setZeilenvektor(i, neueZeile);
			}
		}

		return lr;
	}

	/**
	 * Extrahiert aus der LR-Matrix die untere Dreiecksmatrix L.
	 * 
	 * @param lr
	 *            Eine LR-Zerlegung
	 * @return Die untere Dreiecksmatrix L.
	 */
	public static Matrix extrahiereMatrixL(Matrix lr) {
		Matrix l;

		l = new Matrix(lr.anzahlZeilen(), lr.anzahlSpalten());
		for (int i = 0; i < lr.anzahlZeilen(); i++) {
			for (int j = 0; j < lr.anzahlSpalten(); j++) {
				if (i > j) {
					l.set(i, j, lr.get(i, j));
				} else if (i == j) {
					l.set(i, j, RationaleZahl.EINS);
				}
			}
		}

		return l;
	}

	/**
	 * Extrahiert aus der LR-Matrix die obere Dreiecksmatrix R.
	 * 
	 * @param lr
	 *            Eine LR-Zerlegung
	 * @return Die obere Dreiecksmatrix R.
	 */
	public static Matrix extrahiereMatrixR(Matrix lr) {
		Matrix r;

		r = new Matrix(lr.anzahlZeilen(), lr.anzahlSpalten());
		for (int i = 0; i < lr.anzahlZeilen(); i++) {
			for (int j = 0; j < lr.anzahlSpalten(); j++) {
				if (i <= j) {
					r.set(i, j, lr.get(i, j));
				}
			}
		}

		return r;
	}

	/**
	 * L&ouml;st ein lineares Gleichungssystem durch vollst&auml;ndige
	 * Elimination.
	 * 
	 * @param a
	 *            Koeffizientenmatrix des LGS
	 * @param b
	 *            Absoultes Glied des LGS
	 * @return Der L&ouml;sungsvektor
	 */
	public static Vektor loeseLGS(Matrix a, Vektor b) {
		Matrix d;

		if (a.anzahlZeilen() != a.anzahlSpalten()) {
			throw new IllegalArgumentException(
					"Die Anzahl der Zeilen und Spalten der Matrix sind nicht identisch.");
		}

		if (a.anzahlZeilen() != b.anzahlKomponenten()) {
			throw new IllegalArgumentException(
					"Die Anzahl der Zeilen der Matrix und die Anzahl der Kompontenen des Vektors sind nicht identisch.");
		}

		if (a.get(0, 0).equals(new RationaleZahl(0))) {
			throw new IllegalArgumentException(
					"Das Element in der ersten Zeile und ersten Spalte der Matrix darf nicht 0 sein.");
		}

		// Erweiterte Koeffizientenmatrix bilden
		d = new Matrix(a.anzahlZeilen(), a.anzahlSpalten() + 1);
		for (int i = 0; i < a.anzahlZeilen(); i++) {
			for (int j = 0; j < a.anzahlSpalten(); j++) {
				d.set(i, j, a.get(i, j));
			}
			d.set(i, a.anzahlSpalten(), b.get(i));
		}

		// 1. Schritt: Obere Dreiecksmatrix bestimmen
		d = obereDreiecksmatrix(d);

		// 2. Schritt: Untere Dreiecksmatrix bestimmen
		d = untereDreiecksmatrix(d);

		// 3. Schritt: In der Diagonalen Einsen erzeugen
		for (int i = 0; i < d.anzahlZeilen(); i++) {
			Vektor zeile;

			zeile = d.getZeilenvektor(i);
			d.setZeilenvektor(i, Vektor.dividiere(zeile, zeile.get(i)));
		}

		// Die (unerweiterte) Koeffizientenmatrix ist jetzt eine Einheitsmatrix.
		// Die letzte Spalte der erweiterten Koeffizientenmatrix enth‰lt den
		// Lˆsungsvektor.
		return d.getSpaltenvektor(d.anzahlSpalten() - 1);
	}

	/**
	 * Bestimmt die obere Dreiecksmatrix mittels Gauss-Algorithmus.
	 * 
	 * @param m
	 *            Eine Matrix
	 * @return Die berechnte obere Dreiecksmatrix
	 */
	public static Matrix obereDreiecksmatrix(Matrix m) {
		Matrix d;

		d = new Matrix(m);
		for (int j = 0; j < d.anzahlSpalten(); j++) {
			for (int i = j + 1; i < d.anzahlZeilen(); i++) {
				Vektor v0;
				RationaleZahl z, n, f;

				// Aktuelle Zeile bestimmen, ggf. Zeilen tauschen
				if (d.get(j, j).equals(RationaleZahl.NULL)) {
					// Auf der Diaginalen steht eine 0, die im Nenner st¸nde
					for (int k = j + 1; k < d.anzahlZeilen(); k++) {
						if (!d.get(k, j).equals(RationaleZahl.NULL)) {
							// Zeilen tauschen
							Vektor v1, v2;

							v1 = d.getZeilenvektor(j);
							v2 = d.getZeilenvektor(k);
							d.setZeilenvektor(j, v2);
							d.setZeilenvektor(k, v1);
						}
					}
				}
				v0 = d.getZeilenvektor(j);

				// Faktor bestimmen
				z = d.get(i, j);
				n = v0.get(j);
				f = RationaleZahl.dividiere(z, n);
				f = RationaleZahl.multipliziere(f, new RationaleZahl(-1));

				// Erste Zeile mit Faktor multiplizieren und zur aktuellen Zeile
				// addieren
				v0 = Vektor.multipliziere(v0, f);
				d.setZeilenvektor(i, Vektor.addiere(d.getZeilenvektor(i), v0));
			}
		}

		return d;
	}

	/**
	 * Bestimmt die untere Dreiecksmatrix mittels Gauss-Algorithmus.
	 * 
	 * @param m
	 *            Eine Matrix
	 * @return Die berechnte untere Dreiecksmatrix
	 */
	public static Matrix untereDreiecksmatrix(Matrix m) {
		Matrix d;

		d = new Matrix(m);
		for (int j = d.anzahlSpalten() - 2; j > 0; j--) {
			for (int i = j - 1; i >= 0; i--) {
				Vektor v0;
				RationaleZahl z, n, f;

				// Aktuelle Zeile bestimmen, ggf. Zeilen tauschen
				if (d.get(j, j).equals(RationaleZahl.NULL)) {
					// Auf der Diaginalen steht eine 0, die im Nenner st¸nde
					for (int k = j - 1; k >= 0; k--) {
						if (!d.get(k, j).equals(RationaleZahl.NULL)) {
							// Zeilen tauschen
							Vektor v1, v2;

							v1 = d.getZeilenvektor(j);
							v2 = d.getZeilenvektor(k);
							d.setZeilenvektor(j, v2);
							d.setZeilenvektor(k, v1);
						}
					}
				}
				v0 = d.getZeilenvektor(j);

				// Faktor bestimmen
				z = d.get(i, j);
				n = v0.get(j);
				f = RationaleZahl.dividiere(z, n);
				f = RationaleZahl.multipliziere(f, new RationaleZahl(-1));

				// Letzte Zeile mit Faktor multiplizieren und zur aktuellen
				// Zeile addieren
				v0 = Vektor.multipliziere(v0, f);
				d.setZeilenvektor(i, Vektor.addiere(d.getZeilenvektor(i), v0));
			}
		}

		return d;
	}

}
