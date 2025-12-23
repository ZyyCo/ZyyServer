package cc.zyycc.bk.mixin.mc.world.server;

import cc.zyycc.bk.bridge.world.server.TicketManagerBridge;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.util.SortedArraySet;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.Ticket;
import net.minecraft.world.server.TicketManager;
import net.minecraft.world.server.TicketType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;

@Mixin(TicketManager.class)
public abstract class TicketManagerMixin implements TicketManagerBridge {
    @Final
    @Shadow
    private Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> tickets;
    @Final
    @Shadow
    private TicketManager.ChunkTicketTracker ticketTracker;

    @Shadow
    private static int getLevel(SortedArraySet<Ticket<?>> p_229844_0_) {
        return 0;
    }

    @Override
    public Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> bridge$getTickets() {
        return tickets;
    }

    public <T> void removeAllTicketsFor(TicketType<T> ticketType, int ticketLevel, T ticketIdentifier) {
        Ticket<T> target = new Ticket<>(ticketType, ticketLevel, ticketIdentifier, false);

        for (ObjectIterator<Long2ObjectMap.Entry<SortedArraySet<Ticket<?>>>> objectIterator = this.tickets.long2ObjectEntrySet().fastIterator(); objectIterator.hasNext(); ) {
            Long2ObjectMap.Entry<SortedArraySet<Ticket<?>>> entry = objectIterator.next();
            SortedArraySet<Ticket<?>> tickets = (SortedArraySet<Ticket<?>>) entry.getValue();
            if (tickets.remove(target)) {
                this.ticketTracker.updateSourceLevel(entry.getLongKey(), getLowestTicketLevel(tickets), false);
                if (tickets.isEmpty())
                    objectIterator.remove();
            }
        }
    }

    private static int getLowestTicketLevel(SortedArraySet<Ticket<?>> p_229844_0_) {
        return !p_229844_0_.isEmpty() ? p_229844_0_.getSmallest().getLevel() : ChunkManager.MAX_LOADED_LEVEL + 1;
    }


}
