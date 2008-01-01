/**
 * 
 */
package de.bsvrz.iav.gllib.gllib.modell.onlinedaten;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.Data.Array;
import de.bsvrz.dav.daf.main.config.Aspect;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.DataModel;
import de.bsvrz.iav.gllib.gllib.dav.GlProgAnfrage;
import de.bsvrz.iav.gllib.gllib.modell.ApplikationGanglinienPrognose;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatum;
import de.bsvrz.sys.funclib.bitctrl.modell.AbstractOnlineDatensatz;
import de.bsvrz.sys.funclib.bitctrl.modell.Aspekt;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.systemmodellglobal.Applikation;

/**
 * @author Falko
 * 
 */
public class OdPrognoseGanglinienAnfrage extends
		AbstractOnlineDatensatz<OdPrognoseGanglinienAnfrage.Daten> {

	/**
	 * Die vorhandenen Aspekte des Datensatzes.
	 */
	public enum Aspekte implements Aspekt {

		/** Der Aspekt {@code asp.anfrage}. */
		Anfrage("asp.anfrage");

		/** Der Aspekt, den das enum kapselt. */
		private final Aspect aspekt;

		/**
		 * Erzeugt aus der PID den Aspekt.
		 * 
		 * @param pid
		 *            die PID eines Aspekts.
		 */
		private Aspekte(String pid) {
			DataModel modell = ObjektFactory.getInstanz().getVerbindung()
					.getDataModel();
			aspekt = modell.getAspect(pid);
			assert aspekt != null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see de.bsvrz.sys.funclib.bitctrl.modell.Aspekt#getAspekt()
		 */
		public Aspect getAspekt() {
			return aspekt;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see de.bsvrz.sys.funclib.bitctrl.modell.Aspekt#getName()
		 */
		public String getName() {
			return aspekt.getNameOrPidOrId();
		}

	}

	/**
	 * Kapselt die Daten des Datensatzes.
	 */
	public static class Daten extends AbstractDatum implements
			Collection<GlProgAnfrage> {

		/** Die anfragende Applikation. */
		private Applikation absender;

		/** Eine beliebige Zeichenkette die der Absender frei eingetragen kann. */
		private String absenderZeichen;

		/** Liste der Anfragen in dieser Nachricht. */
		private final List<GlProgAnfrage> anfragen = new ArrayList<GlProgAnfrage>();

		/** Das Flag f&uuml;r die G&uuml;ltigkeit des Datensatzes. */
		private boolean valid;

		/**
		 * @param o
		 *            eine Anfrage.
		 * @return {@code true}, wenn die Anfrage hinzugef&uuml;gt wurde.
		 * @see java.util.Collection#add(java.lang.Object)
		 */
		public boolean add(GlProgAnfrage o) {
			return anfragen.add(o);
		}

		/**
		 * @see java.util.Collection#addAll(java.util.Collection)
		 */
		public boolean addAll(Collection<? extends GlProgAnfrage> c) {
			return anfragen.addAll(c);
		}

		/**
		 * @see java.util.Collection#clear()
		 */
		public void clear() {
			anfragen.clear();

		}

		/**
		 * Es wird eine flache Kopie zur&uuml;ckgegeben.
		 * 
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#clone()
		 */
		@Override
		public Daten clone() {
			Daten klon = new Daten();

			klon.setZeitstempel(getZeitstempel());
			klon.valid = valid;
			klon.absender = absender;
			klon.absenderZeichen = absenderZeichen;
			klon.anfragen.addAll(anfragen);

			return klon;
		}

		/**
		 * @see java.util.Collection#contains(java.lang.Object)
		 */
		public boolean contains(Object o) {
			return anfragen.contains(o);
		}

		/**
		 * @see java.util.Collection#containsAll(java.util.Collection)
		 */
		public boolean containsAll(Collection<?> c) {
			return anfragen.containsAll(c);
		}

		/**
		 * Gibt die anfragende Applikation zur&uuml;ck.
		 * 
		 * @return die Applikation.
		 */
		public Applikation getAbsender() {
			return absender;
		}

		/**
		 * Gibt die Zeichenkette zur&uuml;ck, die der Absender in der Nachricht
		 * frei eintragen darf.
		 * 
		 * @return Eine Zeichenkette
		 */
		public String getAbsenderZeichen() {
			return absenderZeichen;
		}

		/**
		 * @see java.util.Collection#isEmpty()
		 */
		public boolean isEmpty() {
			return anfragen.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isValid() {
			return valid;
		}

		/**
		 * @see java.util.Collection#iterator()
		 */
		public Iterator<GlProgAnfrage> iterator() {
			return anfragen.iterator();
		}

		/**
		 * @see java.util.Collection#remove(java.lang.Object)
		 */
		public boolean remove(Object o) {
			return anfragen.remove(o);
		}

		/**
		 * @see java.util.Collection#removeAll(java.util.Collection)
		 */
		public boolean removeAll(Collection<?> c) {
			return anfragen.removeAll(c);
		}

		/**
		 * @see java.util.Collection#retainAll(java.util.Collection)
		 */
		public boolean retainAll(Collection<?> c) {
			return anfragen.retainAll(c);
		}

		/**
		 * Legt die anfragende Applikation fest.
		 * 
		 * @param absender
		 *            die anfragende Applikation.
		 */
		public void setAbsender(Applikation absender) {
			this.absender = absender;
		}

		/**
		 * Legt die Zeichenkette fest, die der Absender in der Nachricht frei
		 * eintragen darf.
		 * 
		 * @param absenderZeichen
		 *            die Zeichenkette.
		 */
		public void setAbsenderZeichen(String absenderZeichen) {
			this.absenderZeichen = absenderZeichen;
		}

		/**
		 * Setzt das Flag {@code valid} des Datum.
		 * 
		 * @param valid
		 *            der neue Wert des Flags.
		 */
		protected void setValid(boolean valid) {
			this.valid = valid;
		}

		/**
		 * @see java.util.Collection#size()
		 */
		public int size() {
			return anfragen.size();
		}

		/**
		 * @see java.util.Collection#toArray()
		 */
		public Object[] toArray() {
			return anfragen.toArray();
		}

		/**
		 * @see java.util.Collection#toArray(T[])
		 */
		public <T> T[] toArray(T[] a) {
			return anfragen.toArray(a);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			String s = getClass().getName() + "[";

			s += "zeitpunkt=" + getZeitpunkt();
			s += ", valid=" + isValid();
			s += ", absender=" + absender;
			s += ", absenderZeichen=" + absenderZeichen;
			s += ", anfragen=" + anfragen;

			return s + "]";
		}

	}

	/** Die PID der Attributgruppe. */
	public static final String ATG_PROGNOSE_GANGLINIEN_ANFRAGE = "atg.prognoseGanglinienAnfrage";

	/** Die Attributgruppe kann von allen Instanzen gemeinsam genutzt werden. */
	private static AttributeGroup atg;

	/**
	 * Erzeugt eine Anfrage an die Ganglinienprognose.
	 * 
	 * @param app
	 *            die Applikation <em>Ganglinienprognose</em> an die die
	 *            Anfrage gestellt wird.
	 */
	public OdPrognoseGanglinienAnfrage(ApplikationGanglinienPrognose app) {
		super(app);

		if (atg == null) {
			DataModel modell = ObjektFactory.getInstanz().getVerbindung()
					.getDataModel();
			atg = modell.getAttributeGroup(ATG_PROGNOSE_GANGLINIEN_ANFRAGE);
			assert atg != null;
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see de.bsvrz.sys.funclib.bitctrl.modell.Datensatz#erzeugeDatum()
	 */
	public Daten erzeugeDatum() {
		return new Daten();
	}

	/**
	 * @see de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatensatz#getAspekte()
	 */
	@Override
	public Collection<Aspect> getAspekte() {
		Set<Aspect> aspekte = new HashSet<Aspect>();
		for (Aspekt a : Aspekte.values()) {
			aspekte.add(a.getAspekt());
		}
		return aspekte;
	}

	/**
	 * @see de.bsvrz.sys.funclib.bitctrl.modell.Datensatz#getAttributGruppe()
	 */
	public AttributeGroup getAttributGruppe() {
		return atg;
	}

	/**
	 * @see de.bsvrz.sys.funclib.bitctrl.modell.AbstractDatensatz#konvertiere(de.bsvrz.sys.funclib.bitctrl.modell.Datum)
	 */
	@Override
	protected Data konvertiere(Daten datum) {
		Data daten = erzeugeSendeCache();

		Array feld;
		int i;

		daten.getReferenceValue("absenderId").setSystemObject(
				datum.getAbsender().getSystemObject());
		daten.getTextValue("AbsenderZeichen").setText(
				datum.getAbsenderZeichen());

		feld = daten.getArray("PrognoseGanglinienAnfrage");
		feld.setLength(datum.size());
		i = 0;
		for (GlProgAnfrage anfrage : datum) {
			anfrage.getDaten(feld.getItem(i));
		}

		return daten;
	}

	/**
	 * @see de.bsvrz.sys.funclib.bitctrl.modell.Datensatz#setDaten(de.bsvrz.dav.daf.main.ResultData)
	 */
	public void setDaten(ResultData result) {
		check(result);

		Daten datum = new Daten();
		if (result.hasData()) {
			Array feld;
			Data daten = result.getData();
			ObjektFactory factory = ObjektFactory.getInstanz();

			datum.setAbsender((Applikation) factory.getModellobjekt(daten
					.getReferenceValue("absenderId").getSystemObject()));
			datum.setAbsenderZeichen(daten.getTextValue("AbsenderZeichen")
					.getText());
			datum.clear();
			feld = daten.getArray("PrognoseGanglinienAnfrage");
			for (int i = 0; i < feld.getLength(); i++) {
				GlProgAnfrage anfrage;

				anfrage = new GlProgAnfrage();
				anfrage.setDaten(feld.getItem(i));
				datum.add(anfrage);
			}

			datum.setValid(true);
		} else {
			datum.setValid(false);
		}

		datum.setZeitstempel(result.getDataTime());
		setDatum(result.getDataDescription().getAspect(), datum);
		fireDatensatzAktualisiert(result.getDataDescription().getAspect(),
				datum.clone());
	}

}
