package cn.weijia.framework.springmvc.annotation;

import java.lang.annotation.*;

/**
 * Service 注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WeijiaService {
    String value() default "";
}
