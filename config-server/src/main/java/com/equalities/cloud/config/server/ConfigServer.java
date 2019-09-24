package com.equalities.cloud.config.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class ConfigServer {

  private static final Logger logger = LoggerFactory.getLogger(ConfigServer.class);
  
	public static void main(String[] args) {
	  logger.debug("Application started");
		SpringApplication.run(ConfigServer.class, args);
	}
}
