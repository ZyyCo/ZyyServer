package cc.zyycc.bk.asm.mc.network;

import cc.zyycc.bk.util.NetworkSystemUtil;
import io.netty.channel.*;

import java.lang.reflect.Method;


public class NetWorkSystemAsm {
    public static void guard(Channel channel) {
        //NetworkSystemUtil.lock(channel);
    }

    private static volatile Method GET_NETHANDLER; // cache
    public static boolean isLoginPhase(Object networkManagerObj) {
        if (networkManagerObj == null) return false;
        try {
            Method m = GET_NETHANDLER;
            if (m == null || m.getDeclaringClass() != networkManagerObj.getClass()) {
                m = networkManagerObj.getClass().getMethod("func_150729_e");//getNetHandler
                m.setAccessible(true);
                GET_NETHANDLER = m;
            }
            Object handler = m.invoke(networkManagerObj);
            if (handler == null) return false;
            // 不强依赖类加载器：用类名匹配最稳
            String name = handler.getClass().getName();
            return name.endsWith("ServerHandshakeNetHandler")
                    || (name.contains(".login.") && name.contains("NetHandler"));
        } catch (Throwable t) {
            return false;
        }
    }


}
