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
 * Weißenfelser Straße 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib.dav;

import java.util.ArrayList;
import java.util.List;

import stauma.dav.clientside.Data;
import stauma.dav.clientside.Data.Array;
import de.bsvrz.iav.gllib.gllib.Approximation;
import de.bsvrz.iav.gllib.gllib.BSpline;
import de.bsvrz.iav.gllib.gllib.CubicSpline;
import de.bsvrz.iav.gllib.gllib.Ganglinie;
import de.bsvrz.iav.gllib.gllib.Polyline;
import de.bsvrz.iav.gllib.gllib.Stuetzstelle;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.MessQuerschnitt;

/**
 * F&uuml;r Messquerschnitte angepasste Ganglinie. Die vier Verkehrswerte QKfz,
 * QLkw, VPkw und VLkw werden als Gruppe pro Zeitstempel gesichert. Aus diesen
 * Werten lassen sich die drei davon abh&auml;ngigen Gr&ouml;&szlig;e QPkw, VKfz
 * und QB berechnen.
 * 
 * @author BitCtrl, Schumann
 * @version $Id$
 */
public class GanglinieMQ extends Ganglinie<Messwerte> {

	/** Datenkatalogkonstante f&uuml;r einen B-Spline. */
	public static final int BSPLINE = 1;

	/** Datenkatalogkonstante f&uuml;r einen Cubic-Spline. */
	public static final int CUBICSPLINE = 2;

	/** Datenkatalogkonstante f&uuml;r eine Polylinie. */
	public static final int POLYLINE = 3;

	/** Faktor mit dem intern die St&uuml;tzstellenwerte multipliziert werden. */
	private static final int FAKTOR = 100;

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

	/** Der Messquerschnitt, zu dem die Ganglinie geh&ouml;rt. */
	private MessQuerschnitt mq;

	/** Parameter f&uuml;r die Berechnung von QB. */
	private float k1 = 2.0f;

	/** Parameter f&uuml;r die Berechnung von QB. */
	private float k2 = 0.01f;

	/** Zeitpunkt der letzten Verschmelzung. */
	private long letzteVerschmelzung = -1;

	/** Anzahl der Verschmelzung mit anderen Ganglinienb. */
	private long anzahlVerschmelzungen = 0;

	/** Identifier f&uuml;r das mit der Ganglinie verkn&uuml;pfte Ereignis. */
	private String ereignisTyp = null;

	/** Flag, ob die Ganglinie eine Referenzganglinie darstellt. */
	private boolean referenz = false;

	/** Typ der Ganglinie. */
	private Typ typ = Typ.ABSOLUT;

	/** Cached die Approximation f&uuml;r QKfz. */
	private Approximation<Number> qKfz;

	/** Cached die Approximation f&uuml;r QLkw. */
	private Approximation<Number> qLkw;

	/** Cached die Approximation f&uuml;r VPkw. */
	private Approximation<Number> vPkw;

	/** Cached die Approximation f&uuml;r VLkw. */
	private Approximation<Number> vLkw;

	/**
	 * Konstruktor verstecken.
	 */
	GanglinieMQ() {
		// nichts
	}

