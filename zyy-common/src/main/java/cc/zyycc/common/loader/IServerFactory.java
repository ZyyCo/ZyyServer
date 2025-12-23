package cc.zyycc.common.bridge.loader;

import cc.zyycc.common.bridge.server.ICraftServer;
import cc.zyycc.common.bridge.server.IServerProvider;

import java.util.function.Function;

public interface IServerFactory {
     ICraftServer createServer(IServerProvider provider);
}
