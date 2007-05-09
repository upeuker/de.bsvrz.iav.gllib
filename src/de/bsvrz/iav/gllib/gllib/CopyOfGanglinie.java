/**
 * 
 */
package de.bsvrz.iav.gllib.gllib;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import de.bsvrz.sys.funclib.bitctrl.i18n.Messages;

/**
 * Repr&auml;sentiert eine allgemeine Ganglinie, bestehend aus einer sortierten
 * Menge von St&uuml;tzstellen und der Angabe eines Interpolationsverfahren.
 * Wird kein Approximationsverfahren festgelegt, wird ein
 * {@link BSpline B-Spline} mit Standardordnung angenommen.
 * 
 * @author BitrCtrl, Schumann
 * @version $Id$
 */
public class CopyOfGanglinie extends TreeSet<Stuetzstelle> {

	/**
	 * Typ einer Ganglinie. Eine Ganglinie kann absolut oder relativ sein. Eine
	 * relative Ganglinie kann wiederum additiv oder multiplikativ sein.
	 * 
	 * @author BitCtrl, Schumann
	 * @version $Id$
	 */
	public enum Typ {
		/** Eine absolute Ganglinie */
		ABSOLUT,
		/** Eine realitive Ganglinie, die additiv ist */
		ADDITIV,
		/** Eine realitive Ganglinie, die multiplikativ ist */
		MULTIPLIKATIV
	}

	/** Verfahren zur Berechnung der Punkte zwischen den St&uuml;tzstellen */
	private Approximation approximation = new BSpline(null);

	/** Der Typ der Ganglinie */
	private Typ typ;

	/**
	 * Legt die zu verwendende Approximation fest
	 * 
	 * @param approximation
	 *            Objekt einer Approximation
	 */
	public void setApproximation(Approximation approximation) {
		//if (approximation.getGanglinie() != this) {
		//	throw new IllegalArgumentException();
		//}

		this.approximation = approximation;
	}

	/**
	 * Legt die zu verwendende Approximation
	 * 
	 * @param approximation
	 *            Objekt einer Approximation
	 */
	public void setApproximation(Class<Approximation> approximation) {
		Constructor<Approximation> c;

		try {
			c = approximation.getDeclaredConstructor(CopyOfGanglinie.class);
		} catch (NoSuchMethodException e) {
			// TODO Automatisch erstellter Catch-Block
			e.printStackTrace();
			return;
		}

		try {
			this.approximation = c.newInstance(this);
		} catch (InstantiationException e) {
			// TODO Automatisch erstellter Catch-Block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Automatisch erstellter Catch-Block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Automatisch erstellter Catch-Block
			e.printStackTrace();
		}
	}

	/**
	 * Gibt eine Approximation der Ganglinie nach dem festgelegten Verfahren
	 * zur&uuml;ck
	 * 
	 * @return Approximation
	 */
	public Approximation getApproximation() {
		return approximation;
	}

	/**
	 * Gibt den Typ der Ganglinie zur&uuml;ck
	 * 
	 * @return Typ
	 */
	public Typ getTyp() {
		return typ;
	}

	/**
	 * Legt den Typ der Ganglinie fest
	 * 
	 * @param typ
	 *            Typ
	 */
	public void setTyp(Typ typ) {
		this.typ = typ;
	}

	/**
	 * Gibt das Zeitintervall der Ganglinie zur&uuml;ck
	 * 
	 * @return Ein {@link Intervall} oder <code>null</code>, wenn keine
	 *         Stz&uuml;tzstellen vorhanden sind
	 */
	public Intervall getIntervall() {
		try {
			return new Intervall(first().getZeitstempel(), last()
					.getZeitstempel());
		} catch (NoSuchElementException ex) {
			return null;
		}
	}

	/**
	 * Pr&uuml;ft ob ein Zeitstempel im Definitionsbereich der Ganglinie liegt
	 * 
	 * @param zeitstempel
	 *            zu pr&uuml;fender Zeitstempel
	 * @return <code>true</code>, wenn <code>zeitstempel</code> zwischen
	 *         den Zeitstempeln der ersten und letzten St&uuml;tzstelle liegt,
	 *         sonst <code>false</code>
	 */
	public boolean contains(long zeitstempel) {
		if (size() == 0) {
			return false;
		}

		return (first().getZeitstempel() <= zeitstempel && zeitstempel <= last()
				.getZeitstempel());
	}

	/**
	 * Addiert eine Ganglinie zu der dieser
	 * 
	 * @param ganglinie
	 *            Summand
	 */
	public void addiere(CopyOfGanglinie ganglinie) {
		// TODO Automatisch erstellter Methoden-Stub
	}

	/**
	 * Bildet den Mittelwert dieser Ganglinie mit einer anderen
	 * 
	 * @param ganglinie
	 *            Ganglinie
	 */
	public void mittelwert(CopyOfGanglinie ganglinie) {
		// TODO Automatisch erstellter Methoden-Stub
	}

