package cc.zyycc.agent.enhancer.plugin;

import cc.zyycc.common.mapper.method.MethodMappingEntry;
import org.objectweb.asm.commons.Remapper;

public class PluginRemapper extends Remapper {
    private final SimpleRemapper delegate;

    public PluginRemapper(SimpleRemapper delegate) {
        this.delegate = delegate;
    }

    @Override
    public String map(String internalName) {
        String map = delegate.map(internalName);
        checkLeak("map", map);
        return map;
    }

    @Override
    public String mapType(String internalName) {
        String name = delegate.map(internalName);
        checkLeak("mapType", name);
        return name;
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
        String sign = delegate.methodDesc(signature);
        checkLeak("mapSignature", sign);
        return sign;
    }

    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        return delegate.mapFiled(owner, name);
    }

    @Override
    public String mapMethodName(String owner, String methodName, String descriptor) {
        // constructor / static block 不 remap
        if ("<init>".equals(methodName) || "<clinit>".equals(methodName)) return methodName;
        String clazz = owner;
        String name = methodName;
        if (owner.startsWith("net/minecraft/server/v1_")) {
            MethodMappingEntry resultEntry = delegate.mapMethod(owner, methodName, descriptor);
            if (resultEntry != null) {
                clazz = resultEntry.getClassName();
                name = resultEntry.getMethodName();
            }
        }

        checkLeak("mapMethodName owner", clazz);

        return name;
    }

    private void checkLeak(String context, String s) {
        if (s != null && (s.contains("net/minecraft/server/v1_"))) {
            System.out.println("[RemapLeak@" + context + "] " + s);
        }
    }

}
