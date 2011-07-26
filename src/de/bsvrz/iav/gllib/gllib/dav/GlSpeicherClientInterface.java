/*
 * Segment 5 Intelligente Analyseverfahren, SWE 5.3 Automatisches Lernen
 * Ganglinien
 * Copyright (C) 2011 BitCtrl Systems GmbH 
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact Information:
 * BitCtrl Systems GmbH
 * Wei�enfelser Stra�e 67
 * 04229 Leipzig
 * Phone: +49 341-490670
 * mailto: info@bitctrl.de
 */

package de.bsvrz.iav.gllib.gllib.dav;

import java.util.List;

import de.bsvrz.dav.daf.main.DataNotSubscribedException;
import de.bsvrz.dav.daf.main.SendSubscriptionNotConfirmed;
import de.bsvrz.dav.daf.main.config.SystemObject;

/**
 * Schnittstelle, ueber die Ganglinien abgefragt und manipuliert werden koennen.
 * 
 * @author BitCtrl Systems GmbH, Thomas Thierfelder
 * 
 * @version $Id$
 */
public interface GlSpeicherClientInterface {

	/**
	 * Fragt saemtliche Ganglinien eines MQ ab (synchron).
	 * 
	 * @param mqSysObj
	 *            das SystemObjekt des MQ.
	 * @return eine Liste mit saemtlichen Ganglinien am uebergebenen MQ (ggf.
	 *         leere Liste).
	 * @throws DataNotSubscribedException
	 *             wird weitergereicht.
	 * @throws SendSubscriptionNotConfirmed
	 *             wird weitergereicht.
	 * @throws GlSpeicherServerException
	 *             wird geworfen, wenn auf Serverseite ein Fehler auftritt.
	 */
	public List<GanglinieMQ> getGanglinien(final SystemObject mqSysObj)
			throws DataNotSubscribedException, SendSubscriptionNotConfirmed,
			GlSpeicherServerException;

	/**
	 * Setzt (speichert) die Ganglinien eines MQ (synchron).
	 * 
	 * @param mqSysObj
	 *            das SystemObjekt des MQ, an dem die uebergebenen Ganglinien
	 *            gespeichert werden sollen.
	 * @param ganglinien
	 *            <b>alle</b> Ganglinien eines MQ. Wird hier <code>null</code>
	 *            uebergeben, so werden die Ganglinien am MQ geloescht.
	 * @throws DataNotSubscribedException
	 *             wird weitergereicht.
	 * @throws SendSubscriptionNotConfirmed
	 *             wird weitergereicht.
	 * @throws GlSpeicherServerException
	 *             wird geworfen, wenn auf Serverseite ein Fehler auftritt.
	 */
	public void setGanglinien(final SystemObject mqSysObj,
			final List<GanglinieMQ> ganglinien)
			throws DataNotSubscribedException, SendSubscriptionNotConfirmed,
			GlSpeicherServerException;

}
