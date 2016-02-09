/**
 * 
 */
package com.sik.deployment.profiles.debugger;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 * @author sik
 *
 */
public class ApplicationContextBeanDebugLogger {
	private final Logger log;
	
	public ApplicationContextBeanDebugLogger(final Logger log) {
		super();
		this.log = log;
	}
	
	public void logBeansIn(final ApplicationContext applicationContext) {
		if (!log.isInfoEnabled()) {
			return;
		}
		
		log.info("****** " + applicationContext.getBeanDefinitionCount() + " beans in context " + applicationContext.getDisplayName() + " ******");
		
		final String[] beanNames = applicationContext.getBeanDefinitionNames();
		for (final String name: beanNames) {
			log.info("Bean name: " + name + ("[type = " + applicationContext.getType(name) + "]"));
			final Object bean = applicationContext.getBean(name);
			log.info("\tinstance.toString() = " + ((bean == null) ? "null" : bean.toString()));
			final String[] aliases = applicationContext.getAliases(name);
			if (aliases != null && aliases.length > 0) {
				for (final String a: aliases) {
					log.info("\taliased as: " + a);
				}
			}
		}
		
		
		
	}
	
	
}
