package com.brucetoo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//修饰activity，编译时生成代码
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface InjectActivity {
}
