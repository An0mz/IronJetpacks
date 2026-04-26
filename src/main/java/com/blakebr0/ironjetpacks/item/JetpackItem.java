package com.blakebr0.ironjetpacks.item;

import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.handler.InputHandler;
import com.blakebr0.ironjetpacks.lib.ModTooltips;
import com.blakebr0.ironjetpacks.mixins.ServerPlayNetworkHandlerAccessor;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import com.blakebr0.ironjetpacks.util.UnitUtils;
import com.mojang.datafixers.util.Pair;
import dev.architectury.extensions.ItemExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.StringUtils;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyItem;

import java.util.List;

public class JetpackItem extends ArmorItem implements Colored, Enableable, ItemExtension {
    private final Jetpack jetpack;

    public JetpackItem(Jetpack jetpack, Properties settings) {
        super(JetpackUtils.makeArmorMaterial(jetpack), ArmorType.CHESTPLATE, settings.durability(0).rarity(jetpack.rarity));
        this.jetpack = jetpack;
    }

    @Override
    public Component getName(ItemStack stack) {
        String name = StringUtils.capitalize(this.jetpack.name.replace(" ", "_"));
        return Component.translatable("item.iron-jetpacks.jetpack", name);
    }

    /*
     * Jetpack logic is very much like Simply Jetpacks, since I used it to learn how to make this work
     * Credit to Tonius & Tomson124
     * https://github.com/Tomson124/SimplyJetpacks-2/blob/1.12/src/main/java/tonius/simplyjetpacks/item/rewrite/ItemJetpack.java
     */
    @Override
    public void tickArmor(ItemStack stack, Player player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        Item item = chest.getItem();
        if (!chest.isEmpty() && item instanceof JetpackItem) {
            JetpackItem jetpack = (JetpackItem) item;
            if (jetpack.isEngineOn(chest)) {
                boolean hover = jetpack.isHovering(chest);
                if (InputHandler.isHoldingUp(player) || hover && !player.onGround()) {
                    Jetpack info = jetpack.getJetpack();

                    double hoverSpeed = InputHandler.isHoldingDown(player) ? info.speedHover : info.speedHoverSlow;
                    double currentAccel = info.accelVert * (player.getDeltaMovement().y() < 0.3D ? 2.5D : 1.0D);
                    double currentSpeedVertical = info.speedVert * (player.isUnderWater() ? 0.4D : 1.0D);

                    double usage = (player.isSprinting() || InputHandler.isHoldingSprint(player)) ? info.usage * info.sprintFuel : info.usage;

                    boolean creative = info.creative;
                    boolean canFly = creative;

                    if (!creative) {
                        long usageLong = (long) usage;
                        if (player.level().isClientSide()) {
                            canFly = SimpleEnergyItem.getStoredEnergyUnchecked(chest) >= usageLong;
                        } else {
                            long stored = SimpleEnergyItem.getStoredEnergyUnchecked(chest);
                            if (stored >= usageLong) {
                                SimpleEnergyItem.setStoredEnergyUnchecked(chest, stored - usageLong);
                                if (player instanceof ServerPlayer serverPlayer) {
                                    serverPlayer.connection.send(new ClientboundSetEquipmentPacket(
                                            serverPlayer.getId(), List.of(Pair.of(EquipmentSlot.CHEST, chest))
                                    ));
                                }
                                canFly = true;
                            }
                        }
                    }

                    if (canFly) {
                        double motionY = player.getDeltaMovement().y();
                        double throttle = jetpack.getThrottle(chest);
                        double vertSprintMulti = motionY >= 0 && (player.isSprinting() || InputHandler.isHoldingSprint(player)) ? info.sprintSpeedVert : 1.0D;

                        if (InputHandler.isHoldingUp(player)) {
                            if (!hover) {
                                this.fly(player, Math.min(motionY + currentAccel, currentSpeedVertical) * throttle * vertSprintMulti);
                            } else {
                                if (InputHandler.isHoldingDown(player)) {
                                    this.fly(player, Math.min(motionY + currentAccel, -info.speedHoverSlow));
                                } else {
                                    this.fly(player, Math.min(motionY + currentAccel, info.speedHoverAscend) * throttle * vertSprintMulti);
                                }
                            }
                        } else {
                            this.fly(player, Math.min(motionY + currentAccel, -hoverSpeed));
                        }

                        float speedSideways = (float) ((player.isShiftKeyDown() ? info.speedSide * 0.5F : info.speedSide) * throttle);
                        float speedForward = (float) (player.isSprinting() ? speedSideways * info.sprintSpeed : speedSideways);

                        if (InputHandler.isHoldingForwards(player)) {
                            player.moveRelative(1, new Vec3(0, 0, speedForward));
                        }

                        if (InputHandler.isHoldingBackwards(player)) {
                            player.moveRelative(1, new Vec3(0, 0, -speedSideways * 0.8F));
                        }

                        if (InputHandler.isHoldingLeft(player)) {
                            player.moveRelative(1, new Vec3(speedSideways, 0, 0));
                        }

                        if (InputHandler.isHoldingRight(player)) {
                            player.moveRelative(1, new Vec3(-speedSideways, 0, 0));
                        }

                        if (!player.level().isClientSide()) {
                            player.fallDistance = 0.0F;

                            if (player instanceof ServerPlayer) {
                                ((ServerPlayNetworkHandlerAccessor) ((ServerPlayer) player).connection).setFloatingTicks(0);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isEnchantable(ItemStack stack) {
        return ModConfigs.get().general.enchantableJetpacks && this.jetpack.enchantablilty > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        long stored = SimpleEnergyItem.getStoredEnergyUnchecked(stack);
        long capacity = (long) this.jetpack.capacity;
        return (int) Math.round(13.0F - ((double)(capacity - stored) / capacity) * 13.0F);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return !this.jetpack.creative;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag advanced) {
        if (!this.jetpack.creative) {
            long stored = SimpleEnergyItem.getStoredEnergyUnchecked(stack);
            tooltip.add(Component.literal(UnitUtils.formatEnergy(stored, null)));
        } else {
            tooltip.add(Component.literal("-1 E / ").withStyle(ChatFormatting.GRAY).append(ModTooltips.INFINITE.color(ChatFormatting.GRAY)).append(" E"));
        }

        Component tier = ModTooltips.TIER.args(this.jetpack.creative ? "Creative" : this.jetpack.tier).withStyle(this.jetpack.rarity.color());
        Component engine = ModTooltips.ENGINE.color(isEngineOn(stack) ? ChatFormatting.GREEN : ChatFormatting.RED);
        Component hover = ModTooltips.HOVER.color(isHovering(stack) ? ChatFormatting.GREEN : ChatFormatting.RED);

        tooltip.add(ModTooltips.STATE_TOOLTIP_LAYOUT.args(tier, engine, hover));

        if (ModConfigs.getClient().general.enableAdvancedInfoTooltips) {
            tooltip.add(Component.literal(""));
            if (!Screen.hasShiftDown()) {
                tooltip.add(Component.translatable("tooltip.iron-jetpacks.hold_shift_for_info"));
            } else {
                tooltip.add(ModTooltips.FUEL_USAGE.args(this.jetpack.usage + " E/t"));
                tooltip.add(ModTooltips.VERTICAL_SPEED.args(this.jetpack.speedVert));
                tooltip.add(ModTooltips.VERTICAL_ACCELERATION.args(this.jetpack.accelVert));
                tooltip.add(ModTooltips.HORIZONTAL_SPEED.args(this.jetpack.speedSide));
                tooltip.add(ModTooltips.HOVER_SPEED.args(this.jetpack.speedHoverSlow));
                tooltip.add(ModTooltips.DESCEND_SPEED.args(this.jetpack.speedHover));
                tooltip.add(ModTooltips.SPRINT_MODIFIER.args(this.jetpack.sprintSpeed));
                tooltip.add(ModTooltips.SPRINT_FUEL_MODIFIER.args(this.jetpack.sprintFuel));
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public int getColorTint(int i) {
        return i == 0 ? (0xFF000000 | this.jetpack.color) : -1;
    }

    @Override
    public boolean isEnabled() {
        return !this.jetpack.disabled;
    }

    public Jetpack getJetpack() {
        return this.jetpack;
    }

    public double getMaxOutput() {
        return 0;
    }

    public double getMaxInput() {
        return jetpack.capacity / 20.0;
    }

    public boolean isEngineOn(ItemStack stack) {
        if (!stack.has(DataComponents.CUSTOM_DATA)) return false;
        CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
        return tag.contains("Engine") && tag.getBoolean("Engine");
    }

    public boolean toggleEngine(ItemStack stack) {
        CompoundTag tag = stack.has(DataComponents.CUSTOM_DATA) ? stack.get(DataComponents.CUSTOM_DATA).copyTag() : new CompoundTag();
        boolean current = tag.contains("Engine") && tag.getBoolean("Engine");
        tag.putBoolean("Engine", !current);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return !current;
    }

    public boolean isHovering(ItemStack stack) {
        if (!stack.has(DataComponents.CUSTOM_DATA)) return false;
        CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
        return tag.contains("Hover") && tag.getBoolean("Hover");
    }

    public boolean toggleHover(ItemStack stack) {
        CompoundTag tag = stack.has(DataComponents.CUSTOM_DATA) ? stack.get(DataComponents.CUSTOM_DATA).copyTag() : new CompoundTag();
        boolean current = tag.contains("Hover") && tag.getBoolean("Hover");
        tag.putBoolean("Hover", !current);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return !current;
    }

    public double getThrottle(ItemStack stack) {
        if (!stack.has(DataComponents.CUSTOM_DATA)) return 1.0;
        CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
        return tag.contains("Throttle") ? tag.getDouble("Throttle") : 1.0;
    }

    public double incrementThrottle(ItemStack stack) {
        CompoundTag tag = stack.has(DataComponents.CUSTOM_DATA) ? stack.get(DataComponents.CUSTOM_DATA).copyTag() : new CompoundTag();
        double throttle = tag.contains("Throttle") ? tag.getDouble("Throttle") : 1.0;
        if (throttle < 1.0) {
            throttle = Math.min(throttle + 0.2, 1.0);
            tag.putDouble("Throttle", throttle);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
        return throttle;
    }

    public double decrementThrottle(ItemStack stack) {
        CompoundTag tag = stack.has(DataComponents.CUSTOM_DATA) ? stack.get(DataComponents.CUSTOM_DATA).copyTag() : new CompoundTag();
        double throttle = tag.contains("Throttle") ? tag.getDouble("Throttle") : 1.0;
        if (throttle > 0.2) {
            throttle = Math.max(throttle - 0.2, 0.2);
            tag.putDouble("Throttle", throttle);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
        return throttle;
    }

    public boolean isHUDEnabled(ItemStack stack) {
        if (!stack.has(DataComponents.CUSTOM_DATA)) return true;
        CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
        return !tag.contains("HUD") || tag.getBoolean("HUD");
    }

    public boolean toggleHUD(ItemStack stack) {
        CompoundTag tag = stack.has(DataComponents.CUSTOM_DATA) ? stack.get(DataComponents.CUSTOM_DATA).copyTag() : new CompoundTag();
        boolean current = !tag.contains("HUD") || tag.getBoolean("HUD");
        tag.putBoolean("HUD", !current);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return !current;
    }

    private void fly(Player player, double y) {
        Vec3 motion = player.getDeltaMovement();
        player.setDeltaMovement(motion.x(), y, motion.z());
    }
}