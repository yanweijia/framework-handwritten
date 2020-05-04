package cn.weijia.framework.springmvc.annotation;

import java.lang.annotation.*;

/**
 * RequestMapping 注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WeijiaRequestMapping {
    String value() default "";
}
