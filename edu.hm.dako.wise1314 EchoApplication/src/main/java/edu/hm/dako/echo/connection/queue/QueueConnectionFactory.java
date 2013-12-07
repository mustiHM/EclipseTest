package edu.hm.dako.echo.connection.queue;

import edu.hm.dako.echo.connection.Connection;
import edu.hm.dako.echo.connection.ConnectionFactory;

public class QueueConnectionFactory implements ConnectionFactory {

	
	public Connection connectToServer(String remoteServerAddress,
			int serverPort, int localPort) throws Exception {
		EMSConnection connection = null;
		boolean connected = false;
		while(!connected) {
			try {
				connection = new EMSConnection(remoteServerAddress, serverPort, null, null, null);
				connected = true;
			} catch (Exception e) {
				//try again
			}
		}
		return connection;
	}

}
