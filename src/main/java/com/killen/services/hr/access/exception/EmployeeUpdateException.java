package com.killen.services.hr.access.exception;

import com.killen.services.hr.access.entity.Employee;

public class EmployeeUpdateException extends Exception 
{
	private static final long serialVersionUID = -1827777683660362620L;
	
	public EmployeeUpdateException(Employee employee)
	{
		super("Error when updating employee | " + employee);
	}
}
