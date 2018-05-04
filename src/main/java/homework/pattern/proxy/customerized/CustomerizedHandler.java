package homework.pattern.proxy.customerized;

import java.lang.reflect.Method;

public interface CustomerizedHandler {
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
