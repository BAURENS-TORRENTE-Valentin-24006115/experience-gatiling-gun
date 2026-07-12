package com.egg.item;

import com.egg.ExperienceGatlingGun;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {

    private static Item register(String id, Item item) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ExperienceGatlingGun.MOD_ID, id));
        return Registry.register(Registries.ITEM, key, item);
    }

    public static final Item EXPERIENCE_GATLING_GUN = register("experience_gatling_gun",
            new ExperienceGatlingGunItem(new Item.Settings().maxCount(1)));

    public static final Item EXPERIENCE_ORBEEZ = register("experience_orbeez",
            new ExperienceOrbeezItem(new Item.Settings()));

    public static final Item EXPERIENCE_CELLULE = register("experience_cellule",
            new ExperienceCelluleItem(new Item.Settings().maxCount(16)));

    public static void registerItems() {
        ExperienceGatlingGun.LOGGER.info("Registering items for " + ExperienceGatlingGun.MOD_ID);
    }
}