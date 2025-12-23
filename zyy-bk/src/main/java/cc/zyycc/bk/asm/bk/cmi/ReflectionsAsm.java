package cc.zyycc.bk.asm.bk.cmi;

import cc.zyycc.common.bridge.SafeMethodBridge;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.network.IPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionsAsm {
    public static Map<String, Class<?>> reflectionClassCache = new ConcurrentHashMap<>();

    static {
        reflectionClassCache.put("Packet", IPacket.class);

    }

    public static String reflectionMethodEnhancer(String methodName, Class<?> clazz, Class<?>... params) {
        if (clazz.getName().startsWith("net.minecraft") && !methodName.equals("nextContainerCounter") && !methodName.equals("setTitle")) {
            List<Class<?>> params1 = new ArrayList<>();
            for (Class<?> param : params) {
                for (Map.Entry<String, Class<?>> entry : reflectionClassCache.entrySet()) {
                    if (param.getName().contains(entry.getKey())) {
                        params1.add(entry.getValue());
                    } else {
                        params1.add(param);
                    }
                }

            }
            if (methodName.equals("addSlotListener")) {//RepairContainer
                clazz = clazz.getSuperclass().getSuperclass();
                params1.clear();
                params1.add(IContainerListener.class);
            }
            try {
                return SafeMethodBridge.getMethod(clazz, methodName, params1.toArray(new Class[0])).getName();
            } catch (Exception e) {
                System.err.println("[ENHANCER] " + methodName + " -> " + clazz.getName());
                return methodName;
            }
        }
        return methodName;
    }

}
