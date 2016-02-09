/**
 * 
 */
package com.sik.jpa.schemaexport;

import java.io.File;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.sik.jpa.schemaexport.SchemaExporter.Builder;

/**
 * @author sik
 *
 */
public class Main {
	private static final Logger LOG = Logger.getLogger(Main.class);
	
	public enum ExtractProperty {
		SCHEMA("schema","withSchema", true),
		DIALECT("dialect", "withDialect", false),
		ENTITY_SCAN_ROOT("packageRoot", "withEntityScanRoot", false);
		
		private final String propertyName;
		private final Method method;
		private final boolean mandatory;
		
		private ExtractProperty (final String propertyName, final String methodName, final boolean isMandatory) {
			this.propertyName = propertyName;
			this.mandatory = isMandatory;
			try {
				this.method = SchemaExporter.Builder.class.getMethod(methodName, String.class);
			} catch (final Exception e) {
				throw new IllegalArgumentException("Method not found: " + methodName);
			}
		}
		public String getPropertyName() {
			return this.propertyName;
		}
		public boolean isMandatory() {
			return this.mandatory;
		}
		public void updateWith(final SchemaExporter.Builder builder, final String value) {
			method.setAccessible(true);
			try {
				if (value != null && value.length()>0) {
					method.invoke(builder, value);
				}
			} catch (final Exception e) {
				throw new IllegalStateException("Unable to reflectively update builder", e);
			}
		}
	}
	public static String getUsage() {
		final StringBuilder builder = new StringBuilder("executable");
		for (final ExtractProperty property:ExtractProperty.values()) {
			builder.append((property.isMandatory() ? "" : "["))
			.append("-D")
			.append(property.getPropertyName())
			.append("=")
			.append("value")
			.append((property.isMandatory() ? "" : "]"))
			.append(" ");
		}	
		return builder.append("extractdirectory").toString();
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			final IllegalArgumentException ex = new IllegalArgumentException("Extract directory must be supplied as last parameter");
			LOG.error(generateParameterErrorMessage(ex));
			throw ex;
		}
		
		final String extractDirectory = args[0];
		if (extractDirectory == null || !new File(extractDirectory).canWrite()) {
			final IllegalArgumentException ex = new IllegalArgumentException("Extract directory " + extractDirectory + " must be writable");
			LOG.error(generateParameterErrorMessage(ex));
			throw ex;
		}
		
		final SchemaExporter.Builder builder = new SchemaExporter.Builder();
		for (final ExtractProperty property: ExtractProperty.values()) {
			try {
				apply(property, builder);
			} catch (final Exception e) {
				LOG.error(generateParameterErrorMessage(e));
			}
		}
		builder
			.build()
			.generateDdl()
			.exportToLocalFilesystem(extractDirectory);
	}

	/**
	 * @param property
	 * @param builder
	 */
	private static SchemaExporter.Builder apply(ExtractProperty property, final SchemaExporter.Builder builder) {
		final String propertyValue = System.getProperty(property.getPropertyName());
		if (propertyValue == null || propertyValue.length()<0 ) {
			if (property.isMandatory()) {
				throw new IllegalArgumentException("System property " + property.getPropertyName() + "is mandatory");
			}
		} else {
			property.updateWith(builder, propertyValue);
		}
		return builder;
	}

	/**
	 * @param ex
	 * @return
	 */
	private static String generateParameterErrorMessage(
			final Throwable ex) {
		return "Aborted due to parameter error, " + ex.getMessage() + "(usage: " + getUsage() + ")"; 
	}
	
	private Main() {}
	
}
