package cc.zyycc.tool;


import cc.zyycc.common.VersionInfo;
import cc.zyycc.common.util.DownloaderLib;

import java.lang.instrument.Instrumentation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BootstrapAgent {
    private static void agentmain(String args, Instrumentation inst) throws Exception {
        List<Path> paths = DownloaderLib.downloadExtraMavenJar(new String[]{"org.ow2.asm:asm:9.8",
                "org.ow2.asm:asm-commons:9.8", "net.md-5:SpecialSource:1.11.5",
        "net.sf.jopt-simple:jopt-simple:5.0.4"});


        Path agent = Paths.get(VersionInfo.WORKING_DIR, "agent.jar");
        Path bridge = Paths.get(VersionInfo.WORKING_DIR, "zyy-bridge.jar");
        paths.add(agent);
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
       // ClasspathAgent.start( inst);


        Class<?> mainClass = isolated.loadClass("cc.zyycc.agent.ClasspathAgent");
        mainClass.getMethod("start", Instrumentation.class).invoke(null, inst);
    }
}
