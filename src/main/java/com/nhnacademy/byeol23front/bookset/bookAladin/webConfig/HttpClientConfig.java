package com.nhnacademy.byeol23front.bookset.bookAladin.webConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class HttpClientConfig {

	@Bean
	public WebClient webClient(WebClient.Builder builder){
		return builder.build();
	}

}
