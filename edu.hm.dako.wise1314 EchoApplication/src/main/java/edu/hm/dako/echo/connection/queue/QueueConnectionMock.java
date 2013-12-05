package edu.hm.dako.echo.connection.queue;

import java.io.Serializable;

import edu.hm.dako.echo.connection.Connection;

/**
 * Simuliert eine Verbindung zu einer Queue.
 * Dient zum Entwickeln der weiteren Komponenten, solange die tatsächliche Queue-Implementierung nicht fertig ist.
 * @author mustafa
 *
 */
public class QueueConnectionMock implements Connection {

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
