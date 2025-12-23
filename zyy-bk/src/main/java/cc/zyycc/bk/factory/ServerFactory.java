package cc.zyycc.bk.factory;

import cc.zyycc.common.loader.IServerFactory;
import cc.zyycc.common.bridge.server.ICraftServer;
import cc.zyycc.common.bridge.server.IServerProvider;




public class ServerFactory implements IServerFactory {

    @Override
    public ICraftServer createServer(IServerProvider provider) {
        System.out.println("知道了ServerFactory: " + provider.getClass().getClassLoader());

        return new ICraftServer() {
            @Override
            public Object startServer() {
                try {


                }catch (Exception e){
                    e.printStackTrace();
                }

                return null;
            }
        };


    }
}
