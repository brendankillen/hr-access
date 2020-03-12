package com.killen.services.hr.access.exception;

public class UnauthorizedAccessException extends Exception 
{
	private static final long serialVersionUID = -6783400004088708695L;

	public UnauthorizedAccessException()
	{
		super("Unauthorized access, no valid user");
	}
}
