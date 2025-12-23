package cc.zyycc.agent.plugin;


import cc.zyycc.agent.plugin.scan.SuperClassHelper;
import cc.zyycc.common.VersionInfo;
import cc.zyycc.remap.MappingHelper;
import cc.zyycc.remap.MappingUtil;
import cc.zyycc.remap.method.MethodMappingEntry;
import cc.zyycc.util.ConfusionStr;
import cc.zyycc.util.NmsDetector;
import cc.zyycc.util.StrClassName;
import org.objectweb.asm.commons.Remapper;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PluginRemapper extends Remapper {
    private final SimpleRemapper delegate;
    private final ClassLoader loader;
    private static final Map<String, String> scanCondition = new ConcurrentHashMap<>();


    static {
//        scanCondition.put(StrClassName.getStrClass("WorldSettings").getConfusionField("worldName"), "bkWorldName");
//        scanCondition.put(ConfusionStr.getStrMethod("net/minecraft/world/storage/ServerWorldInfo", "getWorldName"), "bridge$getBKWorldName");
    }

    public PluginRemapper(SimpleRemapper delegate, ClassLoader loader) {
        this.delegate = delegate;
        this.loader = loader;
    }

    @Override
    public String map(String internalName) {
        String map = delegate.map(internalName);
        checkLeak("map", map);
        return map;
    }

    @Override
    public String mapType(String internalName) {
        String name = delegate.mapType(internalName);
        checkLeak("mapType", name);
        return name;
    }

    @Override
    public String[] mapTypes(String[] interfaces) {
        for (int i = 0; i < interfaces.length; i++) {
            interfaces[i] = map(interfaces[i]);
            checkLeak("mapTypes", interfaces[i]);
        }
        return interfaces;
    }

    @Override
    public String mapDesc(String descriptor) {
        String desc = delegate.mapDesc(descriptor);
        checkLeak("descriptor", desc);
        return desc;
    }

    @Override
    public String mapMethodDesc(String methodDescriptor) {
        String methodDesc = delegate.methodDesc(methodDescriptor);
        checkLeak("methodDescriptor", methodDesc);
        return methodDesc;
    }

    @Override
    public String mapSignature(String signature, boolean typeSignature) {
        String sign = delegate.mapSignature(signature);
        checkLeak("mapSignature", sign);
        return sign;
    }

    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        if (owner.startsWith("java/") || owner.startsWith("org/bukkit/") || !MappingHelper.hasMappingField(name)) {
            return name;
        }

        if (name.equals("field_234943_a_")) {
            name = "bkWorldName";
        }

        owner = SuperClassHelper.getFieldClassSource(owner, name);
        if (!NmsDetector.isBkNms(owner)) {
            return name;
        }
        return delegate.mapFiled(owner, name);
    }

    public String mapMethodName(String owner, String methodName, String descriptor, boolean isInterface) {

        if ("<init>".equals(methodName) || "<clinit>".equals(methodName)) return methodName;
        if (owner.startsWith("java/") || owner.startsWith("org/bukkit/") || !MappingHelper.hasMappingMethodName(methodName)) {
            return methodName;
        }
        if (methodName.equals("func_76065_j")) {
            methodName = "bridge$getBKWorldName";
        }

        String superClass = SuperClassHelper.getMethodClassSource(owner, methodName, descriptor);

        if (!NmsDetector.isBkNms(superClass)) {
            return methodName;
        }
        MethodMappingEntry searchEntry = MethodMappingEntry.create(superClass, methodName, descriptor);
        String remapMethod = delegate.mapMethod(searchEntry);
        if (remapMethod != null) {
            return remapMethod;
        }
        String map = delegate.map(owner);
        String remapDesc = delegate.methodDesc(descriptor);


        return getMethod(map, methodName, remapDesc, searchEntry);//兜底。
    }


    public String getMethod(String owner, String methodName, String descriptor, MethodMappingEntry searchEntry) {
        MethodMappingEntry resultEntry = MethodMappingEntry.create(owner, methodName, descriptor);
        try {//兜底。
            Method method = Class.forName(owner.replace("/", "."), false, loader)
                    .getMethod(methodName, MappingUtil.getParams(loader, descriptor));
            resultEntry.setClassName(method.getDeclaringClass().getName());
            SimpleRemapper.cacheMethod.addSuccess(searchEntry.generate(), resultEntry.generate());
            return resultEntry.getMethodName();
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {//只是命名参数和父类相同，所以可以跳过。
            SimpleRemapper.cacheMethod.addSuccess(searchEntry.generate(), resultEntry.generate());
            System.out.println("漏网之鱼" + resultEntry.generate());
        } catch (NoSuchMethodException ignored) {
            if (owner.startsWith("net/minecraft/server/v")) {
                SimpleRemapper.cacheMethod.addFail(searchEntry.generate(), "当前remapClass" + "" + "方法 mapMethod");
            }
        }
        return methodName;
    }

    @Override
    public String mapMethodName(String owner, String methodName, String descriptor) {
        return mapMethodName(owner, methodName, descriptor, false);
    }


    private void checkLeak(String context, String s) {
        if (s != null && (s.contains("net/minecraft/server/" + VersionInfo.BUKKIT_VERSION))) {
            System.out.println("[RemapLeak@" + context + "] " + s);
        }
//        if (s != null && s.contains("org/bukkit/craftbukkit/libs/it/unimi/dsi/fastutil/")) {
//            System.out.println("[RemapLeak@" + context + "] " + s);
//        }
    }

}
