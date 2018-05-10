package homework.spring2.framework.servlet;

import homework.spring2.framework.annotation.Controller;
import homework.spring2.framework.annotation.RequestMapping;
import homework.spring2.framework.annotation.RequestParam;
import homework.spring2.framework.context.GPApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DispatcherServlet extends HttpServlet {
    public static final String TEMPLATE_ROOT = "templateRoot";
    private GPApplicationContext context;

    private List<HandlerMapping> handlerMappings = new ArrayList<>();

    private Map<HandlerMapping, HandlerAdapter> handlerAdapters = new ConcurrentHashMap<>();

    private List<ViewReslover> viewResolvers = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatcher(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("<font size='25' color='blue'>500 Exception</font><br/>Details:<br/>" + e.getClass().getName() + ":" + e.getMessage() + "<br>" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s+", "<br/>") + "<br/><font color='green'><i>Copyright@GupaoEDU</i></font>");
        }
    }

    private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HandlerMapping mapping = getHandlerMapping(req);
        HandlerAdapter adapter = getHandlerAdapter(mapping);
        ViewAndModel mv = adapter.handle(req, resp, mapping);
        processDispatchResult(resp, mv);
    }

    private HandlerMapping getHandlerMapping(HttpServletRequest req) {
        String contextPath = req.getContextPath();
        String uri = req.getRequestURI().replace(contextPath, "").replaceAll("/+", "/");
        for (HandlerMapping mapping : handlerMappings) {
            Matcher matcher = mapping.getPattern().matcher(uri);
            if (matcher.matches()) {
                return mapping;
            }
        }
        throw new RuntimeException("no handlerMapping matched to:" + uri);
    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping mapping) {
        return this.handlerAdapters.get(mapping);
    }

    private void processDispatchResult(HttpServletResponse resp, ViewAndModel mv) throws Exception {
        //调用viewResolver的resolveView方法
        if (null == mv) {
            return;
        }

        if (this.viewResolvers.isEmpty()) {
            return;
        }

        for (ViewReslover viewResolver : this.viewResolvers) {

            if (!mv.getViewName().equals(viewResolver.getViewName())) {
                continue;
            }
            String out = viewResolver.viewResolver(mv);
            if (out != null) {
                resp.getWriter().write(out);
                break;
            }
        }

    }

    @Override
    public void init(ServletConfig config) {
        context = new GPApplicationContext(config.getInitParameter("contextConfigLocation"));
        initStrategies(context);
        return;
    }

    private void initStrategies(GPApplicationContext context) {
//        initMultipartResolver(context);
//        initLocaleResolver(context);
//        initThemeResolver(context);
        initHandlerMappings(context);
        initHandlerAdapters(context);
//        initHandlerExceptionResolvers(context);
//        initRequestToViewNameTranslator(context);
        initViewResolvers(context);
//        initFlashMapManager(context);
        return;
    }

    /**
     * 配置Url与method的映射关系
     *
     * @param context
     */
    private void initHandlerMappings(GPApplicationContext context) {
        //获取所有beans
        for (String beanName : context.getBeanDefinitionNames()) {
            Object bean = context.getBean(beanName);
            //筛选Controller
            if (!bean.getClass().isAnnotationPresent(Controller.class)) continue;
            //new handlerMapping
            String classUri = bean.getClass().getAnnotation(RequestMapping.class).value();
            Method[] methods = bean.getClass().getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(RequestMapping.class)) continue;
                String uri = classUri + "/" + method.getAnnotation(RequestMapping.class).value();
                uri = uri.replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(uri);
                handlerMappings.add(new HandlerMapping(bean, method, pattern));
                System.out.println("mapping:" + uri + " to " + method);
            }
        }

    }

    /**
     * 匹配动态参数
     *
     * @param context
     */
    private void initHandlerAdapters(GPApplicationContext context) {
        //遍历handlerMapping
        for (HandlerMapping mapping : handlerMappings) {
            Map<String, Integer> paramMap = new HashMap<>();

            //获取method的annotation二维数组
            Annotation[][] parameterAnnotations = mapping.getMethod().getParameterAnnotations();
            //寻找自定义命名参数,保存参数下标
            for (int i = 0; i < parameterAnnotations.length; i++) {
                for (Annotation annotation : parameterAnnotations[i]) {
                    if (annotation instanceof RequestParam) {
                        String paramName = ((RequestParam) annotation).value();
                        if (!"".equals(paramName)) {
                            paramMap.put(paramName, i);
                        }
                    }
                }
            }
            //处理非命名参数:保存req和resp参数下标
            Class<?>[] parameterTypes = mapping.getMethod().getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == HttpServletRequest.class || parameterTypes[i] == HttpServletResponse.class || parameterTypes[i] == ViewAndModel.class) {
                    paramMap.put(parameterTypes[i].getName(), i);
                }
            }
            this.handlerAdapters.put(mapping, new HandlerAdapter(paramMap));
        }
    }

    /**
     * 解析一套模板语言
     *
     * @param context
     */
    private void initViewResolvers(GPApplicationContext context) {
        //匹配viewName与具体模板文件
        //获取模板文件目录配置信息
        String templateRoot = context.getConfig().getProperty(TEMPLATE_ROOT);
        File templateRootDir = new File(this.getClass().getClassLoader().getResource(templateRoot).getFile());
        //匹配模板名称&模板file
        addViewResolvers(templateRootDir, "");
    }

    private void addViewResolvers(File dir, String namePrefix) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                addViewResolvers(file, namePrefix + file.getName() + "/");
                continue;
            }
            this.viewResolvers.add(new ViewReslover(namePrefix + file.getName(), file));
        }
    }

}
