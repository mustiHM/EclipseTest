package edu.hm.dako.echo.database;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.XAConnection;
import javax.sql.XADataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.enhydra.jdbc.standard.StandardXADataSource;
import org.objectweb.jotm.Jotm;
import org.objectweb.transaction.jta.TMService;

import edu.hm.dako.echo.server.QueueEchoServerImpl;





/**
 * Bietet Dienste zum Verbinden und Datenaustausch mit einer MySQL Datenbank über XA.
 * @author mustafa
 *
 */
public class DBConnector {
	
	private static Log log = LogFactory.getLog(DBConnector.class);
	private String userName;
	private String password;
	private String databaseName;
	private String url;
    private TMService jotm;
    private XADataSource xads;
    private static final String USER_TRANSACTION_JNDI_NAME = "UserTransaction";
	
	public DBConnector(String userName, String password, String databaseName, String url) throws Exception{
		this.userName = userName;
		this.password = password;
		this.databaseName = databaseName;
		this.url = url;
		
		// Get a transction manager       
        try {
        	log.debug("Versuche JOTM anzulegen..");
        	// creates an instance of JOTM with a local transaction factory which is not bound to a registry
            jotm = new Jotm(true, false);
            log.debug("JOTM angelegt, versuche Context anzulegen..");
            InitialContext ictx = new InitialContext();
            log.debug("Context angelegt, versuche Rebind..");
            ictx.rebind(USER_TRANSACTION_JNDI_NAME, jotm.getUserTransaction());
            log.debug("Rebind erfolgreich");
            
        } catch (Exception e){
        	e.printStackTrace();
        }
        
        log.debug("versuche XA DB zu bekommen..");
        xads = new StandardXADataSource();
        log.debug("XA DB bekommen, versuche Treiber zu setzen..");
        ((StandardXADataSource) xads).setDriverName("org.gjt.mm.mysql.Driver");
        // url in diesem Format: jdbc:mysql://localhost/javatest
        ((StandardXADataSource) xads).setUrl(url);
        log.debug("Treiber und URL gesetzt, versuche Manager zu setzen..");
        ((StandardXADataSource) xads).setTransactionManager(jotm.getTransactionManager());
        log.debug("JOTM Manager gesetzt");
        
	}
	
	public Connection getConnection() throws SQLException {
        XAConnection xaconn = xads.getXAConnection(userName, password);
        return xaconn.getConnection();
    }

	public void stop() {
    	xads = null;
        try {
           InitialContext ictx = new InitialContext();
           ictx.unbind(USER_TRANSACTION_JNDI_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        jotm.stop();
        jotm = null;
    }
	
	
}
