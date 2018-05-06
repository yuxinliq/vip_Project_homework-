package homework.spring2.framework.beans;

public class BeanWrapper {
    private Object wrapperInstance;
    private Object originalInstance;

    public BeanWrapper(Object originalInstance) {
        this.originalInstance = originalInstance;
        this.wrapperInstance = originalInstance;
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
