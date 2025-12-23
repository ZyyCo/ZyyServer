package cc.zyycc.plugin.tasks.remap;

import com.google.common.base.Supplier;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;
import net.md_5.specialsource.transformer.MappingTransformer;

import java.util.function.Function;

public class TransformerProvider {
    Supplier<MappingTransformer> mappingTransformerSupplier;

    public TransformerProvider(JarRemapper jarRemapper) {

        this.mappingTransformerSupplier = () -> new MappingTransformer() {
            @Override
            public String transformClassName(String className) {
                return jarRemapper.map(className);
            }

            @Override
            public String transformMethodDescriptor(String oldDescriptor) {
                return jarRemapper.mapMethodDesc(oldDescriptor);
            }
        };
    }


    public MappingTransformer inputTransformer() {
        Function<String, String> function = className -> {
            System.out.println("input" + className);
            return className;
        };
        return transformer(function);
    }

    public static Function<String, String> outputTransformer(String bkVersion) {
        return className -> {
            if (className.startsWith("net/minecraft/server")) {
                String s = "net/minecraft/server/" + bkVersion + "/" + className.substring(className.lastIndexOf('/') + 1);
                System.out.println("原" + className + "修改成" + s);
                return s;
            }

            if (Character.isLowerCase(className.charAt(0)) && !className.contains("/")) {
                String s = "net/minecraft/abc/" + className;
                System.out.println("原" + className + "修改成" + s);
                return s;
            }
            return className;
        };

    }


    public MappingTransformer transformer(Function<String, String> function) {

        return new MappingTransformer() {
            @Override
            public String transformClassName(String className) {
                return function.apply(className);
            }

            @Override
            public String transformMethodDescriptor(String oldDescriptor) {
                return function.apply(oldDescriptor);
            }
        };

    }

}
