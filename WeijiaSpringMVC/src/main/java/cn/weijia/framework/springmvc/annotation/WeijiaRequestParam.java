package cn.weijia.framework.springmvc.annotation;

import java.lang.annotation.*;

/**
 * RequestParam 注解
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WeijiaRequestParam {
    String value() default "";
}
