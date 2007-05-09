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
 * Repr&auml;sentiert eine rationale Zahl.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 */
public class RationaleZahl extends Number implements Comparable<RationaleZahl> {

	/** Repr&auml;sentiert 0 als rationale Zahl. */
	public static final RationaleZahl NULL = new RationaleZahl(0);

	/** Repr&auml;sentiert 1 als rationale Zahl. */
	public static final RationaleZahl EINS = new RationaleZahl(1);

	/** Serialisierungs-ID. */
	private static final long serialVersionUID = 1L;

	/** Z&auml;hler der rationalen Zahl. */
	private long zaehler;

	/** Nenner der rationalen Zahl. */
	private long nenner;

	/**
	 * Addiert zwei rationale Zahlen.
	 * 
	 * @param a
	 *            Erste rationale Zahl
	 * @param b
	 *            Zweite rationale Zahl
	 * @return Das Ergebnis der Addition
	 */
	public static RationaleZahl addiere(RationaleZahl a, RationaleZahl b) {
		long z, n;

		z = a.zaehler * b.nenner + b.zaehler * a.nenner;
		n = a.nenner * b.nenner;

		return kuerze(new RationaleZahl(z, n));
	}

	/**
	 * Subtrahiert zwei rationale Zahlen.
	 * 
	 * @param a
	 *            Erste rationale Zahl
	 * @param b
	 *            Zweite rationale Zahl
	 * @return Das Ergebnis der Subtraktion
	 */
	public static RationaleZahl subtrahiere(RationaleZahl a, RationaleZahl b) {
		long z, n;

		z = a.zaehler * b.nenner - b.zaehler * a.nenner;
		n = a.nenner * b.nenner;

		return kuerze(new RationaleZahl(z, n));
	}

	/**
	 * Multipliziert eine rationale Zahlen mit einer ganzen Zahl.
	 * 
	 * @param a
	 *            Eine rationale Zahl
	 * @param b
	 *            Eine ganze Zahl
	 * @return Das Ergebnis der Multiplikation
	 */
	public static RationaleZahl multipliziere(RationaleZahl a, long b) {
		return multipliziere(a, new RationaleZahl(b));
	}

	/**
	 * Multipliziert zwei rationale Zahlen.
	 * 
	 * @param a
	 *            Erste rationale Zahl
	 * @param b
	 *            Zweite rationale Zahl
	 * @return Das Ergebnis der Multiplikation
	 */
	public static RationaleZahl multipliziere(RationaleZahl a, RationaleZahl b) {
		long z, n;

		z = a.zaehler * b.zaehler;
		n = a.nenner * b.nenner;

		return kuerze(new RationaleZahl(z, n));
	}

	/**
	 * Dividiert eine rationale Zahlen durch eine ganze Zahl.
	 * 
	 * @param a
	 *            Eine rationale Zahl
	 * @param b
	 *            Eine ganze Zahl
	 * @return Das Ergebnis der Division
	 */
	public static RationaleZahl dividiere(RationaleZahl a, long b) {
		return dividiere(a, new RationaleZahl(b));
	}

	/**
	 * Dividiert zwei rationale Zahlen.
	 * 
	 * @param a
	 *            Erste rationale Zahl
	 * @param b
	 *            Zweite rationale Zahl
	 * @return Das Ergebnis der Division
	 */
	public static RationaleZahl dividiere(RationaleZahl a, RationaleZahl b) {
		return multipliziere(a, b.kehrwert());
	}

	public static RationaleZahl potenz(RationaleZahl basis, int exponent) {
		RationaleZahl potenz;

		potenz = new RationaleZahl(basis);
		for (int i = 1; i < exponent; i++) {
			potenz = RationaleZahl.multipliziere(potenz, basis);
		}

		return potenz;
	}

	/**
	 * Bestimmt den gr&ouml;&szlig;ten gemeinsamen Teiler zweier ganzer Zahlen.
	 * 
	 * @param a
	 *            Erste ganze Zahl
	 * @param b
	 *            Zweite ganze Zahl
	 * @return Der gr&ouml;&szlig;te gemeinsame Teiler beider Zahlen
	 */
	public static long ggT(long a, long b) {
		if (b == 0) {
			return a;
		}

		return ggT(b, a % b);
	}

