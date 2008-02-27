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
 * Wei�enfelser Stra�e 67
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
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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
 * F�r Messquerschnitte angepasste Ganglinie. Die vier Verkehrswerte QKfz, QLkw,
 * VPkw und VLkw werden als Gruppe pro Zeitstempel gesichert. Aus diesen Werten
 * lassen sich die drei davon abh�ngigen Gr��e QPkw, VKfz und QB berechnen.
 * 
 * @author BitCtrl Systems GmbH, Falko Schumann
 * @version $Id$
 */
public class GanglinieMQ extends TreeMap<Long, Messwerte> implements
		IGanglinie<Messwerte> {

	/** Die Attributgruppe, in der historische Ganglinien gesichert werden. */
	public static final String ATG_GANGLINIE = "atg.ganglinie";

	/** Datenkatalogkonstante f�r die unbestimmte Approximation. */
	public static final int APPROX_UNBESTIMMT = 0;

	/** Datenkatalogkonstante f�r einen B-Spline. */
	public static final int APPROX_BSPLINE = 1;

	/** Datenkatalogkonstante f�r einen Cubic-Spline. */
	public static final int APPROX_CUBICSPLINE = 2;

	/** Datenkatalogkonstante f�r eine Polylinie. */
	public static final int APPROX_POLYLINE = 3;

	/** Standardordung der Approximation. Nur f�r B-Spline relevant. */
	public static final byte APPROX_STANDARD_ORDNUNG = 5;

	/** Datenkatalogkonstante f�r eine absolute Ganglinie. */
	public static final int TYP_ABSOLUT = 0;

	/** Datenkatalogkonstante f�r eine relative additive Ganglinie. */
	public static final int TYP_ADDITIV = 1;

	/** Datenkatalogkonstante f�r eine relative multiplikative Ganglinie. */
	public static final int TYP_MULTIPLIKATIV = 2;

	/** Die Eigenschaft {@code serialVersionUID}. */
	private static final long serialVersionUID = 0;

	// /** Ganglinie f�r QKfz. */
	// Ganglinie qKfz;
	//
	// /** Ganglinie f�r QLkw. */
	// Ganglinie qLkw;
	//
	// /** Ganglinie f�r VPkw. */
	// Ganglinie vPkw;
	//
	// /** Ganglinie f�r VLkw. */
	// Ganglinie vLkw;

	/** Der Messquerschnitt, zu dem die Ganglinie geh�rt. */
	private MessQuerschnittAllgemein messQuerschnitt;

	/** Parameter f�r die Berechnung von QB. Standard ist 2,0. */
	private float k1 = 2.0f;

	/** Parameter f�r die Berechnung von QB. Standard ist 0,01. */
	private float k2 = 0.01f;

	/** Zeitpunkt der letzten Verschmelzung. */
	private long letzteVerschmelzung;

	/** Anzahl der Verschmelzung mit anderen Ganglinien. */
	private long anzahlVerschmelzungen;

	/** Identifier f�r das mit der Ganglinie verkn�pfte Ereignis. */
	private EreignisTyp ereignisTyp;

	/** Flag, ob die Ganglinie eine Referenzganglinie darstellt. */
	private boolean referenz;

	/** Typ der Ganglinie. Standard ist {@link #TYP_ABSOLUT}. */
	private int typ = TYP_ABSOLUT;

	// Die folgenden vier Ganglinien m�ssen identische St�tzstellen besitzen,
	// was deren Zeitpunkt angeht. Unter Einbehaltung dieser Vorgabe, wird die
	// Ganglinie f�r QKfz stellvertretend f�r alle vier verwendet, wenn nur die
	// Zeitstempel der St�tzstellen relevant sind.

	/** Art der Approximation. Standard ist {@link #APPROX_BSPLINE}. */
	private int approximationDaK = APPROX_BSPLINE;

	/** Ordnung des B-Spline. Standard ist {@link #APPROX_STANDARD_ORDNUNG}. */
	private int bSplineOrdnung = APPROX_STANDARD_ORDNUNG;

	/** Das Intervall f�r das die Ganglinie prognostiziert wird. */
	private Interval prognoseZeitraum;

	/**
	 * Konstruiert eine Ganglinie ohne St�tzstellen.
	 */
	public GanglinieMQ() {
		// nix
	}

	/**
	 * Kopierkonstruktor.
	 * 
	 * @param stuetzstellen
	 *            die St�tzstellen aus denen die Ganglinie bestehen soll.
	 */
	public GanglinieMQ(Collection<Stuetzstelle<Messwerte>> stuetzstellen) {
		this();
		for (Stuetzstelle<Messwerte> s : stuetzstellen) {
			put(s.getZeitstempel(), s.getWert());
		}
	}

	/**
	 * Kopierkonstruktor.
	 * 
	 * @param stuetzstellen
	 *            die St�tzstellen aus denen die Ganglinie bestehen soll.
	 */
	public GanglinieMQ(SortedMap<Long, Messwerte> stuetzstellen) {
		this();
		putAll(stuetzstellen);
	}

	/**
	 * Macht nichts.
	 * 
	 * {@inheritDoc}
	 */
	public void aktualisiereApproximation() {
		// nix
	}

	/**
	 * {@inheritDoc}
	 */
	public int anzahlStuetzstellen() {
		return size();
	}

	/**
	 * Kopiert die St&uumltzstellen, das Approximationsverfahren und alle
	 * anderen Eigenschaften bis auf {@code approximationAktuell}. Der Wert f�r
	 * {@code approximationAktuell} wird auf false gesetzt.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public GanglinieMQ clone() {
		GanglinieMQ g;

		g = new GanglinieMQ(this);
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

		return g;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsStuetzstelle(long zeitstempel) {
		return containsKey(zeitstempel);
	}

	/**
	 * Gibt die Anzahl der bisherigen Verschmelzungen beim automatischen Lernen
	 * zur�ck.
	 * 
	 * @return Anzahl bisheriger Verschmelzungen
	 */
	public long getAnzahlVerschmelzungen() {
		return anzahlVerschmelzungen;
	}

	/**
	 * Gibt eine Approximation ohne St�tzstellen zur�ck. Der Typ der
	 * Approximation ist der, der gesetzt wurde. Die Approximation kann jedoch
	 * nicht zum bestimmen zum St�tzstellen verwendet werden.
	 * 
	 * {@inheritDoc}
	 * 
	 * @deprecated Die Approximation der einzelnen Gr��en kann mit
	 *             {@code getGanglinie*.getApproximation()} abgerufen werden.
	 */
	@Deprecated
	public Approximation getApproximation() {
		switch (approximationDaK) {
		case APPROX_BSPLINE:
			return new BSpline(bSplineOrdnung);
		case APPROX_CUBICSPLINE:
			return new CubicSpline();
		case APPROX_POLYLINE:
			return new Polyline();
		default:
			return new BSpline((byte) 5);
		}
	}

	/**
	 * Gibt die Art der Approximation als Datenkatalogkonstante zur�ck.
	 * 
	 * @return eine der Konstante {@link #APPROX_POLYLINE},
	 *         {@link #APPROX_CUBICSPLINE}, {@link #APPROX_BSPLINE} oder
	 *         {@link #APPROX_UNBESTIMMT}.
	 */
	public int getApproximationDaK() {
		return approximationDaK;
	}

	/**
	 * Gibt die Ordnung des B-Spline zur�ck. Wird zur Approximation kein
	 * B-Spline benutzt, wird der Wert ignoriert.
	 * 
	 * @return die Ordnung des B-Spline.
	 */
	public int getBSplineOrdnung() {
		return bSplineOrdnung;
	}

	/**
	 * Gibt den Ereignistyp der Ganglinie zur�ck.
	 * 
	 * @return der Ereignistyp.
	 */
	public EreignisTyp getEreignisTyp() {
		return ereignisTyp;
	}

	/**
	 * Gibt die Ganglinie f�r QB zur�ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St�tzstellen von
	 *         QB.
	 */
	public Ganglinie getGanglinieQB() {
		Ganglinie g;

		g = new Ganglinie();
		for (Map.Entry<Long, Messwerte> e : entrySet()) {
			g.setStuetzstelle(e.getKey(), e.getValue().getQB());
		}
		g.setApproximation(getApproximation());

		return g;
	}

	/**
	 * Gibt die Ganglinie f�r QKfz zur�ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St�tzstellen von
	 *         QKfz.
	 */
	public Ganglinie getGanglinieQKfz() {
		Ganglinie g;

		g = new Ganglinie();
		for (Map.Entry<Long, Messwerte> e : entrySet()) {
			g.setStuetzstelle(e.getKey(), e.getValue().getQKfz());
		}
		g.setApproximation(getApproximation());

		return g;
	}

	/**
	 * Gibt die Ganglinie f�r QLkw zur�ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St�tzstellen von
	 *         QLkw.
	 */
	public Ganglinie getGanglinieQLkw() {
		Ganglinie g;

		g = new Ganglinie();
		for (Map.Entry<Long, Messwerte> e : entrySet()) {
			g.setStuetzstelle(e.getKey(), e.getValue().getQLkw());
		}
		g.setApproximation(getApproximation());

		return g;
	}

	/**
	 * Gibt die Ganglinie f�r QPkw zur�ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St�tzstellen von
	 *         QPkw.
	 */
	public Ganglinie getGanglinieQPkw() {
		Ganglinie g;

		g = new Ganglinie();
		for (Map.Entry<Long, Messwerte> e : entrySet()) {
			g.setStuetzstelle(e.getKey(), e.getValue().getQPkw());
		}
		g.setApproximation(getApproximation());

		return g;
	}

	/**
	 * Gibt die Ganglinie f�r VKfz zur�ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St�tzstellen von
	 *         VKfz.
	 */
	public Ganglinie getGanglinieVKfz() {
		Ganglinie g;

		g = new Ganglinie();
		for (Map.Entry<Long, Messwerte> e : entrySet()) {
			g.setStuetzstelle(e.getKey(), e.getValue().getVKfz());
		}
		g.setApproximation(getApproximation());

		return g;

	}

	/**
	 * Gibt die Ganglinie f�r VLkw zur�ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St�tzstellen von
	 *         VLkw.
	 */
	public Ganglinie getGanglinieVLkw() {
		Ganglinie g;

		g = new Ganglinie();
		for (Map.Entry<Long, Messwerte> e : entrySet()) {
			g.setStuetzstelle(e.getKey(), e.getValue().getVLkw());
		}
		g.setApproximation(getApproximation());

		return g;
	}

	/**
	 * Gibt die Ganglinie f�r QPkw zur�ck.
	 * 
	 * @return eine einfache mathematische Ganglinie mit den St�tzstellen von
	 *         QPkw.
	 */
	public Ganglinie getGanglinieVPkw() {
		Ganglinie g;

		g = new Ganglinie();
		for (Map.Entry<Long, Messwerte> e : entrySet()) {
			g.setStuetzstelle(e.getKey(), e.getValue().getVPkw());
		}
		g.setApproximation(getApproximation());

		return g;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #setPrognoseZeitraum(Interval)
	 */
	public Interval getIntervall() {
		if (prognoseZeitraum != null) {
			return prognoseZeitraum;
		}

		if (isEmpty()) {
			return null;
		}
		return new Interval(firstKey(), lastKey());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #setPrognoseZeitraum(Interval)
	 */
	public List<Interval> getIntervalle() {
		throw new UnsupportedOperationException("Es m�ssen die Intervalle der "
				+ "einzelnen Ganglinien f�r Q, V und QB abgefragt werden.");
	}

	/**
	 * Gibt einen Parameter f�r die Berechnung von QB zur�ck.
	 * 
	 * @return der Parameter.
	 */
	public float getK1() {
		return k1;
	}

	/**
	 * Gibt einen Parameter f�r die Berechnung von QB zur�ck.
	 * 
	 * @return der Parameter
	 */
	public float getK2() {
		return k2;
	}

	/**
	 * Gibt den Zeitpunkt der letzten Verschmelzung als Zeitstempel zur�ck.
	 * 
	 * @return Zeitstempel
	 */
	public long getLetzteVerschmelzung() {
		return letzteVerschmelzung;
	}

	/**
	 * Gibt den Messquerschnitt der Ganglinie zur�ck.
	 * 
	 * @return ein Messquerschnitt.
	 */
	public MessQuerschnittAllgemein getMessQuerschnitt() {
		return messQuerschnitt;
	}

	/**
	 * Gibt das Prognoseintervall der Ganglinie zur�ck.
	 * 
	 * @return das Prognoseintervall.
	 */
	public Interval getPrognoseIntervall() {
		return prognoseZeitraum;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #setPrognoseZeitraum(Interval)
	 */
	public Stuetzstelle<Messwerte> getStuetzstelle(long zeitstempel) {
		final Ganglinie gQKfz, gQLkw, gVPkw, gVLkw;

		if (prognoseZeitraum != null && !prognoseZeitraum.contains(zeitstempel)) {
			// Zeitstempel liegt nicht innerhalb der Prognoseganglinie
			return new Stuetzstelle<Messwerte>(zeitstempel, new Messwerte(null,
					null, null, null, k1, k2));
		}

		gQKfz = getGanglinieQKfz();
		gQKfz.aktualisiereApproximation();
		gQLkw = getGanglinieQLkw();
		gQLkw.aktualisiereApproximation();
		gVPkw = getGanglinieVPkw();
		gVPkw.aktualisiereApproximation();
		gVLkw = getGanglinieVLkw();
		gVLkw.aktualisiereApproximation();

		return new Stuetzstelle<Messwerte>(zeitstempel, new Messwerte(gQKfz
				.get(zeitstempel), gQLkw.get(zeitstempel), gVPkw
				.get(zeitstempel), gVLkw.get(zeitstempel), k1, k2));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #setPrognoseZeitraum(Interval)
	 */
	public List<Stuetzstelle<Messwerte>> getStuetzstellen() {
		List<Stuetzstelle<Messwerte>> liste;

		liste = new ArrayList<Stuetzstelle<Messwerte>>();
		for (long t : keySet()) {
			if (prognoseZeitraum != null && !prognoseZeitraum.contains(t)) {
				continue;
			}

			liste.add(new Stuetzstelle<Messwerte>(t, get(t)));
		}

		return liste;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #setPrognoseZeitraum(Interval)
	 */
	public List<Stuetzstelle<Messwerte>> getStuetzstellen(Interval intervall) {
		SortedMap<Long, Messwerte> menge;
		List<Stuetzstelle<Messwerte>> liste;

		menge = subMap(intervall.getStart(), intervall.getEnd() + 1);
		liste = new ArrayList<Stuetzstelle<Messwerte>>();
		for (long t : menge.keySet()) {
			if (prognoseZeitraum != null && !prognoseZeitraum.contains(t)) {
				continue;
			}

			liste.add(getStuetzstelle(t));
		}

		return liste;
	}

	/**
	 * Gibt den Ganglinientyp zur�ck.
	 * 
	 * @return der Typ der Ganglinie.
	 */
	public int getTyp() {
		return typ;
	}

	/**
	 * Bestimmt, ob die Ganglinie absolut oder relativ ist. F�r eine relative
	 * Ganglinie wird nicht zwischen additiv und multiplikativ unterschieden.
	 * 
	 * @return {@code true}, wenn die Ganglinie absolut ist.
	 */
	public boolean isAbsolut() {
		return typ == TYP_ABSOLUT;
	}

	/**
	 * Gibt immer {@code true} zur�ck.
	 * 
	 * {@inheritDoc}
	 */
	public boolean isApproximationAktuell() {
		return true;
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
	 * 
	 * @deprecated Es muss die Methode {@code getGanglinie*.isValid()} verwendet
	 *             werden.
	 */
	@Deprecated
	public boolean isValid(Interval intervall) {
		throw new UnsupportedOperationException(
				"Es m�ssen die Methode isValid() an den "
						+ "einzelnen Ganglinien f�r Q, V und QB abgefragt werden.");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Es muss die Methode {@code getGanglinie*.isValid()} verwendet
	 *             werden.
	 */
	@Deprecated
	public boolean isValid(long zeitstempel) {
		throw new UnsupportedOperationException(
				"Es m�ssen die Methode isValid() an den "
						+ "einzelnen Ganglinien f�r Q, V und QB abgefragt werden.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove(long zeitstempel) {
		remove(zeitstempel);
	}

	/**
	 * Legt die Anzahl der bisherigen Verschmelzungen fest.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der �ffentlichen API und
	 * sollte nicht au�erhalb der Ganglinie-API verwendet werden.
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
		if (approximationDaK != APPROX_BSPLINE
				&& approximationDaK != APPROX_CUBICSPLINE
				&& approximationDaK != APPROX_POLYLINE
				&& approximationDaK != APPROX_UNBESTIMMT) {
			this.approximationDaK = APPROX_UNBESTIMMT;
		} else {
			this.approximationDaK = approximationDaK;
		}
	}

	/**
	 * Legt die Ordnung des B-Spline fest. Wird zur Approximation kein B-Spline
	 * benutzt, wird der Wert ignoriert.
	 * 
	 * @param bSplineOrdnung
	 *            die neue Ordnung des B-Spline.
	 */
	public void setBSplineOrdnung(int bSplineOrdnung) {
		this.bSplineOrdnung = bSplineOrdnung;
	}

	/**
	 * Legt den Ereignistyp der Ganglinie fest.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der �ffentlichen API und
	 * sollte nicht au�erhalb der Ganglinie-API verwendet werden.
	 * 
	 * @param ereignisTyp
	 *            PID des Ereignistyp
	 */
	public void setEreignisTyp(EreignisTyp ereignisTyp) {
		this.ereignisTyp = ereignisTyp;
	}

	/**
	 * Legt den Parameter k1 f�r die Berechnung von QB fest.
	 * 
	 * @param k1
	 *            der parameter k1
	 */
	public void setK1(float k1) {
		final SortedMap<Long, Messwerte> neu;

		this.k1 = k1;

		neu = new TreeMap<Long, Messwerte>();
		for (Map.Entry<Long, Messwerte> e : entrySet()) {
			neu.put(e.getKey(), new Messwerte(e.getValue().getQKfz(), e
					.getValue().getQLkw(), e.getValue().getVPkw(), e.getValue()
					.getVLkw(), k1, k2));
		}
		clear();
		putAll(neu);
	}

	/**
	 * Legt den Parameter k2 f�r die Berechnung von QB fest.
	 * 
	 * @param k2
	 *            der parameter k2
	 */
	public void setK2(float k2) {
		final SortedMap<Long, Messwerte> neu;

		this.k2 = k2;

		neu = new TreeMap<Long, Messwerte>();
		for (Map.Entry<Long, Messwerte> e : entrySet()) {
			neu.put(e.getKey(), new Messwerte(e.getValue().getQKfz(), e
					.getValue().getQLkw(), e.getValue().getVPkw(), e.getValue()
					.getVLkw(), k1, k2));
		}
		clear();
		putAll(neu);
	}

	/**
	 * Legt die Anzahl der bisherigen Verschmelzungen fest.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der �ffentlichen API und
	 * sollte nicht au�erhalb der Ganglinie-API verwendet werden.
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
	 * Legt das Prognoseintervall fest. Die getter-Methoden f�r St�tzstellen
	 * liefern nur St�tzstellen innerhalb dieses Intervalls. Ist das
	 * Prognoseintervall gleich {@code null}, dann werden alle vorhanden
	 * St�tzstellen ber�cksichtigt.
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
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der �ffentlichen API und
	 * sollte nicht au�erhalb der Ganglinie-API verwendet werden.
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

		neu = put(zeitstempel, wert) == null;
		return neu;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean setStuetzstelle(Stuetzstelle<Messwerte> s) {
		boolean neu;

		neu = setStuetzstelle(s.getZeitstempel(), s.getWert());
		return neu;
	}

	/**
	 * Legt den Ganglinientyp fest.
	 * <p>
	 * <em>Hinweis:</em> Diese Methode ist nicht Teil der �ffentlichen API und
	 * sollte nicht au�erhalb der Ganglinie-API verwendet werden.
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
		result += ", Anzahl St�tzstellen=" + anzahlStuetzstellen();
		if (anzahlStuetzstellen() > 0) {
			List<Stuetzstelle<Messwerte>> liste;

			liste = getStuetzstellen();
			result += ", erste St�tzstelle=" + liste.get(0);
			result += ", letzte St�tzstelle=" + liste.get(liste.size() - 1);
		}
		result += "]";
		return result;
	}

}
