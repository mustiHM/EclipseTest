package edu.hm.dako.echo.connection.queue;

import java.io.Serializable;
import java.util.ArrayList;

import edu.hm.dako.echo.common.EchoPDU;
import edu.hm.dako.echo.connection.Connection;

/**
 * Simuliert eine Verbindung zu einer Queue.
 * Dient zum Entwickeln der weiteren Komponenten, solange die tatsächliche Queue-Implementierung nicht fertig ist.
 * @author mustafa
 *
 */
public class QueueConnectionMock implements Connection {

	/**
	 * die simulierten Pdus der Queue
	 */
	private static ArrayList<EchoPDU> pdus;
	private static boolean isInitialized = false;
	private static int messageIndex;
	
	/**
	 * Initialisiert die simulierte Queue mit ein paar Nachrichten
	 */
	private static void initializeQueue(){
		pdus = new ArrayList<EchoPDU>();
		
		for(int messageCounter=0; messageCounter<10; messageCounter++){
			for(int clientCounter=0; clientCounter<10; clientCounter++){
				/*
				 * das Flag lastRequest wird bewusst leer gelassen, da auch der Server nicht darauf schaut.
				 * Hintergrund: in der Queue kommen die Pakete gemischt rein.
				 * Für unser Projekt ist es unwichtig, welcher Thread welche Nachricht bearbeitet.
				 */
				EchoPDU pdu = new EchoPDU();
				pdu.setClientName("Client-Nr: " + clientCounter);
				pdu.setMessage("Das ist die Nachricht-Nr: " + messageCounter);
				
				pdus.add(pdu);
			}
		}
		
		messageIndex = 0;
	}
	
	public QueueConnectionMock(){
		/*
		 * Da dieser Mock keine wirkliche Verbindung aufbauen muss, wird einfach nur eine Standart PDU erstellt,
		 * die immer als simulierte erhaltene PDU weitergegeben wird.
		 */
		
		if(!isInitialized){
			initializeQueue();
		}
		
	}
	
	@Override
	public void close() throws Exception {
		// wird nicht implementiert

	}

	@Override
	public Serializable receive() throws Exception {
		Serializable obj = pdus.get(messageIndex);
		messageIndex++;
		return obj;
	}

	@Override
	public void send(Serializable message) throws Exception {
		// wird nicht implementiert

	}

}