	/**
	 * Berechnet den Abstand dieser Ganglinie zu einer anderen mit Hilfe des
	 * Basisabstandsverfahren
	 * 
	 * @param ganglinie
	 *            Ganglinie
	 * @return Abstand
	 */
	public Float basisAbstand(CopyOfGanglinie ganglinie) {
		// TODO Automatisch erstellter Methoden-Stub
		float abstand = 0;

		assert (0 <= abstand && abstand <= 1);

		return abstand;
	}

	/**
	 * Verkn&uuml;pft eine Ganglinie mit dieser
	 * 
	 * @param ganglinie
	 *            Ganglinie
	 */
	public void konkatenation(CopyOfGanglinie ganglinie) {
		// TODO Automatisch erstellter Methoden-Stub
	}

	/**
	 * Verkn&uuml;pft eine Menge von Ganglinien mit dieser
	 * 
	 * @param ganglinien
	 *            Ganglinien
	 */
	public void konkatenation(Collection<CopyOfGanglinie> ganglinien) {
		// TODO Automatisch erstellter Methoden-Stub
	}

	/**
	 * Schneidet ein Intervall aus der Ganglinie heraus
	 * 
	 * @param start
	 *            Startzeit
	 * @param end
	 *            Endezeit
	 */
	public void ausschneiden(long start, long end) {
		// TODO Automatisch erstellter Methoden-Stub
	}

	/**
	 * Schneidet ein Intervall aus der Ganglinie heraus
	 * 
	 * @param intervall
	 *            Intervall
	 */
	public void ausschneiden(Intervall intervall) {
		// TODO Automatisch erstellter Methoden-Stub
	}

	/**
	 * Dividiert diese Ganglinie durch eine andere
	 * 
	 * @param ganglinie
	 *            Divisor
	 */
	public void dividiere(CopyOfGanglinie ganglinie) {
		// TODO Automatisch erstellter Methoden-Stub
	}

	/**
	 * Berechnet den Abstand dieser Ganglinie zu einer anderen mit Hilfe des
	 * komplexen Abstandsverfahren
	 * 
	 * @param ganglinie
	 *            Ganglinie
	 * @return Abstand
	 */
	public Float komplexerAbstand(CopyOfGanglinie ganglinie) {
		// TODO Automatisch erstellter Methoden-Stub
		float abstand = 0;

		assert (0 <= abstand && abstand <= 1);

		return abstand;
	}

	/**
	 * Multipliziert diese Ganglinie mit einer anderen
	 * 
	 * @param ganglinie
	 *            Ganglinie
	 */
	public void multipliziere(CopyOfGanglinie ganglinie) {
		// TODO Automatisch erstellter Methoden-Stub
	}

	/**
	 * F&uuml;hrt ein Pattern-Matching dieser Ganglinie mit einer Liste von
	 * Ganglinie durch
	 * 
	 * @param ganglinien
	 *            Ganglinie
	 * @param offset
	 *            Offset, um den die Ganglinie zeitlich nach vorn und hinten
	 *            verschoben wird
	 * @param maxAbstand
	 *            Maximaler Abstand den eine Ganglinie beim Pattern-Matching
	 *            haben darf, wird dieser Wert &uuml;berschritten wird
	 *            <code>-1</code> zur&uuml;ckgegeben.
	 * @return Index der am besten passenden Ganglinie aus der Eingangsliste
	 *         oder <code>-1</code>
	 */
	public int patternMatching(CopyOfGanglinie ganglinien[], long offset,
			int maxAbstand) {
		// TODO Automatisch erstellter Methoden-Stub
		return -1;
	}

	/**
	 * Verschiebt die Ganglinie auf der Zeitachse
	 * 
	 * @param offset
	 *            Offset, negativ = in Richtung Vergangheit, postiv = in
	 *            Richtung Zukunft
	 */
	public void verschiebe(long offset) {
		// TODO Automatisch erstellter Methoden-Stub
	}

	/**
	 * Subtrahiert eine Ganglinie von dieser
	 * 
	 * @param ganglinie
	 *            Subtrahend
	 */
	public void subtrahiere(CopyOfGanglinie ganglinie) {
		// TODO Automatisch erstellter Methoden-Stub
	}

	protected void vervollstaendigeStuetzstellen() {
		// TODO Automatisch erstellter Methoden-Stub
	}

	/**
	 * Gibt Zeilenweise die St&uuml;tzstellen zur&uuml;ck
	 * 
	 * @see java.util.AbstractCollection#toString()
	 */
	@Override
	public String toString() {
		String result = ""; //$NON-NLS-1$

		for (Stuetzstelle s : this) {
			result += s + "\n"; //$NON-NLS-1$
		}

		return result;
	}
}
