package cc.zyycc.bk.mixin.mc.world.server;

import net.minecraft.util.Unit;
import net.minecraft.world.server.TicketType;
import org.bukkit.plugin.Plugin;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Comparator;

@Mixin(TicketType.class)
public abstract class TicketTypeMixin  {

    private static final TicketType<Unit> PLUGIN = TicketType.create("plugin", (a, b) -> 0);
    private static final TicketType<Plugin> PLUGIN_TICKET = TicketType.create("plugin_ticket", Comparator.comparing(it -> it.getClass().getName()));
//


}
