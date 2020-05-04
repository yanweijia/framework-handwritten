package cn.weijia.framework.springmvc.annotation;

import java.lang.annotation.*;

/**
 * Controller 注解
 */
//作用范围
@Target(ElementType.TYPE)
//系统运行时通过反射获取信息
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WeijiaController {
    String value() default "";
}
