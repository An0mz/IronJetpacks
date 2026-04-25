package com.blakebr0.ironjetpacks.network;

import com.blakebr0.ironjetpacks.IronJetpacks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record NetworkPayload(int messageId, FriendlyByteBuf data) implements CustomPacketPayload {

    public static final ResourceLocation ID_LOC = ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, IronJetpacks.MOD_ID);
    public static final Type<NetworkPayload> TYPE = new Type<>(ID_LOC);

    public static final StreamCodec<FriendlyByteBuf, NetworkPayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeInt(payload.messageId());
                buf.writeBytes(payload.data().copy());
            },
            buf -> {
                int id = buf.readInt();
                // Read remaining bytes into a new buffer
                FriendlyByteBuf dataBuf = new FriendlyByteBuf(buf.readBytes(buf.readableBytes()));
                return new NetworkPayload(id, dataBuf);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}