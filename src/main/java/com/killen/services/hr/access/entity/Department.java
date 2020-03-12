package com.killen.services.hr.access.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="DEPARTMENT", schema="HRACCESS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Department 
{
	@Id
	@Column(name = "ID")
	private int id;
	@Column(name = "NAME", nullable=false)
	private String name;
}
