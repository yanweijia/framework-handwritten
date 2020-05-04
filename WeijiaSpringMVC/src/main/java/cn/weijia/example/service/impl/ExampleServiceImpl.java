package cn.weijia.example.service.impl;

import cn.weijia.framework.springmvc.annotation.WeijiaService;
import cn.weijia.example.service.ExampleService;

@WeijiaService("exampleServiceImpl")
public class ExampleServiceImpl implements ExampleService {

    @Override
    public String sayHello(String msg) {
        return String.format("hello here are your msg: %s", msg);
    }
}
