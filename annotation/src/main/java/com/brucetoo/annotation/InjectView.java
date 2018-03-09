package com.brucetoo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Bruce Too
 * On 09/03/2018.
 * At 15:52
 */

//修饰属性，运行时获取注解信息，注入
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectView {
    //默认命名为value
    int value() default -1;
}
