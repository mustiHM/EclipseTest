package edu.hm.dako.echo.connection.queue;

import java.io.Serializable;

import javax.jms.ObjectMessage;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueConnection;
import javax.jms.JMSException;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.Queue;
import javax.jms.QueueSender;

import com.tibco.tibjms.TibjmsQueueConnectionFactory;

import edu.hm.dako.echo.connection.Connection;

public class EMSConnection implements Connection {

	private String serverUrl;
	private int port;
	private String userName;
	private String password;
	private String queueName;
	private QueueConnection connection;
	private QueueSession session;
	private Queue queue; 
	
	public EMSConnection(String serverUrl, int port, String userName, String password, String queueName){
		this.serverUrl = serverUrl;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.queueName = queueName;
		
		try
		{
			//der port muss in eine String umgewandelt werden, damit der TibjmsQueueConnectionFactory seine Bedingungen erfüllt
			//warum der port am anfang als int deklariert wird, ist wegen der QueueConnectionFactory, da dort im Konstruktor 
			//ein int-Wert erforderlich ist
			String serverPort = String.valueOf(port);
			
			QueueConnectionFactory factory = new TibjmsQueueConnectionFactory(serverUrl, serverPort);
			
			QueueConnection connection = factory.createQueueConnection(userName, password);
			
			QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			
			Queue queue = session.createQueue(queueName);
		
			
		}  catch(JMSException e){
            e.printStackTrace();
            System.exit(0);
        }
			
			
		
		
	}
	
	@Override
	public void close() throws Exception {
		connection.close();

	}

	@Override
	public Serializable receive() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void send(Serializable message) throws Exception {
		QueueSender sender = session.createSender(queue);
		ObjectMessage objmsg = session.createObjectMessage();
		sender.send(objmsg);

	}

}
