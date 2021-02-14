package com.killen.services.hr.access.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.killen.services.hr.access.entity.Employee;

public interface IEmployeeRepository extends JpaRepository<Employee, Long> 
{
	Employee findOneById(int id);
	
	Employee findOneByUserId(int userId);
	
	List<Employee> findAllByRoleId(int roleId, Sort sort);
	
	List<Employee> findAllByManagerId(int managerId, Sort sort);
	
	List<Employee> findByDepartmentNameNot(String departmentName, Sort sort);
	
	List<Employee> findByManagerIdNotAndDepartmentNameNot(int managerId, String departmentName, Sort sort);
	
	List<Employee> findByUserUserEnabled(boolean isEnabled, Sort sort);
}