	/**
	 * Gibt den Messquerschnitt der Ganglinie zur&uuml;ck.
	 * 
	 * @return ein Messquerschnitt.
	 */
	public MessQuerschnitt getMessQuerschnitt() {
		return mq;
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
	 * Gibt den Ganglinientyp zur&uuml;ck.
	 * 
	 * @return der Typ der Ganglinie.
	 */
	public Typ getTyp() {
		return typ;
	}

	/**
	 * Legt den Ganglinientyp fest.
	 * 
	 * @param typ
	 *            der Typ der Ganglinie.
	 */
	public void setTyp(Typ typ) {
		this.typ = typ;
	}

	/**
	 * Gibt einen Parameter f&uuml;r die Berechnung von QB zur&uuml;ck.
	 * 
	 * @return der Parameter.
	 */
	public float getK1() {
		return k1;
	}

	/**
	 * Gibt einen Parameter f&uuml;r die Berechnung von QB zur&uuml;ck.
	 * 
	 * @return der Parameter
	 */
	public float getK2() {
		return k2;
	}

	/**
	 * Gibt den Messquerschnitt der Ganglinie zur&uuml;ck.
	 * 
	 * @return ein Messquerschnitt
	 */
	public MessQuerschnitt getMq() {
		return mq;
	}

	/**
	 * Legt den Parameter k1 f&uuml;r die Berechnung von QB fest.
	 * 
	 * @param k1
	 *            der parameter k1
	 */
	public void setK1(float k1) {
		this.k1 = k1;
	}

	/**
	 * Legt den Parameter k2 f&uuml;r die Berechnung von QB fest.
	 * 
	 * @param k2
	 *            der parameter k2
	 */
	public void setK2(float k2) {
		this.k2 = k2;
	}

	/**
	 * Legt die Anzahl der bisherigen Verschmelzungen fest.
	 * 
	 * @param letzteVerschmelzung
	 *            die neue Anzahl der Verschmelzungen.
	 */
	public void setLetzteVerschmelzung(long letzteVerschmelzung) {
		this.letzteVerschmelzung = letzteVerschmelzung;
	}

	/**
	 * Extrahiert aus den abstrakten Daten die Prognose.
	 * 
	 * @param daten
	 *            die Daten der Prognose.
	 */
	void setDaten(Data daten) {
		Array feld;

		mq = new MessQuerschnitt(daten.getReferenceValue("Messquerschnitt")
				.getSystemObject());

		stuetzstellen.clear();
		setStuetzstelle(daten.getTimeValue("ZeitpunktPrognoseBeginn")
				.getMillis(), null);
		setStuetzstelle(
				daten.getTimeValue("ZeitpunktPrognoseEnde").getMillis(), null);

		// Verfahren
		switch (daten.getUnscaledValue("GanglinienVerfahren").intValue()) {
		case BSPLINE:
			setApproximation(new BSpline(daten.getUnscaledValue("Ordnung")
					.byteValue()));
			break;
		case CUBICSPLINE:
			setApproximation(new CubicSpline());
			break;
		case POLYLINE:
			setApproximation(new Polyline());
			break;
		default:
			break;
		}

		// Stützstellen
		feld = daten.getArray("Stützstelle");
		for (int i = 0; i < feld.getLength(); i++) {
			Messwerte mw;

			mw = new Messwerte(feld.getItem(i).getScaledValue("QKfz")
					.floatValue(), feld.getItem(i).getScaledValue("QLkw")
					.floatValue(), feld.getItem(i).getScaledValue("VPkw")
					.floatValue(), feld.getItem(i).getScaledValue("VLkw")
					.floatValue(), k1, k2);
			setStuetzstelle(feld.getItem(i).getTimeValue("Zeit").getMillis(),
					mw);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void aktualisiereApproximation() {
		List<Stuetzstelle<Number>> stellenQKfz;
		List<Stuetzstelle<Number>> stellenQLkw;
		List<Stuetzstelle<Number>> stellenVPkw;
		List<Stuetzstelle<Number>> stellenVLkw;

		if (getApproximation() == null) {
			// Wenn keine Approximation festgelegt wurde, gibt es nichts zu tun
			return;
		}

		stellenQKfz = new ArrayList<Stuetzstelle<Number>>();
		stellenQLkw = new ArrayList<Stuetzstelle<Number>>();
		stellenVPkw = new ArrayList<Stuetzstelle<Number>>();
		stellenVLkw = new ArrayList<Stuetzstelle<Number>>();

		for (Stuetzstelle<Messwerte> s : getStuetzstellen()) {
			stellenQKfz.add(new Stuetzstelle<Number>(s.getZeitstempel(), s
					.getWert().getQKfz()));
			stellenQLkw.add(new Stuetzstelle<Number>(s.getZeitstempel(), s
					.getWert().getQLkw()));
			stellenVPkw.add(new Stuetzstelle<Number>(s.getZeitstempel(), s
					.getWert().getVPkw()));
			stellenVLkw.add(new Stuetzstelle<Number>(s.getZeitstempel(), s
					.getWert().getVLkw()));
		}

		try {
			qKfz = getApproximation().clone();
			qLkw = getApproximation().clone();
			vPkw = getApproximation().clone();
			vLkw = getApproximation().clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(
					"Die verwendete Approximation muss clone() unterstützen.");
		}
		qKfz.setStuetzstellen(stellenQKfz);
		qKfz.initialisiere();

		qLkw.setStuetzstellen(stellenQLkw);
		qLkw.initialisiere();

		vPkw.setStuetzstellen(stellenVPkw);
		vPkw.initialisiere();

		vLkw.setStuetzstellen(stellenVLkw);
		vLkw.initialisiere();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Stuetzstelle<Messwerte> getStuetzstelle(long zeitstempel) {
		Messwerte w;

		if (getApproximation() != null) {
			// Approximation vorhanden und benutzen
			w = new Messwerte(qKfz.get(zeitstempel).getWert().floatValue()
					/ FAKTOR, qLkw.get(zeitstempel).getWert().floatValue()
					/ FAKTOR, vPkw.get(zeitstempel).getWert().floatValue()
					/ FAKTOR, vLkw.get(zeitstempel).getWert().floatValue()
					/ FAKTOR, k1, k2);
		} else {
			// Keine Approximation festgelegt, evtl. Stützstelle vorhanden?
			Stuetzstelle<Messwerte> s;

			s = super.getStuetzstelle(zeitstempel);

			if (s != null) {
				// Es gibt eine Stützstelle
				w = new Messwerte(s.getWert().getQKfz() / FAKTOR, s.getWert()
						.getQLkw()
						/ FAKTOR, s.getWert().getVPkw() / FAKTOR, s.getWert()
						.getVLkw()
						/ FAKTOR, k1, k2);
			} else {
				// Es gibt auch keine Stützstelle
				return null;
			}
		}

		return new Stuetzstelle<Messwerte>(zeitstempel, w);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Stuetzstelle<Messwerte>> getStuetzstellen() {
		List<Stuetzstelle<Messwerte>> liste;

		liste = new ArrayList<Stuetzstelle<Messwerte>>();
		for (long t : stuetzstellen.keySet()) {
			liste.add(getStuetzstelle(t));
		}

		return liste;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setStuetzstelle(long zeitstempel, Messwerte wert) {
		Messwerte w;

		w = new Messwerte(wert.getQKfz() * FAKTOR, wert.getQLkw() * FAKTOR,
				wert.getVPkw() * FAKTOR, wert.getVLkw() * FAKTOR, k1, k2);

		aktualisiereApproximation();
		return super.setStuetzstelle(zeitstempel, w);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setStuetzstelle(Stuetzstelle<Messwerte> s) {
		return setStuetzstelle(s.getZeitstempel(), s.getWert());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return mq + ": " + getStuetzstellen();
	}

}
