package com.nono.deluxe.configuration.annotation;

import com.nono.deluxe.domain.user.Role;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auth {

    Role role() default Role.ROLE_PARTICIPANT;
}
