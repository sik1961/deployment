/**
 * 
 */
package com.sik.deployment.connection.pooling;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.sik.deployment.properties.EnvironmentProperty;



/**
 * @author sik
 *
 */
public class DbcpConnectionPoolingDatasourceConfigurator {
	
	public enum Property implements EnvironmentProperty {
		IDLE_TIME("dbcp.evictable.idle.time.millis", DefaultValue.fromValue("300000")),
		TIME_BETWEEN_EVICTION_RUNS_MILLIS("dbcp.time.between.eviction.runs.millis", DefaultValue.fromValue("600000")),
		MAX_WAIT_MILLIS("dbcp.max.wait.millis", DefaultValue.fromValue("10000")),
		MAX_ACTIVE_CONNECTIONS("dbcp.max.active.connections", DefaultValue.fromValue("10")),
		MAX_IDLE_CONNECTIONS("dbcp.max.idle.connections", DefaultValue.fromValue("5")),
		NUM_TESTS_PER_EVICTION_RUN("dbcp.num.tests.per.eviction.run", DefaultValue.fromValue("5")),

		JDBC_DRIVER_CLASS("jdbc.driver", DefaultValue.NONE),
		JDBC_URL("jdbc.url", DefaultValue.NONE),
		JDBC_USERNAME("jdbc.username", DefaultValue.NONE),
		JDBC_PASSWORD("jdbc.password", DefaultValue.NONE),
		JDBC_VALIDATION_QUERY("jdbc.validation.query", DefaultValue.NONE);

		private final String key;
		private final DefaultValue defaultValue;
		private Property(final String key, final DefaultValue defaultValue) {
			this.key = key;
			this.defaultValue = defaultValue;
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
			return Collections.unmodifiableCollection(Arrays.asList(values()));
		}
	}
	
	
	
}
