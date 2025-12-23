package cc.zyycc.bk.plugins;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.nio.file.Path;
import java.util.EnumSet;

public class BKMixinPlugin implements ILaunchPluginService {
    @Override
    public String name() {
        return "zyybkmixin";
    }

    @Override
    public void initializeLaunch(ITransformerLoader transformerLoader, Path[] specialPaths) {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.zyy.json");
    }

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
        return EnumSet.of(Phase.AFTER);
    }
    @Override
    public boolean processClass(Phase phase, ClassNode classNode, Type classType, String reason) {
        return false;
    }
}
