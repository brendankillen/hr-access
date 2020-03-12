package com.killen.services.hr.access.exception;

public class UserInvalidException extends Exception {

	private static final long serialVersionUID = 7010125388302170015L;
	
	public UserInvalidException(int userId)
	{
		super("Unable to find user | " + userId);
	}
}
