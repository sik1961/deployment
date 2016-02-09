/**
 * 
 */
package com.sik.jpa.schemaexport;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;

import org.apache.log4j.Logger;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;

/**
 * @author sik
 *
 */
public class SchemaExporter {
	private static final Logger LOG = Logger.getLogger(SchemaExporter.class);
	
	public static final String DEFAULT_DIALECT = "or.hibernate.dialect.MySqlDialect";
	public static final String DEFAULT_ENTITY_SCAN_ROOT = "com.sik";
	public static final String PATH_SEPARATOR = System.getProperty("file.separator");
	
	private final String entityScanRoot;
	private final String dialect;
	private final String schema;
	
	private SchemaExporter(final String entityScanRoot, final String dialect, final String schema) {
		super();
		this.entityScanRoot = entityScanRoot;
		this.dialect = dialect;
		this.schema = schema;
	}
	
	public static class Builder {
		private String entityScanRoot = DEFAULT_ENTITY_SCAN_ROOT;
		private String dialect = DEFAULT_DIALECT;
		private String schema;
		public Builder withEntityScanRoot(final String entityScanRoot) {
			this.entityScanRoot = entityScanRoot;
			return this;
		}
		public Builder withSchema(final String schema) {
			this.schema = schema;
			return this;
		}
		public Builder withDialect(final String dialect) {
			this.dialect = dialect;
			return this;
		}
		public SchemaExporter build() {
			Assert.hasLength(entityScanRoot);
			Assert.hasLength(dialect);
			Assert.hasLength(schema);
			return new SchemaExporter(entityScanRoot, dialect, schema);
		}
	}
	
	public enum ExportType {
		CREATE("create.ddl"),
		DROP("drop.ddl");
		private String scriptName;
		private ExportType(final String scriptName) {
			this.scriptName = scriptName;
		}
		public String getScriptName() {
			return this.scriptName;
		}
	}
	
	final class Ddl {
		private final List<String> create;
		private final List<String> drop;
		private Ddl(final String[] create, final String[] drop) {
			this.create = Collections.unmodifiableList(Arrays.asList(create));
			this.drop = Collections.unmodifiableList(Arrays.asList(drop));
		}
		public List<String> getCreate() {
			return this.create;
		}
		public List<String> getDrop() {
			return this.drop;
		}
		public String exportToLocalFilesystem(final String location) {
			exportScriptToLocalFilesystem(create, ExportType.CREATE, location);
			exportScriptToLocalFilesystem(drop, ExportType.DROP, location);
			return location;
		}
		
		private void exportScriptToLocalFilesystem(List<String> lines,
				ExportType type, 
				String rootLocation) {
			final String location = rootLocation + PATH_SEPARATOR + type.getScriptName();
			if (LOG.isInfoEnabled()) {
				LOG.info("Exporting DDL " + type + " to " + location);
			}
			final PrintWriter writer;
			try {
				writer = new PrintWriter(location);
			} catch (FileNotFoundException e) {
				throw new IllegalArgumentException("Unable to write" + type + " export script to" + location);
			}
			for (String line:lines) {
				writer.println(line);
			}
			writer.close();
		}
	}
	public Ddl generateDdl() {
		final Configuration cfg = new Configuration();
		final ClassPathScanningCandidateComponentProvider scanner =  new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
		for (final BeanDefinition def:scanner.findCandidateComponents(entityScanRoot)) {
			try {
				cfg.addAnnotatedClass(this.getClass().getClassLoader().loadClass(def.getBeanClassName()));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Problem exporting JPA definitions from entities", e);
			}
		}
		cfg.setProperty(AvailableSettings.DIALECT, dialect);
		cfg.setProperty(AvailableSettings.DEFAULT_SCHEMA, schema);
		final Dialect dialect = Dialect.getDialect(cfg.getProperties());
		
		return new Ddl(
				cfg.generateSchemaCreationScript(dialect),
				cfg.generateDropSchemaScript(dialect)
			);
	}
	
}
