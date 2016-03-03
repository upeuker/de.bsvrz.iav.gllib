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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.persistence.EntityManagerFactory;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.iav.gllib.gllib.Stuetzstelle;
import de.bsvrz.iav.gllib.gllib.dav.GanglinieMQ;
import de.bsvrz.iav.gllib.gllib.dav.Messwerte;
import de.bsvrz.iav.gllib.gllib.modell.GanglinienobjektFactory;
import de.bsvrz.iav.gllib.gllib.speicher.GlSpeicherUtil;
import de.bsvrz.sys.funclib.application.StandardApplication;
import de.bsvrz.sys.funclib.application.StandardApplicationRunner;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.SystemObjekt;
import de.bsvrz.sys.funclib.bitctrl.modell.kalender.objekte.EreignisTyp;
import de.bsvrz.sys.funclib.bitctrl.modell.verkehr.objekte.MessQuerschnittAllgemein;
import de.bsvrz.sys.funclib.commandLineArgs.ArgumentList;

@SuppressWarnings("nls")
public class Test implements StandardApplication {

	private static final Random R = new Random();

	private ClientDavInterface dav;

	private GlSpeicher db;

	private ObjektFactory factory;

	private MessQuerschnittAllgemein mq1, mq2, mq3;

	private EreignisTyp et1;

	public static void main(final String args[]) {
		StandardApplicationRunner.run(new Test(), args);
	}

	@Override
	public void parseArguments(final ArgumentList argumentList)
			throws Exception {
		//
	}

