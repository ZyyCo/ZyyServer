package cc.zyycc.bk.bridge.world.server;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.SortedArraySet;
import net.minecraft.world.server.Ticket;

public interface TicketManagerBridge {
    Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> bridge$getTickets();
}
