package com.equalities.cloud.config.bootstrap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.config.server.environment.ConfigTokenProvider;
import org.springframework.cloud.config.server.environment.HttpRequestConfigTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(Another.class)
public class BootstrapConfiguration {

//  @Bean
//  public ConfigTokenProvider configTokenProvider(Environment environment) {
//    return new EnvironmentConfigTokenProvider(environment, "spring.cloud.config.server.vault.token");
//  }
  
  @Configuration(proxyBeanMethods = false)
  protected static class DefaultConfigTokenProvider {

    @Bean
    @ConditionalOnMissingBean(ConfigTokenProvider.class)
    public ConfigTokenProvider configTokenProvider(
        ObjectProvider<HttpServletRequest> httpRequest) {
      return new HttpRequestConfigTokenProvider(httpRequest);
    }

  }
}
