package homework.spring.servlet;

import homework.spring.annotation.Autowired;
import homework.spring.annotation.Component;

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
        //定位
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //加载
        doScanner(contextConfigLocation.getProperty("basepackage"));
        //注册
        doRegistry();
        //依赖注入
        doAutowired();
        //springMvc handlerMapping,将RequestMapping中的path与method关联上
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

    private void doScanner(String basePackage) {
        URL resource = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
        File classDir = new File(resource.getPath());
        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(basePackage + "." + file.getName());
            } else {
                classNames.add(basePackage + "." + file.getName().replace(".class", ""));
            }
        }
    }

    private void doLoadConfig(String location) {
        InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:", ""));
        try {
            this.contextConfigLocation.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String lowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
