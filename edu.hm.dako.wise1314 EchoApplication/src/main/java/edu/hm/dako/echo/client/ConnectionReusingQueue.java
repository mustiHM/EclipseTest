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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Baut pro Client eine Verbindung zum Server auf und benutzt diese, um alle Nachrichten zu versenden.
 * Nachdem alle Nachrichten versendet wurden, wird die Verbindung abgebaut.
 */
public class ConnectionReusingQueue extends AbstractClient {
	
	private static Log Log = LogFactory.getLog(ConnectionReusingQueue.class);
	
	private Connection connection;
	
	private static boolean isInitiated = false;
	
	private Session session = null; 
	private Destination destinationSender = null;
    private Destination destinationReceiver = null;
    
    private long rttStartTime;
	
	// wird von der ClientFactory verwendet und richtet die Queue-Implementierung des AbstractClients her
	public ConnectionReusingQueue(int serverPort, String remoteServerAddress, int numberOfClient,
                                   int messageLength, int numberOfMessages, int clientThinkTime,
                                   SharedClientStatistics sharedData, ConnectionFactory connectionFactory) {
		super(serverPort, remoteServerAddress, numberOfClient, messageLength, numberOfMessages, clientThinkTime,
                sharedData, connectionFactory);
		//if(!isInitiated){
			try {
				//Gilt nur für den ersten Thread, danach wird keine Connection mehr aufgebaut sonst kam immer ein Fehler und ein Mutlithread hat gekracht!
				log.debug("Für den ersten Thread wird eine Connection aufgebaut..."); 
				connection = connectionFactory.connectToServer(remoteServerAddress, serverPort, localPort);
				log.debug("Connection aufgebaut"); 
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		//}
//		try {
//			session = connection.createSession();
//			destinationSender = connection.createDestinationSender();
//			destinationReceiver = connection.createDestinationReceiver();
//			
//		} catch (JMSException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	
	 /**
     * Client-Thread sendet hier alle Requests und wartet auf Antworten
     */
	public void run() {
		log.debug("Run-Methode wird ausgeführt. Name und Nummer des Client-Threads wird initalisiert..."); 
		Thread.currentThread().setName("Client-Thread-" + clientNumber);
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
                	EchoPDU receivedPdu = (EchoPDU) connection.receive();// empfangen
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
            	connection.close();
            	log.debug("Connection abgebaut"); 
            } catch (Exception e) {
                ExceptionHandler.logException(e);
            }
        }
	}
	
	
	private void doEcho(int i) throws Exception {
		// RTT-Startzeit ermitteln
        
        sharedData.incrSentMsgCounter(clientNumber);
        connection.send(constructEchoPDU(i));  
        Thread.sleep(clientThinkTime);
	}
}
