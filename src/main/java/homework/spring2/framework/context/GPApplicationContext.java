package homework.spring2.framework.context;

import homework.spring2.framework.annotation.Autowired;
import homework.spring2.framework.annotation.Component;
import homework.spring2.framework.beans.BeanDefinition;
import homework.spring2.framework.beans.BeanWrapper;
import homework.spring2.framework.context.support.BeanDefinitionReader;
import homework.spring2.framework.core.BeanFactory;
import homework.utils.StrUtil;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
                if (clazz.isInterface() || !clazz.isAnnotationPresent(Component.class)) {
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

    private String getBeanName(Class<?> clazz) {
        if (clazz.isInterface()) {
            return clazz.getName();
        }
        if (!clazz.isAnnotationPresent(Component.class)) {
            return StrUtil.lowerFirstCase(clazz.getSimpleName());
        }
        String personalName = clazz.getAnnotation(Component.class).value();
        return personalName.isEmpty() ? StrUtil.lowerFirstCase(clazz.getSimpleName()) : personalName;
    }

    @Override
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            return null;
        }
        BeanWrapper cache = beanWrapperMap.get(beanDefinition.getBeanClassName());
        if (cache != null) {
            return cache.getWrapperInstance();
        }
        //new instance
        Object instance = initBeanInstance(beanDefinition);
        //new Warrper
        BeanWrapper wrapper = new BeanWrapper(instance);
        //regist to Map
        beanWrapperMap.put(beanDefinition.getBeanClassName(), wrapper);
        //populateProperties
        populateProperties(instance);
        return wrapper.getWrapperInstance();
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
}
