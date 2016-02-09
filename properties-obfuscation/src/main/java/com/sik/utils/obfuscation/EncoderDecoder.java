/**
 * 
 */
package com.sik.utils.obfuscation;

/**
 * @author sik
 *
 */
public interface EncoderDecoder<M, N> {
	M encode(N plain);
	N decode(M encoded);
}
