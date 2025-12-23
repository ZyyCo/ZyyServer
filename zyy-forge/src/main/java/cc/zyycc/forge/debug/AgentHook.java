package cc.zyycc.forge.debug;


import cc.zyycc.common.bridge.BridgeHolder;
import cc.zyycc.forge.MainForge;
import cc.zyycc.forge.mod.MyMinecraftLocator;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import net.minecraftforge.fml.loading.moddiscovery.ModDiscoverer;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileParser;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;

import javax.annotation.Untainted;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class AgentHook {
    public static ModDiscoverer modDiscoverer;

    public static void error(Object obj) {

    }


    public static void saveDiscoverer(Object obj) {
        modDiscoverer = (ModDiscoverer) obj;
        dumpLocatorList();
    }


    public static void dumpLocatorList() {
        try {


            Object discoverer = AgentHook.modDiscoverer;
            Field fieldLocatorClassLoader = discoverer.getClass().getDeclaredField("locatorClassLoader");
            fieldLocatorClassLoader.setAccessible(true);
            URLClassLoader locatorClassLoader = (URLClassLoader) fieldLocatorClassLoader.get(discoverer);


            Enumeration<URL> resources = locatorClassLoader.getResources(
                    "META-INF/services/net.minecraftforge.forgespi.locating.IModLocator");

            while (resources.hasMoreElements()) {
                System.out.println("找到了 SPI 配置文件: " + resources.nextElement());
            }

            System.out.println("locatorClassLoader：" + locatorClassLoader);
            System.out.println("parentLocatorClassLoader：" + locatorClassLoader.getParent());


            System.out.println(Arrays.toString(locatorClassLoader.getURLs()));


            Field field = discoverer.getClass().getDeclaredField("locatorList");
            field.setAccessible(true);
            List<IModLocator> locatorList = (List<IModLocator>) field.get(discoverer);

            Iterator<IModLocator> iterator = locatorList.iterator();

            while (iterator.hasNext()) {
                IModLocator locator = iterator.next();
                if (locator.name().equals("minecraft")) {
                    iterator.remove();
                }
            }
            MyMinecraftLocator myMinecraftLocator = new MyMinecraftLocator();
            locatorList.add(myMinecraftLocator);
            IModFile modFile = null;
            for (IModLocator iModLocator : locatorList) {
                System.out.println("locator:" + iModLocator +
                        "classLoader:" + iModLocator.getClass().getClassLoader() +
                        "className:" + iModLocator.getClass().getName() + "package:" + iModLocator.getClass().getPackage());
                System.out.println("name" + iModLocator.name());
                List<IModFile> iModFiles = iModLocator.scanMods();

                if (iModLocator.name().equals("zyyserver")) {
                    modFile = iModFiles.get(0);
                }
                for (IModFile iModFile : iModFiles) {
                    System.out.println("--模组路径:" + iModFile);
                    System.out.println("--modFile.getFilePath:" + iModFile.getFilePath());
                    if (iModFile.getType() == IModFile.Type.MOD) {
                        System.out.println("类型模组");
                    }
                    System.out.println("--模组.getLocator:" + iModFile.getLocator());

                    System.out.println("--modFile.getModFileInfo:" + iModFile.getModFileInfo());
                    System.out.println("--------------------------------------------------------");
                }
            }

//            IModLocator locator = modFile.getLocator();
//            Path modsjson = locator.findPath(modFile, new String[]{"META-INF", "mods.toml"});
//            System.out.println("哈哈哈modsjson:" + modsjson);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
