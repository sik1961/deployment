/**
 * 
 */
package com.sik.deployment.properties;

import org.springframework.context.annotation.Bean;

import com.sik.deployment.properties.obfuscation.CustomPropertySourcesPlaceholderConfigurer;

/**
 * @author sik
 *
 */
public class PropertiesConfigurer {

	@Bean
	public static CustomPropertySourcesPlaceholderConfigurer decodingPropertySourcesPlaceholderConfigurer() {
		
		final CustomPropertySourcesPlaceholderConfigurer configurer = new CustomPropertySourcesPlaceholderConfigurer();
		configurer.setIgnoreResourceNotFound(true);
		return configurer;
	}
	
	
}
