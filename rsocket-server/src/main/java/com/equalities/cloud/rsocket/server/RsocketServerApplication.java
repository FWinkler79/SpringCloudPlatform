package com.equalities.cloud.rsocket.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RsocketServerApplication {

	public static void main(String[] args) {
	  SpringApplication springApplication = new SpringApplication(RsocketServerApplication.class);
    
	  // Make sure we run reactive. 
    // See: https://docs.spring.io/spring-boot/docs/2.2.0.M6/reference/html/spring-boot-features.html#boot-features-web-environment
	  // springApplication.setWebApplicationType(WebApplicationType.REACTIVE); 
    
	  springApplication.run(args);	
	}
}
