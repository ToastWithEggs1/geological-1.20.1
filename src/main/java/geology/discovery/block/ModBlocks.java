package geology.discovery.block;

import geology.discovery.Geological;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.block.FallingBlock;

public class ModBlocks {
    public static final Block STRANGE_GRANITE = registerBlock(
            "strange_granite",
            new Block(AbstractBlock.Settings.copy(Blocks.GRANITE))
    );
    public static final Block STRANGE_STONE = registerBlock(
            "strange_stone",
            new Block(AbstractBlock.Settings.copy(Blocks.STONE))
    );

    public static final Block STRANGE_ANDESITE = registerBlock(
            "strange_andesite",
            new Block(AbstractBlock.Settings.copy(Blocks.ANDESITE))
    );

    public static final Block STRANGE_DIORITE = registerBlock(
            "strange_diorite",
            new Block(AbstractBlock.Settings.copy(Blocks.DIORITE))
    );

    public static final Block STRANGE_DEEPSLATE = registerBlock(
            "strange_deepslate",
            new Block(AbstractBlock.Settings.copy(Blocks.DEEPSLATE))
    );

    public static final Block STRANGE_TUFF = registerBlock(
            "strange_tuff",
            new Block(AbstractBlock.Settings.copy(Blocks.TUFF))
    );

    public static final Block STRANGE_GRAVEL = registerBlock(
            "strange_gravel",
            new FallingBlock(AbstractBlock.Settings.copy(Blocks.GRAVEL))
    );

    public static final Block STRANGE_BASALT = registerBlock(
            "strange_basalt",
            new Block(AbstractBlock.Settings.copy(Blocks.BASALT))
    );

    public static final Block STRANGE_SANDSTONE = registerBlock(
            "strange_sandstone",
            new Block(AbstractBlock.Settings.copy(Blocks.SANDSTONE))
    );

    public static final Block STRANGE_RED_SANDSTONE = registerBlock(
            "strange_red_sandstone",
            new Block(AbstractBlock.Settings.copy(Blocks.RED_SANDSTONE))
    );

    private static Block registerBlock(String name, Block block) {
        Registry.register(
                Registries.ITEM,
                Geological.id(name),
                new BlockItem(block, new FabricItemSettings())
        );

        return Registry.register(
                Registries.BLOCK,
                Geological.id(name),
                block
        );
    }

    public static void registerModBlocks() {
        Geological.LOGGER.info("Registering Geological blocks");

    }
}