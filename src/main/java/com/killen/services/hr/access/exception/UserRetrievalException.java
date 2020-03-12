package com.killen.services.hr.access.exception;

public class UserRetrievalException extends Exception 
{
	private static final long serialVersionUID = 1604803950958292436L;
	
	public UserRetrievalException(int userId)
	{
		super("Error retrieving user | " + userId);
	}
}
