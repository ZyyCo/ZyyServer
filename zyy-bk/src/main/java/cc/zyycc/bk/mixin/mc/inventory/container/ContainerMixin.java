package cc.zyycc.bk.mixin.mc.inventory.container;

import cc.zyycc.bk.bridge.inventory.IInventoryBridge;
import cc.zyycc.bk.bridge.inventory.container.ContainerBridge;
import com.google.common.base.Preconditions;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventoryView;
import org.bukkit.inventory.InventoryView;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(net.minecraft.inventory.container.Container.class)
public abstract class ContainerMixin implements ContainerBridge {
    public boolean checkReachable = true;
    public CraftInventoryView bukkitEntity;

    public ITextComponent title;

    public InventoryView getBukkitView() {
        //ContainerWorkbench
        System.out.println("这里打个标记要是走这里我看见了。" + (Container) (Object) this);
        return bukkitEntity;
    }


    public void transferTo(Container other, CraftHumanEntity player) {
        InventoryView source = getBukkitView(), destination = ((ContainerBridge) other).bridge$getBukkitView();
        ((IInventoryBridge) ((CraftInventory) source.getTopInventory()).getInventory()).onClose(player);
        ((IInventoryBridge) ((CraftInventory) source.getBottomInventory()).getInventory()).onClose(player);
        ((IInventoryBridge) ((CraftInventory) destination.getTopInventory()).getInventory()).onOpen(player);
        ((IInventoryBridge) ((CraftInventory) destination.getBottomInventory()).getInventory()).onOpen(player);
    }


    @Override
    public InventoryView bridge$getBukkitView() {
        return getBukkitView();
    }

    public final ITextComponent getTitle() {
        return this.title;
    }

    public void setTitle(ITextComponent title) {
       // Preconditions.checkState(this.title == null, "Title already set");
        this.title = title;
    }
}
