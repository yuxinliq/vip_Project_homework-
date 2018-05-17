package homework.spring2.framework.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class AopProxy implements InvocationHandler {
    private AopConfig config;
    private Object target;

    public AopProxy() {
    }

    public Object getProxy(Object instance) {
        this.target = instance;
        Class<?> clazz = target.getClass();
        return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
    }

    /**
     * @param config 根据业务需求配置,扫描配置后由外部注入
     */
    public void setConfig(AopConfig config) {
        this.config = config;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method m = this.target.getClass().getMethod(method.getName(), method.getParameterTypes());

        //在原始方法调用以前要执行增强的代码
        //这里需要通过原生方法去找，通过代理方法去Map中是找不到的
        if (config.contains(m)) {
            AopConfig.Aspect aspect = config.get(method);
            aspect.getPoints()[0].invoke(aspect);
        }

        //反射调用原始的方法
        Object obj = method.invoke(this.target, args);

        //在原始方法调用以后要执行增强的代码
        if (config.contains(method)) {
            AopConfig.Aspect aspect = config.get(method);
            aspect.getPoints()[1].invoke(aspect);
        }

        //将最原始的返回值返回出去
        return obj;
    }
}
