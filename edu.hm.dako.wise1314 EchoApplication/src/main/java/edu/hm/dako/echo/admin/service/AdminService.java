package edu.hm.dako.echo.admin.service;

/**
 * Der Admin Service bietet dem Administrator Zugriff auf alle Datenbanken
 * @author Nastaran
 *
 */
public interface AdminService {

	/**
	 * Löscht alle Daten aus der Trace und der Count Datenbank
	 * @return Wenn alle Daten gelöscht sind, gibt die Methode ein true aus, andernfalls ein false
	 */
	public boolean deleteAllData();
	
	
	/**
	 * Gibt die Anzahl an verschickten Nachrichten eines Clients zurück 
	 * @param clientID Die Client ID wird mitgegeben, damit klar ist um welchen Client es sich handelt
	 * @return Anzahl der verschickten Nachrichten
	 */
	public int getNumberOfMessages (String clientID);
	
	
	
	
	
}
