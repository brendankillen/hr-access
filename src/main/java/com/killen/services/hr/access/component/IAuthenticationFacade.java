package com.killen.services.hr.access.component;

import org.springframework.security.core.Authentication;

public interface IAuthenticationFacade 
{
    Authentication getAuthentication();
}