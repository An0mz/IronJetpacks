package com.blakebr0.ironjetpacks.compat.trinkets;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.client.model.JetpackModel;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class TrinketsClientCompat {

    private static final ResourceLocation JETPACK_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, "textures/armor/jetpack.png");
    private static final ResourceLocation JETPACK_OVERLAY_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, "textures/armor/jetpack_overlay.png");

    public static void init() {
        for (Jetpack jetpack : JetpackRegistry.getInstance().getAllJetpacks()) {
            TrinketRendererRegistry.registerRenderer(jetpack.item.get(), new JetpackTrinketRenderer(jetpack));
        }
    }

    private static class JetpackTrinketRenderer implements TrinketRenderer {
        private final Jetpack jetpack;
        private JetpackModel model;

        JetpackTrinketRenderer(Jetpack jetpack) {
            this.jetpack = jetpack;
        }

        private JetpackModel getModel() {
            if (model == null) {
                model = new JetpackModel(jetpack.item.get());
            }
            return model;
        }

        @Override
        public void render(ItemStack stack, SlotReference slotReference, EntityModel<?> contextModel,
                           PoseStack matrices, MultiBufferSource vertexConsumers, int light,
                           LivingEntity entity, float limbAngle, float limbDistance,
                           float tickDelta, float animationProgress, float headYaw, float headPitch) {
            JetpackModel model = getModel();

            // Sync body pose from the living entity's current render model
            TrinketRenderer.followBodyRotations(entity, model);

            // Set up energy bar display
            model.setCurrentItemStack(stack);
            model.setupEnergyBars();

            // Tinted base layer
            int colorTint = jetpack.item.get().getColorTint(0);
            float r = (float) (colorTint >> 16 & 255) / 255.0F;
            float g = (float) (colorTint >> 8 & 255) / 255.0F;
            float b = (float) (colorTint & 255) / 255.0F;
            int color = (255 << 24) | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);

            VertexConsumer consumer = ItemRenderer.getArmorFoilBuffer(
                    vertexConsumers, RenderType.armorCutoutNoCull(JETPACK_TEXTURE), stack.hasFoil());
            model.renderToBuffer(matrices, consumer, light, OverlayTexture.NO_OVERLAY, color);

            // Overlay layer (energy bar markings, always full-bright white)
            consumer = ItemRenderer.getArmorFoilBuffer(
                    vertexConsumers, RenderType.armorCutoutNoCull(JETPACK_OVERLAY_TEXTURE), stack.hasFoil());
            model.renderToBuffer(matrices, consumer, light, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}

