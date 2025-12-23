package cc.zyycc.bk.plugins;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.nio.file.Path;
import java.util.*;

public class BKMixinPlugin implements ILaunchPluginService {
    private final Map<String, byte[]> mixinBytecode = new HashMap<>();
    //什么时候介入类加载流程 (handlesClass)
    //
    //如何修改目标类 (processClass)
    //
    //在初始化时做什么 (initializeLaunch)
    @Override
    public String name() {
        return "zyybkmixin";
    }

    @Override
    public void initializeLaunch(ITransformerLoader transformerLoader, Path[] specialPaths) {
//        MixinBootstrap.init();
//        Mixins.addConfiguration("mixins.zyy.json");

    }

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
        return EnumSet.of(Phase.AFTER); // 类加载后介入
    }
    @Override
    public boolean processClass(Phase phase, ClassNode classNode, Type classType, String reason) {
        return false;
    }
}
