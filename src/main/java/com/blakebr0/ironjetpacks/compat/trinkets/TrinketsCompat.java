package com.blakebr0.ironjetpacks.compat.trinkets;

import com.blakebr0.ironjetpacks.handler.InputHandler;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.base.SimpleEnergyItem;

import java.util.Optional;

public class TrinketsCompat {
    public static void init() {
        for (Jetpack jetpack : JetpackRegistry.getInstance().getAllJetpacks()) {
            Trinket trinket = (Trinket) jetpack.item.get();
            TrinketsApi.registerTrinket(jetpack.item.get(), trinket);
        }
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
