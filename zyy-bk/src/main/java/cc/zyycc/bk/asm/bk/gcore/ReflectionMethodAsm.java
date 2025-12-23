package cc.zyycc.bk.asm.bk.gcore;

import cc.zyycc.common.bridge.SafeMethodBridge;
import net.minecraft.network.IPacket;

import java.util.Iterator;
import java.util.List;

public class ReflectionMethodAsm {


    public static String reflectionMethodEnhancer(Class<?> original, String name, List<Class<?>> params) {
        if (original.getName().startsWith("net.minecraft")) {
            if (!params.isEmpty()) {
                Iterator<Class<?>> iterator = params.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    Class<?> param = iterator.next();
                    if (param.getName().equals("net.minecraft.network.play.server.SChatPacket")) {
                        params.set(i, IPacket.class);
                        i += 1;
                    }
                }
            }
            try {
                return SafeMethodBridge.getMethod(original, name, params.toArray(new Class[0])).getName();
            } catch (Exception e) {
                System.err.println("[DEBUG] ReflectionMethod new");
                return name;
            }
        }

        return name;
    }

}
