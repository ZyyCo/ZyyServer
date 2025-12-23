package cc.zyycc.bk.mixin.mc.inventory;

import cc.zyycc.bk.bridge.mc.IWorldPosCallableBridge;
import cc.zyycc.bk.bridge.player.PlayerEntityBridge;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.util.IWorldPosCallable;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventoryAnvil;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventoryView;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(RepairContainer.class)
public abstract class RepairContainerMixin extends AbstractRepairContainerMixin {



    public CraftInventoryView getBukkitView() {
        if (this.bukkitEntity != null) {
            return this.bukkitEntity;
        }

        CraftInventory inventory = new CraftInventoryAnvil(
                ((IWorldPosCallableBridge) this.field_234644_e_).bridge$getLocation(), this.field_234643_d_, this.field_234642_c_, (RepairContainer) (Object) this);
        bukkitEntity = new CraftInventoryView(((PlayerEntityBridge) this.field_234645_f_).getBukkitEntity(), inventory, (RepairContainer) (Object) this);
        return bukkitEntity;
    }


}
