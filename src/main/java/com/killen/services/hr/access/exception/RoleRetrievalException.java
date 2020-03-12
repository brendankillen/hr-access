package com.killen.services.hr.access.exception;

public class RoleRetrievalException extends Exception 
{
	private static final long serialVersionUID = 7264000556679078318L;
	
	public RoleRetrievalException(String roleType)
	{
		super("Error retrieving role by type | " + roleType);
	}
	
	public RoleRetrievalException(int roleId)
	{
		super("Error retrieving role by id| " + roleId);
	}
}
