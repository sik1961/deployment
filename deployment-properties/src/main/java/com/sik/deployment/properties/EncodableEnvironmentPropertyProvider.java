/**
 * 
 */
package com.sik.deployment.properties;

import org.springframework.core.env.Environment;

import com.sik.deployment.properties.obfuscation.Base64EncoderDecoder;

/**
 * @author sik
 *
 */
public class EncodableEnvironmentPropertyProvider implements EnvironmentPropertyProvider {
	
	private final EnvironmentPropertyProvider provider;
	private final Base64EncoderDecoder codec = new Base64EncoderDecoder();
	
	public EncodableEnvironmentPropertyProvider(final EnvironmentPropertyProvider provider) {
		this.provider = provider;
	}
	
	public static final EnvironmentPropertyProvider from(final Environment env) {
		return new EncodableEnvironmentPropertyProvider(new SpringEnvironmentPropertyProvider(env));
	}
	
	@Override
	public String getValue(final EnvironmentProperty property) {
		final String value = provider.getValue(property);
		if (!value.startsWith(Base64EncoderDecoder.PREFIX)) {
			return value;
		}
		return codec.decode(value);
	}
	
}
