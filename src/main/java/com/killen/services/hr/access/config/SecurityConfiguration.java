package com.killen.services.hr.access.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.killen.services.hr.access.service.CustomAuthenticationFailureHandler;
import com.killen.services.hr.access.service.SecurityUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter 
{
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private SecurityUserDetailsService userDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder authenticationManager) throws Exception 
	{
		authenticationManager.authenticationProvider(authenticationProvider());
	}
	
	@Bean(name = "userDetailsService")
    public UserDetailsService userDetailsService() 
	{
		return userDetailsService;
	}
	 
	@Bean
	public DaoAuthenticationProvider authenticationProvider()
	{
	    DaoAuthenticationProvider authProvider= new DaoAuthenticationProvider();
	    authProvider.setUserDetailsService(userDetailsService());

	    authProvider.setPasswordEncoder(bCryptPasswordEncoder);
	    
	    return authProvider;
	}
	
	@Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() 
	{
        return new CustomAuthenticationFailureHandler();
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception 
	{
		http.authorizeRequests().antMatchers("/").permitAll()
												 .antMatchers("/login").permitAll()
												 .antMatchers("/admin").hasAnyAuthority("ROLE_ADMIN")
												 .antMatchers("/hr").hasAnyAuthority("ROLE_HR")
												 .antMatchers("/manager").hasAnyAuthority("ROLE_MANAGER")
												 .antMatchers("/employee").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
												 .antMatchers("/employee/*").hasAnyAuthority("ROLE_HR", "ROLE_MANAGER")										
												 .antMatchers("/employee-edit").hasAnyAuthority("ROLE_MANAGER")
												 .antMatchers("/employee-new").hasAnyAuthority("ROLE_HR")
												 .anyRequest().authenticated()
												 .and()
												 .formLogin().loginPage("/login")
												 .defaultSuccessUrl("/redirect")
												 //.failureHandler(customAuthenticationFailureHandler())
												 .and()
												 .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/");
	}

	@Override
	public void configure(WebSecurity web) throws Exception 
	{
		web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
	}
}