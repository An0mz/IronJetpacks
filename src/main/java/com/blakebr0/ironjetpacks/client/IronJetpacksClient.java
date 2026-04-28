package com.blakebr0.ironjetpacks.client;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.client.model.JetpackModel;
import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.handler.ColorHandler;
import com.blakebr0.ironjetpacks.handler.HudHandler;
import com.blakebr0.ironjetpacks.handler.JetpackClientHandler;
import com.blakebr0.ironjetpacks.handler.KeyBindingsHandler;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class IronJetpacksClient {
    private static final Identifier JETPACK_TEXTURE = Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "textures/armor/jetpack.png");
    private static final Identifier JETPACK_OVERLAY_TEXTURE = Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, "textures/armor/jetpack_overlay.png");

    public static void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(KeyBindingsHandler::onClientTick);
        HudRenderCallback.EVENT.register(HudHandler::onRenderGameOverlay);
        ClientTickEvents.END_CLIENT_TICK.register(JetpackClientHandler::onClientTick);

        KeyBindingsHandler.onClientSetup();
        ColorHandler.onClientSetup();
        ModelHandler.onClientSetup();

        AutoConfig.register(ModConfigs.Client.class, JanksonConfigSerializer::new);
        for (Jetpack jetpack : JetpackRegistry.getInstance().getAllJetpacks()) {
            ArmorRenderer.register(new ArmorRenderer() {
                private JetpackModel model;

                @Override
                public void render(PoseStack matrices, SubmitNodeCollector orderedRenderCommandQueue, ItemStack stack,
                                   HumanoidRenderState bipedEntityRenderState, EquipmentSlot slot, int light,
                                   HumanoidModel<HumanoidRenderState> contextModel) {
                    int colorTint = jetpack.item.get().getColorTint(0);
                    float r = (float) (colorTint >> 16 & 255) / 255.0F;
                    float g = (float) (colorTint >> 8 & 255) / 255.0F;
                    float b = (float) (colorTint & 255) / 255.0F;
                    int color = (255 << 24) | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);

                    JetpackModel model = getModel();
                    model.setCurrentItemStack(stack);
                    model.setupAnim(bipedEntityRenderState);

                    orderedRenderCommandQueue.submitModel(
                            model,
                            bipedEntityRenderState,
                            matrices,
                            RenderTypes.armorCutoutNoCull(JETPACK_TEXTURE),
                            light,                    // i = packed lightmap
                            OverlayTexture.NO_OVERLAY, // j = overlay
                            color,                    // k = color tint
                            null,                     // TextureAtlasSprite
                            0,                        // l = extra int (flags/layer index)
                            null                      // CrumblingOverlay
                    );

                    orderedRenderCommandQueue.submitModel(
                            model,
                            bipedEntityRenderState,
                            matrices,
                            RenderTypes.armorCutoutNoCull(JETPACK_OVERLAY_TEXTURE),
                            light,
                            OverlayTexture.NO_OVERLAY,
                            -1,
                            null,
                            0,
                            null
                    );
                }

                private JetpackModel getModel() {
                    if (model == null) {
                        model = new JetpackModel(jetpack.item.get());
                    }
                    return model;
                }
            }, jetpack.item.get());
        }
    }
}
