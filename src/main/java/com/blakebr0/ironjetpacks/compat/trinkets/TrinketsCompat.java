package com.blakebr0.ironjetpacks.compat.trinkets;

import com.blakebr0.ironjetpacks.handler.InputHandler;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.mixins.ServerPlayNetworkHandlerAccessor;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import team.reborn.energy.api.base.SimpleEnergyItem;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TrinketsCompat {

    /**
     * Per-entity map tracking the last item type unequipped from a trinket slot.
     * Prevents playing the equip sound every tick when energy data changes cause
     * Trinkets to fire onUnequip+onEquip repeatedly.
     */
    private static final Map<UUID, Item> LAST_UNEQUIPPED = new ConcurrentHashMap<>();

    public static void init() {
        for (Jetpack jetpack : JetpackRegistry.getInstance().getAllJetpacks()) {
            JetpackItem jetpackItem = jetpack.item.get();
            TrinketsApi.registerTrinket(jetpackItem, new Trinket() {
                @Override
                public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
                    Item last = LAST_UNEQUIPPED.remove(entity.getUUID());
                    if (last != stack.getItem()) {
                        entity.playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value());
                    }
                }

                @Override
                public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
                    LAST_UNEQUIPPED.put(entity.getUUID(), stack.getItem());
                }

                @Override
                public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
                    if (!(entity instanceof Player player)) return;
                    if (!jetpackItem.isEngineOn(stack)) return;

                    boolean hover = jetpackItem.isHovering(stack);
                    if (!InputHandler.isHoldingUp(player) && !(hover && !player.onGround())) return;

                    Jetpack info = jetpack;
                    double hoverSpeed = InputHandler.isHoldingDown(player) ? info.speedHover : info.speedHoverSlow;
                    double currentAccel = info.accelVert * (player.getDeltaMovement().y() < 0.3D ? 2.5D : 1.0D);
                    double currentSpeedVertical = info.speedVert * (player.isUnderWater() ? 0.4D : 1.0D);
                    double usage = (player.isSprinting() || InputHandler.isHoldingSprint(player)) ? info.usage * info.sprintFuel : info.usage;

                    boolean canFly = info.creative;
                    if (!info.creative) {
                        long usageLong = (long) usage;
                        if (player.level().isClientSide()) {
                            canFly = SimpleEnergyItem.getStoredEnergyUnchecked(stack) >= usageLong;
                        } else {
                            long stored = SimpleEnergyItem.getStoredEnergyUnchecked(stack);
                            if (stored >= usageLong) {
                                SimpleEnergyItem.setStoredEnergyUnchecked(stack, stored - usageLong);
                                if (player instanceof ServerPlayer sp) {
                                    sp.inventoryMenu.broadcastChanges();
                                }
                                canFly = true;
                            }
                        }
                    }

                    if (canFly) {
                        double motionY = player.getDeltaMovement().y();
                        double throttle = jetpackItem.getThrottle(stack);
                        double vertSprintMulti = motionY >= 0 && (player.isSprinting() || InputHandler.isHoldingSprint(player)) ? info.sprintSpeedVert : 1.0D;

                        if (InputHandler.isHoldingUp(player)) {
                            if (!hover) {
                                fly(player, Math.min(motionY + currentAccel, currentSpeedVertical) * throttle * vertSprintMulti);
                            } else if (InputHandler.isHoldingDown(player)) {
                                fly(player, Math.min(motionY + currentAccel, -info.speedHoverSlow));
                            } else {
                                fly(player, Math.min(motionY + currentAccel, info.speedHoverAscend) * throttle * vertSprintMulti);
                            }
                        } else {
                            fly(player, Math.min(motionY + currentAccel, -hoverSpeed));
                        }

                        float speedSideways = (float) ((player.isShiftKeyDown() ? info.speedSide * 0.5F : info.speedSide) * throttle);
                        float speedForward = (float) (player.isSprinting() ? speedSideways * info.sprintSpeed : speedSideways);

                        if (InputHandler.isHoldingForwards(player)) player.moveRelative(1, new Vec3(0, 0, speedForward));
                        if (InputHandler.isHoldingBackwards(player)) player.moveRelative(1, new Vec3(0, 0, -speedSideways * 0.8F));
                        if (InputHandler.isHoldingLeft(player)) player.moveRelative(1, new Vec3(speedSideways, 0, 0));
                        if (InputHandler.isHoldingRight(player)) player.moveRelative(1, new Vec3(-speedSideways, 0, 0));

                        if (!player.level().isClientSide()) {
                            player.fallDistance = 0.0F;
                            if (player instanceof ServerPlayer sp) {
                                ((ServerPlayNetworkHandlerAccessor) sp.connection).setFloatingTicks(0);
                            }
                        }
                    }
                }
            });
        }
    }

    private static void fly(Player player, double y) {
        Vec3 motion = player.getDeltaMovement();
        player.setDeltaMovement(motion.x(), y, motion.z());
    }

    /** Returns true if the player is flying via a jetpack worn in a trinket slot. */
    public static boolean isFlying(Player player) {
        return TrinketsApi.getTrinketComponent(player)
            .map(component -> component.getEquipped(s -> s.getItem() instanceof JetpackItem)
                .stream()
                .anyMatch(tuple -> checkFlying((JetpackItem) tuple.getB().getItem(), tuple.getB(), player)))
            .orElse(false);
    }

    /** Returns the first JetpackItem stack equipped in any trinket slot, or {@link ItemStack#EMPTY}. */
    public static ItemStack getEquippedJetpackStack(Player player) {
        return TrinketsApi.getTrinketComponent(player)
            .flatMap(component -> component.getEquipped(s -> s.getItem() instanceof JetpackItem)
                .stream()
                .map(tuple -> tuple.getB())
                .findFirst())
            .orElse(ItemStack.EMPTY);
    }

    private static boolean checkFlying(JetpackItem jetpack, ItemStack stack, Player player) {
        Jetpack info = jetpack.getJetpack();
        boolean hasEnergy = info.creative || player.isCreative()
                || SimpleEnergyItem.getStoredEnergyUnchecked(stack) >= (long) info.usage;
        if (jetpack.isEngineOn(stack) && hasEnergy) {
            return jetpack.isHovering(stack) ? !player.onGround() : InputHandler.isHoldingUp(player);
        }
        return false;
    }
}
