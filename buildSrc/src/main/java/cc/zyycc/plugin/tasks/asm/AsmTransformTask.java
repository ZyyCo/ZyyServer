package cc.zyycc.plugin.tasks.asm;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class AsmTransformTask extends DefaultTask {

    private File inputJar;
    private File outputJar;

    private static final Map<String, Function<ClassWriter, ClassVisitor>> enhancerMap = new HashMap<>();


    public void addEnhancer(ClassEnhancer classEnhancer) {
        enhancerMap.put(classEnhancer.targetClassName(), (cw) -> classEnhancer.createVisitor(cw));
    }

    public void addEnhancers(List<ClassEnhancer> classEnhancers) {
        for (ClassEnhancer classEnhancer : classEnhancers) {
            addEnhancer(classEnhancer);
        }
    }


    public void setInputJar(File inputJar) {
        this.inputJar = inputJar;
    }

    public void setOutputJar(File outputJar) {
        this.outputJar = outputJar;
    }

    public void registerEnhancer(String targetClass, Function<ClassWriter, ClassVisitor> function) {
        if(targetClass.contains("/")){
            targetClass = targetClass.replaceAll("/", ".");
        }
        if (!targetClass.endsWith(".class")) {
            targetClass = targetClass + ".class";
        }

        enhancerMap.put(targetClass, function);
    }

    public void registerEnhancer(Map<String, Function<ClassWriter, ClassVisitor>> enhancer) {
        enhancerMap.putAll(enhancer);
    }


    @TaskAction
    public void transform() throws IOException {
        if (enhancerMap.size() == 0) {
            return;
        }
        try (JarFile jarFile = new JarFile(inputJar);
             JarOutputStream jos = new JarOutputStream(new FileOutputStream(outputJar))) {

            jarFile.stream().forEach(entry -> {
                try {
                    InputStream is = jarFile.getInputStream(entry);
                    byte[] data = readAllBytes(is);
                    if (enhancerMap.containsKey(entry.getName())) {
                        System.out.println(entry.getName());
                        ClassReader classReader = new ClassReader(data);
                        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                        Function<ClassWriter, ClassVisitor> function = enhancerMap.get(entry.getName());
                        ClassVisitor apply = function.apply(classWriter);

                        classReader.accept(apply, 0);
                        data = classWriter.toByteArray();

                    }
                    jos.putNextEntry(new JarEntry(entry.getName()));
                    jos.write(data);
                    jos.closeEntry();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public static byte[] readAllBytes(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;
        while ((nRead = input.read(data)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
}