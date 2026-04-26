package com.blakebr0.ironjetpacks.network.message;

import com.blakebr0.ironjetpacks.item.JetpackItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class DecrementThrottleMessage {
    public static DecrementThrottleMessage read(FriendlyByteBuf buffer) {
        return new DecrementThrottleMessage();
    }

    public static void write(DecrementThrottleMessage message, FriendlyByteBuf buffer) {
    }

    public static void onMessage(DecrementThrottleMessage message, MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            if (player != null) {
                ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
                if (stack.getItem() instanceof JetpackItem jetpack) {
                    jetpack.decrementThrottle(stack);
                }
            }
        });
    }
}
