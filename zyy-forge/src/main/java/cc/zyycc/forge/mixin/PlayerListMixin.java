//package cc.zyycc.forge.mixin;
//
//
//import cc.zyycc.common.bridge.loader.LoaderHelper;
//import cc.zyycc.common.bridge.loader.MyLoader;
//import cc.zyycc.common.bridge.server.IServerProvider;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.server.dedicated.DedicatedServer;
//import net.minecraft.server.management.PlayerList;
//
//import net.minecraft.util.registry.DynamicRegistries;
//import net.minecraft.world.GameRules;
//import net.minecraft.world.storage.PlayerData;
//
//
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//
//@Mixin(net.minecraft.server.management.PlayerList.class)
//public abstract class PlayerListMixin {
//    static {
//        try {
//            Class<?> aClass = Class.forName("net.minecraft.server.management.PlayerList");
//            System.out.println(">>> 记载在其" + aClass.getClassLoader());
//            System.out.println(">>> 父" + aClass.getClassLoader().getParent());
//            System.out.println(">>> 父" + aClass.getClassLoader().getParent().getParent());
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Inject(method = "<init>", at = @At(value = "RETURN"))
//    private void PlayerListMixinCode(MinecraftServer minecraftServer, DynamicRegistries.Impl p_i231425_2_, PlayerData p_i231425_3_, int p_i231425_4_, CallbackInfo ci) {
//        System.out.println("Mixin类的ClassLoader: " + this.getClass().getClassLoader());
//        LoaderHelper.setClassLoader(MyLoader.Transforming, this.getClass().getClassLoader());
//        try {
//
//         //   CraftServer craftServer = new CraftServer((DedicatedServer) minecraftServer, (PlayerList) (Object) this);
//
//            // Init.method((DedicatedServer)minecraftServer, (PlayerList) (Object) this);
//
//
////            ClassLoader bkLoader = LoaderHelper.getClassLoader(MyLoader.BK);
////
////            Class<?> clz = bkLoader.loadClass("org.bukkit.craftbukkit.v1165.CraftServer");
////
////
////            clz.getDeclaredConstructor(DedicatedServer.class, PlayerList.class)
////                    .newInstance((DedicatedServer) minecraftServer, (PlayerList) (Object) this);
//
//
//            //  Class.forName("org.bukkit.craftbukkit.v1165.CraftServer", false, TransformingClassLoader.class.getClassLoader());
////            Class.forName("net.minecraft.server.dedicated.DedicatedServer", false, this.getClass().getClassLoader());
//
//
//            // aClass.getMethod("<init>", DedicatedServer.class, PlayerList.class).invoke(aClass, (DedicatedServer)minecraftServer, (PlayerList) (Object) this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
////        System.out.println("之后" + CraftServer.class.getClassLoader());
////        System.out.println("之后" + Thread.currentThread().getContextClassLoader());
//        //System.out.println(CraftServer.class.getClassLoader());
//        //CraftServer craftServer = new CraftServer((DedicatedServer) minecraftServer, (PlayerList) (Object) this);
//
//
//        // BKMain.create((DedicatedServer)minecraftServer, (PlayerList) (Object)this);
//    }
//
//    private static IServerProvider init(MinecraftServer minecraftServer) {
//        return new IServerProvider() {
//            @Override
//            public Object getDedicatedServer() {
//                return minecraftServer;
//            }
//
//            @Override
//            public Object getPlayerList() {
//                return (PlayerList) (Object) this;
//            }
//        };
//    }
//
//
//}
