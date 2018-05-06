package homework.spring2.framework.core;

import homework.spring2.framework.beans.BeanDefinition;

public interface BeanFactory {
    Object getBean(String name);
}
