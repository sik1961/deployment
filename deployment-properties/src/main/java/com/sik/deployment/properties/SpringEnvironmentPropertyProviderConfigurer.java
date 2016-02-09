/**
 * 
 */
package com.sik.deployment.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

/**
 * @author sik
 *
 */
@Configuration
@Import({
	PropertiesConfigurer.class
})
public class SpringEnvironmentPropertyProviderConfigurer {

	@Autowired private Environment env;
	
	@Bean EnvironmentPropertyProvider springEnvironmentPropertyProvider() {
		return EncodableEnvironmentPropertyProvider.from(env);
	}
	
	
	
}
