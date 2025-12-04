package com.nhnacademy.byeol23front.commons.config;


import feign.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthFeignConfig {

	@Bean
	public Client authFeignClient(Client feignClient) {
		return feignClient;
	}
}
