/**
 * 
 */
package com.sik.utils.obfuscation;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * @author sik
 *
 */
public class Base64EncoderDecoder implements EncoderDecoder<String, String> {
	
	private static final Logger LOG = Logger.getLogger(Base64EncoderDecoder.class);

	public static final String PREFIX = "enc:";
	private static final String USAGE = "Usage: java -jar codec.jar string-to-encode-or-decode";
	private static final String NOTES = "Strings prefixed " + PREFIX + " will be decoded, otherwise encoded";
	
	public static void main(String[] args) {
		try {
			LOG.info(new Base64EncoderDecoder().execute(args));
		} catch (final Exception e) {
			LOG.info("Unable to continue: " + e.getMessage());
			LOG.info(USAGE);
			LOG.info(NOTES);
		}
	}
	
	String execute(final String[] args) {
		if (args.length < 1 || args[0] == null) {
			throw new IllegalArgumentException("A String to encode/decode must be provided.");
		}
		final String arg = coalesceArgs(args);
		return (arg.startsWith(PREFIX) ? decode(arg) : encode(arg));
	}
	
	String coalesceArgs(final String[] args) {
		final StringBuilder builder = new StringBuilder();
		String sep = "";
		for (final String arg:args) {
			builder.append(sep).append(arg);
			sep = " ";
		}
		return builder.toString();
	}

	/**
	 * @param plain
	 * @return
	 */
	public String encode(String plain) {
		return PREFIX + Base64.encodeBase64String(plain.getBytes());
	}

	/**
	 * @param encoded
	 * @return
	 */
	public String decode(String encoded) {
		return new String(Base64.decodeBase64(encoded.substring(PREFIX.length()).getBytes()));
	}



}
