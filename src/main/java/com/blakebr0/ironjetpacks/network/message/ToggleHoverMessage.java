package com.blakebr0.ironjetpacks.network.message;

import com.blakebr0.ironjetpacks.item.JetpackItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ToggleHoverMessage {
    public static ToggleHoverMessage read(FriendlyByteBuf buffer) {
        return new ToggleHoverMessage();
    }
    
    public static void write(ToggleHoverMessage message, FriendlyByteBuf buffer) {
        
    }
    
    public static void onMessage(ToggleHoverMessage message, MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            if (player != null) {
                ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
                Item item = stack.getItem();
                if (item instanceof JetpackItem jetpack) {
                    jetpack.toggleHover(stack);
                    player.inventoryMenu.broadcastChanges();
                }
            }
        });
    }
}
