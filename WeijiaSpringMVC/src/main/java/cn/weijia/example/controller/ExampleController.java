package cn.weijia.example.controller;

import cn.weijia.framework.springmvc.annotation.WeijiaAutowired;
import cn.weijia.framework.springmvc.annotation.WeijiaController;
import cn.weijia.framework.springmvc.annotation.WeijiaRequestMapping;
import cn.weijia.framework.springmvc.annotation.WeijiaRequestParam;
import cn.weijia.example.service.ExampleService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@WeijiaController
@WeijiaRequestMapping("/weijia")
public class ExampleController {

    @WeijiaAutowired("exampleServiceImpl")
    private ExampleService exampleService;

    @WeijiaRequestMapping("/sayHello")
    public void query(HttpServletRequest request, HttpServletResponse response,
                      @WeijiaRequestParam("msg") String msg) throws Exception {

        PrintWriter printWriter = response.getWriter();
        String result = exampleService.sayHello(msg);
        printWriter.write(result);
        printWriter.close();
    }
}
