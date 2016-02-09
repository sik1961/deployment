/**
 * 
 */
package com.sik.deployment.profiles;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import org.springframework.context.annotation.Profile;

/**
 * @author sik
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Profile(Profiles.DEV)
public @interface ProfileLocal {
	
}
