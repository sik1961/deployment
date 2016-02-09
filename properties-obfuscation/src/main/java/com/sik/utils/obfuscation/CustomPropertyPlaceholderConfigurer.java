/**
 * 
 */
package com.sik.utils.obfuscation;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * @author sik
 *
 */
public class CustomPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer{

	private final EncoderDecoder<String, String> decoder = new Base64EncoderDecoder();
	
	@Override
	public String convertPropertyValue(final String originalValue) {
		if (originalValue.startsWith(Base64EncoderDecoder.PREFIX)) {
			return decoder.decode(originalValue);
		}
		return originalValue;
	}
	
}
