package com.egg.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    //public static final Block OLD_IRON_BLOCK = register("old_iron_block",
    //        new Block(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
    //                .requiresTool()
    //                .strength(5.0f, 6.0f))
    //);

    //public static final Block EMPTY_JAR = register("empty_jar",
    //        new EmptyJarBlock(AbstractBlock.Settings.create()
    //                .strength(0.3f)
    //                .sounds(BlockSoundGroup.GLASS)
    //                .nonOpaque())
    //);

    public static final Block EXPERIENCE_GRINDER = register("experience_grinder",
            new ExperienceGrinderBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
                    .requiresTool()
                    .strength(5.0f, 6.0f))
    );

    //public static final Block EXPERIENCE_GRINDER = register("experience_grinder",
    //        new ExperienceGrinderBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
    //                .strength(0.3f)
    //                .sounds(BlockSoundGroup.COPPER)
    //                .nonOpaque())
    //);

    // ============ REGISTRATION ============
    private static Block register(String name, Block block) {
        return Registry.register(
                Registries.BLOCK,
                Identifier.of("egg", name),
                block
        );
    }

    public static void registerBlocks() {
    }
}