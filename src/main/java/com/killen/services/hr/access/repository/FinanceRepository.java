package com.killen.services.hr.access.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.killen.services.hr.access.entity.Finance;

public interface FinanceRepository extends JpaRepository<Finance, Long>
{

}
