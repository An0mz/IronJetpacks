package com.blakebr0.ironjetpacks.network.message;

import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ToggleHUDMessage {
    public static ToggleHUDMessage read(FriendlyByteBuf buffer) {
        return new ToggleHUDMessage();
    }

    public static void write(ToggleHUDMessage message, FriendlyByteBuf buffer) {
    }

    public static void onMessage(ToggleHUDMessage message, MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            if (player != null) {
                JetpackUtils.withJetpack(player, (stack, sync) -> {
                    if (stack.getItem() instanceof JetpackItem jetpack) {
                        jetpack.toggleHUD(stack);
                        sync.run();
                    }
                });
            }
        });
    }
}
