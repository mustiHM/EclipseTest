package edu.hm.dako.echo.database;

import javax.naming.InitialContext;
import javax.sql.XADataSource;

import org.enhydra.jdbc.standard.StandardXADataSource;
import org.objectweb.jotm.Jotm;
import org.objectweb.transaction.jta.TMService;

import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;




/**
 * Bietet Dienste zum Verbinden und Datenaustausch mit einer MySQL Datenbank über XA.
 * @author mustafa
 *
 */
public class DBConnector {
	
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
        	
        	// creates an instance of JOTM with a local transaction factory which is not bound to a registry
            jotm = new Jotm(true, false);
            InitialContext ictx = new InitialContext();
            ictx.rebind(USER_TRANSACTION_JNDI_NAME, jotm.getUserTransaction());
            
        } catch (Exception e){
        	e.printStackTrace();
        }
        
        xads = new StandardXADataSource();
        ((StandardXADataSource) xads).setDriverName("org.gjt.mm.mysql.Driver");
        // url in diesem Format: jdbc:mysql://localhost/javatest
        ((StandardXADataSource) xads).setUrl(url);
        ((StandardXADataSource) xads).setTransactionManager(jotm.getTransactionManager());
        
        // TODO weitere Impl.
	}

	
	
}
