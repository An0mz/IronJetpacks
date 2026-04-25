package com.blakebr0.ironjetpacks.network;

import com.blakebr0.ironjetpacks.network.message.ToggleEngineMessage;
import com.blakebr0.ironjetpacks.network.message.ToggleHoverMessage;
import com.blakebr0.ironjetpacks.network.message.UpdateInputMessage;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;

public class NetworkHandler {

    public static void onCommonSetup() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkPayload.TYPE, (payload, context) -> {
            int id = payload.messageId();
            FriendlyByteBuf buf = payload.data();
            switch (id) {
                case 0 -> ToggleHoverMessage.onMessage(ToggleHoverMessage.read(buf), context.server(), context.player());
                case 1 -> UpdateInputMessage.onMessage(UpdateInputMessage.read(buf), context.server(), context.player());
                case 2 -> ToggleEngineMessage.onMessage(ToggleEngineMessage.read(buf), context.server(), context.player());
            }
        });
    }

    @Environment(EnvType.CLIENT)
    public static void sendToServer(ToggleHoverMessage message) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ToggleHoverMessage.write(message, buf);
        ClientPlayNetworking.send(new NetworkPayload(0, buf));
    }

    @Environment(EnvType.CLIENT)
    public static void sendToServer(UpdateInputMessage message) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        UpdateInputMessage.write(message, buf);
        ClientPlayNetworking.send(new NetworkPayload(1, buf));
    }

    @Environment(EnvType.CLIENT)
    public static void sendToServer(ToggleEngineMessage message) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ToggleEngineMessage.write(message, buf);
        ClientPlayNetworking.send(new NetworkPayload(2, buf));
    }
}