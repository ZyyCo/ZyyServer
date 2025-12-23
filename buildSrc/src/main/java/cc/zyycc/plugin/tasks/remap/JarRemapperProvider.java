package cc.zyycc.plugin.tasks.remap;


import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;
import net.md_5.specialsource.transformer.MappingTransformer;

import java.util.function.Function;
import java.util.function.Supplier;

public class JarRemapperProvider extends JarRemapper {
    private final MappingMap mappingMap;
    private final Supplier<MappingTransformer> mappingTransformerSupplier;

    @Override
    public String map(String className) {
        return mappingMap.className(className);
    }



    public JarRemapperProvider(JarMapping jarMapping, MappingMap mappingMap) {
        super(jarMapping);

        this.mappingMap = mappingMap;
        this.mappingTransformerSupplier = () -> new MappingTransformer() {
            @Override
            public String transformClassName(String className) {
                return JarRemapperProvider.this.map(className);
            }

            @Override
            public String transformMethodDescriptor(String oldDescriptor) {
                return JarRemapperProvider.this.mapMethodDesc(oldDescriptor);
            }
        };
    }

    public MappingTransformer getMappingTransformer() {
        return mappingTransformerSupplier.get();
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
