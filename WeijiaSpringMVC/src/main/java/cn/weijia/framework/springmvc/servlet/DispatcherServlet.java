package cn.weijia.framework.springmvc.servlet;

import cn.weijia.framework.springmvc.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/"}, loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {
    List<String> classNames = new ArrayList<>();
    Map<String, Object> beansMap = new HashMap<>();
    Map<String, Method> handlerMap = new HashMap<>();


    @Override

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取请求路径
        String uri = req.getRequestURI();
        String context = req.getContextPath();
        String path = uri.replaceFirst(context, "");


        Method method = handlerMap.get(path);

        //找不到对应方法进行处理, 报404
        if (null == method) {
            PrintWriter printWriter = resp.getWriter();
            printWriter.write("404. illegal path");
            printWriter.close();
            return;
        }

        //这里其实是 Controller
        Object instance = beansMap.get("/" + path.split("/")[1]);

        //获取请求入参
        Object args[] = hand(req, resp, method);

        try {

            method.invoke(instance, args);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    /**
     * 请求入参映射
     *
     * @param request
     * @param response
     * @param method
     * @return
     */
    private static Object[] hand(HttpServletRequest request, HttpServletResponse response, Method method) {

        //拿到当前待执行方法有哪些参数
        Class<?>[] paramClazzs = method.getParameterTypes();
        Object[] args = new Object[paramClazzs.length];

        int argsNumber = 0;
        int index = 0;
        for (Class<?> paramClazz : paramClazzs) {
            if (ServletRequest.class.isAssignableFrom(paramClazz)) {
                args[argsNumber++] = request;
            }
            if (ServletResponse.class.isAssignableFrom(paramClazz)) {
                args[argsNumber++] = response;
            }
            Annotation[] paramAns = method.getParameterAnnotations()[index];
            if (paramAns.length > 0) {
                for (Annotation paramAn : paramAns) {
                    if (WeijiaRequestParam.class.isAssignableFrom(paramAn.getClass())) {
                        WeijiaRequestParam reqParam = (WeijiaRequestParam) paramAn;

                        args[argsNumber++] = request.getParameter(reqParam.value());
                    }
                }
            }
            index++;
        }
        return args;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        //初始化 MVC 相关数据

        //按照配置的包路径进行扫描
        doScan("cn.weijia.example");

        //实例化
        doInstance();

        //注入
        doAutowired();

        //请求地址与 Controller 映射
        doUrlMapping();
    }


    private void doScan(String basePackages) {
        //扫描编译好的类路径
        URL url = this.getClass().getClassLoader().getResource(basePackages.replaceAll("\\.", "/"));
        String fileStr = url.getFile();

        for (String path : new File(fileStr).list()) {
            File filePath = new File(fileStr + "/" + path);
            if (filePath.isDirectory()) {
                //递归扫描
                doScan(basePackages + "." + path);
            } else {
                //*.class file
                classNames.add(basePackages + "." + filePath.getName());

            }
        }
    }

    private void doInstance() {
        for (String className : classNames) {
            String classNameStr = className.replace(".class", "");
            try {
                Class<?> clazz = Class.forName(classNameStr);
                if (clazz.isAnnotationPresent(WeijiaController.class)) {
                    Object controllerInstance = clazz.newInstance();
                    WeijiaRequestMapping reqMapping = clazz.getAnnotation(WeijiaRequestMapping.class);
                    beansMap.put(reqMapping.value(), controllerInstance);

                } else if (clazz.isAnnotationPresent(WeijiaService.class)) {
                    Object serviceInstance = clazz.newInstance();
                    WeijiaService serviceAnnotation = clazz.getAnnotation(WeijiaService.class);
                    beansMap.put(serviceAnnotation.value(), serviceInstance);
                } else {
                    continue;
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 自动注入, 只考虑了将 service 注入到 controller 中
     */
    private void doAutowired() {
        beansMap.forEach((key, beanInstance) -> {
            Class<?> clazz = beanInstance.getClass();
            if (clazz.isAnnotationPresent(WeijiaController.class)) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(WeijiaAutowired.class)) {
                        WeijiaAutowired autowiredAnnotation = field.getAnnotation(WeijiaAutowired.class);
                        String serviceName = autowiredAnnotation.value();

                        Object serviceInstance = beansMap.get(serviceName);
                        field.setAccessible(true);
                        try {
                            field.set(beanInstance, serviceInstance);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void doUrlMapping() {
        beansMap.forEach((key, beanInstance) -> {
            Class<?> clazz = beanInstance.getClass();
            if (clazz.isAnnotationPresent(WeijiaController.class)) {
                //类路径
                WeijiaRequestMapping reqMapping = clazz.getAnnotation(WeijiaRequestMapping.class);
                String classPath = reqMapping.value();

                Method[] methods = clazz.getMethods();
                //方法路径
                for (Method method : methods) {
                    if (method.isAnnotationPresent(WeijiaRequestMapping.class)) {
                        WeijiaRequestMapping reqMapTemp = method.getAnnotation(WeijiaRequestMapping.class);
                        String methodPath = reqMapTemp.value();
                        handlerMap.put(classPath + methodPath, method);
                    }
                }
            }
        });
    }
}