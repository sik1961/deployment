/**
 * 
 */
package com.sik.property;

import java.util.Collection;

/**
 * @author sik
 *
 */
public interface EnvironmentProperty {

	String getKey();
	
	DefaultValue getDefaultValue();
	
	Collection<? extends EnvironmentProperty> getDefinedProperties();
	
	final class DefaultValue {
		public static final DefaultValue NONE = new DefaultValue("No default value available");
		
		private final String value;
		
		private DefaultValue(final String value) {
			super();
			this.value = value;
		}
		
		public static DefaultValue fromValue(final String value) {
			return new DefaultValue(value);
		}
		
		public String getValue() {
			return this.value;
		}
	}
	
}
