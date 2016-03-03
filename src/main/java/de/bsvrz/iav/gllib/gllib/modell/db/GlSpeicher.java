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
 * Weiﬂenfelser Straﬂe 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib.modell.db;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Dient als Schnittstelle zur Datenbank, in der die Ganglinien gespeichert
 * werden.
 *
 * @author BitCtrl Systems GmbH, Thomas Thierfelder
 */
public class GlSpeicher {

	/**
	 * Speichert pro Thread jeweils eine Schnittstelle zum Ganglinienspeicher.
	 */
	private static final Map<Long, GlSpeicher> THREAD_ID_TO_GLSPEICHER_MAP = new HashMap<Long, GlSpeicher>();

	/** {@link EntityManagerFactory}. */
	private static EntityManagerFactory EMF = null;

	/**
	 * Standardkonstruktor.
	 */
	private GlSpeicher() {
		// tut nix
	}

	/**
	 * Erfragt eine Instanz der Ganglinienspeicherschnittstelle (thread-safe).
	 *
	 * @return eine Instanz der Ganglinienspeicherschnittstelle.
	 */
	public static final GlSpeicher getInstanz() {
		synchronized (THREAD_ID_TO_GLSPEICHER_MAP) {
			final Long threadId = Thread.currentThread().getId();
			GlSpeicher glSpeicher = THREAD_ID_TO_GLSPEICHER_MAP.get(threadId);
			if (glSpeicher == null) {
				glSpeicher = new GlSpeicher();
				THREAD_ID_TO_GLSPEICHER_MAP.put(threadId, glSpeicher);
			}
			return glSpeicher;
		}
	}

	/**
	 * Initialisiert den Ganglinienspeicher fuer Multi-Thread-Use.
	 *
	 * @param emf
	 *            {@link EntityManagerFactory}
	 */
	public static final synchronized void init(final EntityManagerFactory emf) {
		if (EMF == null) {
			GlSpeicher.EMF = emf;
		}
	}

	/**
	 * Liest alle Ganglinien eines MQ.
	 *
	 * @param mqPid
	 *            PID des MQ.
	 * @return ein Objekt mit allen Ganglinien eines MQ.
	 */
	public final synchronized DbGanglinieDaten read(final String mqPid) {
		final EntityManager em = EMF.createEntityManager();
		DbMessQuerschnitt mq = em.find(DbMessQuerschnitt.class, mqPid);
		if (mq == null) {
			mq = new DbMessQuerschnitt(mqPid);
		}
		final DbGanglinieDaten daten = new DbGanglinieDaten(mq);
		em.close();
		return daten;
	}

	/**
	 * Sichert alle Ganglinien eines MQ.
	 *
	 * @param datum
	 *            ein Objekt mit allen Ganglinien eines MQ.
	 */
	public final synchronized void write(final DbGanglinieDaten datum) {
		final EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();
		em.merge(datum.getDatenbankObjekt());
		em.getTransaction().commit();
		em.close();
	}

	/**
	 * Loescht alle Ganglinien eines MQ.
	 *
	 * @param mqPid
	 *            die PID des MQ, dessen Ganglinien geloescht werden sollen.
	 */
	public synchronized final void removeAlleGanglinien(final String mqPid) {
		final EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();
		final DbMessQuerschnitt mq = em.find(DbMessQuerschnitt.class, mqPid);
		if (mq != null) {
			em.remove(mq);
		}
		em.getTransaction().commit();
		em.close();
	}

}
