package cc.zyycc.bk.mixin.mc.tileentity;

import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(net.minecraft.tileentity.TileEntity.class)
public class TileEntityMixin {
    @Shadow
    protected net.minecraft.world.World world;
    @Shadow
    protected BlockPos pos;

//    public BlockPos getPos() {
//        return this.pos;
//    }
}
