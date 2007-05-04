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
 * Wei�enfelser Stra�e 67
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
public class RationaleZahl extends Number {

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

		return new RationaleZahl(z, n);
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

		return new RationaleZahl(z, n);
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

		return new RationaleZahl(z, n);
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
		long z, n;

		z = a.zaehler * b.nenner;
		n = a.nenner * b.zaehler;

		return new RationaleZahl(z, n);
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
		this.zaehler = zaehler;
		this.nenner = nenner;
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

}
