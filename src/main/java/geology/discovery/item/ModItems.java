package geology.discovery.item;

import geology.discovery.Geological;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {
    public static final Item GEOLOGICAL_HAMMER = registerItem(
            "geological_hammer",
            new GeologicalHammerItem(new FabricItemSettings())
    );

    public static final Item PYRITE = registerItem(
            "pyrite",
            new Item(new FabricItemSettings().maxCount(1))
    );

    public static final Item PYRITE_FRAGMENTS = registerItem(
            "pyrite_fragments",
            new Item(new FabricItemSettings())
    );

    public static final Item MALACHITE = registerItem(
            "malachite",
            new Item(new FabricItemSettings().maxCount(1))
    );

    public static final Item MALACHITE_FRAGMENTS = registerItem(
            "malachite_fragments",
            new Item(new FabricItemSettings())
    );

    public static final Item HEMATITE = registerItem(
            "hematite",
            new Item(new FabricItemSettings().maxCount(1))
    );

    public static final Item HEMATITE_FRAGMENTS = registerItem(
            "hematite_fragments",
            new Item(new FabricItemSettings())
    );

    public static final Item GARNET = registerItem(
            "garnet",
            new Item(new FabricItemSettings().maxCount(1))
    );

    public static final Item GARNET_FRAGMENTS = registerItem(
            "garnet_fragments",
            new Item(new FabricItemSettings())
    );

    public static final Item SCHEELITE = registerItem(
            "scheelite",
            new Item(new FabricItemSettings().maxCount(1))
    );

    public static final Item SCHEELITE_FRAGMENTS = registerItem(
            "scheelite_fragments",
            new Item(new FabricItemSettings())
    );

    public static final Item MAGNETITE = registerItem(
            "magnetite",
            new Item(new FabricItemSettings().maxCount(1))
    );

    public static final Item MAGNETITE_FRAGMENTS = registerItem(
            "magnetite_fragments",
            new Item(new FabricItemSettings())
    );

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Geological.id(name), item);
    }

    public static void registerModItems() {
        Geological.LOGGER.info("Registering Geological items");
    }
}