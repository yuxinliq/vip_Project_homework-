package homework.spring2.framework.context.support;

import homework.spring2.framework.annotation.Component;
import homework.spring2.framework.beans.BeanDefinition;
import homework.utils.StrUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BeanDefinitionReader {
    public static final String BASE_PACKAGE = "basepackage";
    private final Properties config = new Properties();
    private List<String> classNames;

    public BeanDefinitionReader(String... locations) {
        for (String location : locations) {
            InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:", ""));
            try {
                this.config.load(inStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> loadBeanDefinitions() {
        if (classNames != null) {
            return classNames;
        }
        classNames = new ArrayList<>();
        doScanner(config.getProperty(BASE_PACKAGE));
        return classNames;
    }

    private void doScanner(String basePackage) {
        if (basePackage == null)
            return;
        URL resource = this.getClass().getClassLoader().getResource(basePackage.replace(".", "/"));
        File classDir = new File(resource.getPath());
        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(basePackage + "." + file.getName());
            } else {
                classNames.add(basePackage + "." + file.getName().replace(".class", ""));
            }
        }
    }

    public BeanDefinition registerBean(String name) {
        if (!classNames.contains(name)) {
            return null;
        }
        BeanDefinition result = new BeanDefinition();
        result.setBeanClassName(name);
        result.setFactoryBeanName(StrUtil.lowerFirstCase(name.substring(name.lastIndexOf(".") + 1)));
        return result;
    }
}
