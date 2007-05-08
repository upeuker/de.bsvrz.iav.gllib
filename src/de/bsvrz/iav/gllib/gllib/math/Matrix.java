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
 * Repr&auml;sentiert eine Matrix. Die Matrix besteht aus aus einer beliebigen
 * Anzahl von Zeilen ({@link Vektor}) mit beliebiger L&auml;nge.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class Matrix {

	/** Interner Speicher der Matrixelemente. */
	private RationaleZahl[][] matrix;

	/**
	 * Addiert zwei Matrizen.
	 * 
	 * @param a
	 *            Erste Matrix
	 * @param b
	 *            Zweite Matrix
	 * @return Das Ergebnis der Matrixaddition
	 * @throws IllegalArgumentException
	 *             Wenn die beiden Matrizen nicht die selbe Ordung besitzen
	 */
	public static Matrix addiere(Matrix a, Matrix b) {
		if (a.anzahlZeilen() != b.anzahlZeilen()
				|| a.anzahlSpalten() != b.anzahlSpalten()) {
			throw new IllegalArgumentException(
					"Die beiden Matrizen haben nicht die selbe Ordung.");
		}

		Matrix m;

		m = new Matrix(a.anzahlZeilen(), a.anzahlSpalten());
		for (int i = 0; i < a.anzahlZeilen(); i++) {
			for (int j = 0; j < a.anzahlSpalten(); j++) {
				m.set(i, j, RationaleZahl.addiere(a.get(i, j), b.get(i, j)));
			}
		}

		return m;
	}

	/**
	 * Subtrahiert zwei Matrizen.
	 * 
	 * @param a
	 *            Erste Matrix
	 * @param b
	 *            Zweite Matrix
	 * @return Das Ergebnis der Matrixsubtraktion
	 * @throws IllegalArgumentException
	 *             Wenn die beiden Matrizen nicht die selbe Ordung besitzen
	 */
	public static Matrix subtrahiere(Matrix a, Matrix b) {
		if (a.anzahlZeilen() != b.anzahlZeilen()
				|| a.anzahlSpalten() != b.anzahlSpalten()) {
			throw new IllegalArgumentException(
					"Die beiden Matrizen haben nicht die selbe Ordung.");
		}

		Matrix m;

		m = new Matrix(a.anzahlZeilen(), a.anzahlSpalten());
		for (int i = 0; i < a.anzahlZeilen(); i++) {
			for (int j = 0; j < a.anzahlSpalten(); j++) {
				m
						.set(i, j, RationaleZahl.subtrahiere(a.get(i, j), b
								.get(i, j)));
			}
		}

		return m;
	}

	/**
	 * Multipliziert eine Matrix mit einem Skalar.
	 * 
	 * @param a
	 *            Eine matrix
	 * @param s
	 *            Ein Skalar
	 * @return Das Vielfache der Matrix
	 */
	public static Matrix multipliziere(Matrix a, long s) {
		return multipliziere(a, new RationaleZahl(s));
	}
	
	/**
	 * Multipliziert eine Matrix mit einem Skalar.
	 * 
	 * @param a
	 *            Eine matrix
	 * @param s
	 *            Ein Skalar
	 * @return Das Vielfache der Matrix
	 */
	public static Matrix multipliziere(Matrix a, RationaleZahl s) {
		Matrix m;

		m = new Matrix(a.anzahlZeilen(), a.anzahlSpalten());
		for (int i = 0; i < a.anzahlZeilen(); i++) {
			for (int j = 0; j < a.anzahlSpalten(); j++) {
				m.set(i, j, RationaleZahl.multipliziere(a.get(i, j), s));
			}
		}

		return m;
	}

	/**
	 * Multipliziert eine Matrix mit einem Vektor.
	 * 
	 * @param a
	 *            Eine Matrix
	 * @param v
	 *            Ein Vektor
	 * @return Das Ergebnis der Matrixmultiplikation
	 * @throws IllegalArgumentException
	 *             Wenn die Spaltenanzahl der Matrix nicht mit der
	 *             Komponentenanzahl des Vektors &uuml;bereinstimmt
	 */
	public static Matrix multipliziere(Matrix a, Vektor v) {
		if (a.anzahlSpalten() != v.anzahlKomponenten()) {
			throw new IllegalArgumentException(
					"Spaltenanzahl der Matrix stimmt nicht mit der Komponentenanzahl des Vektors ¸berein.");
		}

		Matrix m, b;

		b = new Matrix(v, false);
		m = new Matrix(a.anzahlZeilen(), b.anzahlSpalten());
		for (int i = 0; i < a.anzahlZeilen(); i++) {
			for (int j = 0; j < b.anzahlSpalten(); j++) {
				Vektor v1, v2;

				v1 = a.getZeilenvektor(i);
				v2 = b.getSpaltenvektor(j);
				m.set(i, j, Vektor.skalarprodukt(v1, v2));
			}
		}

		return m;
	}

	/**
	 * Multipliziert die beiden Matrizen.
	 * 
	 * @param a
	 *            Erste Matrix
	 * @param b
	 *            Zweite Matrix
	 * @return Das Ergebnis der Matrixmultiplikation
	 * @throws IllegalArgumentException
	 *             Wenn die Spaltenanzahl der ersten Matrix nicht mit der
	 *             Zeilenanzahl der zweiten &uuml;bereinstimmt
	 */
	public static Matrix multipliziere(Matrix a, Matrix b) {
		if (a.anzahlSpalten() != b.anzahlZeilen()) {
			throw new IllegalArgumentException(
					"Spaltenanzahl der ersten Matrix stimmt nicht mit der Zeilenanzahl der zweiten ¸berein.");
		}

		Matrix m;

		m = new Matrix(a.anzahlZeilen(), b.anzahlSpalten());
		for (int i = 0; i < a.anzahlZeilen(); i++) {
			for (int j = 0; j < b.anzahlSpalten(); j++) {
				Vektor v1, v2;

				v1 = a.getZeilenvektor(i);
				v2 = b.getSpaltenvektor(j);
				m.set(i, j, Vektor.skalarprodukt(v1, v2));
			}
		}

		return m;
	}

	/**
	 * Dividiert eine Matrix durch ein Skalar.
	 * 
	 * @param a
	 *            Eine matrix
	 * @param s
	 *            Ein Skalar
	 * @return Das Vielfache der Matrix
	 */
	public static Matrix dividiere(Matrix a, long s) {
		return multipliziere(a, new RationaleZahl(s));
	}
	
	/**
	 * Dividiert eine Matrix durch ein Skalar.
	 * 
	 * @param a
	 *            Eine matrix
	 * @param s
	 *            Ein Skalar
	 * @return Das Vielfache der Matrix
	 */
	public static Matrix dividiere(Matrix a, RationaleZahl s) {
		return multipliziere(a, s.kehrwert());
	}
	
	/**
	 * Konstruiert eine leere Matrix.
	 * 
	 * @param n
	 *            Anzahl Zeilen der Matrix
	 * @param m
	 *            Anzahl Spalten der Matrix
	 * @throws IllegalArgumentException
	 *             Wenn die Zeilen- oder Spaltenanzahl kleiner 1 ist
	 */
	public Matrix(int n, int m) {
		if (n < 1 || m < 1) {
			throw new IllegalArgumentException(
					"Die Zeilen- und Spaltenanzahl muss grˆﬂer oder gleich 1 sein.");
		}

		matrix = new RationaleZahl[n][m];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				matrix[i][j] = RationaleZahl.NULL;
			}
		}
	}

	/**
	 * Konstruiert eine Matrix aus einem Vektor. Abh&auml;ngig vom zweiten
	 * Parameter wird der Vektor zur ersten und einzigen Zeile oder zur ersten
	 * und einzigen Spalte.
	 * 
	 * @param vektor
	 *            Ein Vektor
	 * @param zeilenvektor
	 *            {@code true}, wenn der Vektor zur Zeile der Matrix werden
	 *            soll. {@code false}, wenn der Vektor zur Spalte der Matrix
	 *            werden soll.
	 */
	public Matrix(Vektor vektor, boolean zeilenvektor) {
		if (zeilenvektor) {
			matrix = new RationaleZahl[1][vektor.anzahlKomponenten()];
			for (int j = 0; j < vektor.anzahlKomponenten(); j++) {
				matrix[0][j] = vektor.get(j);
			}
		} else {
			matrix = new RationaleZahl[vektor.anzahlKomponenten()][1];
			for (int i = 0; i < vektor.anzahlKomponenten(); i++) {
				matrix[i][0] = vektor.get(i);
			}
		}
	}

	/**
	 * Konstruiert eine Matrix aus einet bestehenden Matrix.
	 * 
	 * @param matrix
	 *            Eine Matrix
	 */
	public Matrix(Matrix matrix) {
		this(matrix.anzahlZeilen(), matrix.anzahlSpalten());
		for (int i = 0; i < matrix.anzahlZeilen(); i++) {
			for (int j = 0; j < matrix.anzahlSpalten(); j++) {
				this.matrix[i][j] = matrix.matrix[i][j];
			}
		}
	}

	/**
	 * Gibt die Anzahl der Zeilen in der Matrix zur&uuml;ck.
	 * 
	 * @return Zeilenanzahl
	 */
	public int anzahlZeilen() {
		return matrix.length;
	}

	/**
	 * Gibt die Anzahl der Spalten in der Matrix zur&uuml;ck.
	 * 
	 * @return Spaltenanzahl
	 */
	public int anzahlSpalten() {
		return matrix[0].length;
	}

	/**
	 * Gibt ein bestimmtes Element der Matrix zur&uuml;ck.
	 * 
	 * @param i
	 *            Zeilenindex des gesuchten Elements
	 * @param j
	 *            Spaltenindex des gesuchten Elements
	 * @return Wert des gesuchten Elements
	 */
	public RationaleZahl get(int i, int j) {
		return matrix[i][j];
	}

	/**
	 * Legt den Wert eines bestimmten Elements der Matrix fest.
	 * 
	 * @param i
	 *            Zeilenindex des Elements
	 * @param j
	 *            Spaltenindex des Elements
	 * @param wert
	 *            Neuer Wert des Elements
	 */
	public void set(int i, int j, long wert) {
		set(i, j, new RationaleZahl(wert));
	}

	/**
	 * Legt den Wert eines bestimmten Elements der Matrix fest.
	 * 
	 * @param i
	 *            Zeilenindex des Elements
	 * @param j
	 *            Spaltenindex des Elements
	 * @param wert
	 *            Neuer Wert des Elements
	 */
	public void set(int i, int j, RationaleZahl wert) {
		matrix[i][j] = new RationaleZahl(wert);
	}

	/**
	 * Gibt eine bestimmte Zeile der Matrix als Vektor zur&uuml;ck.
	 * 
	 * @param i
	 *            Zeilenindex
	 * @return Die Matrixzeile als Vektor
	 */
	public Vektor getZeilenvektor(int i) {
		return new Vektor(matrix[i]);
	}

	/**
	 * &Uuml;berschreibt eine Zeile der Matrix mit einem gegebenen Vektor.
	 * 
	 * @param i
	 *            Die Matrixzeile, die &uuml;berschrieben werden soll
	 * @param v
	 *            Der Vektor, durch den die Matrixzeile ersetzt werden soll
	 */
	public void setZeilenvektor(int i, Vektor v) {
		if (anzahlSpalten() != v.anzahlKomponenten()) {
			throw new IllegalArgumentException(
					"Der Anzahl der Vektorelemente stimmt nicht mit der Spaltenanzahl der Matrix ¸berein.");
		}

		for (int j = 0; j < anzahlSpalten(); j++) {
			matrix[i][j] = v.get(j);
		}
	}

	/**
	 * Gibt eine bestimmte Spalte der Matrix als Vektor zur&uuml;ck.
	 * 
	 * @param j
	 *            Spalteindex
	 * @return Die Matrixspalte als Vektor
	 */
	public Vektor getSpaltenvektor(int j) {
		Vektor v;

		v = new Vektor(anzahlZeilen());
		for (int i = 0; i < anzahlZeilen(); i++) {
			v.set(i, matrix[i][j]);
		}

		return v;
	}

	/**
	 * &Uuml;berschreibt eine Spalte der Matrix mit einem gegebenen Vektor.
	 * 
	 * @param j
	 *            Die Matrixspalte, die &uuml;berschrieben werden soll
	 * @param v
	 *            Der Vektor, durch den die Matrixspalte ersetzt werden soll
	 */
	public void setSpaltenvektor(int j, Vektor v) {
		if (anzahlZeilen() != v.anzahlKomponenten()) {
			throw new IllegalArgumentException(
					"Der Anzahl der Vektorelemente stimmt nicht mit der Zeilenanzahl der Matrix ¸berein.");
		}

		for (int i = 0; i < anzahlZeilen(); i++) {
			matrix[i][j] = v.get(j);
		}
	}

	/**
	 * Gibt die Matrix als Vektor zur&uuml;ck. Dies ist nur m&ouml;glich, wenn
	 * die Matrix entweder aus genau einer Zeile oder genau einer Spalte
	 * besteht. In allen anderen F&auml;llen wird {@code null}
	 * zur&uuml;ckgegeben.
	 * 
	 * @return Die Matrix als Vektor oder {@code null}, wenn dies nicht
	 *         m&ouml;glich ist
	 */
	public Vektor getVektor() {
		if (anzahlZeilen() == 1) {
			return getZeilenvektor(0);
		} else if (anzahlSpalten() == 1) {
			return getSpaltenvektor(0);
		}

		return null;
	}

	/**
	 * Bestimmt die transponierte Matrix.
	 * 
	 * @return Die transponierte Matrix
	 */
	public Matrix transponiert() {
		Matrix m;

		m = new Matrix(anzahlSpalten(), anzahlZeilen());
		for (int i = 0; i < anzahlZeilen(); i++) {
			for (int j = 0; j < anzahlSpalten(); j++) {
				m.set(j, i, matrix[i][j]);
			}
		}

		return m;
	}

	/**
	 * Ist die Matrix symetrisch?
	 * 
	 * @return {@code true}, wenn die Matrix symetrisch ist
	 */
	public boolean symetrisch() {
		return equals(transponiert());
	}

	/**
	 * Zwei Matrizen sind gleich, wenn sie gleiche Ordnung haben und in allen
	 * Elementen &uuml;bereinstimmen.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Matrix) {
			Matrix m;
			boolean gleich;

			m = (Matrix) o;

			if (anzahlZeilen() != m.anzahlZeilen()
					|| anzahlSpalten() != m.anzahlSpalten()) {
				return false;
			}

			gleich = true;
			for (int i = 0; i < anzahlZeilen(); i++) {
				for (int j = 0; j < anzahlSpalten(); j++) {
					if (!matrix[i][j].equals(m.matrix[i][j])) {
						gleich = false;
						break;
					}
				}
			}
			return gleich;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String s;

		s = "";
		for (int i = 0; i < anzahlZeilen(); i++) {
			for (int j = 0; j < anzahlSpalten(); j++) {
				s += matrix[i][j];
				if (j < anzahlSpalten() - 1) {
					s += "\t";
				}
			}
			if (i < anzahlZeilen() - 1) {
				s += "\n";
			}
		}

		return s;
	}

}
