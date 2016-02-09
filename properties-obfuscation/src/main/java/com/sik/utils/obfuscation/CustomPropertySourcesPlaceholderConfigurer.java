/**
 * 
 */
package com.sik.utils.obfuscation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.util.StringValueResolver;

/**
 * @author sik
 *
 */
public class CustomPropertySourcesPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer {
	private static final Logger LOG = Logger.getLogger(CustomPropertySourcesPlaceholderConfigurer.class);
	
	private final EncoderDecoder<String, String> decoder = new Base64EncoderDecoder();
	
	@Override
	protected void processProperties(final ConfigurableListableBeanFactory beanFactoryToProcess,
			final ConfigurablePropertyResolver propertyResolver) {
		propertyResolver.setPlaceholderPrefix(this.placeholderPrefix);
		propertyResolver.setPlaceholderSuffix(this.placeholderSuffix);
		propertyResolver.setValueSeparator(this.valueSeparator);
		
		final StringValueResolver valueResolver = new DecodingValueResolver(propertyResolver);
		
		doProcessProperties(beanFactoryToProcess, valueResolver);
	}
	
	private final class DecodingValueResolver implements StringValueResolver {
		private final ConfigurablePropertyResolver propertyResolver;
		
		private DecodingValueResolver(final ConfigurablePropertyResolver propertyResolver) {
			this.propertyResolver = propertyResolver;
		}
		
		@Override
		public String resolveStringValue(final String strVal) {
			
			String resolved = resolve(propertyResolver, strVal);
			
			boolean decoded = false;
			if(resolved != null && resolved.startsWith(Base64EncoderDecoder.PREFIX)) {
				decoded = true;
				resolved = decoder.decode(resolved);
			}
			
			if (strVal.startsWith(placeholderPrefix) && strVal.endsWith(placeholderSuffix)) {
				LOG.info(((decoded) ? "Decoded ***** for " : "Resolved '" + resolved + "' from  plain text property"));
			}
			
			return (resolved.equals(nullValue) ? null : resolved);
		}

		/**
		 * @param propertyResolver2
		 * @param strVal
		 * @return
		 */
		private String resolve(ConfigurablePropertyResolver propertyResolver2,
				String strVal) {
			final String resolved = ignoreUnresolvablePlaceholders ?
					propertyResolver.resolvePlaceholders(strVal) :
					propertyResolver.resolveRequiredPlaceholders(strVal);	
			return resolved;
		}
		
		
	}
	
}
