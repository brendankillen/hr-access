package com.killen.services.hr.access.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.killen.services.hr.access.test.controller.EmployeeControllerTests;
import com.killen.services.hr.access.test.service.EmployeeServiceTests;

@RunWith(Suite.class)
@Suite.SuiteClasses
({
	EmployeeControllerTests.class,
	EmployeeServiceTests.class
})
public class RunAllSuite 
{

}
