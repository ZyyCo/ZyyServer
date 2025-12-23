package cc.zyycc.bk.asm.bk.cmi;

import cc.zyycc.common.bridge.SafeMethodBridge;
import net.minecraft.network.IPacket;

import java.util.Iterator;
import java.util.List;

public class ReflectionMethodAsm {


    public static String reflectionMethodEnhancer(String methodName, Class<?> clazz, Class<?>... params) {
//        if (original.getName().startsWith("net.minecraft")) {
//            if (!params.isEmpty()) {
//                Iterator<Class<?>> iterator = params.iterator();
//                int i = 0;
//                while (iterator.hasNext()) {
//                    Class<?> param = iterator.next();
//                    if (param.getName().equals("net.minecraft.network.play.server.SChatPacket")) {
//                        params.set(i, IPacket.class);
//                        i += 1;
//                    }
//                }
//            }
//        }
        try {
            return SafeMethodBridge.getMethod(clazz, methodName, params).getName();
        } catch (Exception e) {
            System.err.println("[DEBUG] ReflectionMethod new");
            return methodName;
        }

    }

}
