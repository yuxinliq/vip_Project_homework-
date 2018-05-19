package homework.spring2.framework.context;

import homework.spring2.framework.annotation.Autowired;
import homework.spring2.framework.annotation.Component;
import homework.spring2.framework.annotation.Controller;
import homework.spring2.framework.aop.AopConfig;
import homework.spring2.framework.beans.BeanDefinition;
import homework.spring2.framework.beans.BeanWrapper;
import homework.spring2.framework.context.support.BeanDefinitionReader;
import homework.spring2.framework.core.BeanFactory;
import homework.utils.StrUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GPApplicationContext implements BeanFactory {
    private final String[] locations;
    private BeanDefinitionReader reader;
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private Map<String, BeanWrapper> beanWrapperMap = new ConcurrentHashMap<>();

    private Map<String, Object> beanInstanceCacheMap = new ConcurrentHashMap<>();

    public GPApplicationContext(String... locations) {
        this.locations = locations;
        refresh();
    }

    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[beanDefinitionMap.size()]);
    }

    public void refresh() {
        //定位
        this.reader = new BeanDefinitionReader(locations);
        //加载
        List<String> classNames = reader.loadBeanDefinitions();
        //注册
        doRegistry(classNames);
        //依赖注入:lazy-init=false时
        doAutowried();

        return;
    }

    private void doAutowried() {
        for (Map.Entry<String, BeanDefinition> definitionEntry : this.beanDefinitionMap.entrySet()) {
            if (!definitionEntry.getValue().isLazyInit()) {
                getBean(definitionEntry.getKey());
            }
        }
    }

    private void doRegistry(List<String> classNames) {
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isInterface() || !isSpringBean(clazz)) {
                    continue;
                }
                BeanDefinition beanDefinition = reader.registerBean(className);
                if (beanDefinition == null) {
                    continue;
                }
                this.beanDefinitionMap.put(getBeanName(clazz), beanDefinition);
                for (Class<?> in : clazz.getInterfaces()) {
                    this.beanDefinitionMap.put(in.getName(), beanDefinition);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isSpringBean(Class<?> clazz) {
        return clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Controller.class);
    }

    private String getAnnotationBeanName(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Component.class)) {
            return clazz.getAnnotation(Component.class).value();
        }
        if (clazz.isAnnotationPresent(Controller.class)) {
            return clazz.getAnnotation(Controller.class).value();
        }
        return null;
    }

    private String getBeanName(Class<?> clazz) {
        if (clazz.isInterface()) {
            return clazz.getName();
        }
        if (!isSpringBean(clazz)) {
            return StrUtil.lowerFirstCase(clazz.getSimpleName());
        }
        String personalName = getAnnotationBeanName(clazz);
        return personalName.isEmpty() ? StrUtil.lowerFirstCase(clazz.getSimpleName()) : personalName;
    }

    @Override
    public Object getBean(String beanName) {
        return this.getBeanWrapper(beanName).getWrapperInstance();
    }

    public BeanWrapper getBeanWrapper(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            return null;
        }
        BeanWrapper cache = beanWrapperMap.get(beanDefinition.getBeanClassName());
        if (cache != null) {
            return cache;
        }
        //new instance
        Object instance = initBeanInstance(beanDefinition);
        //new Warrper
        BeanWrapper wrapper = new BeanWrapper(instance, initAopConfig(beanDefinition));
        //regist to Map
        beanWrapperMap.put(beanDefinition.getBeanClassName(), wrapper);
        //populateProperties
        populateProperties(instance);
        return wrapper;
    }

    private AopConfig initAopConfig(BeanDefinition beanDefinition) {
        try {
            String[] before = reader.getConfig().getProperty("aspectBefore").split("#");
            String[] after = reader.getConfig().getProperty("aspectAfter").split("#");
            String name = beanDefinition.getBeanClassName();
            if (name.equals(before[0])) {
                return null;
            }
            AopConfig config = new AopConfig();
            //1.检查原始bean的各个方法是否被切面切到
            Class clazz = Class.forName(name);
            Pattern pattern = Pattern.compile(reader.getConfig().getProperty("aopPoint"));

            Class aspectClass = Class.forName(before[0]);
            for (Method method : clazz.getMethods()) {
                String key = clazz.getName() + "#" + method.getName();
                Matcher matcher = pattern.matcher(key);
                if (matcher.find()) {
                    //2.获取代理对象及通知方法
                    config.put(method, this.getBean(getBeanName(aspectClass)), new Method[]{aspectClass.getMethod(before[1]), aspectClass.getMethod(after[1])});
                }
            }
            //3.返回aopconfig
            if (config.getPoints().isEmpty()) {
                return null;
            }
            return config;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void populateProperties(Object instance) {
        Field[] fields = instance.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                if (!field.isAnnotationPresent(Autowired.class)) {
                    return;
                }
                field.setAccessible(true);
                String beanName = field.getAnnotation(Autowired.class).value();
                if (beanName.isEmpty()) {
                    beanName = getBeanName(field.getType());
                }
                field.set(instance, getBean(beanName));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Object initBeanInstance(BeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();
        try {
            if (beanInstanceCacheMap.containsKey(className)) {
                return beanInstanceCacheMap.get(className);
            }
            Class<?> clazz = Class.forName(className);
            Object instance = clazz.newInstance();
            beanInstanceCacheMap.put(className, instance);
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }
}
