package me.emvoh.ae2toggleableviewcells.mixin;

import appeng.items.storage.ItemViewCell;
import appeng.util.Platform;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = ItemViewCell.class, remap = false)
public abstract class MixinItemViewCell_DisabledProperty {

    private static final String TAG_FILTER_ENABLED = "FilterEnabled";

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void ae2toggleableviewcells$addDisabledProperty(final CallbackInfo ci) {
        ((ItemViewCell) (Object) this).addPropertyOverride(
                new ResourceLocation("ae2toggleableviewcells", "disabled"),
                new IItemPropertyGetter() {
                    @Override
                    public float apply(final ItemStack stack, @Nullable final World worldIn, @Nullable final EntityLivingBase entityIn) {
                        if (stack == null || stack.isEmpty()) {
                            return 0.0F;
                        }

                        final NBTTagCompound tag = Platform.openNbtData(stack);
                        final boolean enabled = !tag.hasKey(TAG_FILTER_ENABLED) || tag.getBoolean(TAG_FILTER_ENABLED);

                        return enabled ? 0.0F : 1.0F;
                    }
                }
        );
    }
}
