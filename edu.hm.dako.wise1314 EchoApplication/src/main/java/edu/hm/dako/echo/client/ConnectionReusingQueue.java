package edu.hm.dako.echo.client;

import java.net.SocketTimeoutException;

import edu.hm.dako.echo.common.EchoPDU;
import edu.hm.dako.echo.common.ExceptionHandler;
import edu.hm.dako.echo.common.SharedClientStatistics;
import edu.hm.dako.echo.connection.Connection;
import edu.hm.dako.echo.connection.ConnectionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConnectionReusingQueue extends AbstractClient {
	
	private static Log Log = LogFactory.getLog(ConnectionReusingQueue.class);
	
	private Connection connection;
	
	// wird von der ClientFactory verwendet und richtet die Queue-Implementierung des AbstractClients her
	public ConnectionReusingQueue(int serverPort, String remoteServerAddress, int numberOfClient,
                                   int messageLength, int numberOfMessages, int clientThinkTime,
                                   SharedClientStatistics sharedData, ConnectionFactory connectionFactory) {
		super(serverPort, remoteServerAddress, numberOfClient, messageLength, numberOfMessages, clientThinkTime,
                sharedData, connectionFactory);
	}
	
	
	 /**
     * Client-Thread sendet hier alle Requests und wartet auf Antworten
     */
	public void run() {
		Thread.currentThread().setName("Client-Thread-" + clientNumber);
        try {
            waitForOtherClients();
            connection = connectionFactory.connectToServer(remoteServerAddress, serverPort, localPort);
            for (int i = 0; i < numberOfMessagesToSend; i++) {
                try {
                    doEcho(i);
                } catch (SocketTimeoutException e) {
                   Log.debug(e.getMessage());
                }
            }
        } catch (Exception e) {
            ExceptionHandler.logExceptionAndTerminate(e);
        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                ExceptionHandler.logException(e);
            }
        }
	}
	
	
	private void doEcho(int i) throws Exception {
		// RTT-Startzeit ermitteln
        long rttStartTime = System.nanoTime();
        sharedData.incrSentMsgCounter(clientNumber);
        connection.send(constructEchoPDU(i));
        EchoPDU receivedPdu = (EchoPDU) connection.receive();
        long rtt = System.nanoTime() - rttStartTime;
        postReceive(i, receivedPdu, rtt);
        Thread.sleep(clientThinkTime);
	}
}
