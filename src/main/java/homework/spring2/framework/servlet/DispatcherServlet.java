package homework.spring2.framework.servlet;

import homework.spring2.framework.annotation.Autowired;
import homework.spring2.framework.annotation.Component;
import homework.spring2.framework.context.GPApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DispatcherServlet extends HttpServlet {

    private GPApplicationContext context;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("-----------------doPost-----------------");
    }

    @Override
    public void init(ServletConfig config) {
        context = new GPApplicationContext(config.getInitParameter("contextConfigLocation"));
        initHandlerMapping();

        return;
    }

    private void initHandlerMapping() {

    }

}
