package com.killen.services.hr.access.exception;

public class DepartmentRetrievalException extends Exception 
{
	private static final long serialVersionUID = -7597062292866500191L;
	
	public DepartmentRetrievalException(String departmentName)
	{
		super("Error retrieving department by name | " + departmentName);
	}
	
	public DepartmentRetrievalException(int departmentId)
	{
		super("Error retrieving department by id | " + departmentId);
	}	
}
