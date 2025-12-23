package cc.zyycc.agent.transformer;

import cc.zyycc.agent.ClasspathAgent;
import cc.zyycc.agent.plugin.DynamicRemapper;
import cc.zyycc.agent.plugin.PluginRemapper;
import cc.zyycc.agent.plugin.SimpleRemapper;
import cc.zyycc.common.VersionInfo;
import org.objectweb.asm.*;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NmsRemapTransformer implements ClassFileTransformer {

    public NmsRemapTransformer() {

    }


    private final Set<String> allowedLoaders = Collections.unmodifiableSet(new HashSet<String>() {{
        add("org.bukkit.plugin.java.PluginClassLoader");
        //  add("cpw.mods.modlauncher.TransformingClassLoader$DelegatedClassLoader");
    }});

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if(!className.equals("org/bukkit/craftbukkit/" + VersionInfo.BUKKIT_VERSION + "/CraftWorld")){
            if (loader == null || !allowed(loader)) return null;
            //预扫描常量池，没命中就不做（性能优化）
            if (!mayContainNmsRef(classfileBuffer)) return null;
        }




        try {
            byte[] current = load(loader, className, classfileBuffer);
//            if (SimpleRemapper.alreadyRemap.get().equals(className)) {
//                current = load(loader, className, classfileBuffer);
//            }

            //ClasspathAgent.dump(className, current);

            return current;
        } catch (Throwable throwable) {
            System.out.println(throwable.getMessage());
            return null;
        }
    }

    private static byte[] load(ClassLoader loader, String className, byte[] current) {
        ClassReader cr = new ClassReader(current);
        ClassWriter cw = new ClassWriter(0) {
            @Override
            protected String getCommonSuperClass(String t1, String t2) {
                return "java/lang/Object"; // 防止 ASM 去加载外部类
            }
        };

        SimpleRemapper remapper = new SimpleRemapper(className, loader);
        PluginRemapper pluginRemapper = new PluginRemapper(remapper, loader);
        ClassVisitor cv = new DynamicRemapper(cw, className, pluginRemapper);

        cr.accept(cv, 0);

        return cw.toByteArray();
    }

    private boolean allowed(ClassLoader loader) {
        String n = loader.getClass().getName();
        return allowedLoaders.contains(n) || n.contains("PluginClassLoader");
    }

    private boolean mayContainNmsRef(byte[] buf) {
        for (int i = 0; i + 24 < buf.length; i++) {
            if (buf[i] == 'e' && buf[i + 1] == 'n' && buf[i + 2] == 't'
                    && buf[i + 3] == 'i' && buf[i + 4] == 't' && buf[i + 5] == 'y'
                    ||
                    buf[i] == 'n' && buf[i + 1] == 'e' && buf[i + 2] == 't'
                            && buf[i + 3] == '/' && buf[i + 4] == 'm' && buf[i + 5] == 'i'
                            && buf[i + 6] == 'n' && buf[i + 7] == 'e' && buf[i + 8] == 'c'
                            && buf[i + 9] == 'r' && buf[i + 10] == 'a' && buf[i + 11] == 'f'
                            && buf[i + 12] == 't' && buf[i + 13] == '/'
//                    && buf[i + 14] == 's'
//                    && buf[i + 15] == 'e' && buf[i + 16] == 'r' && buf[i + 17] == 'v'
//                    && buf[i + 18] == 'e' && buf[i + 19] == 'r' && buf[i + 20] == '/'
            ) return true;
        }
        return false;
    }
}




