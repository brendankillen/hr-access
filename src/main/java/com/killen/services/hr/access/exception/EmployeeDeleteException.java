package com.killen.services.hr.access.exception;

import com.killen.services.hr.access.entity.Employee;

public class EmployeeDeleteException extends Exception
{
	private static final long serialVersionUID = -4596861053311715882L;
	
	public EmployeeDeleteException(Employee employee)
	{
		super("Error saving employee | " + employee);
	}
}
