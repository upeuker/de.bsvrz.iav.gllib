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

package de.bsvrz.iav.gllib.gllib.speicher;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.Data.Array;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.iav.gllib.gllib.dav.Messwerte;
import de.bsvrz.iav.gllib.gllib.modell.db.DbGanglinie;
import de.bsvrz.iav.gllib.gllib.modell.db.DbMessQuerschnitt;
import de.bsvrz.iav.gllib.gllib.modell.db.DbStuetzstelle;

/**
 * Hilft beim Lesen von Ganglinien aus der Datenbank.
 *
 * @author BitCtrl Systems GmbH, Thomas Thierfelder
 */
class DbGanglinienHelper {

	/** Verbindung zum Datenverteiler. */
	private final ClientDavInterface dav;

	/** {@link EntityManagerFactory}. */
	private final EntityManagerFactory emf;

	/**
	 * Standardkonstruktor.
	 *
	 * @param dav
	 *            Verbindung zum Datenverteiler.
	 * @param emf
	 *            {@link EntityManagerFactory}.
	 */
	public DbGanglinienHelper(final ClientDavInterface dav,
			final EntityManagerFactory emf) {
		this.dav = dav;
		this.emf = emf;
	}

	/**
	 * Loescht alle Ganglinien eines MQ.
	 *
	 * @param mqPid
	 *            die PID des MQ.
	 * @return eine Fehlermeldung oder <code>null</code>, wenn es geklappt hat.
	 */
	public final synchronized String delete(final String mqPid) {
		final EntityManager em = emf.createEntityManager();

		String error = null;
		EntityTransaction tx = null;
		try {
			em.clear();
			tx = em.getTransaction();
			tx.begin();
			final DbMessQuerschnitt mq = em.find(DbMessQuerschnitt.class,
					mqPid);
			if (mq != null) {
				em.remove(mq);
			}
			tx.commit();
			em.detach(mq);
		} catch (final RuntimeException ex) {
			error = ex.getLocalizedMessage() + "\n"; //$NON-NLS-1$
			if ((tx != null) && tx.isActive()) {
				try {
					tx.rollback();
					error += "Transaction rolled back."; //$NON-NLS-1$
				} catch (final RuntimeException ex2) {
					error += ex2 + "\n"; //$NON-NLS-1$
				}
			}
		} finally {
			em.close();
		}

		return error;
	}

