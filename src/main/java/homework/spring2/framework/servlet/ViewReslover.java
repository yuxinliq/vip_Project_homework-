package homework.spring2.framework.servlet;

import java.io.File;

/**
 * 1.将一个静态文件解析为一个动态模板
 * 2.根据用户传参,生成不同的结果
 */
public class ViewReslover {
    private String viewName;
    private File viewFile;

    public ViewReslover(String viewName, File viewFile) {
        this.viewName = viewName;
        this.viewFile = viewFile;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public File getViewFile() {
        return viewFile;
    }

    public void setViewFile(File viewFile) {
        this.viewFile = viewFile;
    }
}
