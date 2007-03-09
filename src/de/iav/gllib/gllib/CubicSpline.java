package de.iav.gllib.gllib;


/**
 * Approximation einer Ganglinie mit Hilfe eines Cubic-Splines
 * 
 * @author BitCtrl, Schumann
 * @version $Id: CubicSpline.java 31 2006-12-11 09:27:45Z Schumann $
 */
public class CubicSpline extends AbstractApproximation {

	/**
	 * @see AbstractApproximation
	 * @param ganglinie
	 */
	public CubicSpline(Ganglinie ganglinie) {
		super(ganglinie);
	}

	/**
	 * {@inheritDoc}
	 */
	public Stuetzstelle getWert(long zeitstempel) {
		// TODO Automatisch erstellter Methoden-Stub
		return null;
	}

	
}
