
# Section1 实现简单的 SpringMVC
直接运行项目 Application.main()方法

然后打开浏览器输入: `http://localhost:9999/weijia/sayHello?msg=weijia`

## SpringMVC需要知识点

```java
    Class<?> clazz = Class.forName(cn.weijia.service.impl.xxxImpl)
    XxxService xxxService = clazz.newInstance()
    Class<?> clazz = xxxService.getClass();
    Field[] fields = clazz.getDeclaredFields();
    Method[] method = clazz.getMethods();
    method.invoke(a,arga[]);
    request.getRequestURI();
```