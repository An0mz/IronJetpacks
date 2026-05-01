package com.blakebr0.ironjetpacks.network.message;

import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class IncrementThrottleMessage {
    public static IncrementThrottleMessage read(FriendlyByteBuf buffer) {
        return new IncrementThrottleMessage();
    }

    public static void write(IncrementThrottleMessage message, FriendlyByteBuf buffer) {
    }

    public static void onMessage(IncrementThrottleMessage message, MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            if (player != null) {
                JetpackUtils.withJetpack(player, (stack, sync) -> {
                    if (stack.getItem() instanceof JetpackItem jetpack) {
                        jetpack.incrementThrottle(stack);
                        sync.run();
                    }
                });
            }
        });
    }
}
