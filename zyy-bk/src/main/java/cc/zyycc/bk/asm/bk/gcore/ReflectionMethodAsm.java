package cc.zyycc.bk.mixin.bk.plugins.gcore;

import java.util.List;

public class ReflectionMethodAsm {


    public static void quest(Class<?> original, String name, List<Class<?>> params) {
        if(original.getName().startsWith("net.minecraft")){
            
        }
        if(name.equals("getTag")){
            System.err.println("[DEBUG] ReflectionMethod new");
            System.err.println("  class = " + original);
            System.err.println("  name  = " + name);
            System.err.println("  params= " + params);
            new Exception("trace").printStackTrace();
        }

    }

}
