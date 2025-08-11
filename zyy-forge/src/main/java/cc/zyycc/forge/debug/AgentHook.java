package cc.zyycc.forge.debug;


import cc.zyycc.common.bridge.BridgeHolder;
import cc.zyycc.forge.MainForge;
import cc.zyycc.forge.mod.MyMinecraftLocator;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import net.minecraftforge.fml.loading.moddiscovery.ModDiscoverer;
import net.minecraftforge.forgespi.locating.IModLocator;

import javax.annotation.Untainted;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.*;

public class AgentHook {
    public static ModDiscoverer modDiscoverer;

    public static void error(Object obj) {



    }


    public static void saveDiscoverer(Object obj) {
        System.out.println("加载器agentHook" + AgentHook.class.getClassLoader());
        System.out.println("加载器MainForge" + MainForge.class.getClassLoader() + "父类加载器" + MainForge.class.getClassLoader().getParent());
        System.out.println("加载器br" + BridgeHolder.getInstance().getClassLoader());
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
            // field.set(discoverer, BridgeHolder.SPI_BRIDGE.getClassLoader());
            List<IModLocator> locatorList = (List<IModLocator>) field.get(discoverer);

            Iterator<IModLocator> iterator = locatorList.iterator();

            while (iterator.hasNext()) {
                IModLocator locator = iterator.next();
                System.out.println("locator:" + locator);
                System.out.println("name" + locator.name());
                ClassLoader classLoader = locator.getClass().getClassLoader();
                System.out.println("classLoader:" + classLoader);
                System.out.println("className:" + locator.getClass().getName());
                System.out.println("package:" + locator.getClass().getPackage());
//
                if (locator.name().equals("minecraft")) {
                    iterator.remove();
                }
            }
            MyMinecraftLocator myMinecraftLocator = new MyMinecraftLocator();
            locatorList.add(myMinecraftLocator);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
