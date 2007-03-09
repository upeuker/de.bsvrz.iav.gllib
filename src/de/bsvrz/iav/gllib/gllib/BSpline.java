package de.bsvrz.iav.gllib.gllib;

import de.bsvrz.sys.funclib.i18n.Messages;


/**
 * Approximation einer Ganglinie mit Hilfe eines B-Splines beliebiger Ordung
 * 
 * @author BitCtrl, Schumann
 * @version $Id: BSpline.java 160 2007-02-23 15:09:31Z Schumann $
 */
public class BSpline extends AbstractApproximation {
	
	private int ordnung;
	
	
	/**
	 * Erstellt einen B-Spline der Ordung 5 
	 * 
	 * @see AbstractApproximation
	 * @param ganglinie 
	 */
	public BSpline(Ganglinie ganglinie) {
		super(ganglinie);
		setOrdnung(5);
	}
	
	/**
	 * Erstellt einen B-Spline beliebiger Ordnung
	 * 
	 * @see AbstractApproximation
	 * @param ganglinie
	 * @param ordnung Ordnung
	 */
	public BSpline(Ganglinie ganglinie, int ordnung) {
		super(ganglinie);
		setOrdnung(ordnung);
	}

	/**
	 * {@inheritDoc}
	 */
	public Stuetzstelle getWert(long zeitstempel) {
		// TODO Automatisch erstellter Methoden-Stub
		return null;
	}

	/** 
	 * Gibt die Ordgung des B-Splines zur&uuml;ck
	 * 
	 * @return Ordnung
	 */
	public int getOrdnung() {
		return ordnung;
	}

	/** 
	 * Legt die Ordnung des B-Splines fest
	 *  
	 * @param ordnung Ordung
	 */
	public void setOrdnung(int ordnung) {
		assert ordnung > 0 :
			Messages.get(GlLibMessages.Ganglinie_BadBSplineDegree, ordnung);
		
		this.ordnung = ordnung;
	}

}
