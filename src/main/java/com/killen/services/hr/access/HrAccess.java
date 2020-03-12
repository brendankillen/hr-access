package com.killen.services.hr.access;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@SpringBootApplication
@EnableEncryptableProperties
public class HrAccess 
{
    public static void main(String[] args) 
    {
        SpringApplication.run(HrAccess.class, args);
    }
}