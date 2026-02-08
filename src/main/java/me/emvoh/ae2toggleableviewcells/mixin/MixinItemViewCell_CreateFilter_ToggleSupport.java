package me.emvoh.ae2toggleableviewcells.mixin;

import appeng.api.storage.data.IAEItemStack;
import appeng.items.storage.ItemViewCell;
import appeng.util.Platform;
import appeng.util.prioritylist.IPartitionList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemViewCell.class, remap = false)
public abstract class MixinItemViewCell_CreateFilter_ToggleSupport {

    private static final String TAG_FILTER_ENABLED = "FilterEnabled";

    @Inject(
            method = "createFilter",
            at = @At("HEAD"),
            remap = false,
            require = 1
    )
    private static void ae2toggleableviewcells$nullOutDisabledCells(final ItemStack[] list,
                                                                    final CallbackInfoReturnable<IPartitionList<IAEItemStack>> cir) {
        if (list == null) {
            return;
        }

        for (int i = 0; i < list.length; i++) {
            final ItemStack stack = list[i];

            if (stack == null || stack.isEmpty()) {
                continue;
            }

            // Only apply to view cells (subclasses included)
            if (!(stack.getItem() instanceof ItemViewCell)) {
                continue;
            }

            final NBTTagCompound tag = Platform.openNbtData(stack);
            final boolean enabled = !tag.hasKey(TAG_FILTER_ENABLED) || tag.getBoolean(TAG_FILTER_ENABLED);

            if (!enabled) {
                // Null is important because older AE2 code checks only for null
                list[i] = null;
            }
        }
    }
}
