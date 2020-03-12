package com.killen.services.hr.access.exception;

public class EmployeeNameException extends Exception 
{
	private static final long serialVersionUID = -3365397778786958189L;
	
	public EmployeeNameException(String firstName, String lastName)
	{
		super("Employee name issues firstName | " + firstName + " lastName | " + lastName);
	}

}
