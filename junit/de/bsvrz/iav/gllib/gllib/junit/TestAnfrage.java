package de.bsvrz.iav.gllib.gllib.junit;

import java.util.Calendar;
import java.util.Collections;

import com.bitctrl.Constants;
import com.bitctrl.util.Interval;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.iav.gllib.gllib.dav.Ganglinienprognose;
import de.bsvrz.iav.gllib.gllib.dav.GlProgAnfrage;
import de.bsvrz.iav.gllib.gllib.dav.GlProgAntwortEvent;
import de.bsvrz.iav.gllib.gllib.dav.GlProgAntwortListener;
import de.bsvrz.sys.funclib.application.StandardApplication;
import de.bsvrz.sys.funclib.application.StandardApplicationRunner;
import de.bsvrz.sys.funclib.bitctrl.modell.DefaultObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.ObjektFactory;
import de.bsvrz.sys.funclib.bitctrl.modell.tmverkehrglobal.objekte.MessQuerschnitt;
import de.bsvrz.sys.funclib.bitctrl.modell.tmverkehrglobal.objekte.MessQuerschnittAllgemein;
import de.bsvrz.sys.funclib.commandLineArgs.ArgumentList;

/**
 * Ausf�hrung einer Testanfrage an die Ganglinienpeorgnose-Application.
 * 
 * @author BitCtrl Systems GmbH, peuker
 * @version $Id$
 */
public class TestAnfrage implements StandardApplication {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		StandardApplicationRunner.run(new TestAnfrage(), args);
	}

	@Override
	public void initialize(final ClientDavInterface connection)
			throws Exception {
		final ObjektFactory factory = DefaultObjektFactory.getInstanz();
		factory.setDav(connection);

		final Ganglinienprognose prognose = new Ganglinienprognose(factory);
		prognose.addAntwortListener(new GlProgAntwortListener() {

			@Override
			public void antwortEingetroffen(final GlProgAntwortEvent e) {
				System.out.println(e);

				try {
					Thread.sleep(10 * Constants.MILLIS_PER_SECOND);
				} catch (final InterruptedException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				System.exit(0);
			}
		});

		final Calendar cal = Calendar.getInstance();
		final long start = cal.getTimeInMillis();

		final MessQuerschnittAllgemein mq = (MessQuerschnittAllgemein) factory
				.bestimmeModellobjekte(MessQuerschnitt.PID).get(0);

		final GlProgAnfrage anfrage = new GlProgAnfrage();
		anfrage.setMessQuerschnitt(mq);
		anfrage.setNurLangfristigeAuswahl(true);
		anfrage.setPrognoseZeitraum(new Interval(start, start
				+ Constants.MILLIS_PER_HOUR));
		anfrage.setPruefIntervall(60 * Constants.MILLIS_PER_SECOND);
		anfrage.setSendeIntervall(60 * Constants.MILLIS_PER_SECOND);
		prognose.sendeAnfrage("Testanfrage", Collections.singleton(anfrage));
	}

	@Override
	public void parseArguments(final ArgumentList argumentList)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
