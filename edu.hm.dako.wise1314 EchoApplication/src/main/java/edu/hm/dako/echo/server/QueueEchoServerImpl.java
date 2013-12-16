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
import java.sql.SQLException;
import java.sql.Statement;

import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

public class QueueEchoServerImpl implements EchoServer {

	private static Log log = LogFactory.getLog(QueueEchoServerImpl.class);

	private final ExecutorService executorService;

	private QueueServerSocket socket;// da das Arbeiten mit der Queue anders ist als über TCP wird kein ServerSocket, sondern gleich der QueueServerSocket genutzt. 
	
	private DBConnector dbConnectorCountDB;
	private DBConnector dbConnectorTraceDB;
	private java.sql.Connection dbConnectionCountDB;
	private java.sql.Connection dbConnectionTraceDB;
	/**
	 * Wird z.B. von der ServerFactory aufgerufen und richtet die Queue-Implementierung des Echo-Servers her.
	 * @param executorService der Executor-Service für die Thread-Steuerung
	 * @param socket das Socket durch dem die Packete empfangen werden sollen
	 */
	public QueueEchoServerImpl(ExecutorService executorService, ServerSocket socket){
        this.executorService = executorService;
        if(socket instanceof QueueServerSocket){
        	this.socket = (QueueServerSocket) socket;
        }
        else {
        	throw new RuntimeException("Es wurde kein QueueServerSocket übergeben!");
        }
	}

	public void start() {
		PropertyConfigurator.configureAndWatch("log4j.server.properties", 60 * 1000);
        System.out.println("Echoserver wartet auf PDUs aus der Request Queue...");

        log.info("DB Objekte werden initiiert");
		try {
			dbConnectorCountDB = new DBConnector("app", "passwort", "countdb", "jdbc:mysql://localhost/countdb");
			dbConnectionCountDB = dbConnectorCountDB.getConnection();
			dbConnectorTraceDB = new DBConnector("app", "passwort", "tracedb", "jdbc:mysql://localhost/tracedb");
			dbConnectionTraceDB = dbConnectorTraceDB.getConnection();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		log.info("DB Objekte initiiert");
        
		//TODO aufm VM entfernen
		// fürs lokalte Testen wird hier ein Zähler gesetzt, der dafür sorgt, dass nur 105 Threads erstellt werden
        int i=0;
        while (!Thread.currentThread().isInterrupted() && !socket.isClosed() && i<105) {
        	try {
				
				/*
				 * Bei der QueueConnection wird an dieser Stelle keine Verbindung angenommen wie bei TCP.
				 * Hier wird einfach eine Verbindung zur Queue hergestellt und gewartet bis ein Paket eintrifft.
				 * Vorher bringt es nichts, unnötige Threads und Verbindungen aufzubauen.
				 */
				
        		Connection requestConnection = socket.accept();
        		Connection responseConnection = socket.respond(null); // muss vom Worker-Thread dynamisch richtig erstellt werden, sonst greift die Standart-Queue
        		
        		// Neuen Workerthread starten
                executorService.submit(new EchoWorker(requestConnection, responseConnection));
                
             // fürs lokale Testen wird hier eine kleine Schlafpause eingebaut, damit unsere Rechner nicht abrauchen (auf VM entfernen)
                Thread.sleep(500);
                i++;
        		
        		
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Fehler beim Verbinden zu den Queues");
			}
        }
        
        
	}

	public void stop() throws Exception {
		System.out.println("EchoServer beendet sich");
		log.info("EchoServer wird beendet..");
        Thread.currentThread().interrupt();
        dbConnectionCountDB.close();
        dbConnectionCountDB = null;
        dbConnectionTraceDB.close();
        dbConnectionTraceDB = null;
        
        dbConnectorCountDB.stop();
        dbConnectorTraceDB.stop();
        
        socket.close();
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("Das beenden des ExecutorService wurde unterbrochen");
            e.printStackTrace();
        }

        log.info("alle Verbindungen wurden beendet, der EchoServer wird jetzt beendet.");
	}
	
	/**
	 * Threads die Nachrichten aus der Request-Queue abholen, bearbeiten und zurück in die Response-Queue schicken
	 * @author mustafa
	 *
	 */
	private class EchoWorker implements Runnable {
		
		private Connection requestConnection;
		private Connection responseConnection;
		
		
		
		private static final String SQL_SELECT_NUMBER = "select number from counter where client_id = ?";
	    private static final String SQL_UPDATE_NUMBER = "update counter set number = number+1 where client_id = ?";
	    private static final String SQL_INSERT_COUNTER = "insert into counter(client_id, number) values (?,?);";
	    private static final String SQL_INSERT_LOGS = "insert into logs(client_name, request) values (?,?);";
	    private static final String USER_TRANSACTION_JNDI_NAME = "UserTransaction";
		
