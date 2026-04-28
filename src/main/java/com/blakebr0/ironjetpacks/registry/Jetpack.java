package com.blakebr0.ironjetpacks.registry;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.item.ComponentItem;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.google.common.base.Suppliers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public class Jetpack {
    public String name;
    public int tier;
    public int color;
    public int armorPoints;
    public int enchantablilty;
    public String craftingMaterialString;
    private Ingredient craftingMaterial;
    private boolean craftingMaterialResolved = false;
    public Supplier<JetpackItem> item;
    public boolean creative = false;
    public boolean disabled = false;
    public Rarity rarity = Rarity.COMMON;
    public ComponentItem cell;
    public ComponentItem thruster;
    public ComponentItem capacitor;
    public double capacity;
    public double usage;
    public double speedVert;
    public double accelVert;
    public double speedSide;
    public double speedHoverAscend;
    public double speedHover;
    public double speedHoverSlow;
    public double sprintSpeed;
    public double sprintSpeedVert;
    public double sprintFuel;
    
    public Jetpack(String name, int tier, int color, int armorPoints, int enchantability, String craftingMaterialString) {
        this.name = name;
        this.tier = tier;
        this.color = color;
        this.armorPoints = armorPoints;
        this.enchantablilty = enchantability;
        this.craftingMaterialString = craftingMaterialString;
        this.item = Suppliers.memoize(() -> {
            ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, name + "_jetpack"));
            return new JetpackItem(this, new Item.Properties().setId(key));
        });
    }
    
    public Jetpack setStats(double capacity, double usage, double speedVert, double accelVert, double speedSide, double speedHoverAscend, double speedHover, double speedHoverSlow, double sprintSpeed, double sprintSpeedVert, double sprintFuel) {
        this.capacity = capacity;
        this.usage = usage;
        this.speedVert = speedVert;
        this.accelVert = accelVert;
        this.speedSide = speedSide;
        this.speedHoverAscend = speedHoverAscend;
        this.speedHover = speedHover;
        this.speedHoverSlow = speedHoverSlow;
        this.sprintSpeed = sprintSpeed;
        this.sprintSpeedVert = sprintSpeedVert;
        this.sprintFuel = sprintFuel;

        return this;
    }
    
    public Jetpack setCreative() {
        this.creative = true;
        this.tier = -1;
        this.rarity = Rarity.EPIC;
        
        return this;
    }
    
    public Jetpack setCreative(boolean set) {
        if (set) this.setCreative();
        return this;
    }
    
    public Jetpack setDisabled() {
        this.disabled = true;
        return this;
    }
    
    public Jetpack setDisabled(boolean set) {
        if (set) this.setDisabled();
        return this;
    }
    
    public Jetpack setRarity(Rarity rarity) {
        this.rarity = rarity;
        return this;
    }
    
    public Jetpack setCellItem(ComponentItem item) {
        this.cell = item;
        return this;
    }
    
    public Jetpack setThrusterItem(ComponentItem item) {
        this.thruster = item;
        return this;
    }
    
    public Jetpack setCapacitorItem(ComponentItem item) {
        this.capacitor = item;
        return this;
    }
    
    public int getTier() {
        return this.tier;
    }
    
    public Ingredient getCraftingMaterial() {
        if (!this.craftingMaterialResolved) {
            this.craftingMaterialResolved = true;
            try {
                if (!this.craftingMaterialString.equalsIgnoreCase("null")) {
                    String[] parts = craftingMaterialString.split(":");
                    if (parts.length >= 3 && this.craftingMaterialString.startsWith("tag:")) {
                        TagKey<Item> tag = TagKey.create(Registries.ITEM, Identifier.parse(parts[1] + ":" + parts[2]));
                        BuiltInRegistries.ITEM.get(tag).ifPresent(holderSet ->
                            this.craftingMaterial = Ingredient.of(holderSet));
                    } else if (parts.length >= 2) {
                        BuiltInRegistries.ITEM.get(Identifier.parse(parts[0] + ":" + parts[1]))
                            .ifPresent(ref -> this.craftingMaterial = Ingredient.of(ref.value()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return this.craftingMaterial;
    }
}
