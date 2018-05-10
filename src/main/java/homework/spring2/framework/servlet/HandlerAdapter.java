package homework.spring2.framework.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

public class HandlerAdapter {

    private Map<String, Integer> paramMap;

    public HandlerAdapter(Map<String, Integer> paramMap) {
        this.paramMap = paramMap;
    }

    /**
     * @param req     用来进行参数匹配
     * @param resp    用来传递给用户method
     * @param mapping 用来进行参数匹配
     * @return
     */
    public ViewAndModel handle(HttpServletRequest req, HttpServletResponse resp, HandlerMapping mapping) throws InvocationTargetException, IllegalAccessException {
        //1、要准备好这个方法的形参列表
        //方法重载：形参的决定因素：参数的个数、参数的类型、参数顺序、方法的名字
        Class<?>[] paramTypes = mapping.getMethod().getParameterTypes();

        //2、拿到自定义命名参数所在的位置
        //用户通过URL传过来的参数列表
        Map<String, String[]> reqParameterMap = req.getParameterMap();

        //3、构造实参列表
        Object[] paramValues = new Object[paramTypes.length];
        for (Map.Entry<String, String[]> param : reqParameterMap.entrySet()) {
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll("\\s", "");

            if (!this.paramMap.containsKey(param.getKey())) {
                continue;
            }

            int index = this.paramMap.get(param.getKey());

            //因为页面上传过来的值都是String类型的，而在方法中定义的类型是千变万化的
            //要针对我们传过来的参数进行类型转换
            paramValues[index] = caseStringValue(value, paramTypes[index]);
        }

        if (this.paramMap.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = this.paramMap.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }

        if (this.paramMap.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = this.paramMap.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }

        ViewAndModel viewAndModel = null;
        if (this.paramMap.containsKey(ViewAndModel.class.getName())) {
            int viewIndex = this.paramMap.get(ViewAndModel.class.getName());
            viewAndModel = new ViewAndModel();
            paramValues[viewIndex] = viewAndModel;
        }

        //4、从handler中取出controller、method，然后利用反射机制进行调用
        Object result = mapping.getMethod().invoke(mapping.getController(), paramValues);
        if (result == null) {
            return null;
        }
        if (result instanceof ViewAndModel) {
            return (ViewAndModel) result;
        }
        if (result instanceof String) {
            if (viewAndModel != null) {
                viewAndModel.setViewName((String) result);
                return viewAndModel;
            }
            return new ViewAndModel((String) result);
        }
        return null;
    }

    private Object caseStringValue(String value, Class<?> clazz) {
        if (clazz == String.class) {
            return value;
        } else if (clazz == Integer.class) {
            return Integer.valueOf(value);
        } else if (clazz == int.class) {
            return Integer.valueOf(value).intValue();
        } else {
            return null;
        }
    }

    public Map<String, Integer> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Integer> paramMap) {
        this.paramMap = paramMap;
    }
}
