package cc.zyycc.forge;

import cc.zyycc.common.loader.IServerFactory;
import cc.zyycc.common.bridge.server.ICraftServer;
import cc.zyycc.common.bridge.server.IServerProvider;


public class ForgeEntry {
    private final IServerFactory factory;

    public ForgeEntry(IServerFactory factory) {
        this.factory = factory;
    }

    public ICraftServer start(IServerProvider provider) {
        return factory.createServer(provider);
    }
}
