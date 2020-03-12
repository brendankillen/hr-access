package com.killen.services.hr.access.exception;

import com.killen.services.hr.access.entity.Employee;

public class EmployeeCreateException extends Exception
{
	private static final long serialVersionUID = 6719316310302879455L;
	
	public EmployeeCreateException(Employee employee)
	{
		super("Error saving new employee | " + employee.toString());
	}

}
