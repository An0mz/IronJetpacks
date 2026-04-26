package com.blakebr0.ironjetpacks.network.message;

import com.blakebr0.ironjetpacks.item.JetpackItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ToggleHUDMessage {
    public static ToggleHUDMessage read(FriendlyByteBuf buffer) {
        return new ToggleHUDMessage();
    }

    public static void write(ToggleHUDMessage message, FriendlyByteBuf buffer) {
    }

    public static void onMessage(ToggleHUDMessage message, MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            if (player != null) {
                ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
                if (stack.getItem() instanceof JetpackItem jetpack) {
                    jetpack.toggleHUD(stack);
                }
            }
        });
    }
}
