package me.emvoh.ae2toggleableviewcells.mixin;

import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.container.slot.SlotRestrictedInput;
import appeng.items.storage.ItemViewCell;
import appeng.util.Platform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AEBaseContainer.class, remap = false)
public abstract class MixinAEBaseContainer_ViewCellSlotToggle {

    private static final String TAG_FILTER_ENABLED = "FilterEnabled";

    private static boolean isFilterEnabled(final ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return true;
        }
        final NBTTagCompound tag = Platform.openNbtData(stack);
        return !tag.hasKey(TAG_FILTER_ENABLED) || tag.getBoolean(TAG_FILTER_ENABLED);
    }

    private static void setFilterEnabled(final ItemStack stack, final boolean enabled) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        Platform.openNbtData(stack).setBoolean(TAG_FILTER_ENABLED, enabled);
    }

    @Inject(
            method = { "slotClick", "func_184996_a" },
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 1
    )
    private void ae2toggleableviewcells$rightClickToggleViewCellSlot(final int slotId, final int dragType, final ClickType clickType, final EntityPlayer player, final CallbackInfoReturnable<ItemStack> cir) {
        // If you only want this behavior in the ME Monitorable container, keep this guard.
        // If you want it for any AE2 container that has VIEW_CELL slots, remove this.
        if (!((Object) this instanceof ContainerMEMonitorable)) {
            return;
        }

        if (slotId < 0) {
            return;
        }

        final Slot slot = ((AEBaseContainer) (Object) this).getSlot(slotId);
        if (slot == null) {
            return;
        }

        // Right-click pickup
        if (clickType != ClickType.PICKUP || dragType != 1) {
            return;
        }

        // Must be a VIEW_CELL restricted slot
        if (!(slot instanceof SlotRestrictedInput)) {
            return;
        }

        if (((SlotRestrictedInput) slot).getPlaceableItemType() != SlotRestrictedInput.PlacableItemType.VIEW_CELL) {
            return;
        }

        // Only toggle if cursor is empty
        if (!player.inventory.getItemStack().isEmpty()) {
            return;
        }

        final ItemStack inSlot = slot.getStack();

        // Must be a view cell item (subclasses included)
        if (inSlot.isEmpty() || !(inSlot.getItem() instanceof ItemViewCell)) {
            return;
        }

        // Respect AE2 edit permission gating
        if (!slot.canTakeStack(player)) {
            cir.setReturnValue(player.inventory.getItemStack());
            cir.cancel();
            return;
        }

        // Server: toggle NBT and sync
        if (!player.world.isRemote) {
            final ItemStack updated = inSlot.copy();

            final boolean newState = !isFilterEnabled(updated);
            setFilterEnabled(updated, newState);

            slot.putStack(updated);
            slot.onSlotChanged();

            ((AEBaseContainer) (Object) this).detectAndSendChanges();
        }

        // Block default pickup behavior on both sides
        cir.setReturnValue(player.inventory.getItemStack());
        cir.cancel();
    }
}
