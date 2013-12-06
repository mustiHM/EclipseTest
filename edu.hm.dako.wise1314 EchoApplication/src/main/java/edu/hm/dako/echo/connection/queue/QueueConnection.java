package edu.hm.dako.echo.connection.queue;

import java.io.Serializable;

import edu.hm.dako.echo.connection.Connection;

public class QueueConnection implements Connection {

	private String queueName;
	
	public QueueConnection(String queueName){
		this.queueName = queueName;
	}
	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Serializable receive() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void send(Serializable message) throws Exception {
		// TODO Auto-generated method stub

	}

}
