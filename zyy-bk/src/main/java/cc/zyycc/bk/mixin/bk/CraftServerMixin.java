package cc.zyycc.bk.mixin.bk;

import cc.zyycc.bk.bridge.server.MinecraftServerBridge;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.command.CraftCommandMap;
import org.spigotmc.AsyncCatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.io.File;
import java.util.Map;
import java.util.UUID;

@Mixin(value = CraftServer.class, remap = false)
public abstract class CraftServerMixin {
    @Shadow(remap = false)
    @Final
    protected DedicatedServer console;

    @Shadow(remap = false)
    public abstract World getWorld(UUID uid);

    @Final
    @Shadow(remap = false)
    private Map<String, World> worlds;
    @Shadow(remap = false)
    @Final
    private CraftCommandMap commandMap;

    @Shadow
    public abstract DedicatedServer getServer();

    @ModifyVariable(method = "dispatchCommand", remap = false, index = 2, at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lorg/spigotmc/AsyncCatcher;catchOp(Ljava/lang/String;)V"), argsOnly = true)
    public String dispatchCommand(String value) {
        if (value.startsWith("/")) {
            value = value.substring(1);
        }
        return value;
    }


    public File getWorldContainer(String worldName) {
        RegistryKey<net.minecraft.world.World> world = net.minecraft.world.World.OVERWORLD;
        if (worldName.equals("the_nether")) {
            world = net.minecraft.world.World.THE_NETHER;
        }
        if (worldName.equals("the_end")) {
            world = net.minecraft.world.World.THE_END;
        }

        return this.getServer().anvilConverterForAnvilFile.getDimensionFolder(world).getParentFile();
    }


//    public void addWorld(World world) {
//
//        if (this.getWorld(world.getUID()) != null) {
//            System.out.println("World " + world.getName() + " is a duplicate of another world and has been prevented from loading. Please delete the uid.dat file from " + world.getName() + "'s world directory if you want to be able to load the duplicate world.");
//        } else {
//            this.worlds.put(world.getName().toLowerCase(Locale.ENGLISH), world);
//        }
//    }
//    @Overwrite
//    public boolean isPrimaryThread() {
//        if(Thread.currentThread().equals(this.console.serverThread)){
//            return true;
//        }else if(((MinecraftServerBridge)this.console).bridge$hasStopped()){
//            return true;
//        }else if(!AsyncCatcher.enabled){
//            return true;
//        }
//        return false;
//    }


}
