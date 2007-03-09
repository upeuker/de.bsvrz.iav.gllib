package de.iav.gllib.gllib;

import java.util.TreeSet;

import de.sys.funclib.i18n.Messages;


/**
 * Implementiert nur die Property <code>Ganglinie</code> der Schnittstelle
 * 
 * @author BitCtrl, Schumann
 * @version $Id: AbstractApproximation.java 160 2007-02-23 15:09:31Z Schumann $
 */
public abstract class AbstractApproximation implements Approximation {	
	
	private final Ganglinie ganglinie;
	
	
	/**
	 * Konstruiert eine Approximation und ordnet ihr eine Ganglinie zu
	 * 
	 * @param ganglinie Eine Ganglinie 
	 */
	public AbstractApproximation(Ganglinie ganglinie) {
		this.ganglinie = ganglinie;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Ganglinie getGanglinie() {
		return ganglinie;
	}
	
	/* Bestimmt, falls vorhanden, die St&uuml;tzstellen direkt vor und nach einem
	 * bestimmten Zeitstempel */
	protected Stuetzstelle[] getNextStuetzstellen(long zeitstempel) {
		Stuetzstelle sp = new Stuetzstelle(zeitstempel, null);
		Stuetzstelle s1; // St&uuml;tzstelle vor Zeitstempel
		Stuetzstelle s2; // St&uuml;tzstelle am oder nach Zeitstempel
		Stuetzstelle result[] = new Stuetzstelle[0];
		
		// Sonderfall: keine St&uuml;tzstellen oder Zeitstempel au&szlig;erhalb Ganglinie
		if (!ganglinie.contains(zeitstempel)) {
			return result;
		}
		
		s2 = ganglinie.tailSet(sp).first();
		if (s2.getZeitstempel() == zeitstempel) {
			// Zeitstempel f&auml;llt genau auf eine St&uuml;tzstelle
			result = new Stuetzstelle[1];
			result[0] = s2;
		} else {
			// Zeitstempel liegt zwischen zwei St&uuml;tzstellen
			result = new Stuetzstelle[2];
			s1 = ganglinie.headSet(sp).last();
			result[0] = s1;
			result[1] = s2; 
		}
		
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public TreeSet<Stuetzstelle> getInterpolation(int anzahlIntervalle) {
		assert anzahlIntervalle > 0 :
			Messages.get(GlLibMessages.Common_BadCount, anzahlIntervalle);
	
		TreeSet<Stuetzstelle> result = new TreeSet<Stuetzstelle>();
		long start;
		long intervall;
		Stuetzstelle s;
		long zeitstempel;

		// Sonderfall: keine St&uuml;tzstellen vorhanden
		if (getGanglinie().size() == 0) {
			return result;
		}
			
		start = getGanglinie().first().getZeitstempel();
		intervall = getGanglinie().last().getZeitstempel() - start;
		for (int i = 0; i < anzahlIntervalle+1; i++) {
			zeitstempel = start + i * intervall;
			s = new Stuetzstelle(zeitstempel, getWert(zeitstempel).getWert());
			result.add(s);
			zeitstempel += intervall;
		}
		
		return result;
	}
	
}
