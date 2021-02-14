package com.killen.services.hr.access.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.killen.services.hr.access.entity.User;

public interface IUserRepository extends JpaRepository<User, Long> 
{
	User findByUsername(String username);
	
	User findOneById(int userId);
	
	boolean existsByUsername(String username);
}