	/**
	 * Liest Ganglinien bzgl. MQ aus der Datenbank.
	 *
	 * @param mqPid
	 *            die PID des MQs.
	 * @param ganglinien
	 *            ein DAV-Array vom Typ <code>atl.ganglinie</code>. In diesen
	 *            werden die Ergebnisse hineingelesen.
	 * @return Fehlermeldungen bzw. <code>null</code> fuer ok.
	 */
	public final synchronized String read(final String mqPid,
			final Data.Array ganglinien) {
		final EntityManager em = emf.createEntityManager();
		ganglinien.setLength(0);

		String err = null;

		List<DbGanglinie> dbGanglinien = null;
		try {
			dbGanglinien = new ArrayList<DbGanglinie>();
			em.clear();
			em.getTransaction().begin();

			DbMessQuerschnitt dbMq = em.find(DbMessQuerschnitt.class, mqPid);

			em.getTransaction().commit();
			if (dbMq != null) {
				dbGanglinien.addAll(dbMq.getGanglinien());
			} else {
				err = "Keine Ganglinien fuer " + mqPid + " abgespeichert."; //$NON-NLS-1$ //$NON-NLS-2$
			}

			ganglinien.setLength(dbGanglinien.size());
			int i = 0;
			for (final DbGanglinie g : dbGanglinien) {
				Array stuetzstellen;

				final SystemObject et = dav.getDataModel()
						.getObject(g.getEreignisTypId());

				if (et == null) {
					ganglinien.setLength(0);
					em.clear();
					em.getTransaction().begin();
					dbMq = em.find(DbMessQuerschnitt.class, mqPid);
					if (dbMq != null) {
						em.remove(dbMq);
					}
					em.getTransaction().commit();
					return "Ereignistyp " + g.getEreignisTypId()
							+ " in Ganglinie existiert nicht mehr";
				}
				ganglinien.getItem(i).getReferenceValue("EreignisTyp") //$NON-NLS-1$
						.setSystemObject(et);
				ganglinien.getItem(i).getUnscaledValue("AnzahlVerschmelzungen") //$NON-NLS-1$
						.set(g.getAnzahlVerschmelzungen());
				ganglinien.getItem(i).getTimeValue("LetzteVerschmelzung") //$NON-NLS-1$
						.setMillis(g.getLetzteVerschmelzung());
				ganglinien.getItem(i).getUnscaledValue("GanglinienTyp") //$NON-NLS-1$
						.set(g.getTyp());

				if (g.isReferenzGanglinie()) {
					ganglinien.getItem(i).getUnscaledValue("Referenzganglinie") //$NON-NLS-1$
							.setText("Ja"); //$NON-NLS-1$
				} else {
					ganglinien.getItem(i).getUnscaledValue("Referenzganglinie") //$NON-NLS-1$
							.setText("Nein"); //$NON-NLS-1$
				}

				ganglinien.getItem(i).getUnscaledValue("GanglinienVerfahren") //$NON-NLS-1$
						.set(g.getApproximationsverfahren());
				ganglinien.getItem(i).getUnscaledValue("Ordnung") //$NON-NLS-1$
						.set(g.getOrdnung());

				stuetzstellen = ganglinien.getItem(i).getArray("Stützstelle"); //$NON-NLS-1$
				final List<DbStuetzstelle> liste = g.getStuetzstellen();
				int j = 0;
				stuetzstellen.setLength(liste.size());
				for (final DbStuetzstelle s : liste) {
					stuetzstellen.getItem(j).getTimeValue("Zeit") //$NON-NLS-1$
							.setMillis(s.getZeit());

					if (Double.isInfinite(s.getqKfz())
							|| Double.isNaN(s.getqKfz())) {
						stuetzstellen.getItem(j).getScaledValue("QKfz") //$NON-NLS-1$
								.set(Messwerte.UNDEFINIERT);
					} else {
						stuetzstellen.getItem(j).getScaledValue("QKfz") //$NON-NLS-1$
								.set(s.getqKfz());
					}

					if (Double.isInfinite(s.getqLkw())
							|| Double.isNaN(s.getqLkw())) {
						stuetzstellen.getItem(j).getScaledValue("QLkw") //$NON-NLS-1$
								.set(Messwerte.UNDEFINIERT);
					} else {
						stuetzstellen.getItem(j).getScaledValue("QLkw") //$NON-NLS-1$
								.set(s.getqLkw());
					}

					if (Double.isInfinite(s.getvLkw())
							|| Double.isNaN(s.getvLkw())) {
						stuetzstellen.getItem(j).getScaledValue("VLkw") //$NON-NLS-1$
								.set(Messwerte.UNDEFINIERT);
					} else {
						stuetzstellen.getItem(j).getScaledValue("VLkw") //$NON-NLS-1$
								.set(s.getvLkw());
					}

					if (Double.isInfinite(s.getvPkw())
							|| Double.isNaN(s.getvPkw())) {
						stuetzstellen.getItem(j).getScaledValue("VPkw") //$NON-NLS-1$
								.set(Messwerte.UNDEFINIERT);
					} else {
						stuetzstellen.getItem(j).getScaledValue("VPkw") //$NON-NLS-1$
								.set(s.getvPkw());
					}

					j++;
				}
				i++;
			}
		} catch (final RuntimeException ex) {
			err = ex.getLocalizedMessage() + "\n"; //$NON-NLS-1$
		} finally {
			em.close();
		}

		return err;
	}

