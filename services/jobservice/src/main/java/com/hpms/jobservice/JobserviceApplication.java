package com.hpms.jobservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.hpms.jobservice",
		"com.hpms.commonlib"
})
@EnableDiscoveryClient
public class JobserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobserviceApplication.class, args);
	}

}
