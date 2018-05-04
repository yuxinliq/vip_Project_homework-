package homework.pattern.proxy.jdk;

import homework.pattern.proxy.Airlines;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JDKScalper implements InvocationHandler {
    private Airlines airlines;

    public static Airlines getInstance(Airlines airlines) {
        JDKScalper scalper = new JDKScalper();
        scalper.airlines = airlines;
        return (Airlines) Proxy.newProxyInstance(airlines.getClass().getClassLoader(), airlines.getClass().getInterfaces(), scalper);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("我是黄牛:");
        Object result = method.invoke(airlines, args);
        System.out.println("每张票附加200元的服务费!");
        return result;
    }
}
