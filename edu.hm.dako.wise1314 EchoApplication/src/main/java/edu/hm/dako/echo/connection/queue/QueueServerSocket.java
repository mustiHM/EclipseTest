package edu.hm.dako.echo.connection.queue;

import edu.hm.dako.echo.connection.Connection;
import edu.hm.dako.echo.connection.ServerSocket;

public class QueueServerSocket implements ServerSocket {

	String requestQueue;
	String responseQueue;
	
	public QueueServerSocket(String requestQueue, String responseQueue){
		this.requestQueue = requestQueue;
		this.responseQueue = responseQueue;
	}
	
	@Override
	public Connection accept() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

}
