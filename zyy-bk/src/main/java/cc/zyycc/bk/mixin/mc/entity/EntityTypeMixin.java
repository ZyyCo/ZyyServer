package cc.zyycc.bk.mixin.mc.entity;


import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import net.minecraft.util.registry.Registry;

import net.minecraftforge.registries.ForgeRegistries;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(EntityType.class)
public class EntityTypeMixin {

//    @Inject(method = "readEntityType", at = @At("HEAD"), cancellable = true)
//    private static void readEntityType(CompoundNBT compound, CallbackInfoReturnable<Optional<EntityType<?>>> cir) {
//        System.out.println("来自" + Registry.ENTITY_TYPE.getClass().getName());
//        if(!Registry.ENTITY_TYPE.getClass().getName().equals("net.citizensnpcs.nms.v1_16_R3.util.CustomEntityRegistry")){
//            ResourceLocation rl = new ResourceLocation(compound.getString("id"));
//            EntityType<?> forgeType = ForgeRegistries.ENTITIES.getValue(rl);//NamespacedDefaultedWrapper
//            if (forgeType != null) {
//                cir.setReturnValue(Optional.of(forgeType));
//            }
//        }
//
//    }


//    @Inject(method = "loadEntityUnchecked", at = @At("HEAD"))
//    private static void loadEntityUnchecked(CompoundNBT compound, World worldIn, CallbackInfoReturnable<Optional<Entity>> cir) {
//
//
//        String id1 = compound.getString("id");
//        System.out.println("loadEntityUnchecked啊ID" + id1);
//
//        Optional<EntityType<?>> id = Registry.ENTITY_TYPE.getOptional(new ResourceLocation(compound.getString("id")));
//        System.out.println("loadEntityUnchecked啊" + id);
//
//        System.out.println("[DEBUG] id=" + id);
//        System.out.println("[DEBUG] Registry.ENTITY_TYPE class = " + Registry.ENTITY_TYPE.getClass());
//        System.out.println("[DEBUG] Registry.ENTITY_TYPE identity = " + System.identityHashCode(Registry.ENTITY_TYPE));
//        System.out.println("[DEBUG] ForgeRegistries.ENTITIES class = " + ForgeRegistries.ENTITIES.getClass());
//        System.out.println("[DEBUG] ForgeRegistries.ENTITIES identity = " + System.identityHashCode(ForgeRegistries.ENTITIES));
//
//        System.out.println("[DEBUG] Registry.ENTITY_TYPE.getOptional(rl) = " + Registry.ENTITY_TYPE.getOptional(new ResourceLocation(compound.getString("id"))));
//        System.out.println("[DEBUG] ForgeRegistries.ENTITIES.getValue(rl) = " + ForgeRegistries.ENTITIES.getValue(new ResourceLocation(compound.getString("id"))));
//
//    }


}
