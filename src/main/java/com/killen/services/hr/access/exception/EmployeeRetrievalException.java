package com.killen.services.hr.access.exception;

public class EmployeeRetrievalException extends Exception 
{
	private static final long serialVersionUID = -5513229067075215296L;
	
	public EmployeeRetrievalException(int employeeId) 
	{
		super("Error getting employee | " + employeeId);
	}
}
