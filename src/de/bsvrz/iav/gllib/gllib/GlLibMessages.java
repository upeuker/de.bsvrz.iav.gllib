package de.bsvrz.iav.gllib.gllib;

import java.util.ResourceBundle;

import de.bsvrz.sys.funclib.i18n.MessageHandler;

/**
 * Versorgt das Package de.bwl.rpt.ref95.iav, samt Subpackages, mit
 * lokalisierten Meldungen
 * 
 * @author BitCtrl, Schumann
 * @version $Id: IavMessage.java 160 2007-02-23 15:09:31Z Schumann $
 */
public enum GlLibMessages implements MessageHandler {

	/** Anzahl muss >0 sein, Argumente: Anzahl */
	Common_BadCount,

	/** Prozentzahl muss >=0 und <=1 sein, Argumente: Wert */
	Common_BadPercentage,

	/** Anfang muss vor Ende des Intervalls liegen, Argumente: Anfang, Ende */
	Common_BadIntervall,

	/** Zeitstempel muss >=0 sein, Argumente: Wert */
	Common_BadTimestamp,

	/** Zugeh&ouml;rigkeit muss >=0 und <=1 sein, Argumente: Wert */
	Fuzzy_BadMembership,

	/** Die Trapezecke des Fuzzy-Sets sind nicht aufsteigend sortiert. */
	Fuzzy_UnorderedTrapeziumEdges,

	/** Der Name eines Terms darf nicht null oder leer "" sein. */
	Fuzzy_BadTermName,

	/** Der Name einer Variable darf nicht null oder leer "" sein. */
	Fuzzy_BadVariableName,
	
	/** Fuzzy-Set ung&uuml;ltig  */
	Fuzzy_BadFuzzySetDefinitions,
	
	/** Fuzzy-Variable und linguistische Variable passen nicht zusammen **/
	Fuzzy_WrongFuzzyVariabble,

	/** B-Spline-Ordnung muss > 0 sein, Argumente: Ordnung */
	Ganglinie_BadBSplineDegree;

	private static final String BUNDLE_NAME = GlLibMessages.class.getCanonicalName();

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	/**
	 * @see de.bsvrz.sys.funclib.i18n.MessageHandler#getResourceBundle()
	 */
	public ResourceBundle getResourceBundle() {
		return RESOURCE_BUNDLE;
	}

}
