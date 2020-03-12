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
@Table(name = "ROLE", schema = "HRACCESS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Role 
{
	@Id
	@Column(name = "ID")
	private int id;
	@Column(name = "TYPE", nullable=false)
	private String type;
}
