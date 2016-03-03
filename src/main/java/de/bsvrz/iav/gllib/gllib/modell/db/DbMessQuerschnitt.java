/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.5 Funktionen Ganglinie
 * Copyright (C) 2011-2015 BitCtrl Systems GmbH
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

package de.bsvrz.iav.gllib.gllib.modell.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

import de.bsvrz.iav.gllib.gllib.GlLibMsg;
import de.bsvrz.sys.funclib.bitctrl.daf.BetriebsmeldungDaten;
import de.bsvrz.sys.funclib.bitctrl.daf.LogTools;
import de.bsvrz.sys.funclib.debug.Debug;

/**
 * Speichert alle Ganglinien <b>eines</b> MQs. Im Prinzip steht diese Klasse
 * stellvertretend fuer die Parameter-Attributgruppe <code>atg.ganglinie</code>.
 *
 * @author Thierfelder
 */
@Entity
public class DbMessQuerschnitt {

	@Transient
	private final Debug log = Debug.getLogger();

	/**
	 * PID des MQ.
	 */
	@Id
	private String mqPid;

	/**
	 * Alle Ganglinien am MQ.
	 */
	@Lob
	private byte[] ganglinien;

	/**
	 * Standardkonstruktor.
	 */
	public DbMessQuerschnitt() {
		// tut nix
	}

	/**
	 * Konstruktor.
	 *
	 * @param mqPid
	 *            PID des MQ.
	 */
	DbMessQuerschnitt(final String mqPid) {
		this.mqPid = mqPid;
	}

	/**
	 * Erfragt die PID des MQ.
	 *
	 * @return die PID des MQ.
	 */
	public String getMqPid() {
		return mqPid;
	}

	/**
	 * Erfragt die Liste aller Ganglinien dieses MQ.
	 *
	 * @return die Liste aller Ganglinien dieses MQ.
	 */
	public List<DbGanglinie> getGanglinien() {
		if (ganglinien != null) {
			final ByteArrayInputStream bais = new ByteArrayInputStream(
					ganglinien);
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(bais);
				List<DbGanglinie> ganglinienDb = null;

				/*
				 * Warnung wegen "type safety" muss ignoriert werden, weil mit
				 * instanceof nicht der generische Typ der Liste geprüft werden
				 * kann.
				 */
				ganglinienDb = (List<DbGanglinie>) ois.readObject();
				return ganglinienDb;
			} catch (final IOException ex) {
				LogTools.log(log, new BetriebsmeldungDaten().setId(mqPid),
						GlLibMsg.ErrorDeserialisieren, mqPid, ex);
			} catch (final ClassNotFoundException ex) {
				LogTools.log(log, new BetriebsmeldungDaten().setId(mqPid),
						GlLibMsg.ErrorDeserialisieren, mqPid, ex);

			}
		}

		return new ArrayList<DbGanglinie>();
	}

	/**
	 * Setzt die Liste der Ganglinien an diesem MQ.
	 *
	 * @param neueGanglinien
	 *            alle neuen Ganglinien.
	 */
	public void setGanglinien(final ArrayList<DbGanglinie> neueGanglinien) {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			final ObjectOutputStream ous = new ObjectOutputStream(baos);
			ous.writeObject(neueGanglinien);
			ganglinien = baos.toByteArray();
		} catch (final IOException ex) {
			LogTools.log(log, new BetriebsmeldungDaten().setId(mqPid),
					GlLibMsg.ErrorSerialisieren, mqPid, ex);

		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((mqPid == null) ? 0 : mqPid.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DbMessQuerschnitt other = (DbMessQuerschnitt) obj;
		if (mqPid == null) {
			if (other.mqPid != null) {
				return false;
			}
		} else if (!mqPid.equals(other.mqPid)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[mqPid=" + mqPid + "]"; //$NON-NLS-1$//$NON-NLS-2$
	}

}
