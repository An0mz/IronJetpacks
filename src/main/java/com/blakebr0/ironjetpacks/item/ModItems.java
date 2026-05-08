package com.blakebr0.ironjetpacks.item;

import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.config.ModJetpacks;
import com.blakebr0.ironjetpacks.registry.Jetpack;
import com.blakebr0.ironjetpacks.registry.JetpackRegistry;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final Map<Identifier, Supplier<Item>> ENTRIES = Maps.newHashMap();

    public static final Supplier<Item> STRAP = register("strap");
    public static final Supplier<Item> BASIC_COIL = register("basic_coil");
    public static final Supplier<Item> ADVANCED_COIL = register("advanced_coil");
    public static final Supplier<Item> ELITE_COIL = register("elite_coil");
    public static final Supplier<Item> ULTIMATE_COIL = register("ultimate_coil");
    public static final Supplier<Item> EXPERT_COIL = register("expert_coil");

    public static void register() {
        Registry<Item> registry = BuiltInRegistries.ITEM;
        JetpackRegistry jetpacks = JetpackRegistry.getInstance();

        ENTRIES.forEach((id, item) -> Registry.register(registry, id, item.get()));

        ModJetpacks.loadJsons();

        // Energy Cells
        for (Jetpack jetpack : jetpacks.getAllJetpacks()) {
            Identifier cellLoc = Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, jetpack.name + "_cell");
            ComponentItem item = new ComponentItem(jetpack, "cell", new Item.Properties().setId(ResourceKey.create(Registries.ITEM, cellLoc)));
            jetpack.setCellItem(item);
            Registry.register(registry, cellLoc, item);
        }

        // Thrusters
        for (Jetpack jetpack : jetpacks.getAllJetpacks()) {
            Identifier thrusterLoc = Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, jetpack.name + "_thruster");
            ComponentItem item = new ComponentItem(jetpack, "thruster", new Item.Properties().setId(ResourceKey.create(Registries.ITEM, thrusterLoc)));
            jetpack.setThrusterItem(item);
            Registry.register(registry, thrusterLoc, item);
        }

        // Capacitors
        for (Jetpack jetpack : jetpacks.getAllJetpacks()) {
            Identifier capacitorLoc = Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, jetpack.name + "_capacitor");
            ComponentItem item = new ComponentItem(jetpack, "capacitor", new Item.Properties().setId(ResourceKey.create(Registries.ITEM, capacitorLoc)));
            jetpack.setCapacitorItem(item);
            Registry.register(registry, capacitorLoc, item);
        }

        // Jetpacks
        for (Jetpack jetpack : jetpacks.getAllJetpacks()) {
            Registry.register(registry, Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, jetpack.name + "_jetpack"), jetpack.item.get());
        }
    }

    private static Supplier<Item> register(String name) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, name));
        return register(name, Suppliers.memoize(() -> new Item(new Item.Properties().setId(key))));
    }

    private static Supplier<Item> register(String name, Supplier<Item> item) {
        Identifier loc = Identifier.fromNamespaceAndPath(IronJetpacks.MOD_ID, name);
        ENTRIES.put(loc, item);
        return item;
    }
}