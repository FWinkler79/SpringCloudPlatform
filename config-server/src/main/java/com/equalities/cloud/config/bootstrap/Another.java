package com.equalities.cloud.config.bootstrap;

import org.springframework.cloud.config.server.environment.ConfigTokenProvider;
import org.springframework.cloud.config.server.environment.EnvironmentConfigTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class Another {
  
  @Bean
  public ConfigTokenProvider configTokenProvider(Environment environment) {
    return new EnvironmentConfigTokenProvider(environment, "spring.cloud.config.server.vault.token");
  }
}
