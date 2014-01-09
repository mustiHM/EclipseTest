package edu.hm.dako.echo.admin.service.impl;

import edu.hm.dako.echo.admin.service.AdminService;

public class AdminServiceMock implements AdminService{

	private int counter = 0;
	
	
	
	@Override
	public boolean deleteAllData() {
		boolean deleted = false;
		counter++;
			if (counter % 2 != 0){
				deleted = true;
				return deleted;
			}
			else
				return deleted;
		
	}

	@Override
	public int getNumberOfMessages(String clientID) {
		if (clientID.equals("client1")){
			return 1;
		}
		else
		return 0;
	}
	
	

}
