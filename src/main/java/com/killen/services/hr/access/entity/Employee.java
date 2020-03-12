package com.killen.services.hr.access.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.killen.services.hr.access.service.EmployeeService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="EMPLOYEE", schema="HRACCESS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Employee 
{
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(name = "FIRST_NAME", nullable=false)
	private String firstName;
	@Column(name = "LAST_NAME", nullable=false)
	private String lastName;
	@Column(name = "MANAGER_ID", nullable=false)
	private int managerId = -1;
	@OneToOne
	private Role role;
	@OneToMany(fetch = FetchType.EAGER)
	@Column(nullable = true)
	private List<Finance> finances;
	@OneToOne
	private User user;
	@OneToOne
	Department department;
	@Column(name = "VACATION_BALANCE")
	private int vacation;
	@Column(name = "SALARY")
	private double salary;
	@Column(name = "BONUS")
	private double bonus;	
	
	public String getFullName()
	{
		return this.firstName + " " + this.lastName;
	}
	
	public boolean isManager()
	{
		if (this.getRole().getType().equals(EmployeeService.ROLE_MANAGER))
		{
			return true;
		}
		
		return false;
	}
	
	public boolean isHr()
	{
		if (this.getDepartment().getName().equals(EmployeeService.DEPARTMENT_HR))
		{
			return true;
		}
		
		return false;
	}
}
