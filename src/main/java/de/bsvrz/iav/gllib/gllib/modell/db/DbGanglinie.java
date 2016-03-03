/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.5 Funktionen Ganglinie
 * Copyright (C) 2011-2015 BitCtrl Systems GmbH
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA.
 *
 * Contact Information:
 * BitCtrl Systems GmbH
 * Weißenfelser Straße 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib.modell.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;
import de.bsvrz.iav.gllib.gllib.dav.Messwerte;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.objekte.EreignisTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.objekte.MessQuerschnittAllgemein;

/**
 * {@link Entity} zum Speichern von Ganglinien in der Datenbank. Korrespondiert
 * mit DAV-Attributliste <code>atl.ganglinie</code>.
 *
 * @author BitCtrl Systems GmbH, Thomas Thierfelder
 */
public class DbGanglinie implements Serializable {

	private static final long serialVersionUID = 2L;

	/**
	 * PID des MQ.
	 */
	private String mqPid;

	/**
	 * ID des Ereignistyps.
	 */
	private long ereignisTypId;

	/**
	 * Ob es sich um eine Referenzganglinie handelt.
	 */
	private boolean referenzGanglinie;

	/**
	 * Zeitpunkt der letzten Verschmelzung.
	 */
	private long letzteVerschmelzung;

	/**
	 * Anzahl der Verschmelzungen insgesamt.
	 */
	private long anzahlVerschmelzungen;

	/**
	 * GL-Typ.
	 */
	private int typ;

	/**
	 * Approximationsverfahren.
	 */
	private int approximationsverfahren;

	/**
	 * B-Spline-Ordnung.
	 */
	private int ordnung;

	/**
	 * Liste aller Stuetzstellen.
	 */
	private final ArrayList<DbStuetzstelle> stuetzstellen = new ArrayList<DbStuetzstelle>();

	/**
	 * Standardkonstruktor.
	 */
	public DbGanglinie() {
		//
	}

	/**
	 * Erfragt, ob es sich bei der uebergebenen Stuetzstelle um eine leere
	 * Stuetzstelle handelt.
	 *
	 * @param value
	 *            ein Q-/V-Wert einer Stuetzstelle.
	 * @return ob es sich bei der uebergebenen Stuetzstelle um eine leere
	 *         Stuetzstelle handelt.
	 */
	public static final boolean isLeereStuetzstelle(final Double value) {
		return (value == null) || (value == Messwerte.UNDEFINIERT);
	}

	/**
	 * Erfragt eine Instanz der innerhalb des GL-Lernen bzw. der Prognose
	 * benutzten Klasse {@link GanglinieMQ}, die aus den Daten dieses Objekts
	 * gespeist wird.
	 *
	 * @return eine Instanz der innerhalb des GL-Lernen bzw. der Prognose
	 *         benutzten Klasse {@link GanglinieMQ}, die aus den Daten dieses
	 *         Objekts gespeist wird, oder <code>null</code>, wenn die
	 *         MQ-Ganglinie nicht rekonstruiert werden konnte (z.B., wenn der
	 *         Ereignistyp im DAV nicht mehr vorhanden ist).
	 */
	final GanglinieMQ convert() {
		final GanglinieMQ instanz = new GanglinieMQ();

		instanz.setAnzahlVerschmelzungen(anzahlVerschmelzungen);
		instanz.setApproximationDaK(approximationsverfahren);
		instanz.setBSplineOrdnung(ordnung);
		final EreignisTyp et = (EreignisTyp) ObjektFactory.getInstanz()
				.getModellobjekt(ereignisTypId);
		if (et == null) {
			return null;
		}
		instanz.setEreignisTyp(et);
		instanz.setLetzteVerschmelzung(letzteVerschmelzung);
		final MessQuerschnittAllgemein mq = (MessQuerschnittAllgemein) ObjektFactory
				.getInstanz().getModellobjekt(mqPid);
		if (mq == null) {
			return null;
		}
		instanz.setMessQuerschnitt(mq);
		instanz.setReferenz(referenzGanglinie);
		instanz.setTyp(typ);
		for (final DbStuetzstelle stuetzStelle : stuetzstellen) {
			long zeitstempel;
			Double qKfz0, qLkw0, vPkw0, vLkw0;

			zeitstempel = stuetzStelle.getZeit();
			qKfz0 = stuetzStelle.getqKfz();

			if (isLeereStuetzstelle(qKfz0)) {
				qKfz0 = null;
			}
			qLkw0 = stuetzStelle.getqLkw();
			if (isLeereStuetzstelle(qLkw0)) {
				qLkw0 = null;
			}
			vPkw0 = stuetzStelle.getvPkw();
			if (isLeereStuetzstelle(vPkw0)) {
				vPkw0 = null;
			}
			vLkw0 = stuetzStelle.getvLkw();
			if (isLeereStuetzstelle(vLkw0)) {
				vLkw0 = null;
			}

			instanz.put(zeitstempel, new Messwerte(qKfz0, qLkw0, vPkw0, vLkw0));
		}

		return instanz;
	}

	/**
	 * Gibt die ID des Ereignistyps zurück.
	 *
	 * @return die ID des Ereignistyps.
	 */
	public long getEreignisTypId() {
		return ereignisTypId;
	}

	/**
	 * Legt die PID des Ereignistyps fest.
	 *
	 * @param ereignisTypId
	 *            die ID des Ereignistyps.
	 */
	public void setEreignisTypId(final long ereignisTypId) {
		this.ereignisTypId = ereignisTypId;
	}

