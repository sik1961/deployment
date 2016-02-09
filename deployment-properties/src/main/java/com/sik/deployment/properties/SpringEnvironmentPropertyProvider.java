/**
 * 
 */
package com.sik.deployment.properties;

import org.springframework.core.env.Environment;

import com.sik.deployment.properties.EnvironmentProperty.DefaultValue;

/**
 * @author sik
 *
 */
public class SpringEnvironmentPropertyProvider implements EnvironmentPropertyProvider {

	private final Environment env;
	
	public SpringEnvironmentPropertyProvider(final Environment env) {
		this.env = env;
	}
	
	@Override
	public String getValue(final EnvironmentProperty property) {
		if (env.containsProperty(property.getKey())) {
			return env.getProperty(property.getKey());
		}
		if (property.getDefaultValue() == DefaultValue.NONE) {
			throw new PropertyNotSetAndNoDefaultValueException(property);
		}
		return property.getDefaultValue().getValue();
	}
	
	
}
