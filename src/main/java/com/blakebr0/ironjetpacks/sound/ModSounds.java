package com.blakebr0.ironjetpacks.sound;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.google.common.base.Suppliers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class ModSounds {
    public static final ResourceLocation JETPACK_ID = ResourceLocation.fromNamespaceAndPath(IronJetpacks.MOD_ID, "jetpack");
    public static final Supplier<SoundEvent> JETPACK = Suppliers.memoize(() -> SoundEvent.createVariableRangeEvent(JETPACK_ID));
    
    public static void register() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, JETPACK_ID, JETPACK.get());
    }
}
