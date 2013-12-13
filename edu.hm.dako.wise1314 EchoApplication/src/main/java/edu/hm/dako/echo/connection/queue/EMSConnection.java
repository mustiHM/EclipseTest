package edu.hm.dako.echo.connection.queue;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

import com.tibco.tibjms.TibjmsConnectionFactory;

import edu.hm.dako.echo.connection.Connection;

public class EMSConnection implements Connection {

	//Parameter für den Konstruktor
	private String serverUrl;
	private int port;
	private String userName;
	private String password;
	//private String queueName;
	
	//Variablen
	private javax.jms.Connection connection = null;
    private Session session = null;
    private MessageProducer msgProducer = null;
    private MessageConsumer msgConsumer = null;
    private Destination destinationSender = null;
    private Destination destinationReceiver = null;
	
    private static Log log = LogFactory.getLog(EMSConnection.class);
    
    private static boolean isInitialized = false;
    private static ConnectionFactory factory;
    
	public EMSConnection(String serverUrl, int port, String userName, String password){
		this.serverUrl = serverUrl;
		this.port = port;
		this.userName = userName;
		this.password = password;
		
		try
		{
			//der port muss in eine String umgewandelt werden, damit der TibjmsQueueConnectionFactory seine Bedingungen erfüllt
			//warum der port am anfang als int deklariert wird, ist wegen der QueueConnectionFactory, da dort im Konstruktor 
			//ein int-Wert erforderlich ist
			String serverPort = String.valueOf(port);
			
			if(!isInitialized){
				factory = new TibjmsConnectionFactory(serverUrl, serverPort);
				isInitialized = true;
			}
			
			log.debug("Connection mit Username und Password aufbauen");
			connection = factory.createConnection(userName, password);
			log.debug("Connection aufgebaut");
			
			//Session anlegen
			log.debug("Session aufbauen");
			session = connection.createSession(javax.jms.Session.AUTO_ACKNOWLEDGE);
			log.debug("Session aufgebaut");
			
			//Destinations für request-queue und repsonse-queue 
			log.debug("Destinations für request und repsonse queue anlegen");
			destinationSender = session.createQueue("requests"); //Request-Queue
			destinationReceiver = session.createQueue("responses"); // Response-Queue
			log.debug("Destinations angelegt");
			
			
		}  catch(JMSException e){
            e.printStackTrace();
            System.exit(0);
        }
			
			
		
		
	}
	
//	public static Session createSession() throws JMSException {
//		//Session anlegen
//		log.debug("Session aufbauen");
//		return null;
//		//return connection.createSession(javax.jms.Session.AUTO_ACKNOWLEDGE);
//	}
//	
//	public static Destination createDestinationSender() throws JMSException {
//		//Destination für request-queue anlegen
//		log.debug("Destination für Request-Queue anlegen");
//		//return connection.createSession(javax.jms.Session.AUTO_ACKNOWLEDGE).createQueue("requests");
//		return null;
//	}
//	
//	public static Destination createDestinationReceiver() throws JMSException {
//		//Destination für response-queue anlegen
//		log.debug("Destination für Response-Queue anlegen");
//		//return connection.createSession(javax.jms.Session.AUTO_ACKNOWLEDGE).createQueue("responses");
//		return null;
//	}
	
	




	//QueueConnection wird beendet
	public void close() throws Exception {
		connection.close();
		log.debug("Connection closed");
		
	}

	//Message wird aus der Queue received
	public Serializable receive() throws Exception {
		//Message anlegen
		ObjectMessage msg = session.createObjectMessage();
		
		//Consumer anlegen, mit der Respones-Queue als Destination
		log.debug("MessageConsumer für RepsonseQueue anlegen");
		msgConsumer = session.createConsumer(destinationReceiver);
		log.debug("MessageConsumer angelegt");
		
		//Message empfangen
		log.debug("Versuche Message zu empfangen...");
		msg = (ObjectMessage) msgConsumer.receive();
		if(msg == null){
			log.debug("Keine Message empfangen!!!");
			return null;
		}
		log.debug("Message empfangen");
		return (Serializable) msg;
		
	}

	//Message wird an die Queue gesendet
	public void send(Serializable message) throws Exception {
		//Message anlegen
		ObjectMessage msg = session.createObjectMessage(message);
		
		//Producer anlegen, mit Standard-Destination null
		log.debug("MessageProducer anlegen");
		msgProducer = session.createProducer(null);
		log.debug("MessageProducer angelegt");
		
		//Message senden an Request-Queue
		log.debug("Message an Request-Queue senden");
		msgProducer.send(destinationSender, msg);
		log.debug("Message an Request-Queue gesendet!");

		
	}

}
