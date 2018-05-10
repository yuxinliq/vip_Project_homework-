package homework.spring2.framework.servlet;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public String viewResolver(ViewAndModel mv) throws Exception {
        StringBuffer sb = new StringBuffer();

        RandomAccessFile ra = new RandomAccessFile(this.viewFile, "r");

        try {
            String line = null;
            while (null != (line = ra.readLine())) {
//                line = new String(line.getBytes("utf-8"), "utf-8");
                Matcher m = matcher(line);
                while (m.find()) {
                    for (int i = 1; i <= m.groupCount(); i++) {
                        String paramName = m.group(i);
                        Object paramValue = mv.getMap().get(paramName);
                        if (null == paramValue) {
                            continue;
                        }
                        line = line.replaceAll("\\*\\{" + paramName + "\\}", paramValue.toString());
                    }
                }
                sb.append(line);
            }
        } finally {
            ra.close();
        }

        return sb.toString();
    }

    private Matcher matcher(String str) {
        Pattern pattern = Pattern.compile("\\*\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher;
    }
}
