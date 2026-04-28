package com.blakebr0.ironjetpacks;

import com.blakebr0.ironjetpacks.compat.ftl.FtlCompat;
import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.crafting.JetpackRecipeInjector;
import com.blakebr0.ironjetpacks.crafting.ModRecipeSerializers;
import com.blakebr0.ironjetpacks.handler.InputHandler;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.item.ModItems;
import com.blakebr0.ironjetpacks.network.NetworkHandler;
import com.blakebr0.ironjetpacks.network.NetworkPayload;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import com.blakebr0.ironjetpacks.sound.ModSounds;
import dev.architectury.event.events.common.PlayerEvent;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;


public class IronJetpacks implements ModInitializer {
    public static final String MOD_ID = "iron-jetpacks";
    public static final String NAME = "Iron Jetpacks";
    
    public static final CreativeModeTab ITEM_GROUP = FabricItemGroup.builder()
            .title(Component.translatable("itemGroup.iron-jetpacks.iron-jetpacks"))
            .icon(() -> {
                return new ItemStack(ModItems.STRAP.get());
            })
            .displayItems((featureFlagSet, output) -> {
                for (Jetpack jetpack : JetpackRegistry.getInstance().getAllJetpacks()) {
                    output.accept(jetpack.cell);
                    output.accept(jetpack.thruster);
                    output.accept(jetpack.capacitor);
                    JetpackItem item = jetpack.item.get();
                    output.accept(new ItemStack(item));

                    if (!jetpack.creative) {
                        ItemStack stack = new ItemStack(item);
                        CompoundTag tag = stack.has(DataComponents.CUSTOM_DATA) ? stack.get(DataComponents.CUSTOM_DATA).copyTag() : new CompoundTag();
                        tag.putDouble("energy", jetpack.capacity);
                        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                        output.accept(stack);
                    }
                }
            })
            .build();
    
    @Override
    public void onInitialize() {
        ModItems.register();
        ModSounds.register();
        ModRecipeSerializers.register();
        ModRecipeSerializers.onCommonSetup();

        ServerLifecycleEvents.SERVER_STARTED.register(JetpackRecipeInjector::inject);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) JetpackRecipeInjector.inject(server);
        });
        PayloadTypeRegistry.playC2S().register(NetworkPayload.TYPE, NetworkPayload.CODEC);
        NetworkHandler.onCommonSetup();

        AutoConfig.register(ModConfigs.Common.class, JanksonConfigSerializer::new);
        
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            try {
                Class.forName("com.blakebr0.ironjetpacks.client.IronJetpacksClient").getDeclaredMethod("onInitializeClient").invoke(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        FtlCompat.init();
        
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> InputHandler.clear());
        PlayerEvent.CHANGE_DIMENSION.register((player, oldLevel, newLevel) -> InputHandler.onChangeDimension(player));
        PlayerEvent.PLAYER_QUIT.register(InputHandler::onLogout);

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
                if (!chest.isEmpty() && chest.getItem() instanceof JetpackItem jetpackItem) {
                    jetpackItem.tickArmor(chest, player);
                }
            }
        });
    }
}
