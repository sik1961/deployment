package com.sik.jpa;



import javax.validation.Validator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;



@Configuration
public class BeanValidationConfigurator {

	/**
	 * @return factory for validator instances
	 */
	@Bean
	public LocalValidatorFactoryBean localValidatorFactoryBean() {
		return new LocalValidatorFactoryBean();
	}

	/**
	 * @return validator
	 */
	@Bean
	public Validator validator() {
		return localValidatorFactoryBean().getValidator();
	}
}
