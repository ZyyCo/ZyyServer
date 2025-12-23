package cc.zyycc.common.tool;


import cc.zyycc.common.bridge.InstrumentationBridge;
import cc.zyycc.common.loader.LoaderManager;
import cc.zyycc.common.loader.MyLoader;
import cc.zyycc.common.util.DownloaderLib;
import cc.zyycc.common.util.FileManager;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BootstrapAgent {

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        premain(args, inst);
    }

    public static void premain(String args, Instrumentation inst) throws Exception {

        InstrumentationBridge.instrumentation = inst;

        List<Path> paths = DownloaderLib.downloadExtraMavenJar(new String[]{"org.ow2.asm:asm:9.8",
                "org.ow2.asm:asm-commons:9.8", "net.md-5:SpecialSource:1.11.5",
                "net.sf.jopt-simple:jopt-simple:5.0.4"});
        Path bridge = FileManager.extractJar("zyy-bridge.jar", "bridge.jar");
        paths.add(bridge);
        URLClassLoader isolated = new URLClassLoader(
                paths.stream().map(path -> {
                    try {
                        return path.toUri().toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }).toArray(URL[]::new),
                null
        );

        Path cacheAgent = FileManager.extractJar("zyy-agent.jar", "agent.jar");
        URLClassLoader agentLoader = new URLClassLoader(new URL[]{cacheAgent.toUri().toURL()}, isolated);

        LoaderManager.registerClassLoader(MyLoader.AGENT, agentLoader);
        Class<?> mainClass = agentLoader.loadClass("cc.zyycc.agent.ClasspathAgent");
        mainClass.getMethod("start", String.class, Instrumentation.class).invoke(null, args, inst);
    }
}
