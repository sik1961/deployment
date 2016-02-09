/**
 * 
 */
package com.sik.deployment.caching;

import net.sf.ehcache.CacheManager;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sik
 *
 */
@Configuration
public class CachingConfigurator {
	public static final String EHCACHE_XML_PROPERTY = "caching.ehcache.location";
	
	public static final String EHCACHE_XML_PROPERTY_DEFAULT = "classpath:ehcache.xml";
	
	private static final Logger LOG = Logger.getLogger(CachingConfigurator.class);
	
	@Autowired private ApplicationContext ctx;
	
	@Value("${" + EHCACHE_XML_PROPERTY + ":" + EHCACHE_XML_PROPERTY_DEFAULT + "}")
	private String ehCacheXmlLocation;
	
	@Bean public EhCacheCacheManager cacheManager() {
		final CacheManager manager = ehCacheFactoryBean().getObject();
		final EhCacheCacheManager bean = new EhCacheCacheManager();
		bean.setCacheManager(manager);
		return bean;
	}
	
	@Bean public EhCacheManagerFactoryBean ehCacheFactoryBean() {
		final EhCacheManagerFactoryBean bean = new EhCacheManagerFactoryBean();
		bean.setConfigLocation(ctx.getResource(ehCacheXmlLocation));
		bean.setShared(true);
		if (LOG.isInfoEnabled()) {
			LOG.info("Sharing EhCache configuration at: " + ehCacheXmlLocation);
		}
		return bean;
	}
	
}
