package edu.hm.dako.echo.server;

import java.util.concurrent.ExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import edu.hm.dako.echo.connection.Connection;
import edu.hm.dako.echo.connection.ServerSocket;

public class QueueEchoServerImpl implements EchoServer {

	private static Log log = LogFactory.getLog(QueueEchoServerImpl.class);

	private final ExecutorService executorService;

	private ServerSocket socket;
	
	/**
	 * Wird z.B. von der ServerFactory aufgerufen und richtet die Queue-Implementierung des Echo-Servers her.
	 * @param executorService der Executor-Service für die Thread-Steuerung
	 * @param socket das Socket durch dem die Packet empfangen werden sollen
	 */
	public QueueEchoServerImpl(ExecutorService executorService, ServerSocket socket){
        this.executorService = executorService;
        this.socket = socket;
	}

	public void start() {
		PropertyConfigurator.configureAndWatch("log4j.server.properties", 60 * 1000);
        System.out.println("Echoserver wartet auf PDUs aus der Request Queue...");

        while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
        	try {
				Connection connection = socket.accept(); 
				/*
				 * Bei der QueueConnection wird an dieser Stelle keine Verbindung angenommen wie bei TCP.
				 * Hier wird einfach eine Verbindung zur Queue hergestellt und gewartet bis ein Paket eintrifft.
				 * Vorher bringt es nichts, unnötige Threads und Verbindungen aufzubauen.
				 */
				
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Fehler beim Verbinden zur Queue");
			}
        }
	}

	public void stop() throws Exception {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Threads die Nachrichten aus der Request-Queue abholen, bearbeiten und zurück in die Response-Queue schicken
	 * @author mustafa
	 *
	 */
	private class EchoWorker implements Runnable {
		
		private Connection connection;
		
		public void run() {
			
		}
	}

}
