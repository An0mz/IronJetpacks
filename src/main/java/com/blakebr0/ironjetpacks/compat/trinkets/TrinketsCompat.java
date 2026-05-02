package com.blakebr0.ironjetpacks.compat.trinkets;

import com.blakebr0.ironjetpacks.handler.InputHandler;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.base.SimpleEnergyItem;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

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
                    jetpackItem.performFlight(stack, player, () -> slot.inventory().markUpdate());
                }
            });
        }
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

    /** Runs the given action with the trinket-slot jetpack stack, providing a markUpdate runnable. */
    public static void withJetpack(Player player, BiConsumer<ItemStack, Runnable> action) {
        TrinketsApi.getTrinketComponent(player).ifPresent(c -> {
            var equipped = c.getEquipped(s -> s.getItem() instanceof JetpackItem);
            if (!equipped.isEmpty()) {
                var t = equipped.get(0);
                action.accept(t.getB(), () -> t.getA().inventory().markUpdate());
            }
        });
    }

    /** Returns true if the player is flying via a jetpack worn in a trinket slot. */
    public static boolean isFlying(Player player) {
        return TrinketsApi.getTrinketComponent(player)
            .map(component -> component.getEquipped(s -> s.getItem() instanceof JetpackItem)
                .stream()
                .anyMatch(tuple -> checkFlying((JetpackItem) tuple.getB().getItem(), tuple.getB(), player)))
            .orElse(false);
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
