package com.egg.item;

import com.egg.ExperienceGatlingGun;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup EXPERIENCE_GATLING_GUN_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            Identifier.of(ExperienceGatlingGun.MOD_ID, "egg_tab"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModItems.EXPERIENCE_CELLULE))
                    .displayName(Text.literal("EGG"))
                    .entries((context, entries) -> {
                        entries.add(new ItemStack(ModItems.EXPERIENCE_ORBEEZ));
                        entries.add(new ItemStack(ModItems.EXPERIENCE_CELLULE));
                        entries.add(new ItemStack(ModItems.EXPERIENCE_GATLING_GUN));

                        entries.add(new ItemStack(ModItems.EXPERIENCE_GRINDER));
                        entries.add(new ItemStack(ModItems.EXPERIENCE_DRAINER));
                    })
                    .build()
    );

    public static void registerItemGroups() {
        ExperienceGatlingGun.LOGGER.info("Registering item groups for " + ExperienceGatlingGun.MOD_ID);
    }
}