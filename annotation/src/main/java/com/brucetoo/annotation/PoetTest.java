package com.brucetoo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Bruce Too
 * On 09/03/2018.
 * At 16:09
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface PoetTest {
}
