package com.killen.services.hr.access.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.killen.services.hr.access.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> 
{
	Department findByName(String name);

	Department findOneById(int departmentId);
}
