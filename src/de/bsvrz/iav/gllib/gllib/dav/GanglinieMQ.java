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

package de.bsvrz.iav.gllib.gllib.dav;

import de.bsvrz.iav.gllib.gllib.Approximation;
import de.bsvrz.iav.gllib.gllib.Ganglinie;
import de.bsvrz.sys.funclib.bitctrl.util.dav.Umrechung;

/**
 * F&uuml;r Messquerschnitte angepasste Ganglinie. Die vier Verkehrswerte QKfz,
 * QLkw, VPkw und VLkw werden als Gruppe pro Zeitstempel gesichert. Aus diesen
 * Werten lassen sich die drei davon abh&auml;ngigen Gr&ouml;&szlig;e QPkw, VKfz
 * und QB berechnen.
 * 
 * @author BitCtrl, Schumann
 * @version $Id$
 */
public class GanglinieMQ extends Ganglinie {

	/**
	 * Typ der Ganglinie.
	 * 
	 * @author BitCtrl, Schumann
	 * @version $Id$
	 */
	public enum Typ {

		/** Eine absolute Ganglinie. */
		ABSOLUT,

		/** Eine relative additive Ganglinie. */
		ADDITIV,

		/** Eine relative multiplikative Ganglinie. */
		MULTIPLIKATIV;
	}

	/** Ganglinie der Kfz-Verkehrsst&auml;rke. */
	private Ganglinie qKfz;

	/** Ganglinie der Lkw-Verkehrsst&auml;rke. */
	private Ganglinie qLkw;

	/** Ganglinie der Pkw-Geschwindigkeit. */
	private Ganglinie vPkw;

	/** Ganglinie der Lkw-Geschwindigkeit. */
	private Ganglinie vLkw;

	/** Parameter f&uuml;r die Berechnung von QB. */
	private float k1;

	/** Parameter f&uuml;r die Berechnung von QB. */
	private float k2;

	/** Zeitpunkt der letzten Verschmelzung. */
	private long letzteVerschmelzung;

	/** Anzahl der Verschmelzung mit anderen Ganglinienb. */
	private long anzahlVerschmelzungen = 0;

	/** Identifier f&uuml;r das mit der Ganglinie verkn&uuml;pfte Ereignis. */
	private String ereignisTyp;

	/** Flag, ob die Ganglinie eine Referenzganglinie darstellt. */
	private boolean referenz;

	/**
	 * Gibt eine Approximation f&uuml;r QKfz zur&uuml;ck.
	 * 
	 * @return Approximation
	 */
	public Approximation getApproximationQKfz() {
		return qKfz.getApproximation();
	}

	/**
	 * Gibt eine Approximation f&uuml;r QPkw zur&uuml;ck.
	 * 
	 * @return Approximation
	 */
	public Approximation getApproximationQPkw() {
		Ganglinie qPkw;

		qPkw = new Ganglinie();
		for (int i = 0; i < qKfz.anzahlStuetzstellen(); i++) {
			long z;

			z = qKfz.getStuetzstelle(i).zeitstempel;
			qPkw.set(z, Umrechung.getQPkw(qKfz.getStuetzstelle(i).wert, qLkw
					.getStuetzstelle(i).wert));
		}

		return qPkw;
	}

	/**
	 * Gibt eine Approximation f&uuml;r QLkw zur&uuml;ck.
	 * 
	 * @return Approximation
	 */
	public Approximation getApproximationQLkw() {
		return qLkw.getApproximation();
	}

	/**
	 * Gibt eine Approximation f&uuml;r VKfz zur&uuml;ck.
	 * 
	 * @return Approximation
	 */
	public Approximation getApproximationVKfz() {
		Ganglinie vKfz;

		vKfz = new Ganglinie();
		for (int i = 0; i < qKfz.anzahlStuetzstellen(); i++) {
			long z;

			z = qKfz.getStuetzstelle(i).zeitstempel;
			vKfz.set(z, Umrechung.getVKfz(qLkw.getStuetzstelle(i).wert, qKfz
					.getStuetzstelle(i).wert, vPkw.getStuetzstelle(i).wert,
					vLkw.getStuetzstelle(i).wert));
		}

		return vKfz;
	}

	/**
	 * Gibt eine Approximation f&uuml;r VPkw zur&uuml;ck.
	 * 
	 * @return Approximation
	 */
	public Approximation getApproximationVPkw() {
		return vPkw.getApproximation();
	}

	/**
	 * Gibt eine Approximation f&uuml;r VLkw zur&uuml;ck.
	 * 
	 * @return Approximation
	 */
	public Approximation getApproximationVLkw() {
		return vLkw.getApproximation();
	}

