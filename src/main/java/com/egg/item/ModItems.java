package com.egg.item;

import com.egg.ExperienceGatlingGun;
import net.minecraft.item.ExperienceBottleItem;
import net.minecraft.item.Item;

import static net.minecraft.item.Items.register;

public class ModItems {

    public static final Item EXPERIENCE_GATLING_GUN = register("experience_gatling_gun",
            new ExperienceGatlingGunItem(new Item.Settings().maxCount(1)));

    public static final Item EXPERIENCE_ORBEEZ = register("experience_orbeez",
            new Item(new Item.Settings()));

    public static final Item EXPERIENCE_CELLULE = register("experience_cellule",
            new Item(new Item.Settings().maxCount(16)));


    public static void registerItems() {
        ExperienceGatlingGun.LOGGER.info("Registering items for " + ExperienceGatlingGun.MOD_ID);
    }
}