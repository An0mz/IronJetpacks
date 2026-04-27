package com.blakebr0.ironjetpacks.client;

import com.blakebr0.ironjetpacks.item.Colored;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ColoredTintSource implements ItemTintSource {
    public static final ColoredTintSource INSTANCE = new ColoredTintSource();
    public static final MapCodec<ColoredTintSource> CODEC = MapCodec.unit(INSTANCE);

    private ColoredTintSource() {}

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        if (stack.getItem() instanceof Colored colored) {
            return colored.getColorTint(0);
        }
        return -1;
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }
}