	/**
	 * Bestimmt das kleinste gemeinsame Vielfache zweier ganzer Zahlen.
	 * 
	 * @param a
	 *            Erste ganze Zahl
	 * @param b
	 *            Zweite ganze Zahl
	 * @return Das kleinste gemeinsame Vielfache beider Zahlen
	 */
	public static long kgV(long a, long b) {
		return Math.abs(a * b) / ggT(a, b);
	}

	/**
	 * K&uuml;rzt einen Bruch.
	 * 
	 * @param a
	 *            Ein Bruch als rationale Zahl
	 * @return Der gek&uuml;rzte Bruch
	 */
	public static RationaleZahl kuerze(RationaleZahl a) {
		long ggT;

		ggT = ggT(a.zaehler, a.nenner);

		return new RationaleZahl(a.zaehler / ggT, a.nenner / ggT);
	}

	/**
	 * Konstruiert eine rationale Zahl als ganze Zahl. Der Nenner ist hier 1.
	 * 
	 * @param zaehler
	 *            Der Z&auml;hler
	 */
	public RationaleZahl(long zaehler) {
		this(zaehler, 1);
	}

	/**
	 * Konstruiert eine rationale Zahl als Quotient.
	 * 
	 * @param zaehler
	 *            Der Z&auml;hler
	 * @param nenner
	 *            Der Nenner
	 */
	public RationaleZahl(long zaehler, long nenner) {
		if (nenner == 0) {
			throw new ArithmeticException("Null als Nenner ist nicht erlaubt.");
		}

		if ((zaehler > 0 && nenner < 0) || (zaehler < 0 && nenner < 0)) {
			this.zaehler = -zaehler;
			this.nenner = -nenner;
		} else {
			this.zaehler = zaehler;
			this.nenner = nenner;
		}
	}

	/**
	 * Konstruiert eine rationale Zahl aus einer anderen.
	 * 
	 * @param zahl
	 *            Eine rationale Zahl
	 */
	public RationaleZahl(RationaleZahl zahl) {
		this(zahl.zaehler, zahl.nenner);
	}

	/**
	 * Gibt den Z&auml;hler der rationalen Zahl zur&uuml;ck.
	 * 
	 * @return Der Z&auml;hler
	 */
	public long getZaehler() {
		return zaehler;
	}

	/**
	 * Gibt den Nenner der rationalen Zahl zur&uuml;ck.
	 * 
	 * @return Der Nenner
	 */
	public long getNenner() {
		return nenner;
	}

	/**
	 * Bildet den Kehrwert der rationalen Zahl. Es werden Z&auml;hler und Nenner
	 * vertauscht.
	 * 
	 * @return Der Kehrwert der rationalen Zahl
	 */
	public RationaleZahl kehrwert() {
		return new RationaleZahl(nenner, zaehler);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Number#doubleValue()
	 */
	@Override
	public double doubleValue() {
		double a, b;

		a = zaehler;
		b = nenner;

		return a / b;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Number#floatValue()
	 */
	@Override
	public float floatValue() {
		float a, b;

		a = zaehler;
		b = nenner;

		return a / b;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Number#intValue()
	 */
	@Override
	public int intValue() {
		return Math.round(floatValue());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Number#longValue()
	 */
	@Override
	public long longValue() {
		return Math.round(doubleValue());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(RationaleZahl zahl) {
		long kgv;
		Long a, b;

		kgv = kgV(nenner, zahl.nenner);
		a = zaehler * kgv;
		b = zahl.zaehler * kgv;

		return a.compareTo(b);
	}

	/**
	 * Zwei rationale Zahlen sind identisch, wenn.
	 * <p>
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof RationaleZahl) {
			RationaleZahl r1, r2;

			r1 = kuerze(this);
			r2 = kuerze((RationaleZahl) o);
			return r1.zaehler == r2.zaehler && r1.nenner == r2.nenner;
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
		if (nenner == 1) {
			return String.valueOf(zaehler);
		}

		return zaehler + "/" + nenner;
	}
}
