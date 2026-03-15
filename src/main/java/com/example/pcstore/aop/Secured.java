package com.example.pcstore.aop;

import com.example.pcstore.enums.RoleEnum;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Secured {
    RoleEnum role() default RoleEnum.ALL;
}
