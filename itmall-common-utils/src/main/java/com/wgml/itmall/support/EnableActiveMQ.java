package com.wgml.itmall.support;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author oislfy
 * @date 2021-11-08 17:39
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({ActiveMqSupport.class})
public @interface EnableActiveMQ {
}
