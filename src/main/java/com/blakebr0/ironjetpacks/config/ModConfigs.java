package com.blakebr0.ironjetpacks.config;

import com.blakebr0.ironjetpacks.IronJetpacks;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ModConfigs {
    @Environment(EnvType.CLIENT)
    @Config(name = IronJetpacks.MOD_ID + "/client")
    public static class Client implements ConfigData {
        @ConfigEntry.Category("general")
        @ConfigEntry.Gui.TransitiveObject
        @Comment("General configuration options.")
        public General general = new General();
        
        @ConfigEntry.Category("hud")
        @ConfigEntry.Gui.TransitiveObject
        @Comment("HUD configuration options.")
        public Hud hud = new Hud();
        
        public static class General {
            @Comment("Enable jetpack sounds?")
            public boolean enableJetpackSounds = true;
            @Comment("Enable jetpack particles?")
            public boolean enableJetpackParticles = true;
            @Comment("Enable jetpack stat tooltips?")
            public boolean enableAdvancedInfoTooltips = true;
        }
        
        public static class Hud {
            @Comment("Enable the HUD?")
            public boolean enableHud = true;
            @Comment("The position preset for the HUD.\n0=Top Left, 1=Middle Left, 2=Bottom Left, 3=Top Right, 4=Middle Right, 5=Bottom Right")
            @ConfigEntry.Gui.Tooltip(count = 2)
            @ConfigEntry.BoundedDiscrete(min = 0, max = 5)
            public int hudPosition = 1;
            @Comment("The X offset for the HUD.")
            public int hudOffsetX = 0;
            @Comment("The Y offset for the HUD.")
            public int hudOffsetY = 0;
            @Comment("Show HUD over the chat?")
            public boolean showHudOverChat = false;
        }
    }
    
    @Config(name = IronJetpacks.MOD_ID + "/common")
    public static class Common implements ConfigData {
        @ConfigEntry.Category("general")
        @ConfigEntry.Gui.TransitiveObject
        @Comment("General configuration options.")
        public General general = new General();
        
        @ConfigEntry.Category("recipe")
        @ConfigEntry.Gui.TransitiveObject
        @Comment("Dynamic recipe options.")
        public Recipe recipe = new Recipe();
        
        public static class General {
            @Comment("Should jetpacks be enachantable?")
            public boolean enchantableJetpacks = false;
        }
        
        public static class Recipe {
            @Comment("Enable default recipes for Energy Cells?")
            public boolean enableCellRecipes = true;
            @Comment("Enable default recipes for Thrusters?")
            public boolean enableThrusterRecipes = true;
            @Comment("Enable default recipes for Capacitors?")
            public boolean enableCapacitorRecipes = true;
            @Comment("Enable default recipes for Jetpacks?")
            public boolean enableJetpackRecipes = true;
        }
    }
    
    public static Common get() {
        return AutoConfig.getConfigHolder(Common.class).getConfig();
    }
    
    @Environment(EnvType.CLIENT)
    public static Client getClient() {
        return AutoConfig.getConfigHolder(Client.class).getConfig();
    }
}
