package cn.weijia.example;

import cn.weijia.framework.springmvc.servlet.DispatcherServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Application {
    public static void main(String[] args) throws Exception {

        Server server = new Server(9999);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        server.setHandler(context);
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new DispatcherServlet()), "/");
        server.start();
        server.join();
    }
}
