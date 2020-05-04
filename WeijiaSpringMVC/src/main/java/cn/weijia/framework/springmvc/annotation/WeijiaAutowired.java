package cn.weijia.framework.springmvc.annotation;

import java.lang.annotation.*;

/**
 * Autowired 注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WeijiaAutowired {
    String value() default "";
}
