/**
 * 
 */
package com.sik.deployment.properties;

/**
 * @author sik
 *
 */
public class PropertyNotSetAndNoDefaultValueException extends RuntimeException {

	private static final long serialVersionUID = -3638673533928778466L;

	public PropertyNotSetAndNoDefaultValueException(final EnvironmentProperty property) {
		super(property.getKey() + " was not defined in the environment and has no default value");
	}
	
	
}