	@Override
	public void initialize(final ClientDavInterface dav) throws Exception {
		try {
			this.dav = dav;
			setUp();

			final EntityManagerFactory emf = GlSpeicherUtil
					.getDefaultGlSpeicherEmf();
			GlSpeicher.init(emf);
			db = GlSpeicher.getInstanz();

			final Set<SystemObjekt> alleMqSet = new HashSet<SystemObjekt>();
			final int a = 0;
			for (final SystemObjekt sysObj : factory
					.bestimmeModellobjekte("typ.messQuerschnittAllgemein")) {
				// if(a++ == 10)break;
				// if(a++ < 10) {
				// continue;
				// }
				// if(a > 13) {
				// break;
				// }
				alleMqSet.add(sysObj);
				// break;
			}

			for (int i = 0; i < 1000; i++) {
				for (final SystemObjekt mqObj : alleMqSet) {
					// if(R.nextInt(10) != 0) {
					// continue;
					// }
					final MessQuerschnittAllgemein mqa = (MessQuerschnittAllgemein) mqObj;
					final long start = System.currentTimeMillis();
					final DbGanglinieDaten gldb1 = db.read(mqObj.getPid());
					final boolean gelesen = gldb1 != null;

					final GanglinieMQ gl11 = new GanglinieMQ();
					fill2(gl11, 1, et1, mqa);
					final GanglinieMQ gl12 = new GanglinieMQ();
					fill2(gl12, i + 1, et1, mqa);
					final GanglinieMQ gl13 = new GanglinieMQ();
					fill2(gl13, i + 2, et1, mqa);
					final GanglinieMQ gl14 = new GanglinieMQ();
					fill2(gl14, i + 3, et1, mqa);
					final GanglinieMQ gl15 = new GanglinieMQ();
					fill2(gl15, i + 4, et1, mqa);
					final GanglinieMQ gl16 = new GanglinieMQ();
					fill2(gl16, i + 5, et1, mqa);
					final GanglinieMQ gl17 = new GanglinieMQ();
					fill2(gl17, i + 6, et1, mqa);
					final GanglinieMQ gl18 = new GanglinieMQ();
					fill2(gl18, i + 7, et1, mqa);

					if (i > 0) {
						gldb1.set(0, gl11);
						gldb1.set(1, gl12);
						gldb1.add(2, gl13);
						gldb1.add(3, gl14);
						gldb1.add(4, gl15);
						gldb1.add(5, gl16);
						gldb1.add(6, gl17);
						gldb1.add(7, gl18);
					} else {
						gldb1.add(gl11);
						gldb1.add(gl12);
						gldb1.add(gl13);
						gldb1.add(gl14);
						gldb1.add(gl15);
						gldb1.add(gl16);
						gldb1.add(gl17);
						gldb1.add(gl18);
					}

					db.write(gldb1);
					final boolean geschrieben = true;
					final long zeit = System.currentTimeMillis() - start;
					System.out.println(i + " (" + mqObj.getName()
							+ "), gelesen: " + gelesen + ", geschrieben: "
							+ geschrieben + ", Zeit: " + zeit + "ms");
				}
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
		}

		// DbGanglinieDaten gld1 = db.read(mq1.getPid());
		// DbGanglinieDaten gld2 = db.read(mq2.getPid());
		// DbGanglinieDaten gld3 = db.read(mq3.getPid());
		//
		// GanglinieMQ gl11 = new GanglinieMQ();
		// GanglinieMQ gl12 = new GanglinieMQ();
		// GanglinieMQ gl13 = new GanglinieMQ();
		// GanglinieMQ gl14 = new GanglinieMQ();
		// GanglinieMQ gl15 = new GanglinieMQ();
		// GanglinieMQ gl16 = new GanglinieMQ();
		// GanglinieMQ gl17 = new GanglinieMQ();
		// GanglinieMQ gl18 = new GanglinieMQ();
		//
		// // 3 Ganglinien fuer et1
		// fill(gl11, 1, et1, mq1, new long[][] { { 1, 10 }, { 2, 11 }, { 3, 12
		// },
		// { 4, 13 }, { 5, 10 } });
		// fill(gl12, 2, et1, mq1, new long[][] { { 1, 5 }, { 2, 15 }, { 3, 20
		// },
		// { 4, 15 }, { 5, 5 } });
		// fill(gl13, 3, et1, mq1, new long[][] { { 1, 1 }, { 2, 2 }, { 3, 3 },
		// { 4, 4 }, { 5, 10 } });
		// gld1.add(gl11);
		// gld1.add(gl12);
		// gld1.add(gl13);
		//
		// // fill(gl14, 1, et2, mq1, new long[][] { { 1, 10 }, { 2, 11 }, { 3,
		// 12
		// // },
		// // { 4, 13 }, { 5, 10 } });
		// // fill(gl15, 2, et2, mq1, new long[][] { { 1, 5 }, { 2, 15 }, { 3,
		// 20
		// // },
		// // { 4, 15 }, { 5, 5 } });
		// // gld1.add(gl14);
		// // gld1.add(gl15);
		// //
		// // fill(gl16, 1, et3, mq1, new long[][] { { 1, 1 }, { 2, 2 }, { 3, 3
		// },
		// // { 4, 4 }, { 5, 5 } });
		// // fill(gl17, 2, et3, mq1, new long[][] { { 1, 5 }, { 2, 4 }, { 3, 3
		// },
		// // { 4, 2 }, { 5, 1 } });
		// // fill(gl18, 3, et3, mq1, new long[][] { { 1, 5 }, { 2, 2 }, { 3, 1
		// },
		// // { 4, 2 }, { 5, 5 } });
		// // gld1.add(gl16);
		// // gld1.add(gl17);
		// // gld1.add(gl18);
		//
		// GanglinieMQ gl21 = new GanglinieMQ();
		// GanglinieMQ gl22 = new GanglinieMQ();
		// GanglinieMQ gl23 = new GanglinieMQ();
		// GanglinieMQ gl24 = new GanglinieMQ();
		// GanglinieMQ gl25 = new GanglinieMQ();
		// GanglinieMQ gl26 = new GanglinieMQ();
		// GanglinieMQ gl27 = new GanglinieMQ();
		// GanglinieMQ gl28 = new GanglinieMQ();
		// fill2(gl21, 1, et1, mq2);
		// fill2(gl22, 2, et1, mq2);
		// fill2(gl23, 3, et1, mq2);
		// gld2.add(gl21);
		// gld2.add(gl22);
		// gld2.add(gl23);
		//
		// // fill2(gl24, 1, et2, mq2);
		// // fill2(gl25, 2, et2, mq2);
		// // gld2.add(gl24);
		// // gld2.add(gl25);
		// //
		// // fill2(gl26, 1, et3, mq2);
		// // fill2(gl27, 2, et3, mq2);
		// // fill2(gl28, 3, et3, mq2);
		// // gld2.add(gl26);
		// // gld2.add(gl27);
		// // gld2.add(gl28);
		//
		// db.write(gld1);
		// db.write(gld2);
		// // db.write(gld3);
		//
		// // gld1 = db.read(mq1.getPid());
		// // gld1.iterator()
		// // .next()
		// // .setStuetzstelle(
		// // new Stuetzstelle<Messwerte>(2, new Messwerte(4.0, 5.0,
		// // 6.0, 7.0)));
		// // System.out.println(gld1);
		// // db.write(gld1);
		// System.out.println(gld1);
	}

	private void setUp() {
		factory = ObjektFactory.getInstanz();
		factory.setVerbindung(dav);
		factory.registerStandardFactories();
		factory.registerFactory(new GanglinienobjektFactory());

		// mq1 = (MessQuerschnittAllgemein) factory.getModellobjekt("mq.MQ1");
		// mq2 = (MessQuerschnittAllgemein) factory.getModellobjekt("mq.MQ2");
		// mq3 = (MessQuerschnittAllgemein) factory.getModellobjekt("mq.MQ3");

		mq1 = (MessQuerschnittAllgemein) factory
				.getModellobjekt("mq.MQ.A81.5402.HFB.N");
		mq2 = (MessQuerschnittAllgemein) factory
				.getModellobjekt("mq.MQ.A81.5301.Ein.N");
		mq3 = (MessQuerschnittAllgemein) factory
				.getModellobjekt("mq.MQ.A81.5404.HFB.N");

		et1 = (EreignisTyp) factory.getModellobjekt("ereignisTyp.Baustelle");
	}

	private void fill(final GanglinieMQ gl, final int a, final EreignisTyp et,
			final MessQuerschnittAllgemein mq, final long[][] ss) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.DAY_OF_YEAR, a);

