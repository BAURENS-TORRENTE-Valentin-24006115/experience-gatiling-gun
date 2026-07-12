package com.egg;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModItems {
    public static Item register(Item item, String id) {
        // Create the identifier for the item.
        ResourceLocation itemID = ResourceLocation.fromNamespaceAndPath(ExperienceGatlingGun.MOD_ID, id);

        // Return the registered item!
        return Registry.register(BuiltInRegistries.ITEM, itemID, item);
    }

    public static void initialize() {
        register(new ExperienceGatlingGunItem(new Item.Properties().stacksTo(1)), "experience-gatling-gun");
    }
}