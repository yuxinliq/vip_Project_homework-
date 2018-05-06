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

    private Properties contextConfigLocation = new Properties();

    private Map<String, Object> beanMap = new ConcurrentHashMap<>();

    private List<String> classNames = new ArrayList<>();

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
    }

    private void initHandlerMapping() {

    }

    private void doAutowired() {
        if (beanMap == null) {
            return;
        }
        for (Object bean : beanMap.values()) {
            for (Field field : bean.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    String beanName = autowired.value().isEmpty() ? field.getType().getName() : autowired.value();
                    try {
                        field.set(bean, beanMap.get(beanName));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void doRegistry() {
        if (classNames.isEmpty()) {
            return;
        }
        try {
            for (String name : classNames) {
                Class<?> clazz = Class.forName(name);
                if (clazz.isAnnotationPresent(Component.class)) {
                    Component annotation = clazz.getAnnotation(Component.class);
                    Object instance = clazz.newInstance();
                    if (!annotation.value().isEmpty()) {
                        beanMap.put(annotation.value(), instance);
                    } else {
                        beanMap.put(lowerFirstCase(clazz.getSimpleName()), instance);
                    }
                    for (Class in : clazz.getInterfaces()) {
                        beanMap.put(in.getName(), instance);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String lowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
