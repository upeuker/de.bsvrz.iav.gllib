/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.5 Funktionen Ganglinie
 * Copyright (C) 2007 BitCtrl Systems GmbH 
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

package de.bsvrz.iav.gllib.gllib.dav;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;

import com.bitctrl.util.Interval;

import de.bsvrz.iav.gllib.gllib.Approximation;
import de.bsvrz.iav.gllib.gllib.BSpline;
import de.bsvrz.iav.gllib.gllib.CubicSpline;
import de.bsvrz.iav.gllib.gllib.Ganglinie;
import de.bsvrz.iav.gllib.gllib.IGanglinie;
import de.bsvrz.iav.gllib.gllib.Polyline;
import de.bsvrz.iav.gllib.gllib.Stuetzstelle;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.objekte.EreignisTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.objekte.MessQuerschnittAllgemein;

/**
 * F&uuml;r Messquerschnitte angepasste Ganglinie. Die vier Verkehrswerte QKfz,
 * QLkw, VPkw und VLkw werden als Gruppe pro Zeitstempel gesichert. Aus diesen
 * Werten lassen sich die drei davon abh&auml;ngigen Gr&ouml;&szlig;e QPkw, VKfz
 * und QB berechnen.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class GanglinieMQ implements IGanglinie<Messwerte> {

	/** Die Attributgruppe, in der historische Ganglinien gesichert werden. */
	public static final String ATG_GANGLINIE = "atg.ganglinie";

	/** Datenkatalogkonstante f&uuml;r die unbestimmte Approximation. */
	public static final int APPROX_UNBESTIMMT = 0;

	/** Datenkatalogkonstante f&uuml;r einen B-Spline. */
	public static final int APPROX_BSPLINE = 1;

	/** Datenkatalogkonstante f&uuml;r einen Cubic-Spline. */
	public static final int APPROX_CUBICSPLINE = 2;

	/** Datenkatalogkonstante f&uuml;r eine Polylinie. */
	public static final int APPROX_POLYLINE = 3;

	/** Standardordung der Approximation. Nur f&uuml;r B-Spline relevant. */
	public static final byte APPROX_STANDARD_ORDNUNG = 5;

	/** Datenkatalogkonstante f&uuml;r eine absolute Ganglinie. */
	public static final int TYP_ABSOLUT = 0;

	/** Datenkatalogkonstante f&uuml;r eine relative additive Ganglinie. */
	public static final int TYP_ADDITIV = 1;

	/** Datenkatalogkonstante f&uuml;r eine relative multiplikative Ganglinie. */
	public static final int TYP_MULTIPLIKATIV = 2;

	/** Ganglinie f&uuml;r QKfz. */
	Ganglinie qKfz;

	/** Ganglinie f&uuml;r QLkw. */
	Ganglinie qLkw;

	/** Ganglinie f&uuml;r VPkw. */
	Ganglinie vPkw;

	/** Ganglinie f&uuml;r VLkw. */
	Ganglinie vLkw;

	/** Der Messquerschnitt, zu dem die Ganglinie geh&ouml;rt. */
	private MessQuerschnittAllgemein messQuerschnitt;

	/** Parameter f&uuml;r die Berechnung von QB. Standard ist 2,0. */
	private float k1 = 2.0f;

	/** Parameter f&uuml;r die Berechnung von QB. Standard ist 0,01. */
	private float k2 = 0.01f;

	/** Zeitpunkt der letzten Verschmelzung. */
	private long letzteVerschmelzung;

	/** Anzahl der Verschmelzung mit anderen Ganglinienb. */
	private long anzahlVerschmelzungen;

	/** Identifier f&uuml;r das mit der Ganglinie verkn&uuml;pfte Ereignis. */
	private EreignisTyp ereignisTyp;

	/** Flag, ob die Ganglinie eine Referenzganglinie darstellt. */
	private boolean referenz;

	/** Typ der Ganglinie. Standard ist {@link #TYP_ABSOLUT}. */
	private int typ = TYP_ABSOLUT;

	// Die folgenden vier Ganglinien müssen identische Stützstellen besitzen,
	// was deren Zeitpunkt angeht. Unter Einbehaltung dieser Vorgabe, wird die
	// Ganglinie für QKfz stellvertretend für alle vier verwendet, wenn nur die
	// Zeitstempel der Stützstellen relevant sind.

	/** Art der Approximation. Standard ist {@link #APPROX_BSPLINE}. */
	private int approximationDaK = APPROX_BSPLINE;

	/** Ordnung des B-Spline. Standard ist {@link #APPROX_STANDARD_ORDNUNG}. */
	private byte bSplineOrdnung = APPROX_STANDARD_ORDNUNG;

	/** Flag, ob die Approximation aktuallisiert werden muss. */
	private boolean approximationAktuell = false;

	/** Das Intervall für das die Ganglinie prognostiziert wird. */
	private Interval prognoseZeitraum;

	/**
	 * Allgemeine Initialisierung.
	 */
	public GanglinieMQ() {
		qKfz = new Ganglinie();
		qLkw = new Ganglinie();
		vPkw = new Ganglinie();
		vLkw = new Ganglinie();
		aktualisiereApproximation();
	}

	/**
	 * Kopierkonstruktor.
	 * 
	 * @param stuetzstellen
	 *            die St&uuml;tzstellen aus denen die Ganglinie bestehen soll.
	 */
	public GanglinieMQ(Collection<Stuetzstelle<Messwerte>> stuetzstellen) {
		this();
		setStuetzstellen(stuetzstellen);
	}

	/**
	 * Kopierkonstruktor.
	 * 
	 * @param stuetzstellen
	 *            die St&uuml;tzstellen aus denen die Ganglinie bestehen soll.
	 */
	public GanglinieMQ(SortedMap<Long, Messwerte> stuetzstellen) {
		this();
		for (Long t : stuetzstellen.keySet()) {
			qKfz.setStuetzstelle(t, stuetzstellen.get(t).getQKfz());
			qLkw.setStuetzstelle(t, stuetzstellen.get(t).getQLkw());
			vPkw.setStuetzstelle(t, stuetzstellen.get(t).getVPkw());
			vLkw.setStuetzstelle(t, stuetzstellen.get(t).getVLkw());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void aktualisiereApproximation() {
		switch (approximationDaK) {
		case APPROX_POLYLINE:
			qKfz.setApproximation(new Polyline());
			qLkw.setApproximation(new Polyline());
			vPkw.setApproximation(new Polyline());
			vLkw.setApproximation(new Polyline());
			break;
		case APPROX_CUBICSPLINE:
			qKfz.setApproximation(new CubicSpline());
			qLkw.setApproximation(new CubicSpline());
			vPkw.setApproximation(new CubicSpline());
			vLkw.setApproximation(new CubicSpline());
			break;
		case APPROX_BSPLINE:
			qKfz.setApproximation(new BSpline(bSplineOrdnung));
			qLkw.setApproximation(new BSpline(bSplineOrdnung));
			vPkw.setApproximation(new BSpline(bSplineOrdnung));
			vLkw.setApproximation(new BSpline(bSplineOrdnung));
			break;
		default:
			qKfz.setApproximation(new BSpline(APPROX_STANDARD_ORDNUNG));
			qLkw.setApproximation(new BSpline(APPROX_STANDARD_ORDNUNG));
			vPkw.setApproximation(new BSpline(APPROX_STANDARD_ORDNUNG));
			vLkw.setApproximation(new BSpline(APPROX_STANDARD_ORDNUNG));
			break;
		}
		qKfz.aktualisiereApproximation();
		qLkw.aktualisiereApproximation();
		vPkw.aktualisiereApproximation();
		vLkw.aktualisiereApproximation();
		approximationAktuell = true;
	}

	/**
	 * {@inheritDoc}
	 */
	public int anzahlStuetzstellen() {
		return qKfz.anzahlStuetzstellen();
	}

	/**
	 * Kopiert die St&uumltzstellen, das Approximationsverfahren und alle
	 * anderen Eigenschaften bis auf {@code approximationAktuell}. Der Wert
	 * f&uuml;r {@code approximationAktuell} wird auf false gesetzt.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public GanglinieMQ clone() {
		GanglinieMQ g;

		g = new GanglinieMQ();
		g.qKfz = new Ganglinie(qKfz.getStuetzstellen());
		g.qLkw = new Ganglinie(qLkw.getStuetzstellen());
		g.vPkw = new Ganglinie(vPkw.getStuetzstellen());
		g.vLkw = new Ganglinie(vLkw.getStuetzstellen());
		g.setAnzahlVerschmelzungen(anzahlVerschmelzungen);
		g.setApproximationDaK(approximationDaK);
		g.setBSplineOrdnung(bSplineOrdnung);
		g.setEreignisTyp(ereignisTyp);
		g.setK1(k1);
		g.setK2(k2);
		g.setLetzteVerschmelzung(letzteVerschmelzung);
		g.setMessQuerschnitt(messQuerschnitt);
		g.setPrognoseZeitraum(prognoseZeitraum);
		g.setReferenz(referenz);
		g.setTyp(typ);
		g.approximationAktuell = false;

		return g;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsStuetzstelle(long zeitstempel) {
		return qKfz.existsStuetzstelle(zeitstempel);
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
	 * Gibt stellvertretend die Approximation f&uuml;r QKfz zur&uuml;ck.
	 * 
	 * {@inheritDoc}
	 * 
	 * @deprecated Die Approximation der einzelnen Größen kann
	 *             {@code getGanglinie*.getApproximation()} abgerufen werden.
	 */
	@Deprecated
	public Approximation getApproximation() {
		return qKfz.getApproximation();
	}

	/**
	 * Gibt die Art der Approximation als Datenkatalogkonstante zur&uuml;ck.
	 * 
	 * @return eine der Konstante {@link #APPROX_POLYLINE},
	 *         {@link #APPROX_CUBICSPLINE}, {@link #APPROX_BSPLINE} oder
	 *         {@link #APPROX_UNBESTIMMT}.
	 */
	public int getApproximationDaK() {
		return approximationDaK;
	}

	/**
	 * Gibt die Ordnung des B-Spline zur&uuml;ck. Wird zur Approximation kein
	 * B-Spline benutzt, wird der Wert ignoriert.
	 * 
	 * @return die Ordnung des B-Spline.
	 */
	public byte getBSplineOrdnung() {
		return bSplineOrdnung;
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
	 * Gibt die Ganglinie f&uuml;r QB zur&uuml;ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St&uuml;tzstellen
	 *         von QB.
	 */
	public Ganglinie getGanglinieQB() {
		Ganglinie qb;

		qb = new Ganglinie();
		for (Stuetzstelle<Messwerte> s : getStuetzstellen()) {
			qb.setStuetzstelle(s.getZeitstempel(), s.getWert().getQB());
		}
		return qb;

	}

	/**
	 * Gibt die Ganglinie f&uuml;r QKfz zur&uuml;ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St&uuml;tzstellen
	 *         von QKfz.
	 */
	public Ganglinie getGanglinieQKfz() {
		return qKfz.clone();
	}

	/**
	 * Gibt die Ganglinie f&uuml;r QLkw zur&uuml;ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St&uuml;tzstellen
	 *         von QLkw.
	 */
	public Ganglinie getGanglinieQLkw() {
		return qLkw.clone();
	}

	/**
	 * Gibt die Ganglinie f&uuml;r QPkw zur&uuml;ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St&uuml;tzstellen
	 *         von QPkw.
	 */
	public Ganglinie getGanglinieQPkw() {
		Ganglinie qPkw;

		qPkw = new Ganglinie();
		for (Stuetzstelle<Messwerte> s : getStuetzstellen()) {
			qPkw.setStuetzstelle(s.getZeitstempel(), s.getWert().getQPkw());
		}
		return qPkw;
	}

	/**
	 * Gibt die Ganglinie f&uuml;r VKfz zur&uuml;ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St&uuml;tzstellen
	 *         von VKfz.
	 */
	public Ganglinie getGanglinieVKfz() {
		Ganglinie vKfz;

		vKfz = new Ganglinie();
		for (Stuetzstelle<Messwerte> s : getStuetzstellen()) {
			vKfz.setStuetzstelle(s.getZeitstempel(), s.getWert().getVKfz());
		}
		return vKfz;

	}

	/**
	 * Gibt die Ganglinie f&uuml;r VLkw zur&uuml;ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St&uuml;tzstellen
	 *         von VLkw.
	 */
	public Ganglinie getGanglinieVLkw() {
		return vLkw.clone();
	}

	/**
	 * Gibt die Ganglinie f&uuml;r QPkw zur&uuml;ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St&uuml;tzstellen
	 *         von QPkw.
	 */
	public Ganglinie getGanglinieVPkw() {
		return vPkw.clone();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #setPrognoseZeitraum(Intervall)
	 */
	public Interval getIntervall() {
		if (prognoseZeitraum != null) {
			return prognoseZeitraum;
		}

		return qKfz.getIntervall();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #setPrognoseZeitraum(Intervall)
	 */
	public List<Interval> getIntervalle() {
		throw new UnsupportedOperationException("Es müssen die Intervalle der "
				+ "einzelnen Ganglinien für Q, V und QB abgefragt werden.");
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
	 * Gibt den Zeitpunkt der letzten Verschmelzung als Zeitstempel zur&uuml;ck.
	 * 
	 * @return Zeitstempel
	 */
	public long getLetzteVerschmelzung() {
		return letzteVerschmelzung;
	}

	/**
	 * Gibt den Messquerschnitt der Ganglinie zur&uuml;ck.
	 * 
	 * @return ein Messquerschnitt.
	 */
	public MessQuerschnittAllgemein getMessQuerschnitt() {
		return messQuerschnitt;
	}

	/**
	 * Gibt das Prognoseintervall der Ganglinie zur&uuml;ck.
	 * 
	 * @return das Prognoseintervall.
	 */
	public Interval getPrognoseIntervall() {
		return prognoseZeitraum;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #setPrognoseZeitraum(Intervall)
	 */
	public Stuetzstelle<Messwerte> getStuetzstelle(long zeitstempel) {
		Double qKfz0, qLkw0, vPkw0, vLkw0;

		if (!isValid(zeitstempel)) {
			return new Stuetzstelle<Messwerte>(zeitstempel, new Messwerte(null,
					null, null, null, k1, k2));
		}

		qKfz0 = qKfz.getStuetzstelle(zeitstempel).getWert();
		qLkw0 = qLkw.getStuetzstelle(zeitstempel).getWert();
		vPkw0 = vPkw.getStuetzstelle(zeitstempel).getWert();
		vLkw0 = vLkw.getStuetzstelle(zeitstempel).getWert();

		return new Stuetzstelle<Messwerte>(zeitstempel, new Messwerte(qKfz0,
				qLkw0, vPkw0, vLkw0, k1, k2));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #setPrognoseZeitraum(Intervall)
	 */
	public List<Stuetzstelle<Messwerte>> getStuetzstellen() {
		List<Stuetzstelle<Messwerte>> liste;
		List<Stuetzstelle<Double>> qKfz0, qLkw0, vPkw0, vLkw0;

		liste = new ArrayList<Stuetzstelle<Messwerte>>();
		qKfz0 = qKfz.getStuetzstellen();
		qLkw0 = qLkw.getStuetzstellen();
		vPkw0 = vPkw.getStuetzstellen();
		vLkw0 = vLkw.getStuetzstellen();
		for (int i = 0; i < qKfz0.size(); i++) {
			long zeitstempel;

			zeitstempel = qKfz0.get(i).getZeitstempel();
			if (prognoseZeitraum != null
					&& !prognoseZeitraum.contains(zeitstempel)) {
				continue;
			}
			liste
					.add(new Stuetzstelle<Messwerte>(zeitstempel,
							new Messwerte(qKfz0.get(i).getWert(), qLkw0.get(i)
									.getWert(), vPkw0.get(i).getWert(), vLkw0
									.get(i).getWert(), k1, k2)));
		}

		return liste;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #setPrognoseZeitraum(Intervall)
	 */
	public List<Stuetzstelle<Messwerte>> getStuetzstellen(Interval intervall) {
		List<Stuetzstelle<Double>> qKfz0, qLkw0, vPkw0, vLkw0;
		List<Stuetzstelle<Messwerte>> liste;

		qKfz0 = qKfz.getStuetzstellen(intervall);
		qLkw0 = qLkw.getStuetzstellen(intervall);
		vPkw0 = vPkw.getStuetzstellen(intervall);
		vLkw0 = vLkw.getStuetzstellen(intervall);

		liste = new ArrayList<Stuetzstelle<Messwerte>>();
		for (int i = 0; i < qKfz0.size(); i++) {
			long zeitstempel;

			zeitstempel = qKfz0.get(i).getZeitstempel();
			if (prognoseZeitraum != null
					&& !prognoseZeitraum.contains(zeitstempel)) {
				continue;
			}
			liste
					.add(new Stuetzstelle<Messwerte>(zeitstempel,
							new Messwerte(qKfz0.get(i).getWert(), qLkw0.get(i)
									.getWert(), vPkw0.get(i).getWert(), vLkw0
									.get(i).getWert(), k1, k2)));
		}

		return liste;
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
	 * Bestimmt, ob die Ganglinie absolut oder relativ ist. Für eine relative
	 * Ganglinie wird nicht zwischen additiv und multiplikativ unterschieden.
	 * 
	 * @return {@code true}, wenn die Ganglinie absolut ist.
	 */
	public boolean isAbsolut() {
		return typ == TYP_ABSOLUT;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isApproximationAktuell() {
		return approximationAktuell;
	}

	/**
	 * Besitzt die Ganglinie die Auszeichnung als Referenz?
	 * 
	 * @return <code>true</code>, wenn diese Ganglinie eine Referenzganglinie
	 *         ist, sonst <code>false</code>
	 */
	public boolean isReferenz() {
		return referenz;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isValid(Interval intervall) {
		if (prognoseZeitraum != null && !prognoseZeitraum.contains(intervall)) {
			// Teilintervall liegt nicht innerhalb der Prognoseganglinie
			return false;
		}
		return qKfz.isValid(intervall);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isValid(long zeitstempel) {
		if (prognoseZeitraum != null && !prognoseZeitraum.contains(zeitstempel)) {
			// Zeitstempel liegt nicht innerhalb der Prognoseganglinie
			return false;
		}
		return qKfz.isValid(zeitstempel);
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove(long zeitstempel) {
		qKfz.remove(zeitstempel);
		qLkw.remove(zeitstempel);
		vPkw.remove(zeitstempel);
		vLkw.remove(zeitstempel);
	}

	/**
	 * Legt die Anzahl der bisherigen Verschmelzungen fest.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der öffentlichen API und
	 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
	 * 
	 * @param anzahlVerschmelzungen
	 *            Anzahl der Verschmelzungen
	 */
	public void setAnzahlVerschmelzungen(long anzahlVerschmelzungen) {
		this.anzahlVerschmelzungen = anzahlVerschmelzungen;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setApproximation(Approximation approximation) {
		if (approximation instanceof Polyline) {
			approximationDaK = APPROX_POLYLINE;
		} else if (approximation instanceof CubicSpline) {
			approximationDaK = APPROX_CUBICSPLINE;
		} else if (approximation instanceof BSpline) {
			approximationDaK = APPROX_BSPLINE;
			bSplineOrdnung = ((BSpline) approximation).getOrdnung();
		} else {
			approximationDaK = APPROX_UNBESTIMMT;
		}
		approximationAktuell = false;
	}

	/**
	 * Legt die zu verwendende Approximation mit Hilfe einer
	 * Datenkatalogkonstante fest.
	 * 
	 * @param approximationDaK
	 *            eine der Konstante {@link #APPROX_POLYLINE},
	 *            {@link #APPROX_CUBICSPLINE}, {@link #APPROX_BSPLINE} oder
	 *            {@link #APPROX_UNBESTIMMT}.
	 */
	public void setApproximationDaK(int approximationDaK) {
		this.approximationDaK = approximationDaK;
		approximationAktuell = false;
	}

	/**
	 * Legt die Ordnung des B-Spline fest. Wird zur Approximation kein B-Spline
	 * benutzt, wird der Wert ignoriert.
	 * 
	 * @param bSplineOrdnung
	 *            die neue Ordnung des B-Spline.
	 */
	public void setBSplineOrdnung(byte bSplineOrdnung) {
		this.bSplineOrdnung = bSplineOrdnung;
		approximationAktuell = false;
	}

	/**
	 * Legt den Ereignistyp der Ganglinie fest.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der öffentlichen API und
	 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
	 * 
	 * @param ereignisTyp
	 *            PID des Ereignistyp
	 */
	public void setEreignisTyp(EreignisTyp ereignisTyp) {
		this.ereignisTyp = ereignisTyp;
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
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der öffentlichen API und
	 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
	 * 
	 * @param letzteVerschmelzung
	 *            die neue Anzahl der Verschmelzungen.
	 */
	public void setLetzteVerschmelzung(long letzteVerschmelzung) {
		this.letzteVerschmelzung = letzteVerschmelzung;
	}

	/**
	 * Legt den Messquerschnitt fest, auf den sich die Ganglinie bezieht.
	 * 
	 * @param messQuerschnitt
	 *            ein Messquerschnitt.
	 */
	public void setMessQuerschnitt(MessQuerschnittAllgemein messQuerschnitt) {
		this.messQuerschnitt = messQuerschnitt;
	}

	/**
	 * Legt das Prognoseintervall fest. Die getter-Methoden für Stützstellen
	 * liefern nur Stützstellen innerhalb dieses Intervalls. Ist das
	 * Prognoseintervall gleich {@code null}, dann werden alle vorhanden
	 * Stützstellen berücksichtigt.
	 * 
	 * @param prognoseZeitraum
	 *            ein Intervall.
	 */
	public void setPrognoseZeitraum(Interval prognoseZeitraum) {
		this.prognoseZeitraum = prognoseZeitraum;
	}

	/**
	 * Kennzeichnet die Ganglinie als Referenzganglinie.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der öffentlichen API und
	 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
	 * 
	 * @param referenz
	 *            <code>true</code>, wenn diese Ganglinie eine
	 *            Referenzganglinie sein soll, sonst <code>false</code>
	 */
	public void setReferenz(boolean referenz) {
		this.referenz = referenz;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean setStuetzstelle(long zeitstempel, Messwerte wert) {
		boolean neu;

		neu = qKfz.setStuetzstelle(zeitstempel, wert.getQKfz());
		qLkw.setStuetzstelle(zeitstempel, wert.getQLkw());
		vPkw.setStuetzstelle(zeitstempel, wert.getVPkw());
		vLkw.setStuetzstelle(zeitstempel, wert.getVLkw());
		approximationAktuell = false;
		return neu;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean setStuetzstelle(Stuetzstelle<Messwerte> s) {
		boolean neu;

		neu = setStuetzstelle(s.getZeitstempel(), s.getWert());
		approximationAktuell = false;
		return neu;
	}

	/**
	 * Ersetzt die St&uuml;tzstellen der Ganglinie durch neue.
	 * 
	 * @param stuetzstellen
	 *            die neuen St&uuml;tzstellen der Ganglinie.
	 */
	public void setStuetzstellen(
			Collection<Stuetzstelle<Messwerte>> stuetzstellen) {
		qKfz.removeAll();
		qLkw.removeAll();
		vPkw.removeAll();
		vLkw.removeAll();
		for (Stuetzstelle<Messwerte> s : stuetzstellen) {
			qKfz.setStuetzstelle(s.getZeitstempel(), s.getWert().getQKfz());
			qLkw.setStuetzstelle(s.getZeitstempel(), s.getWert().getQLkw());
			vPkw.setStuetzstelle(s.getZeitstempel(), s.getWert().getVPkw());
			vLkw.setStuetzstelle(s.getZeitstempel(), s.getWert().getVLkw());
		}
	}

	/**
	 * Legt den Ganglinientyp fest.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der öffentlichen API und
	 * sollte nicht außerhalb der Ganglinie-API verwendet werden.
	 * 
	 * @param typ
	 *            der Typ der Ganglinie.
	 */
	public void setTyp(int typ) {
		this.typ = typ;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String result;

		result = getClass().getSimpleName() + "[";
		result += "messQuerschnitt=" + messQuerschnitt;
		result += ", ereignisTyp=" + ereignisTyp;
		result += ", referenz=" + referenz;
		result += ", anzahlVerschmelzungen=" + anzahlVerschmelzungen;
		result += ", letzteVerschmelzung="
				+ DateFormat.getDateInstance().format(
						new Date(letzteVerschmelzung));
		result += ", typ=" + typ;
		result += ", approximationAktuell=" + isApproximationAktuell();
		result += ", approximationDaK=" + getApproximationDaK();
		result += ", approximation=" + getApproximation();
		result += ", prognoseZeitraum=" + prognoseZeitraum;
		result += ", Anzahl Stützstellen=" + anzahlStuetzstellen();
		if (anzahlStuetzstellen() > 0) {
			List<Stuetzstelle<Double>> liste;

			liste = qKfz.getStuetzstellen();
			result += ", erste Stützstelle=" + liste.get(0);
			result += ", letzte Stützstelle=" + liste.get(liste.size() - 1);
		}
		result += "]";
		return result;
	}

}
