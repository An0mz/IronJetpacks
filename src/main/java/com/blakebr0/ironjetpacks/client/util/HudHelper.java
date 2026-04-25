package com.blakebr0.ironjetpacks.client.util;

import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.lib.ModTooltips;
import com.blakebr0.ironjetpacks.util.UnitUtils;
import com.mojang.blaze3d.platform.Window;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;

@Environment(EnvType.CLIENT)
public class HudHelper {
    public static HudPos getHudPos() {
        Window window = Minecraft.getInstance().getWindow();
        int xOffset = ModConfigs.getClient().hud.hudOffsetX;
        int yOffset = ModConfigs.getClient().hud.hudOffsetY;
        
        switch (ModConfigs.getClient().hud.hudPosition) {
            case 0:
                return new HudPos(10 + xOffset, 30 + yOffset, 0);
            case 1:
                return new HudPos(10 + xOffset, window.getGuiScaledHeight() / 2 + yOffset, 0);
            case 2:
                return new HudPos(10 + xOffset, window.getGuiScaledHeight() - 30 + yOffset, 0);
            case 3:
                return new HudPos(window.getGuiScaledWidth() - 8 - xOffset, 30 + yOffset, 1);
            case 4:
                return new HudPos(window.getGuiScaledWidth() - 8 - xOffset, window.getGuiScaledHeight() / 2 + yOffset, 1);
            case 5:
                return new HudPos(window.getGuiScaledWidth() - 8 - xOffset, window.getGuiScaledHeight() - 30 + yOffset, 1);
        }
        
        return null;
    }
    
    public static int getEnergyBarScaled(JetpackItem jetpack, ItemStack stack) {
        if (jetpack.getJetpack().creative) return 156;
        EnergyStorage energy = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
        double i = energy.getAmount();
        double j = energy.getCapacity();
        return (int) (j != 0 && i != 0 ? (long) i * 156 / j : 0);
    }
    
    public static String getFuel(JetpackItem jetpack, ItemStack stack) {
        if (jetpack.getJetpack().creative) return ModTooltips.INFINITE.asFormattedString() + ChatFormatting.GRAY + " E";
        double number = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack)).getAmount();
        return UnitUtils.formatEnergy(number, ChatFormatting.GRAY);
    }
    
    public static String getOn(boolean on) {
        return on ? ModTooltips.ON.color(ChatFormatting.GREEN).getString() : ModTooltips.OFF.color(ChatFormatting.RED).getString();
    }
    
    public static class HudPos {
        public int x;
        public int y;
        public int side;
        
        public HudPos(int x, int y, int side) {
            this.x = x;
            this.y = y;
            this.side = side;
        }
    }
}
