package com.sik.jpa;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

import com.sik.property.EnvironmentProperty;
import com.sik.property.EnvironmentPropertyProvider;

@Configuration
@EnableTransactionManagement
@Import(BeanValidationConfigurator.class)
public class JpaContextConfiguration {

	private static final Logger LOG = Logger.getLogger(JpaContextConfiguration.class);

	public enum Property implements EnvironmentProperty {
		JPA_PROPERTIES_LOCATION("jpa.properties", "classpath:com/hsbc/htse/esm/jpa/deploy/hibernate.properties"),
		REPOSITORY_SCAN_ROOT("jpa.packages", "com.hsbc.htse.esm");
		private final String key;
		private final DefaultValue defaultValue;
		private Property(final String key, final String defaultValue) {
			this.key = key;
			this.defaultValue = DefaultValue.fromValue(defaultValue);
		}
		@Override
		public String getKey() {
			return key;
		}
		@Override
		public DefaultValue getDefaultValue() {
			return defaultValue;
		}
		@Override
		public Collection<? extends EnvironmentProperty> getDefinedProperties() {
			return Collections.unmodifiableCollection(Arrays.asList(Property.values()));
		}
	}

	@Autowired private DataSource datasource;
	@Autowired private ApplicationContext ctx;
	@Autowired private EnvironmentPropertyProvider props;
	@Autowired private Environment env;

	private String[] packagesToScan;
	private Properties jpaProperties;

	private void checkPreconditions() {
		Assert.notNull(this.datasource, "A datasource object must be provided.");
		this.packagesToScan = props.getValue(Property.REPOSITORY_SCAN_ROOT)
				.replaceAll("\\s", "")
				.split(",");

		this.jpaProperties = this.getJpaProperties(props.getValue(Property.JPA_PROPERTIES_LOCATION));
		LOG.info(Property.REPOSITORY_SCAN_ROOT.getKey() + ":" + Arrays.toString(this.packagesToScan));
		LOG.info(Property.JPA_PROPERTIES_LOCATION.getKey() + ":" + this.jpaProperties);
		this.enforceEnvironmentRules();
	}

	/**
	 * @return entity manager factory configured with Hibernate, Spring platform transactions, provided JPA properties and
	 * managing entities scanned from the provided root.
	 */
	@Bean(name="entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
		this.checkPreconditions();
		final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource(this.datasource);
		factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
		factoryBean.setJpaProperties(this.jpaProperties);
		factoryBean.setJpaDialect(dialect());
		return factoryBean;
	}

	/**
	 * @return JPA transaction manager
	 */
	@Bean
	public PlatformTransactionManager transactionManager() {
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
		return transactionManager;
	}

	/**
	 * @return Hibernate JPA vendor adapter
	 */
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		return new HibernateJpaVendorAdapter();
	}

	/**
	 * @return Hibernate dialect
	 */
	@Bean
	public HibernateJpaDialect dialect() {
		return new HibernateJpaDialect();
	}

	private Properties getJpaProperties(final String jpaPropertiesLocation) {
		LOG.info("Loading JPA properties from '" + jpaPropertiesLocation + "'");
		if (jpaPropertiesLocation == null || "".equals(jpaPropertiesLocation)) {
			throw new IllegalStateException("Unable to find JPA properties at '" + jpaPropertiesLocation + "'");
		}
		try {
			final Resource jpaPropertyResource = ctx.getResource(jpaPropertiesLocation);
			final Properties props = new Properties();
			props.load(jpaPropertyResource.getInputStream());
			return props;
		} catch (final Exception e) {
			throw new IllegalArgumentException(
				String.format("Unable to load JPA properties from location %s specified by property %s",
					jpaPropertiesLocation, Property.JPA_PROPERTIES_LOCATION.getKey()), e);
		}
	}

	private void enforceEnvironmentRules() {
		for (final String profile:this.env.getActiveProfiles()) {
			if ("prod".equals(profile)) {
				enforceProductionRules();
			}
		}
	}

	private void enforceProductionRules() {
		this.disallowHibernateDdlAutoValues("create", "drop");
	}

	private void disallowHibernateDdlAutoValues(final String... illegalValues) {
		final String hibernateDdlAutoProperty = "hibernate.hbm2ddl.auto";
		final String ddlAuto = jpaProperties.getProperty(hibernateDdlAutoProperty);
		if (ddlAuto == null) return;
		for (final String illegalValue:illegalValues) {
			if (ddlAuto.toLowerCase().contains(illegalValue.toLowerCase()))
				throw new IllegalArgumentException(
					String.format("JPA properties may not contain %s when profile is prod",
						Arrays.toString(illegalValues)));
		}
	}
}
