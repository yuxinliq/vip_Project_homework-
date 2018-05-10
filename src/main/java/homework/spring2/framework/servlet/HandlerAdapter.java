package homework.spring2.framework.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class HandlerAdapter {

    private Map<String,Integer> paramMap;

    public HandlerAdapter(Map<String, Integer> paramMap) {
        this.paramMap = paramMap;
    }

    /**
     *
     * @param req       用来进行参数匹配
     * @param resp      用来传递给用户method
     * @param mapping   用来进行参数匹配
     * @return
     */
    public ViewAndModel handle(HttpServletRequest req, HttpServletResponse resp, HandlerMapping mapping) {
        return null;
    }

    public Map<String, Integer> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Integer> paramMap) {
        this.paramMap = paramMap;
    }
}
