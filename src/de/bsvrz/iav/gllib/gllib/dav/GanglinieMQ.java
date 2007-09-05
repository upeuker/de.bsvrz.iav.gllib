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
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.Data.Array;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.iav.gllib.gllib.Approximation;
import de.bsvrz.iav.gllib.gllib.BSpline;
import de.bsvrz.iav.gllib.gllib.CubicSpline;
import de.bsvrz.iav.gllib.gllib.Ganglinie;
import de.bsvrz.iav.gllib.gllib.Polyline;
import de.bsvrz.iav.gllib.gllib.Stuetzstelle;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.EreignisTyp;

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
	public static final int APPROX_BSPLINE = 1;

	/** Datenkatalogkonstante f&uuml;r einen Cubic-Spline. */
	public static final int APPROX_CUBICSPLINE = 2;

	/** Datenkatalogkonstante f&uuml;r eine Polylinie. */
	public static final int APPROX_POLYLINE = 3;

	/** Datenkatalogkonstante f&uuml;r eine absolute Ganglinie. */
	public static final int TYP_ABSOLUT = 0;

	/** Datenkatalogkonstante f&uuml;r eine relative additive Ganglinie. */
	public static final int TYP_ADDITIV = 1;

	/** Datenkatalogkonstante f&uuml;r eine relative multiplikative Ganglinie. */
	public static final int TYP_MULTIPLIKATIV = 2;

	/** Konstante f&uuml;r den undefinierten Wert einer St&uuml;tzstelle. */
	protected static final int UNDEFINIERT = Integer.MIN_VALUE;

	/** Faktor mit dem intern die St&uuml;tzstellenwerte multipliziert werden. */
	protected static final int FAKTOR = 1000;

	/** Der Messquerschnitt, zu dem die Ganglinie geh&ouml;rt. */
	protected SystemObject mq;

	/** Parameter f&uuml;r die Berechnung von QB. */
	private float k1;

	/** Parameter f&uuml;r die Berechnung von QB. */
	private float k2;

	/** Zeitpunkt der letzten Verschmelzung. */
	protected long letzteVerschmelzung;

	/** Anzahl der Verschmelzung mit anderen Ganglinienb. */
	protected long anzahlVerschmelzungen;

	/** Identifier f&uuml;r das mit der Ganglinie verkn&uuml;pfte Ereignis. */
	protected EreignisTyp ereignisTyp;

	/** Flag, ob die Ganglinie eine Referenzganglinie darstellt. */
	protected boolean referenz;

	/** Typ der Ganglinie. */
	protected int typ;

	/** Cached die Approximation f&uuml;r QKfz. */
	private Approximation<Double> qKfz;

	/** Cached die Approximation f&uuml;r QLkw. */
	private Approximation<Double> qLkw;

	/** Cached die Approximation f&uuml;r VPkw. */
	private Approximation<Double> vPkw;

	/** Cached die Approximation f&uuml;r VLkw. */
	private Approximation<Double> vLkw;

	/**
	 * F&uuml;hrt die Initialisierung der Member durch.
	 */
	protected GanglinieMQ() {
		k1 = 2.0f;
		k2 = 0.01f;
		letzteVerschmelzung = 1;
		anzahlVerschmelzungen = 0;
		ereignisTyp = null;
		referenz = false;
		typ = TYP_ABSOLUT;
	}

	protected GanglinieMQ(Collection<Stuetzstelle<Messwerte>> stuetzstellen) {
		super(stuetzstellen);
		k1 = 2.0f;
		k2 = 0.01f;
		letzteVerschmelzung = 1;
		anzahlVerschmelzungen = 0;
		ereignisTyp = null;
		referenz = false;
		typ = TYP_ABSOLUT;
	}
	
	public GanglinieMQ(SortedMap<Long, Messwerte> stuetzstellen) {
		super(stuetzstellen);
		k1 = 2.0f;
		k2 = 0.01f;
		letzteVerschmelzung = 1;
		anzahlVerschmelzungen = 0;
		ereignisTyp = null;
		referenz = false;
		typ = TYP_ABSOLUT;
	}

	/**
	 * Gibt den Messquerschnitt der Ganglinie zur&uuml;ck.
	 * 
	 * @return ein Messquerschnitt.
	 */
	public SystemObject getMessQuerschnitt() {
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
	 * Gibt die Anzahl der bisherigen Verschmelzungen beim automatischen Lernen
	 * zur&uuml;ck.
	 * 
	 * @return Anzahl bisheriger Verschmelzungen
	 */
	public long getAnzahlVerschmelzungen() {
		return anzahlVerschmelzungen;
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
	 * @return der Ereignistyp.
	 */
	public EreignisTyp getEreignisTyp() {
		return ereignisTyp;
	}

	/**
	 * Gibt den Ganglinientyp zur&uuml;ck.
	 * 
	 * @return der Typ der Ganglinie.
	 */
	public int getTyp() {
		return typ;
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
	 * Extrahiert aus den abstrakten Daten die Prognose.
	 * 
	 * @param daten
	 *            die Daten der Prognose.
	 */
	protected void setDatenVonPrognoseGanglinie(Data daten) {
		Array feld;

		mq = daten.getReferenceValue("Messquerschnitt").getSystemObject();

		stuetzstellen.clear();

		// Verfahren
		switch (daten.getUnscaledValue("GanglinienVerfahren").intValue()) {
		case APPROX_BSPLINE:
			setApproximation(new BSpline(daten.getUnscaledValue("Ordnung")
					.byteValue()));
			break;
		case APPROX_CUBICSPLINE:
			setApproximation(new CubicSpline());
			break;
		case APPROX_POLYLINE:
			setApproximation(new Polyline());
			break;
		default:
			break;
		}

		// Stützstellen
		feld = daten.getArray("Stützstelle");
		for (int i = 0; i < feld.getLength(); i++) {
			long zeitstempel;
			Double qKfz, qLkw, vPkw, vLkw;

			zeitstempel = feld.getItem(i).getTimeValue("Zeit").getMillis();
			if (feld.getItem(i).getUnscaledValue("QKfz").intValue() == UNDEFINIERT) {
				qKfz = null;
			} else {
				qKfz = feld.getItem(i).getScaledValue("QKfz").doubleValue();
			}
			if (feld.getItem(i).getUnscaledValue("QLkw").intValue() == UNDEFINIERT) {
				qLkw = null;
			} else {
				qLkw = feld.getItem(i).getScaledValue("QLkw").doubleValue();
			}
			if (feld.getItem(i).getUnscaledValue("VPkw").intValue() == UNDEFINIERT) {
				vPkw = null;
			} else {
				vPkw = feld.getItem(i).getScaledValue("VPkw").doubleValue();
			}
			if (feld.getItem(i).getUnscaledValue("VLkw").intValue() == UNDEFINIERT) {
				vLkw = null;
			} else {
				vLkw = feld.getItem(i).getScaledValue("VLkw").doubleValue();
			}
			setStuetzstelle(zeitstempel, new Messwerte(qKfz, qLkw, vPkw, vLkw));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void aktualisiereApproximation() {
		List<Stuetzstelle<Double>> stellenQKfz;
		List<Stuetzstelle<Double>> stellenQLkw;
		List<Stuetzstelle<Double>> stellenVPkw;
		List<Stuetzstelle<Double>> stellenVLkw;

		if (getApproximation() == null) {
			// Wenn keine Approximation festgelegt wurde, gibt es nichts zu tun
			return;
		}

		stellenQKfz = new ArrayList<Stuetzstelle<Double>>();
		stellenQLkw = new ArrayList<Stuetzstelle<Double>>();
		stellenVPkw = new ArrayList<Stuetzstelle<Double>>();
		stellenVLkw = new ArrayList<Stuetzstelle<Double>>();

		for (long t : stuetzstellen.keySet()) {
			Messwerte werte;

			werte = stuetzstellen.get(t);
			stellenQKfz.add(new Stuetzstelle<Double>(t, werte.getQKfz()));
			stellenQLkw.add(new Stuetzstelle<Double>(t, werte.getQLkw()));
			stellenVPkw.add(new Stuetzstelle<Double>(t, werte.getVPkw()));
			stellenVLkw.add(new Stuetzstelle<Double>(t, werte.getVLkw()));
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

			Double qKfz, qLkw, vPkw, vLkw;

			qKfz = this.qKfz.get(zeitstempel).getWert();
			if (qKfz != null) {
				qKfz /= FAKTOR;
			}

			qLkw = this.qLkw.get(zeitstempel).getWert();
			if (qLkw != null) {
				qLkw /= FAKTOR;
			}

			vPkw = this.vPkw.get(zeitstempel).getWert();
			if (vPkw != null) {
				vPkw /= FAKTOR;
			}

			vLkw = this.vLkw.get(zeitstempel).getWert();
			if (vLkw != null) {
				vLkw /= FAKTOR;
			}

			w = new Messwerte(qKfz, qLkw, vPkw, vLkw, k1, k2);
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
		boolean neu;
		Double qKfz, qLkw, vPkw, vLkw;

		qKfz = wert.getQKfz();
		if (qKfz != null) {
			qKfz *= FAKTOR;
		}

		qLkw = wert.getQLkw();
		if (qLkw != null) {
			qLkw *= FAKTOR;
		}

		vPkw = wert.getVPkw();
		if (vPkw != null) {
			vPkw *= FAKTOR;
		}

		vLkw = wert.getVLkw();
		if (vLkw != null) {
			vLkw *= FAKTOR;
		}

		w = new Messwerte(qKfz, qLkw, vPkw, vLkw, k1, k2);

		neu = stuetzstellen.put(zeitstempel, w) == null;
		aktualisiereApproximation();
		return neu;
		// return super.setStuetzstelle(zeitstempel, w);
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
