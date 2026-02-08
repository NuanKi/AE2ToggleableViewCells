package me.emvoh.ae2toggleableviewcells.client;

import appeng.items.storage.ItemViewCell;
import appeng.util.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = "ae2toggleableviewcells", value = Side.CLIENT)
public final class ViewCellModelHook {

    private static final String TAG_FILTER_ENABLED = "FilterEnabled";

    private static final ResourceLocation DISABLED_MODEL =
            new ResourceLocation("ae2toggleableviewcells", "item/view_cell_disabled");

    @SubscribeEvent
    public static void onTextureStitchPre(TextureStitchEvent.Pre event) {
        if (event.getMap() != Minecraft.getMinecraft().getTextureMapBlocks()) {
            return;
        }

        event.getMap().registerSprite(
                new ResourceLocation("ae2toggleableviewcells", "items/view_cell_disabled")
        );
    }

    private static boolean isEnabled(final ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return true;
        }
        final NBTTagCompound tag = Platform.openNbtData(stack);
        return !tag.hasKey(TAG_FILTER_ENABLED) || tag.getBoolean(TAG_FILTER_ENABLED);
    }

    @SubscribeEvent
    public static void onModelBake(final ModelBakeEvent event) {
        final IBakedModel disabled = bakeItemModel(DISABLED_MODEL);
        if (disabled == null) {
            return;
        }

        for (final Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
            if (!(item instanceof ItemViewCell)) {
                continue;
            }

            final ResourceLocation rl = item.getRegistryName();
            if (rl == null) {
                continue;
            }

            final ModelResourceLocation mrl = new ModelResourceLocation(rl, "inventory");
            final IBakedModel base = event.getModelRegistry().getObject(mrl);

            if (base != null) {
                event.getModelRegistry().putObject(mrl, new ToggleModel(base, disabled));
            }
        }
    }

    @Nullable
    private static IBakedModel bakeItemModel(final ResourceLocation modelLocation) {
        try {
            return ModelLoaderRegistry.getModel(modelLocation).bake(
                    TRSRTransformation.identity(),
                    DefaultVertexFormats.ITEM,
                    ModelLoader.defaultTextureGetter()
            );
        } catch (final Exception e) {
            return null;
        }
    }

    private static final class ToggleModel implements IBakedModel {
        private final IBakedModel base;
        private final IBakedModel disabled;

        private final ItemOverrideList overrides = new ItemOverrideList(Collections.emptyList()) {
            @Override
            public IBakedModel handleItemState(final IBakedModel originalModel,
                                               final ItemStack stack,
                                               @Nullable final World world,
                                               @Nullable final EntityLivingBase entity) {

                if (!isEnabled(stack)) {
                    return disabled.getOverrides().handleItemState(disabled, stack, world, entity);
                }

                return base.getOverrides().handleItemState(base, stack, world, entity);
            }
        };

        private ToggleModel(final IBakedModel base, final IBakedModel disabled) {
            this.base = base;
            this.disabled = disabled;
        }

        @Override
        public List<net.minecraft.client.renderer.block.model.BakedQuad> getQuads(
                @Nullable net.minecraft.block.state.IBlockState state,
                @Nullable net.minecraft.util.EnumFacing side,
                long rand
        ) {
            return base.getQuads(state, side, rand);
        }

        @Override public boolean isAmbientOcclusion() { return base.isAmbientOcclusion(); }
        @Override public boolean isGui3d() { return base.isGui3d(); }
        @Override public boolean isBuiltInRenderer() { return base.isBuiltInRenderer(); }
        @Override public TextureAtlasSprite getParticleTexture() { return base.getParticleTexture(); }
        @Override public net.minecraft.client.renderer.block.model.ItemCameraTransforms getItemCameraTransforms() { return base.getItemCameraTransforms(); }
        @Override public ItemOverrideList getOverrides() { return overrides; }
    }
}
