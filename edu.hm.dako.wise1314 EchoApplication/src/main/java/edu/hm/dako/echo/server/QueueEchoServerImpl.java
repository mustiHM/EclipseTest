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
import edu.hm.dako.echo.database.DBConnector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

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
		private DBConnector dbConnector;
		private java.sql.Connection dbConnection;
		
		private static final String USAGE = "usage: java JdbcExample [database] [commit|rollback] [number]";
	    private static final String SQL_SELECT = "select number from counter where client_id = ?";
	    private static final String SQL_UPDATE = "update counter set number = ? where id = ?";
	    private static final String USER_TRANSACTION_JNDI_NAME = "UserTransaction";
		
		private EchoWorker(Connection requestQueue, Connection responseQueue){
			this.requestConnection = requestQueue;
			this.responseConnection = responseQueue;
			
			// Anlegen der XA-Ressourcen
			
		}
		
		/**
		 * Liest die Anzahl an Nachrichten eines Clients aus der DB
		 * @param clientId die ClientID
		 * @return Anzahl der Nachrichten
		 */
		private int getNumberForClient(String clientId){
			
			int number = 0;
			try {
	            PreparedStatement pstmt = dbConnection.prepareStatement(SQL_SELECT);
	            pstmt.setString(1, clientId);
	            ResultSet rset = pstmt.executeQuery();
	            int numcols = rset.getMetaData().getColumnCount();
	            while (rset.next()) {
	                for (int i = 1; i <= numcols; i++) {
	                    number = rset.getInt(i);
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
			
			
			return number;
		}
		
		private void updateNumberForClient(String clientId, int number){
			
		}
		
		public void run() {
			// Abholen der Nachricht aus der Queue
			try {
				EchoPDU pdu = (EchoPDU) requestConnection.receive();
				if(pdu != null){
					// TODO weiterer Ablauf
					long startTime = System.currentTimeMillis(); // Zeit nehmen
					
					dbConnector = new DBConnector("root", "Password", "CountDB", "jdbc:mysql://localhost/CountDB");
					
					UserTransaction utx = null;
			        try {
			            System.out.println("create initial context");
			            Context ictx = new InitialContext();
			            System.out.println("lookup UserTransaction at : " + USER_TRANSACTION_JNDI_NAME);
			            utx = (UserTransaction) ictx.lookup(USER_TRANSACTION_JNDI_NAME);
			        } catch (Exception e) {
			            System.out.println("Exception of type :" + e.getClass().getName() + " has been thrown");
			            System.out.println("Exception message :" + e.getMessage());
			            e.printStackTrace();
			            System.exit(1);
			        }
			        
			        dbConnection = dbConnector.getConnection();
			        
			        utx.begin();
			        int oldNumber = getNumberForClient(pdu.getClientName());
			        
			        // TODO update statement
			        
			        utx.commit();
			        
			        utx = null;
			        
			        dbConnection.close();
			        dbConnection = null;
			        
			        dbConnector.stop();
			        
			        
					
					
					// mit Count-DB verbinden und Zähler erhöhen
					
					// mit Trace-DB verbinden und neuen Eintrag erstellen
					
					pdu.setMessage("das ist die Antwort des Servers");
					pdu.setServerThreadName("EchoWorker");
					
					long serverTime = System.currentTimeMillis() - startTime;
					pdu.setServerTime(serverTime);
					
					// Antwort in die Response-Queue schicken
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
