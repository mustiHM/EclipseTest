package edu.hm.dako.echo.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import edu.hm.dako.echo.common.EchoPDU;
import edu.hm.dako.echo.connection.Connection;
import edu.hm.dako.echo.connection.ServerSocket;
import edu.hm.dako.echo.connection.queue.QueueServerSocket;

public class QueueEchoServerImpl implements EchoServer {

	private static Log log = LogFactory.getLog(QueueEchoServerImpl.class);

	private final ExecutorService executorService;

	private QueueServerSocket socket; // da das Arbeiten mit der Queue anders ist als über TCP wird kein ServerSocket, sondern gleich der QueueServerSocket genutzt. 
	
	/**
	 * Wird z.B. von der ServerFactory aufgerufen und richtet die Queue-Implementierung des Echo-Servers her.
	 * @param executorService der Executor-Service für die Thread-Steuerung
	 * @param socket das Socket durch dem die Packet empfangen werden sollen
	 */
	public QueueEchoServerImpl(ExecutorService executorService, ServerSocket socket){
        this.executorService = executorService;
        this.socket = (QueueServerSocket) socket;
	}

	public void start() {
		PropertyConfigurator.configureAndWatch("log4j.server.properties", 60 * 1000);
        System.out.println("Echoserver wartet auf PDUs aus der Request Queue...");

        while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
        	try {
				
				/*
				 * Bei der QueueConnection wird an dieser Stelle keine Verbindung angenommen wie bei TCP.
				 * Hier wird einfach eine Verbindung zur Queue hergestellt und gewartet bis ein Paket eintrifft.
				 * Vorher bringt es nichts, unnötige Threads und Verbindungen aufzubauen.
				 */
				
        		Connection requestConnection = socket.accept();
        		Connection responseConnection = socket.respond();
        		
        		// Neuen Workerthread starten
                executorService.submit(new EchoWorker(requestConnection, responseConnection));
        		
        		
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Fehler beim Verbinden zu den Queues");
			}
        }
	}

	public void stop() throws Exception {
		System.out.println("EchoServer beendet sich");
        Thread.currentThread().interrupt();
        socket.close();
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("Das beenden des ExecutorService wurde unterbrochen");
            e.printStackTrace();
        }

	}
	
	/**
	 * Threads die Nachrichten aus der Request-Queue abholen, bearbeiten und zurück in die Response-Queue schicken
	 * @author mustafa
	 *
	 */
	private class EchoWorker implements Runnable {
		
		private Connection requestConnection;
		private Connection responseConnection;
		
		private EchoWorker(Connection requestQueue, Connection responseQueue){
			this.requestConnection = requestQueue;
			this.responseConnection = responseQueue;
		}
		
		public void run() {
			// Abholen der Nachricht aus der Queue
			try {
				EchoPDU pdu = (EchoPDU) requestConnection.receive();
				if(pdu != null){
					// TODO weiterer Ablauf
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Fehler beim Abholen aus der Request-Queue", e);
			}
			closeConnections();
		}
		
		/**
		 * Beendet alle Verbindungen zu den Queues
		 */
		private void closeConnections() {
			try {
				requestConnection.close();
				responseConnection.close();
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Fehler beim Schließen der Queue-Verbindungen", e);
			}
			
		}
		
	}

}
