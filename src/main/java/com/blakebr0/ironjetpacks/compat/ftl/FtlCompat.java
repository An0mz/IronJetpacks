package com.blakebr0.ironjetpacks.compat.ftl;

import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyItem;

public class FtlCompat {
    public static void init() {
        for (Jetpack jetpack : JetpackRegistry.getInstance().getAllJetpacks()) {
            double maxInput = jetpack.item.get().getMaxInput();
            EnergyStorage.ITEM.registerForItems((itemStack, context) -> {
                return SimpleEnergyItem.createStorage(context, (long) jetpack.capacity, (long) maxInput, (long) Math.max(maxInput, jetpack.usage * jetpack.sprintFuel));
            }, jetpack.item.get());
        }
    }
}
