package de.iav.gllib.dav;

import java.util.ArrayList;
import stauma.dav.clientside.ClientDavInterface;
import stauma.dav.clientside.ClientReceiverInterface;
import stauma.dav.clientside.ClientSenderInterface;
import stauma.dav.clientside.DataDescription;
import stauma.dav.clientside.ReceiveOptions;
import stauma.dav.clientside.ResultData;
import stauma.dav.clientside.SenderRole;
import stauma.dav.common.OneSubscriptionPerSendData;
import stauma.dav.configuration.interfaces.ConfigurationException;
import stauma.dav.configuration.interfaces.SystemObject;
import stauma.dav.clientside.ReceiverRole;

/**
 * Kapselt die Kommunikation mit dem Datenverteiler beim Bearbeiten von
 * Ganglinien. Die Ganglinien des Messquerschnitt werden als sortierte Liste
 * zwischengespeichert. Die Ordnung der Liste entspricht der im Datenverteiler.
 * 
 * @author BitCtrl, Schumann
 * @version $Id: GanglinienListe.java 160 2007-02-23 15:09:31Z Schumann $
 */
public class GanglinienListe extends ArrayList {

	private static final long serialVersionUID = 1L;
	private boolean autoUpdate;
	
	
	/** 
	 * @author Schumann
	 */
	protected class Sender implements ClientSenderInterface {

		/**
		 * @see stauma.dav.clientside.ClientSenderInterface#dataRequest(stauma.dav.configuration.interfaces.SystemObject, stauma.dav.clientside.DataDescription, byte)
		 */
		public void dataRequest(SystemObject arg0, DataDescription arg1,
				byte arg2) {
			// TODO Automatisch erstellter Methoden-Stub

		}

		/**
		 * @see stauma.dav.clientside.ClientSenderInterface#isRequestSupported(stauma.dav.configuration.interfaces.SystemObject, stauma.dav.clientside.DataDescription)
		 */
		public boolean isRequestSupported(SystemObject arg0,
				DataDescription arg1) {
			// TODO Automatisch erstellter Methoden-Stub
			return false;
		}

	}

	/** 
	 * @author Schumann
	 */
	protected class Receiver implements ClientReceiverInterface {

		/**
		 * @see stauma.dav.clientside.ClientReceiverInterface#update(stauma.dav.clientside.ResultData[])
		 */
		public void update(ResultData[] arg0) {
			// TODO Automatisch erstellter Methoden-Stub
		}

	}
	

	/**
	 * Meldet sich als Empf&auml;nger und Sender der Ganglinienliste eines
	 * Messquerschnitts am Datenverteiler an und bezieht erstmalig die Liste
	 * der vorhandenen Ganglinien
	 *  
	 * @param connection Verbindung zum Datenverteiler
	 * @param object Messquerschnitt
	 * @param description Attributgruppe und Aspekt
	 */
	public GanglinienListe(ClientDavInterface connection, SystemObject object,
			DataDescription description) {
		connection.subscribeReceiver(new Receiver(), object, description, ReceiveOptions.normal(), ReceiverRole.receiver());
		try {
			connection.subscribeSender(new Sender(), object, description, SenderRole.sender());
		} catch (ConfigurationException e) {
			// TODO Automatisch erstellter Catch-Block
			e.printStackTrace();
		} catch (OneSubscriptionPerSendData e) {
			// TODO Automatisch erstellter Catch-Block
			e.printStackTrace();
		}
	}
	
	/**
	 * Liest die Liste der Ganglinien des Messquerschnitts vom Datenverteiler
	 */
	public void read() {
		return;
	}
	
	/**
	 * Schreibt die Liste der Ganglinien des Messquerschnitts zum Datenverteiler
	 */
	public void write() {
		return;
	}

	/**
	 * Wird die lokale Liste der Ganglinien bei &Auml;nderung im Datenverteiler
	 * automatisch aktualisiert?
	 *
	 * @return  Returns the autoUpdate.
	 */
	public boolean getAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * Legt fest, ob die lokale Liste der Ganglinien bei &Auml;nderung im
	 * Datenverteiler automatisch aktualisiert werden soll
	 * 
	 * @param autoUpdate  The autoUpdate to set.
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}
	
}
