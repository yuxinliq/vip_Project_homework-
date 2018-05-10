package homework.spring2.framework.servlet;

import javafx.beans.binding.ObjectExpression;

import java.util.HashMap;
import java.util.Map;

public class ViewAndModel {
    private String viewName;
    private Map<String, Object> map = new HashMap<>();

    public ViewAndModel() {
    }

    public ViewAndModel(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
