package cc.zyycc.bk.mixin;



import cc.zyycc.common.bridge.BridgeHolder;
import cpw.mods.modlauncher.TransformingClassLoader;
import net.minecraft.server.MinecraftServer;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.management.PlayerList;

import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.storage.PlayerData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URL;
import java.net.URLClassLoader;


public abstract class PlayerListMixin2 {
    static {
        try {
            Class<?> aClass = Class.forName("net.minecraft.server.management.PlayerList");
            System.out.println(">>> 记载在其" + aClass.getClassLoader());
            System.out.println(">>> 父" + aClass.getClassLoader().getParent());
            System.out.println(">>> 父" + aClass.getClassLoader().getParent().getParent());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

   // @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void PlayerListMixinCode(MinecraftServer minecraftServer, DynamicRegistries.Impl p_i231425_2_, PlayerData p_i231425_3_, int p_i231425_4_, CallbackInfo ci) {
        System.out.println("Mixin类的ClassLoader: " + this.getClass().getClassLoader());
        try {
//            URLClassLoader classLoader = (URLClassLoader) BridgeHolder.INSTANCE.classLoader;
//            System.out.println("djskdjoklsjdklsjdlks");
//            for (URL url : classLoader.getURLs()) {
//                System.out.println(url);
//            }

            //  Class.forName("org.bukkit.craftbukkit.v1165.CraftServer", false, TransformingClassLoader.class.getClassLoader());
//            Class.forName("net.minecraft.server.dedicated.DedicatedServer", false, this.getClass().getClassLoader());



           // aClass.getMethod("<init>", DedicatedServer.class, PlayerList.class).invoke(aClass, (DedicatedServer)minecraftServer, (PlayerList) (Object) this);
        } catch (Exception e) {
            System.out.println("无法找到类org.bukkit.craftbukkit.v1165.CraftServer");
            e.printStackTrace();
        }



        // BKMain.create((DedicatedServer)minecraftServer, (PlayerList) (Object)this);
    }


}
