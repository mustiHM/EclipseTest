package edu.hm.dako.echo.connection.queue;

import java.io.Serializable;

import javax.jms.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.hm.dako.echo.common.EchoPDU;
import edu.hm.dako.echo.connection.Connection;

public class QueueConnection implements Connection {

	private static Log log = LogFactory.getLog(QueueConnection.class);
	
	private String url;
	private int port;
	private String queueName;
	private javax.jms.Connection connection   = null;
	private Session session;
	private Destination destination;
	private MessageProducer msgProducer;
	private MessageConsumer msgConsumer;
	
	private String username = "";
	private String password = "";
	
	public QueueConnection(String url, int port, String queueName){
		this.url = url;
		this.port = port;
		this.queueName = queueName;
		
		ConnectionFactory factory = new com.tibco.tibjms.TibjmsConnectionFactory(url);
		
		try {
			connection = factory.createConnection(username,password);
			connection.start();
			
			/* create the session */
            session = connection.createSession(javax.jms.Session.AUTO_ACKNOWLEDGE);
            
            destination = session.createQueue(queueName);
            msgProducer = session.createProducer(null);
            msgConsumer = session.createConsumer(destination);
            
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public Session createSession() throws JMSException{
		return session = connection.createSession(javax.jms.Session.AUTO_ACKNOWLEDGE);
        
	}
	
	public Serializable receive() throws JMSException {
		
		
		ObjectMessage msg = null;
		
		boolean receivedSomething = false;
		while (!receivedSomething)
        {
			msg = (ObjectMessage) msgConsumer.receive();
	        if(msg != null){
	        	receivedSomething = true;
		    }
	        else{
	        	// falls nichts empfangen wurde, wieder neu empfangen
	        	log.debug("nichts aus der Queue erhalten");	
	        }
        }
		
        
		if(msg.getObject() instanceof EchoPDU){
			log.info("msg ist eine EchoPDU");
			return (EchoPDU) msg.getObject();
		}
		log.debug("msg ist keine EchoPDU");
		return null;
	}

	public void send(Serializable message) throws Exception {
		ObjectMessage msg;
		
		msg = session.createObjectMessage();
		msg.setObject(message);
		
		msgProducer.send(destination, msg);
		
	}

	public void close() throws Exception {
		connection.close();
	}

}
