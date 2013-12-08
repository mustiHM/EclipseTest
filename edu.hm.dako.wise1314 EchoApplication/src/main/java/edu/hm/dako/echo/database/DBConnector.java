package edu.hm.dako.echo.database;

import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

/**
 * Bietet Dienste zum Verbinden und Datenaustausch mit einer MySQL Datenbank über XA.
 * @author mustafa
 *
 */
public class DBConnector {
	
	/**
	 * Erstellt die Verbindung zu einer MySQL Datanbank über XA
	 * @param userName der Benutzername
	 * @param password das Passwort
	 * @param databaseName der Name der Datenbank
	 * @throws Exception Fehler beim Verbinden
	 */
	public DBConnector(String userName, String password, String databaseName) throws Exception{
		MysqlXADataSource srcDB = new MysqlXADataSource();
		srcDB.setUser(userName);
		srcDB.setPassword(password);
		srcDB.setDatabaseName(databaseName);
		XAConnection srcXAConnection = srcDB.getXAConnection();
		Connection con = (Connection) srcXAConnection.getConnection();
		XAResource xaresource = srcXAConnection.getXAResource();
		Xid xid1 = null;
		xaresource.start(xid1, XAResource.TMNOFLAGS);
	}

}
