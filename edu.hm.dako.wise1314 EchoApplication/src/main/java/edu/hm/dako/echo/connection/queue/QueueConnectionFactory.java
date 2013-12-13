package edu.hm.dako.echo.connection.queue;

import edu.hm.dako.echo.connection.Connection;
import edu.hm.dako.echo.connection.ConnectionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class QueueConnectionFactory implements ConnectionFactory {

	private static Log log = LogFactory.getLog(QueueConnectionFactory.class);
	
	//Connection zum Senden an die Requests-Queue
	public Connection connectToServer(String remoteServerAddress,
			int serverPort, int localPort) throws Exception {
		EMSConnection connection = null;
		boolean connected = false;
		while(!connected) {
			try {
				log.debug("EMS Connection wird aufgebaut...");
				connection = new EMSConnection(remoteServerAddress, serverPort, "", "");
				connected = true;
				log.debug("EMS Connection aufgebaut");
			} catch (Exception e) {
				//try again
			}
		}
		log.debug("ConnectToServer mit Erfolg");
		return connection;
	}

/*	//Connection zum Empfangen von der Repsonses-Queue
	public Connection acceptFromServer(String remoteServerAddress,
			int serverPort, int localPort) throws Exception {
		EMSConnection connection = null;
		boolean connected = false;
		while(!connected) {
			try {
				connection = new EMSConnection(remoteServerAddress, serverPort, "", "","responses");
				connected = true;
			} catch (Exception e) {
				//try again
			}
		}
		return connection;
	}*/
	
	
}
