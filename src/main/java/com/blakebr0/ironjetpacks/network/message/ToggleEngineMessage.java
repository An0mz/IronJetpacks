package com.blakebr0.ironjetpacks.network.message;

import com.blakebr0.ironjetpacks.item.JetpackItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ToggleEngineMessage {
    public static ToggleEngineMessage read(FriendlyByteBuf buffer) {
        return new ToggleEngineMessage();
    }
    
    public static void write(ToggleEngineMessage message, FriendlyByteBuf buffer) {
        
    }
    
    public static void onMessage(ToggleEngineMessage message, MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            if (player != null) {
                ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
                Item item = stack.getItem();
                if (item instanceof JetpackItem jetpack) {
                    jetpack.toggleEngine(stack);
                    player.inventoryMenu.broadcastChanges();
                }
            }
        });
    }
}
