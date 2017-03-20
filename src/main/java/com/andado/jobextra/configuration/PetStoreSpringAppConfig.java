
package com.andado.jobextra.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

//@formatter:off
@Configuration
@Import({DynamoDBConfiguration.class})
@ComponentScan(basePackages = {
                //Common for all function
                "com.andado.jobextra.controller.errors",
                // Specific per function
                "com.andado.jobextra.dao.pets",
                "com.andado.jobextra.controller.pets"
      })
@PropertySource({
        "classpath:/application.properties"})
//@formatter:on
public class PetStoreSpringAppConfig {

  @Bean
  static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  LocalValidatorFactoryBean validator() {
    return new LocalValidatorFactoryBean();
  }

}