	/**
	 * Gibt die PID des MQs zurück.
	 *
	 * @return die PID des MQs.
	 */
	public String getMqPid() {
		return mqPid;
	}

	/**
	 * Legt die PID des MQs fest.
	 *
	 * @param mqPid
	 *            die PID des MQs.
	 */
	public void setMqPid(final String mqPid) {
		this.mqPid = mqPid;
	}

	/**
	 * Flag, ob es sich um eine Referenzganglinie handelt.
	 *
	 * @return <code>true</code>, wenn die Ganglinie eine Referenzganglinie ist.
	 */
	public boolean isReferenzGanglinie() {
		return referenzGanglinie;
	}

	/**
	 * Flag, ob es sich um eine Referenzganglinie handelt.
	 *
	 * @param referenzGanglinie
	 *            <code>true</code>, wenn die Ganglinie eine Referenzganglinie
	 *            ist.
	 */
	public void setReferenzGanglinie(final boolean referenzGanglinie) {
		this.referenzGanglinie = referenzGanglinie;
	}

	/**
	 * Gibt den Zeitpunkt der letzten Verschmelzung in Millisekunden zurück.
	 *
	 * @return der Zeitstempel der letzten Verschmelzung in Millisekunden.
	 */
	public long getLetzteVerschmelzung() {
		return letzteVerschmelzung;
	}

	/**
	 * Legt den Zeitpunkt der letzten Verschmelzung in Millisekunden fest.
	 *
	 * @param letzteVerschmelzung
	 *            der Zeitstempel der letzten Verschmelzung in Millisekunden.
	 */
	public void setLetzteVerschmelzung(final long letzteVerschmelzung) {
		this.letzteVerschmelzung = letzteVerschmelzung;
	}

	/**
	 * Gibt die Anzahl der Verschmelzungen insgesamt zurück.
	 *
	 * @return die Anzahl der Verschmelzungen.
	 */
	public long getAnzahlVerschmelzungen() {
		return anzahlVerschmelzungen;
	}

	/**
	 * Legt die Anzahl der Verschmelzungen insgesamt fest.
	 *
	 * @param anzahlVerschmelzungen
	 *            die Anzahl der Verschmelzungen.
	 */
	public void setAnzahlVerschmelzungen(final long anzahlVerschmelzungen) {
		this.anzahlVerschmelzungen = anzahlVerschmelzungen;
	}

	/**
	 * Gibt den Typ der Ganglinie zurück.
	 *
	 * @return der Ganglinientyp.
	 */
	public int getTyp() {
		return typ;
	}

	/**
	 * Legt den Typ der Ganglinie fest.
	 *
	 * @param typ
	 *            der Ganglinientyp.
	 */
	public void setTyp(final int typ) {
		this.typ = typ;
	}

	/**
	 * Gibt das Approximationsverfahren der Ganglinie zurück.
	 *
	 * @return das Approximationsverfahren der Ganglinie.
	 */
	public int getApproximationsverfahren() {
		return approximationsverfahren;
	}

	/**
	 * Legt das Approximationsverfahren der Ganglinie fest.
	 *
	 * @param approximationsverfahren
	 *            das Approximationsverfahren der Ganglinie.
	 */
	public void setApproximationsverfahren(final int approximationsverfahren) {
		this.approximationsverfahren = approximationsverfahren;
	}

	/**
	 * Gibt die Ordnung des B-Splines zurück. Diese Eigenschaft ist nur
	 * relevant, wenn als Approximationsverfahren B-Spline verwendet wird.
	 *
	 * @return die B-Spline-Ordnung.
	 * @see #getApproximationsverfahren()
	 */
	public int getOrdnung() {
		return ordnung;
	}

	/**
	 * Legt die Ordnung des B-Splines fest. Diese Eigenschaft ist nur relevant,
	 * wenn als Approximationsverfahren B-Spline verwendet wird.
	 *
	 * @param ordnung
	 *            die B-Spline-Ordnung.
	 * @see #getApproximationsverfahren()
	 */
	public void setOrdnung(final int ordnung) {
		this.ordnung = ordnung;
	}

	/**
	 * Gibt die Liste der Stützstellen der Ganglinie zurück.
	 *
	 * @return die Stützstellen der Ganglinie.
	 */
	public List<DbStuetzstelle> getStuetzstellen() {
		return stuetzstellen;
	}

	/**
	 * Legt die Liste der Stützstellen der Ganglinie fest.
	 *
	 * @param stuetzstellen
	 *            die Stützstellen der Ganglinie.
	 */
	public void setStuetzstellen(final List<DbStuetzstelle> stuetzstellen) {
		this.stuetzstellen.clear();
		this.stuetzstellen.addAll(stuetzstellen);
	}

	@Override
	public String toString() {
		String txt = getClass().getSimpleName();

		txt += "["; //$NON-NLS-1$
		txt += "mqPid=" + mqPid; //$NON-NLS-1$
		txt += ", ereignisTypPid=" + ereignisTypId; //$NON-NLS-1$
		txt += ", referenzGanglinie=" + referenzGanglinie; //$NON-NLS-1$
		txt += ", letzteVerschmelzung=" + letzteVerschmelzung; //$NON-NLS-1$
		txt += ", anzahlVerschmelzungen=" + anzahlVerschmelzungen; //$NON-NLS-1$
		txt += ", typ=" + typ; //$NON-NLS-1$
		txt += ", approximationsverfahren=" + approximationsverfahren; //$NON-NLS-1$
		txt += ", ordnung=" + ordnung; //$NON-NLS-1$
		txt += "]"; //$NON-NLS-1$

		return txt;
	}

}
