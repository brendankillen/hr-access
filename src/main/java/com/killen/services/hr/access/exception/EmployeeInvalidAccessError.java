package com.killen.services.hr.access.exception;

public class EmployeeInvalidAccessError extends Exception 
{
	private static final long serialVersionUID = 6625213091208033430L;

	public EmployeeInvalidAccessError(String lookupName, String loggedInName)
	{
		super("Invalid access to employee data | " + lookupName + " | by employee | " + loggedInName);
	}
}
