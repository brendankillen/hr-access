package com.killen.services.hr.access.exception;

public class EmployeeByUserNameException extends Exception
{
	private static final long serialVersionUID = -5591133850834976644L;
	
	public EmployeeByUserNameException(String username)
	{
		super("Error getting employee from username | " + username);
	}

}
