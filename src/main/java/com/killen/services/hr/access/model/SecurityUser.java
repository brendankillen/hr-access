package com.killen.services.hr.access.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.killen.services.hr.access.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SecurityUser implements UserDetails 
{
	private static final String ROLE_PREFIX = "ROLE_";
	private static final long serialVersionUID = -3388528417969990585L;
	
	private User user;
	private List<String> authorities;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() 
	{
		List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();

		for(String authority: authorities)
		{
			list.add(new SimpleGrantedAuthority(ROLE_PREFIX + authority.toUpperCase()));
		}
        
        return list;
	}

	@Override
	public String getPassword() 
	{
		return user.getPassword();
	}

	@Override
	public String getUsername() 
	{
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() 
	{
		return true;
	}

	@Override
	public boolean isAccountNonLocked() 
	{
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() 
	{
		return true;
	}

	@Override
	public boolean isEnabled() 
	{
		if (user.getFailedCount() >= 3)
		{
			return false;
		}
		
		return user.getUserEnabled();
	}
}
