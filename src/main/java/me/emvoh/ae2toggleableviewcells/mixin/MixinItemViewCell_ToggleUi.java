package me.emvoh.ae2toggleableviewcells.mixin;

import appeng.items.AEBaseItem;
import appeng.items.storage.ItemViewCell;
import appeng.util.Platform;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = ItemViewCell.class, remap = false)
public abstract class MixinItemViewCell_ToggleUi extends AEBaseItem {

    @Unique
    private static final String AE2TVC_TAG_FILTER_ENABLED = "FilterEnabled";

    @Unique
    private static boolean ae2tvc$isEnabled(final ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return true;
        }
        final NBTTagCompound tag = Platform.openNbtData(stack);
        return !tag.hasKey(AE2TVC_TAG_FILTER_ENABLED) || tag.getBoolean(AE2TVC_TAG_FILTER_ENABLED);
    }

    @Unique
    private static void ae2tvc$setEnabled(final ItemStack stack, final boolean enabled) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        Platform.openNbtData(stack).setBoolean(AE2TVC_TAG_FILTER_ENABLED, enabled);
    }

    // This method will be added to ItemViewCell and override Item#onItemRightClick
    public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
        final ItemStack stack = player.getHeldItem(hand);

        // Match your behavior: sneaking -> let normal behavior happen, not sneaking -> toggle
        if (player.isSneaking()) {
            return super.onItemRightClick(world, player, hand);
        }

        if (!world.isRemote) {
            final boolean newState = !ae2tvc$isEnabled(stack);
            ae2tvc$setEnabled(stack, newState);

            final ITextComponent msg =
                    new TextComponentTranslation("chat.appliedenergistics2.view_cell.filter_prefix")
                            .appendSibling(
                                    new TextComponentTranslation(
                                            newState
                                                    ? "chat.appliedenergistics2.view_cell.state.enabled"
                                                    : "chat.appliedenergistics2.view_cell.state.disabled"
                                    ).setStyle(
                                            new Style().setColor(newState ? TextFormatting.GREEN : TextFormatting.RED)
                                    )
                            );

            player.sendStatusMessage(msg, true);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    // This method will be added to ItemViewCell and override AEBaseItem#addCheckedInformation
    @SideOnly(Side.CLIENT)
    protected void addCheckedInformation(final ItemStack stack, final World world, final List<String> lines, final ITooltipFlag advancedTooltips) {
        final boolean enabled = ae2tvc$isEnabled(stack);

        final String stateKey = enabled
                ? "tooltip.appliedenergistics2.view_cell.state.enabled"
                : "tooltip.appliedenergistics2.view_cell.state.disabled";

        final String stateText = (enabled ? TextFormatting.GREEN : TextFormatting.RED) + I18n.format(stateKey);

        lines.add(TextFormatting.GRAY + I18n.format("tooltip.appliedenergistics2.view_cell.filter", stateText));
        super.addCheckedInformation(stack, world, lines, advancedTooltips);
    }
}
