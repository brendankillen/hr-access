package com.killen.services.hr.access.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.killen.services.hr.access.entity.Role;

public interface IRoleRepository extends JpaRepository<Role, Long>
{
	Role findOneById(int roleId);
	
	Role findByType(String type);
}