		private EchoWorker(Connection requestQueue, Connection responseQueue){
			this.requestConnection = requestQueue;
			this.responseConnection = responseQueue;
		}
		
		/**
		 * Speichert einen Log-Eintrag zu einem Request-PDU in die Datenbank.
		 * @param clientName Name des Clients
		 * @param message die Nachricht des Clients
		 */
		private void insertTrace(String clientName, String message){
			try {
				PreparedStatement pstmt = dbConnectionTraceDB.prepareStatement(SQL_INSERT_LOGS);
				pstmt.setString(1, clientName);
				pstmt.setString(2, message);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Speichert einen Client mit seiner Nachrichtenanzahl in die Datenbank
		 * @param clientId die ClientID
		 * @param number die Nachrichtennr dieses Clients
		 */
		private void insertClient(String clientId, int number){
			try {
				PreparedStatement pstmt = dbConnectionCountDB.prepareStatement(SQL_INSERT_COUNTER);
				pstmt.setString(1, clientId);
				pstmt.setInt(2, number);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		/**
		 * Liest die Anzahl an Nachrichten eines Clients aus der DB
		 * @param clientId die ClientID
		 * @return Anzahl der Nachrichten
		 */
		private int getNumberForClient(String clientId){
			
			int number = 0;
			try {
	            PreparedStatement pstmt = dbConnectionCountDB.prepareStatement(SQL_SELECT_NUMBER);
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
		
		
		/**
		 * Erhöht die Anzahl an Nachrichten eines Clients in der DB
		 * @param clientId die ClientID
		 */
		private void increaseNumberForClient(String clientId){
			try {
				PreparedStatement pstmt = dbConnectionCountDB.prepareStatement(SQL_UPDATE_NUMBER);
				pstmt.setString(1, clientId);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			// Abholen der Nachricht aus der Queue
			try {
				log.info("Worker-Thread wartet auf Requests..");
				EchoPDU pdu = (EchoPDU) requestConnection.receive();
				if(pdu != null){
					
					log.info("Worker-Thread hat was erhalten..");
					long startTime = System.currentTimeMillis(); // Zeit nehmen
					
					// ResponseQueue für den Client dynamisch erstellen
					responseConnection = socket.respond(pdu.getClientName());
					
					
					UserTransaction utx = null;
			        try {
			            System.out.println("create initial context");
			            Context ictx = new InitialContext();
			            System.out.println("lookup UserTransaction at : " + USER_TRANSACTION_JNDI_NAME);
			            log.debug("Baue Transaktion auf..");
			            utx = (UserTransaction) ictx.lookup(USER_TRANSACTION_JNDI_NAME);
			            log.debug("Transaktion aufgebaut..");
			        } catch (Exception e) {
			            System.out.println("Exception of type :" + e.getClass().getName() + " has been thrown");
			            System.out.println("Exception message :" + e.getMessage());
			            e.printStackTrace();
			            System.exit(1);
			        }
			        
			        if(dbConnectionCountDB == null || dbConnectionTraceDB == null){
			        	log.error("Keine DB Verbindung");
			        }
			        else{
			        	log.debug("DB Verbindungen erstellt");
			        }
			        
			        utx.begin();
			        
			        int oldValue = getNumberForClient(pdu.getClientName());
			        log.debug("Die alte Anzahl: " + oldValue);
			        
			        // falls der Client neu ist, muss erst ein Insert gemacht werden, ansonsten ein Update
			        if(oldValue == 0){
			        	insertClient(pdu.getClientName(), 1);
			        }
			        else{
			        	increaseNumberForClient(pdu.getClientName());
			        }
			        
			        // mit Trace-DB verbinden und neuen Eintrag erstellen
			        insertTrace(pdu.getClientName(), pdu.getMessage());
			        
			        try {
			        	utx.commit();
			        } catch (Exception e){
			        	log.error("Fehler beim DB Commit" + e);
			        	log.info("Verschicke Packet wieder zurück in die Request-Queue");
						requestConnection.send(pdu);
			        }
			        
			        utx = null;
			        
					
					pdu.setMessage("das ist die Antwort des Servers");
					pdu.setServerThreadName("EchoWorker");
					
					long serverTime = System.currentTimeMillis() - startTime;
					pdu.setServerTime(serverTime);
					
					// Antwort in die Response-Queue schicken
					responseConnection.send(pdu);
				}
				else{
					log.info("Worker-Thread hat nichts erhalten..");
				}
				
			} 
			catch (Exception e) {
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
