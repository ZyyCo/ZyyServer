package cc.zyycc.installer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Supplier;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


public class InstallForge {


    public static void install(String jarResourcePath, String workingDirectory, String forgeLocalPath, Path forgeJar, Supplier<String[]> supplierLibs) throws IOException, InterruptedException {

        File workingDir = new File(workingDirectory);//本地安装包路径,带安装包名称

        if (!workingDir.exists()) {
            //从jar包里移动文件到临时目录
            System.out.println("从 jar 包中提取安装器...");
            extractInstallerToTemp(jarResourcePath, workingDir);
        }


        // 调用 installer 安装服务端
        System.out.println("开始安装 Forge 服务端...");
        //控制台命令 java -jar forge-1.19.2-43.2.0-installer.jar --installServer
        ProcessBuilder pb = new ProcessBuilder(
                "java", "-jar", workingDir.getAbsolutePath(), "--installServer"
        );
        pb.directory(new File(forgeLocalPath)); // 设置解压目录
        pb.inheritIO(); // 输出子进程日志
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("Forge 服务端安装成功！");
        } else {
            System.err.println("Forge 安装失败，退出码：" + exitCode);
        }
    }

    private static String[] checkLibs(Path forgeJar, Supplier<String[]> supplierLibs) {
        if (!forgeJar.toFile().exists()) {
            return new String[0];
        }
        String[] libs = supplierLibs.get();
        if (libs == null || libs.length == 0) {
            return new String[0];
        }
        //检查依赖是否全
        for (String lib : libs) {
            if (!Paths.get(lib).toFile().exists()) {
                return new String[0];
            }
        }

        return libs;
    }

    public static String[] checkOrInstall(String jarResourcePath, String workingDirectory, String forgeLocalPath, Path forgeJar, Supplier<String[]> supplierLibs) throws IOException, InterruptedException {
        String[] libs = checkLibs(forgeJar, supplierLibs);
        if (libs.length > 0) {
            return libs;
        }
        install(jarResourcePath, workingDirectory, forgeLocalPath, forgeJar, supplierLibs);
        libs = checkLibs(forgeJar, supplierLibs);
        if (libs.length == 0) {
            throw new RuntimeException("Forge 安装失败，依赖未就绪");
        }

        return libs;
    }


    public static void extractInstallerToTemp(String jarResourcePath, File target) throws IOException {

        //如果不存在创建文件夹
        Files.createDirectories(target.toPath());
        // 从 JAR 包内部提取安装器资源
        try (InputStream in = InstallForge.class.getClassLoader().getResourceAsStream(String.valueOf(jarResourcePath))) {
            if (in == null) {
                throw new IOException("无法在资源中找到安装器文件：" + target);
            }
            Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("安装器成功提取到: " + target.getAbsolutePath());
        }
    }


}


