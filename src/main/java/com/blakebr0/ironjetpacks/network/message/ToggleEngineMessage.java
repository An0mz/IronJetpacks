package com.blakebr0.ironjetpacks.network.message;

import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
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
                ItemStack stack = JetpackUtils.getActiveJetpackStack(player);
                if (stack.getItem() instanceof JetpackItem jetpack) {
                    jetpack.toggleEngine(stack);
                }
            }
        });
    }
}
