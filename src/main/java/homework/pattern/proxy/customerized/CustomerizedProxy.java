package homework.pattern.proxy.customerized;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class CustomerizedProxy {
    public static final String ln = "\r\n";
    public static final String PACKAGE_NAME = "homework.pattern.proxy.customerized";

    public static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces, CustomerizedHandler handler) {
        try {
            //1、动态生成源代码.java文件
            String src = generateSrc(interfaces);
            //2、Java文件输出磁盘
            File f = writeFile(src);
            //3、把生成的.java文件编译成.class文件
            compileFile(f);
            //4、编译生成的.class文件加载到JVM中来
            Class proxyClass = loader.loadClass(PACKAGE_NAME + ".$Proxy0");
            Constructor c = proxyClass.getConstructor(CustomerizedHandler.class);
            f.delete();

            //5、返回字节码重组以后的新的代理对象
            return c.newInstance(handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void compileFile(File f) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager manage = compiler.getStandardFileManager(null, null, null);
        Iterable iterable = manage.getJavaFileObjects(f);
        JavaCompiler.CompilationTask task = compiler.getTask(null, manage, null, null, null, iterable);
        task.call();
        manage.close();
    }

    private static File writeFile(String src) throws IOException {
        String filePath = CustomerizedProxy.class.getResource("").getPath();
        System.out.println(filePath);
        File f = new File(filePath + "$Proxy0.java");
        FileWriter fw = new FileWriter(f);
        fw.write(src);
        fw.flush();
        fw.close();
        return f;
    }

    private static String generateSrc(Class<?>[] interfaces) {
        StringBuilder sb = new StringBuilder();
        sb.append("package " + PACKAGE_NAME + ";" + ln);
        sb.append("import homework.pattern.proxy.Airlines;" + ln);
        sb.append("import java.lang.reflect.Method;" + ln);
        sb.append("public class $Proxy0 implements " + interfaces[0].getName() + "{" + ln);
        sb.append("CustomerizedHandler h;" + ln);
        sb.append("public $Proxy0(CustomerizedHandler h) { " + ln);
        sb.append("this.h = h;");
        sb.append("}" + ln);
        for (Method m : interfaces[0].getMethods()) {
            sb.append("public " + m.getReturnType().getName() + " " + m.getName() + "() {" + ln);
            sb.append("try{" + ln);
            sb.append("Method m = " + interfaces[0].getName() + ".class.getMethod(\"" + m.getName() + "\",new Class[]{});" + ln);
            sb.append("this.h.invoke(this,m,null);" + ln);
            sb.append("}catch(Throwable e){" + ln);
            sb.append("e.printStackTrace();" + ln);
            sb.append("}");
            sb.append("}");
        }
        sb.append("}" + ln);
        return sb.toString();
    }
}
