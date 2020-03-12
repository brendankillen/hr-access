package com.killen.services.hr.access.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="FINANCE", schema="HRACCESS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Finance 
{
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(name = "ADDED", nullable=false)
	private Date added;
	@Column(name = "TYPE", nullable=false)
	private String type;
	@Column(name = "OLD_VALUE", nullable=false)
	private double oldValue;
	@Column(name = "NEW_VALUE", nullable=false)
	private double newValue;
}
