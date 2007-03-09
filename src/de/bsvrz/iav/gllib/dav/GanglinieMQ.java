package de.bsvrz.iav.gllib.dav;

import java.util.ArrayList;

import de.bsvrz.iav.gllib.gllib.Approximation;
import de.bsvrz.iav.gllib.gllib.Ganglinie;



/**
 * F&uuml;r Messquerschnitte angepasste Ganglinie. Die vier Verkehrswerte QKfz, QLkw,
 * VPkw und VLkw werden als Gruppe pro Zeitstempel gesichert. Aus diesen Werten
 * lassen sich die drei davon abh&auml;ngigen Gr&ouml;&szlig;e QPkw, VKfz und QB berechnen.
 * 
 * @author BitCtrl, Schumann
 * @version $Id: GanglinieMQ.java 160 2007-02-23 15:09:31Z Schumann $
 */
public class GanglinieMQ extends Ganglinie {

	private static final long serialVersionUID = 1L;
	private long letzteVerschmelzung;
	private long anzahlVerschmelzungen = 0;
	private String ereignisTyp;
	private boolean referenz;

	
	/**
	 * Gibt eine Approximation f&uuml;r QKfz zur&uuml;ck
	 * 
	 * @return Approximation
	 */
	public Approximation getApproximationQKfz() {
		return null;
	}
	
	/**
	 * Gibt eine Approximation f&uuml;r QPkw zur&uuml;ck
	 * 
	 * @return Approximation
	 */
	public Approximation getApproximationQPkw() {
		return null;
	}
	
	/**
	 * Gibt eine Approximation f&uuml;r QLkw zur&uuml;ck
	 * 
	 * @return Approximation
	 */
	public Approximation getApproximationQLkw() {
		return null;
	}
		
	/**
	 * Gibt eine Approximation f&uuml;r VKfz zur&uuml;ck
	 * 
	 * @return Approximation
	 */
	public Approximation getApproximationVKfz() {
		return null;
	}
	
	/**
	 * Gibt eine Approximation f&uuml;r VPkw zur&uuml;ck
	 * 
	 * @return Approximation
	 */
	public Approximation getApproximationVPkw() {
		return null;
	}
	
	/**
	 * Gibt eine Approximation f&uuml;r VLkw zur&uuml;ck
	 * 
	 * @return Approximation
	 */
	public Approximation getApproximationVLkw() {
		return null;
	}
	
	/**
	 * Gibt eine Approximation f&uuml;r QB zur&uuml;ck
	 * 
	 * @return Approximation
	 */
	public Approximation getApproximationQB() {
		return null;
	}

	/**
	 * Besitzt die Ganglinie die Auszeichnung als Referenz?
	 * 
	 * @return <code>true</code>, wenn diese Ganglinie eine Referenzganglinie
	 * ist, sonst <code>false</code>
	 */
	public boolean getReferenz() {
		return referenz;
	}

	/**
	 * Kennzeichnet die Ganglinie als Referenzganglinie
	 * 
	 * @param referenz <code>true</code>, wenn diese Ganglinie eine
	 * Referenzganglinie sein soll, sonst <code>false</code>
	 */
	public void setReferenz(boolean referenz) {
		this.referenz = referenz;
	}

	/**
	 * Gibt die Anzahl der bisherigen Verschmelzungen beim automatischen Lernen
	 * zur&uuml;ck
	 * 
	 * @return Anzahl bisheriger Verschmelzungen
	 */
	public long getAnzahlVerschmelzungen() {
		return anzahlVerschmelzungen;
	}

	void incAnzahlVerschmelzungen() {
		this.anzahlVerschmelzungen++;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mittelwert(Ganglinie ganglinie) {
		// TODO Automatisch erstellter Methoden-Stub
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Float basisAbstand(Ganglinie ganglinie) {
		// TODO Automatisch erstellter Methoden-Stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void konkatenation(Ganglinie ganglinie) {
		// TODO Automatisch erstellter Methoden-Stub
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addiere(Ganglinie ganglinie) {
		// TODO Automatisch erstellter Methoden-Stub
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void ausschneiden(long start, long end) {
		// TODO Automatisch erstellter Methoden-Stub
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dividiere(Ganglinie ganglinie) {
		// TODO Automatisch erstellter Methoden-Stub
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Float komplexerAbstand(Ganglinie ganglinie) {
		// TODO Automatisch erstellter Methoden-Stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void multipliziere(Ganglinie ganglinie) {
		// TODO Automatisch erstellter Methoden-Stub
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int patternMatching(Ganglinie ganglinien[], long offset, int maxAbstand) {
		// TODO Automatisch erstellter Methoden-Stub
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void verschiebe(long time) {
		// TODO Automatisch erstellter Methoden-Stub
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void subtrahiere(Ganglinie ganglinie) {
		// TODO Automatisch erstellter Methoden-Stub
	}

	/**
	 * Gibt den Zeitpunkt der letzten Verschmelzung als Zeitstempel zur&uuml;ck
	 * 
	 * @return Zeitstempel
	 */
	public long getLetzteVerschmelzung() {
		return letzteVerschmelzung;
	}

	/**
	 * Setzt den Zeitpunkt der letzten Verschmelzung
	 *  
	 * @param letzteVerschmelzung Zeitstempel
	 */
	void setLetzteVerschmelzung(long letzteVerschmelzung) {
		this.letzteVerschmelzung = letzteVerschmelzung;
	}

	/** 
	 * Gibt den Ereignistyp der Ganglinie zur&uuml;ck
	 * 
	 * @return PID des Ereignistyp
	 */
	public String getEreignisTyp() {
		return ereignisTyp;
	}

	/** 
	 * Legt den Ereignistyp der Ganglinie fest
	 * 
	 * @param ereignisTyp PID des Ereignistyp
	 */
	public void setEreignisTyp(String ereignisTyp) {
		this.ereignisTyp = ereignisTyp;
	}
	
}