	/**
	 * Schreibt Ganglinien bzgl. MQ aus der Datenbank.
	 *
	 * @param mqPid
	 *            die PID des MQs.
	 * @param ganglinien
	 *            ein DAV-Array vom Typ <code>atl.ganglinie</code>. In diesen
	 *            werden die Ergebnisse hineingelesen.
	 * @return Fehlermeldungen bzw. <code>null</code> fuer ok.
	 */
	public final synchronized String write(final String mqPid,
			final Data.Array ganglinien) {
		final EntityManager em = emf.createEntityManager();

		final ArrayList<DbGanglinie> neueGanglinien = new ArrayList<DbGanglinie>();
		for (int i = 0; i < ganglinien.getLength(); i++) {
			final DbGanglinie dbGanglinie = new DbGanglinie();

			final Data.Array stuetzstellen = ganglinien.getItem(i)
					.getArray("Stützstelle"); //$NON-NLS-1$
			final List<DbStuetzstelle> dbStuetzstellen = new ArrayList<DbStuetzstelle>();
			for (int j = 0; j < stuetzstellen.getLength(); j++) {
				final Data ss = stuetzstellen.getItem(j);

				final double qLkw = ss.getScaledValue("QLkw").doubleValue(); //$NON-NLS-1$
				final double qKfz = ss.getScaledValue("QKfz").doubleValue(); //$NON-NLS-1$
				final double vPkw = ss.getScaledValue("VPkw").doubleValue(); //$NON-NLS-1$
				final double vLkw = ss.getScaledValue("VLkw").doubleValue(); //$NON-NLS-1$

				final DbStuetzstelle stuetzStelle = new DbStuetzstelle(
						ss.getTimeValue("Zeit").getMillis(), qLkw, qKfz, vPkw, //$NON-NLS-1$
						vLkw);
				dbStuetzstellen.add(stuetzStelle);
			}
			dbGanglinie.setStuetzstellen(dbStuetzstellen);

			dbGanglinie.setAnzahlVerschmelzungen(ganglinien.getItem(i)
					.getUnscaledValue("AnzahlVerschmelzungen").longValue()); //$NON-NLS-1$
			dbGanglinie.setEreignisTypId(
					ganglinien.getItem(i).getReferenceValue("EreignisTyp") //$NON-NLS-1$
							.getSystemObject().getId());
			dbGanglinie.setLetzteVerschmelzung(ganglinien.getItem(i)
					.getTimeValue("LetzteVerschmelzung").getMillis()); //$NON-NLS-1$
			dbGanglinie.setMqPid(mqPid);
			dbGanglinie.setOrdnung((int) ganglinien.getItem(i)
					.getUnscaledValue("Ordnung").longValue()); //$NON-NLS-1$
			dbGanglinie.setTyp(ganglinien.getItem(i)
					.getUnscaledValue("GanglinienTyp").intValue()); //$NON-NLS-1$
			dbGanglinie.setApproximationsverfahren(ganglinien.getItem(i)
					.getUnscaledValue("GanglinienVerfahren").intValue()); //$NON-NLS-1$
			dbGanglinie.setReferenzGanglinie(ganglinien.getItem(i)
					.getTextValue("Referenzganglinie").getText().equals("Ja")); //$NON-NLS-1$ //$NON-NLS-2$

			neueGanglinien.add(dbGanglinie);
		}

		String error = null;
		EntityTransaction tx = null;
		try {
			tx = em.getTransaction();
			tx.begin();
			final DbMessQuerschnitt mq = em.find(DbMessQuerschnitt.class,
					mqPid);
			mq.setGanglinien(neueGanglinien);
			em.persist(mq);
			tx.commit();
			em.detach(mq);
		} catch (final RuntimeException ex1) {
			error = ex1.getLocalizedMessage() + "\n"; //$NON-NLS-1$
			if ((tx != null) && tx.isActive()) {
				try {
					tx.rollback();
					error += "Transaction rolled back."; //$NON-NLS-1$
				} catch (final RuntimeException ex2) {
					error += ex2 + "\n"; //$NON-NLS-1$
				}
			}
		} finally {
			em.close();
		}

		return error;
	}

}
