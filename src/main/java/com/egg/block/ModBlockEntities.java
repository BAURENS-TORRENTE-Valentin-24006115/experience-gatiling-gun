package com.egg.block;

import com.egg.block.entity.ExperienceDrainerBlockEntity;
import com.egg.block.entity.ExperienceGrinderBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static final BlockEntityType<ExperienceGrinderBlockEntity> EXPERIENCE_GRINDER =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of("egg", "experience_grinder"),
                    FabricBlockEntityTypeBuilder.create(
                            ExperienceGrinderBlockEntity::new,
                            ModBlocks.EXPERIENCE_GRINDER
                    ).build()
            );

    public static final BlockEntityType<ExperienceDrainerBlockEntity> EXPERIENCE_DRAINER =
            Registry.register(Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of("egg", "experience_drainer"),
                    FabricBlockEntityTypeBuilder.create(
                            ExperienceDrainerBlockEntity::new,
                            ModBlocks.EXPERIENCE_DRAINER
                    ).build()
            );

    public static void registerBlockEntities() {

    }
}