	/**
	 * Gibt eine Approximation f&uuml;r QB zur&uuml;ck.
	 * 
	 * @return Approximation
	 */
	public Approximation getApproximationQB() {
		Ganglinie qb;

		qb = new Ganglinie();
		for (int i = 0; i < qKfz.anzahlStuetzstellen(); i++) {
			long z;

			z = qKfz.getStuetzstelle(i).zeitstempel;
			qb.set(z, Umrechung.getQB(qLkw.getStuetzstelle(i).wert,
					qKfz.getStuetzstelle(i).wert, vPkw.getStuetzstelle(i).wert,
					vLkw.getStuetzstelle(i).wert, k1, k2));
		}

		return qb;
	}

	/**
	 * Besitzt die Ganglinie die Auszeichnung als Referenz?
	 * 
	 * @return <code>true</code>, wenn diese Ganglinie eine Referenzganglinie
	 *         ist, sonst <code>false</code>
	 */
	public boolean getReferenz() {
		return referenz;
	}

	/**
	 * Kennzeichnet die Ganglinie als Referenzganglinie.
	 * 
	 * @param referenz
	 *            <code>true</code>, wenn diese Ganglinie eine
	 *            Referenzganglinie sein soll, sonst <code>false</code>
	 */
	public void setReferenz(boolean referenz) {
		this.referenz = referenz;
	}

	/**
	 * Gibt die Anzahl der bisherigen Verschmelzungen beim automatischen Lernen
	 * zur&uuml;ck.
	 * 
	 * @return Anzahl bisheriger Verschmelzungen
	 */
	public long getAnzahlVerschmelzungen() {
		return anzahlVerschmelzungen;
	}

	/**
	 * Legt die Anzahl der bisherigen Verschmelzungen fest.
	 * 
	 * @param anzahlVerschmelzungen
	 *            Anzahl der Verschmelzungen
	 */
	public void setAnzahlVerschmelzungen(long anzahlVerschmelzungen) {
		this.anzahlVerschmelzungen = anzahlVerschmelzungen;
	}

	/**
	 * Gibt den Zeitpunkt der letzten Verschmelzung als Zeitstempel zur&uuml;ck.
	 * 
	 * @return Zeitstempel
	 */
	public long getLetzteVerschmelzung() {
		return letzteVerschmelzung;
	}

	/**
	 * Gibt den Ereignistyp der Ganglinie zur&uuml;ck.
	 * 
	 * @return PID des Ereignistyp
	 */
	public String getEreignisTyp() {
		return ereignisTyp;
	}

	/**
	 * Legt den Ereignistyp der Ganglinie fest.
	 * 
	 * @param ereignisTyp
	 *            PID des Ereignistyp
	 */
	public void setEreignisTyp(String ereignisTyp) {
		this.ereignisTyp = ereignisTyp;
	}

	/**
	 * Gibt die entsprechende Ganglinie zur&uuml;ck.
	 * 
	 * @return Die Ganglinie
	 */
	public Ganglinie getQKfz() {
		return qKfz;
	}

	/**
	 * Legt die entsprechende Ganglinie fest.
	 * 
	 * @param qKfz
	 *            Die neue Ganglinie
	 */
	public void setQKfz(Ganglinie qKfz) {
		this.qKfz = qKfz;
	}

	/**
	 * Gibt die entsprechende Ganglinie zur&uuml;ck.
	 * 
	 * @return Die Ganglinie
	 */
	public Ganglinie getQLkw() {
		return qLkw;
	}

	/**
	 * Legt die entsprechende Ganglinie fest.
	 * 
	 * @param qLkw
	 *            Die neue Ganglinie
	 */
	public void setQLkw(Ganglinie qLkw) {
		this.qLkw = qLkw;
	}

	/**
	 * Gibt die entsprechende Ganglinie zur&uuml;ck.
	 * 
	 * @return Die Ganglinie
	 */
	public Ganglinie getVLkw() {
		return vLkw;
	}

	/**
	 * Legt die entsprechende Ganglinie fest.
	 * 
	 * @param vLkw
	 *            Die neue Ganglinie
	 */
	public void setVLkw(Ganglinie vLkw) {
		this.vLkw = vLkw;
	}

	/**
	 * Gibt die entsprechende Ganglinie zur&uuml;ck.
	 * 
	 * @return Die Ganglinie
	 */
	public Ganglinie getVPkw() {
		return vPkw;
	}

	/**
	 * Legt die entsprechende Ganglinie fest.
	 * 
	 * @param vPkw
	 *            Die neue Ganglinie
	 */
	public void setVPkw(Ganglinie vPkw) {
		this.vPkw = vPkw;
	}

}
