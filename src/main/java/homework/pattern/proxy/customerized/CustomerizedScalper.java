package homework.pattern.proxy.customerized;

import homework.pattern.proxy.Airlines;

import java.lang.reflect.Method;

public class CustomerizedScalper implements CustomerizedHandler {
    private Airlines airlines;

    public static Airlines getInstance(Airlines airlines) {
        CustomerizedScalper scalper = new CustomerizedScalper();
        scalper.airlines = airlines;
        return (Airlines) CustomerizedProxy.newProxyInstance(airlines.getClass().getClassLoader(), airlines.getClass().getInterfaces(), scalper);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("我是黄牛:");
        Object result = method.invoke(airlines, args);
        System.out.println("每张票附加200元的服务费!");
        return result;
    }
}
