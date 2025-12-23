package cc.zyycc.bk.mixin.mc;

import cc.zyycc.bk.bridge.EntityBridge;
import cc.zyycc.bk.bridge.server.SimpleBridge;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.projectiles.ProjectileSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public class EntityMixin implements EntityBridge {

    private CraftEntity bukkitEntity;
    @Shadow
    public net.minecraft.world.World world;

    public boolean valid;

    public ProjectileSource projectileSource;


    public CraftEntity getBukkitEntity() {
        if (this.bukkitEntity == null) {
            this.bukkitEntity = CraftEntity.getEntity(SimpleBridge.craftServer, (Entity) (Object) this);
        }

        return this.bukkitEntity;
    }

    public CommandSender bridge$getBukkitSender(CommandSource wrapper) {
        return this.getBukkitEntity();
    }

    @Override
    public void bridge$isValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public CraftEntity bridge$getBukkitEntity() {
        return bukkitEntity;
    }

    public CommandSender getBukkitSender(CommandSource wrapper) {
        return getBukkitEntity();
    }


}
