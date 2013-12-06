package edu.hm.dako.echo.connection.queue;

import edu.hm.dako.echo.connection.Connection;
import edu.hm.dako.echo.connection.ServerSocket;

public class QueueServerSocket implements ServerSocket {

	private String requestIP;
	private int requestPort;
	private String requestQueue;
	private String responseIP;
	private int responsePort;
	private String responseQueue;
	private boolean isClosed; // in dieser Implementierung nicht so relevant, verglichen mit der TCP-Implementierung
	
	public QueueServerSocket(String requestIP, int requestPort, String requestQueue, 
			String responseIP, int responsePort, String responseQueue){
		this.requestIP = requestIP;
		this.requestPort = requestPort;
		this.requestQueue = requestQueue;
		this.responseIP = responseIP;
		this.responsePort = responsePort;
		this.responseQueue = responseQueue;
		isClosed = false;
	}
	
	@Override
	public Connection accept() throws Exception {
		/*
		 * Diese Methode muss ein blockierendes Verhalten haben!
		 * Vergleich: bei TCP wird solange blockiert, bis ein Verbindungswunsch eintrifft.
		 * Da hier keine Wünsche eintreffen, sondern nur Pakete in der Queue landen, wird solange blockiert, bis ein Packet eintrifft.
		 */
		Connection con = new QueueConnectionMock(requestIP, requestPort, requestQueue, 
				responseIP, responsePort, responseQueue);
		
		
		
		return con;
	}

	@Override
	public void close() throws Exception {
		isClosed = true;
	}

	@Override
	public boolean isClosed() {
		return isClosed;
	}

}
