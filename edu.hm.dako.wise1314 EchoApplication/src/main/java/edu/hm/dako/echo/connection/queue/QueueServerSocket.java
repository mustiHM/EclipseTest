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
	
	private Connection requestConnection;
	private Connection responseConnection;
	
	public QueueServerSocket(String requestIP, int requestPort, String requestQueue, 
			String responseIP, int responsePort, String responseQueue){
		this.requestIP = requestIP;
		this.requestPort = requestPort;
		this.requestQueue = requestQueue;
		this.responseIP = responseIP;
		this.responsePort = responsePort;
		this.responseQueue = responseQueue;
		isClosed = false;
		
		requestConnection = new QueueConnectionMock(requestIP, requestPort, requestQueue);
		responseConnection = new QueueConnectionMock(responseIP, responsePort, responseQueue);
	}
	
	@Override
	public Connection accept() throws Exception {
		/*
		 * Diese Methode hat kein blockierendes Verhalten!
		 * Vergleich: bei TCP wird solange blockiert, bis ein Verbindungswunsch eintrifft.
		 * Da hier keine Verbindungswünsche eintreffen, wird sofort die Verbindung zur Request-Queue zurückgegeben.
		 */
		
		return requestConnection;
	}

	@Override
	public void close() throws Exception {
		isClosed = true;
	}

	@Override
	public boolean isClosed() {
		return isClosed;
	}
	
	/**
	 * Gibt eine Verbindung zur richtigen Response-Queue zurück.
	 * Falls kein neuer Name angegeben wird, wird die Standard Queue zurückgegeben.
	 * @param Name der Queue bzw. des Clients
	 * @return Verbindung zur Response-Queue
	 */
	public Connection respond(String queueNameForClient){
		if(queueNameForClient==null)
			return responseConnection;
		else{
			return new QueueConnectionMock(responseIP, responsePort, queueNameForClient);
		}
	}

}
