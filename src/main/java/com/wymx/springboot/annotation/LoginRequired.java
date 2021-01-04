package com.wymx.springboot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) //这个注解可以定义在方法上
@Retention(RetentionPolicy.RUNTIME) //这个注解在程序运行时才有效
public @interface LoginRequired {
}