		gl.setAnzahlVerschmelzungen(a);
		gl.setApproximationDaK(a);
		gl.setBSplineOrdnung(a);
		gl.setEreignisTyp(et);
		gl.setK1(a);
		gl.setK2(a * 2);
		gl.setLetzteVerschmelzung(cal.getTimeInMillis());
		gl.setMessQuerschnitt(mq);
		gl.setReferenz(false);
		gl.setTyp(0);
		final List<Stuetzstelle<Messwerte>> ssList = new ArrayList<Stuetzstelle<Messwerte>>();

		final int i = 0;
		cal = new GregorianCalendar();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, i);

		for (final long[] s : ss) {

			ssList.add(new Stuetzstelle<Messwerte>(cal.getTimeInMillis(),
					new Messwerte((double) s[1], (double) s[1], (double) s[1],
							(double) s[1])));
			cal.add(Calendar.HOUR_OF_DAY, 1);
		}
		gl.setStuetzstellen(ssList);
	}

	private void fill2(final GanglinieMQ gl, final int a, final EreignisTyp et,
			final MessQuerschnittAllgemein mq) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.DAY_OF_YEAR, a);

		gl.setAnzahlVerschmelzungen(a);
		gl.setApproximationDaK(a);
		gl.setBSplineOrdnung(a);
		gl.setEreignisTyp(et);
		gl.setK1(a);
		gl.setK2(a * 2);
		gl.setLetzteVerschmelzung(cal.getTimeInMillis());
		gl.setMessQuerschnitt(mq);
		gl.setReferenz(false);
		gl.setTyp(0);
		final List<Stuetzstelle<Messwerte>> ssList = new ArrayList<Stuetzstelle<Messwerte>>();

		cal = new GregorianCalendar();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);

		for (int i = 0; i < 1440; i++) {
			if (R.nextInt(10) == 2) {
				ssList.add(new Stuetzstelle<Messwerte>(cal.getTimeInMillis(),
						new Messwerte(Messwerte.UNDEFINIERT,
								Messwerte.UNDEFINIERT, Messwerte.UNDEFINIERT,
								Messwerte.UNDEFINIERT)));

			} else {
				ssList.add(new Stuetzstelle<Messwerte>(cal.getTimeInMillis(),
						new Messwerte(R.nextDouble() * 100.0,
								R.nextDouble() * 100.0, R.nextDouble() * 100.0,
								R.nextDouble() * 100.0)));
			}
			cal.add(Calendar.MINUTE, 1);
		}
		gl.setStuetzstellen(ssList);
	}

}
