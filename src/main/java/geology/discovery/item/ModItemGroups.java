package geology.discovery.item;

import geology.discovery.Geological;
import geology.discovery.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public class ModItemGroups {
    public static final ItemGroup GEOLOGICAL_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            Geological.id("geological_group"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemGroup.geological"))
                    .icon(() -> new ItemStack(ModBlocks.STRANGE_GRANITE))
                    .entries((displayContext, entries) -> {
                        entries.add(ModBlocks.STRANGE_STONE);
                        entries.add(ModBlocks.STRANGE_GRANITE);
                        entries.add(ModBlocks.STRANGE_DIORITE);
                        entries.add(ModBlocks.STRANGE_ANDESITE);
                        entries.add(ModBlocks.STRANGE_DEEPSLATE);
                        entries.add(ModBlocks.STRANGE_TUFF);
                        entries.add(ModBlocks.STRANGE_BASALT);
                        entries.add(ModBlocks.STRANGE_SANDSTONE);
                        entries.add(ModBlocks.STRANGE_RED_SANDSTONE);
                        entries.add(ModBlocks.STRANGE_GRAVEL);
                    })
                    .build()
    );

    public static void registerItemGroups() {
        Geological.LOGGER.info("Registering Geological item groups");
    }
}