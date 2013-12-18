package edu.hm.dako.echo.client;

import java.net.SocketTimeoutException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import edu.hm.dako.echo.common.EchoPDU;
import edu.hm.dako.echo.common.ExceptionHandler;
import edu.hm.dako.echo.common.SharedClientStatistics;
import edu.hm.dako.echo.connection.Connection;
import edu.hm.dako.echo.connection.ConnectionFactory;
import edu.hm.dako.echo.connection.queue.EMSConnection;
import edu.hm.dako.echo.connection.queue.QueueConnectionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Baut pro Client eine Verbindung zum Server auf und benutzt diese, um alle Nachrichten zu versenden.
 * Nachdem alle Nachrichten versendet wurden, wird die Verbindung abgebaut.
 */
public class ConnectionReusingQueue extends AbstractClient {
	
	private static Log Log = LogFactory.getLog(ConnectionReusingQueue.class);
	
	private Connection requestConnection;
	private Connection responseConnection;
	
	private QueueConnectionFactory factory;
	
	private long rttStartTime;
	private String clientName;
	
	// wird von der ClientFactory verwendet und richtet die Queue-Implementierung des AbstractClients her
	public ConnectionReusingQueue(int serverPort, String remoteServerAddress, int numberOfClient,
                                   int messageLength, int numberOfMessages, int clientThinkTime,
                                   SharedClientStatistics sharedData, ConnectionFactory connectionFactory) {
		super(serverPort, remoteServerAddress, numberOfClient, messageLength, numberOfMessages, clientThinkTime,
                sharedData, connectionFactory);
			try {
				clientName = "Client-Thread-" + clientNumber;
				//Gilt nur für den ersten Thread, danach wird keine Connection mehr aufgebaut sonst kam immer ein Fehler und ein Mutlithread hat gekracht!
				log.debug("Für den ersten Thread wird eine Connection aufgebaut..."); 
				requestConnection = connectionFactory.connectToServer(remoteServerAddress, serverPort, localPort);
				log.debug("Connection aufgebaut");
				if(connectionFactory instanceof QueueConnectionFactory){
					factory = (QueueConnectionFactory) connectionFactory;
					responseConnection = factory.acceptFromServer(remoteServerAddress, serverPort, clientName);
				}
				else {
					throw new Exception("Falsche QueueFactory!");
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
	}
	
	
	 /**
     * Client-Thread sendet hier alle Requests und wartet auf Antworten
     */
	public void run() {
		log.debug("Run-Methode wird ausgeführt. Name und Nummer des Client-Threads wird initalisiert..."); 
		Thread.currentThread().setName(clientName);
		log.debug("Client-Thread wurde initalisiert"); 
        try {
        	log.debug("Warte auf andere Clients..."); 
            waitForOtherClients();
//            connection = connectionFactory.connectToServer(remoteServerAddress, serverPort, localPort); //wurde nach oben in den Konstruktor ausgelagert, weil sonst bei Multithread expection
            log.debug("Schleife versucht alle Requests zu senden"); 
            for (int i = 0; i < numberOfMessagesToSend; i++) {
                try {
                	log.debug("EchoPdu wird gesendet: Nummer: "+i); 
                	//startzeit festlegen
                	rttStartTime = System.nanoTime();
                	doEcho(i); // nur senden
                	log.debug("Nachricht wurde verschickt. Warte auf Antwort..");
                	Thread.sleep(clientThinkTime);
                	EchoPDU receivedPdu = (EchoPDU) responseConnection.receive();// empfangen
                	log.debug("Antwort erhalten");
                	long rtt = System.nanoTime() - rttStartTime;
                	postReceive(i, receivedPdu, rtt);
                	
                } catch (SocketTimeoutException e) {
                   Log.debug(e.getMessage());
                }
            }
        } catch (Exception e) {
            ExceptionHandler.logExceptionAndTerminate(e);
        } finally {
            try {
            	log.debug("Client hat seine Requests durch. Connection abbauen..."); 
            	requestConnection.close();
            	responseConnection.close();
            	log.debug("Connection abgebaut"); 
            } catch (Exception e) {
                ExceptionHandler.logException(e);
            }
        }
	}
	
	
	private void doEcho(int i) throws Exception {
		requestConnection.send(constructEchoPDU(i));  
        sharedData.incrSentMsgCounter(clientNumber);
	}
}
