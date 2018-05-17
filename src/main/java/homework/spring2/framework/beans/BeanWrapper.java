package homework.spring2.framework.beans;

import homework.spring2.framework.aop.AopConfig;
import homework.spring2.framework.aop.AopProxy;

public class BeanWrapper {
    private Object wrapperInstance;
    private Object originalInstance;

    public BeanWrapper(Object originalInstance, AopConfig aopConfig) {
        this.originalInstance = originalInstance;
        if (aopConfig == null) {
            this.wrapperInstance = originalInstance;
            return;
        }
        AopProxy aopProxy = new AopProxy();
        aopProxy.setConfig(aopConfig);
        this.wrapperInstance = aopProxy.getProxy(originalInstance);
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public void setWrapperInstance(Object wrapperInstance) {
        this.wrapperInstance = wrapperInstance;
    }

    public Object getOriginalInstance() {
        return originalInstance;
    }

    public void setOriginalInstance(Object originalInstance) {
        this.originalInstance = originalInstance;
    }
}
