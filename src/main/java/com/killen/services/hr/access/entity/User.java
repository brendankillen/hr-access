package com.killen.services.hr.access.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="USER", schema="HRACCESS")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User 
{
	public User(String username, String password)
	{
		this.username = username;
		this.password = password;
	}
	
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(name = "USER_NAME", unique=true, nullable=false)
	private String username;
	@ToString.Exclude
	@Column(name = "PASSWORD", nullable=false)
	private String password;
	@Column(name = "USER_ENABLED", nullable=false)
	private boolean userEnabled = true;
	@Column(name = "PRIVILEGE", nullable=false)
	private String privilege = "USER";
	@Column(name = "FAILED_COUNT")
	private int failedCount = 0;
	
	public boolean getUserEnabled()
	{
		return this.userEnabled;
	}
}
