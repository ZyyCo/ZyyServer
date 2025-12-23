package cc.zyycc.bk.mixin.mc.server.dedicated;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.FoodStats;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.command.CraftCommandMap;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;
import java.util.UUID;

@Mixin(CraftServer.class)
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
    @ModifyVariable(method = "dispatchCommand", remap = false, index = 2, at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lorg/spigotmc/AsyncCatcher;catchOp(Ljava/lang/String;)V"), argsOnly = true)
    public String dispatchCommand(String value) {
        if (value.startsWith("/")) {
            value = value.substring(1);
        }
        return value;
    }





//    public void addWorld(World world) {
//
//        if (this.getWorld(world.getUID()) != null) {
//            System.out.println("World " + world.getName() + " is a duplicate of another world and has been prevented from loading. Please delete the uid.dat file from " + world.getName() + "'s world directory if you want to be able to load the duplicate world.");
//        } else {
//            this.worlds.put(world.getName().toLowerCase(Locale.ENGLISH), world);
//        }
//    }


}
