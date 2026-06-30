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
            new MineralItem("Sulfide", "FeS₂")
    );

    public static final Item PYRITE_FRAGMENTS = registerItem(
            "pyrite_fragments",
            new Item(new FabricItemSettings())
    );

    public static final Item MALACHITE = registerItem(
            "malachite",
            new MineralItem("Carbonate", "Cu₂CO₃(OH)₂")
    );

    public static final Item MALACHITE_FRAGMENTS = registerItem(
            "malachite_fragments",
            new Item(new FabricItemSettings())
    );

    public static final Item HEMATITE = registerItem(
            "hematite",
            new MineralItem("Oxide", "Fe₂O₃")
    );

    public static final Item HEMATITE_FRAGMENTS = registerItem(
            "hematite_fragments",
            new Item(new FabricItemSettings())
    );

    public static final Item GARNET = registerItem(
            "garnet",
            new MineralItem("Silicate", "X₃Y₂(SiO₄)₃")
    );

    public static final Item GARNET_FRAGMENTS = registerItem(
            "garnet_fragments",
            new Item(new FabricItemSettings())
    );

    public static final Item SCHEELITE = registerItem(
            "scheelite",
            new MineralItem("Tungstate", "CaWO₄")
    );

    public static final Item SCHEELITE_FRAGMENTS = registerItem(
            "scheelite_fragments",
            new Item(new FabricItemSettings())
    );

    public static final Item MAGNETITE = registerItem(
            "magnetite",
            new MineralItem("Oxide", "Fe₃O₄")
    );

    public static final Item MAGNETITE_FRAGMENTS = registerItem(
            "magnetite_fragments",
            new Item(new FabricItemSettings())
    );

    public static final Item CALCITE = registerItem(
            "calcite",
            new MineralItem("Carbonate", "CaCO₃")
    );

    public static final Item CALCITE_FRAGMENTS = registerItem(
            "calcite_fragments",
            new Item(new FabricItemSettings())
    );

    public static final Item AZURITE = registerItem(
            "azurite",
            new MineralItem("Carbonate", "Cu₃(CO₃)₂(OH)₂")
    );

    public static final Item AZURITE_FRAGMENTS = registerItem(
            "azurite_fragments",
            new Item(new FabricItemSettings())
    );

    public static final Item TIGERS_EYE = registerItem(
            "tigers_eye",
            new MineralItem("Silicate", "SiO₂")
    );

    public static final Item TIGERS_EYE_FRAGMENTS = registerItem(
            "tigers_eye_fragments",
            new Item(new FabricItemSettings())
    );

    public static final Item LABRADORITE = registerItem(
            "labradorite",
            new MineralItem("Silicate", "(Ca,Na)(Al,Si)₄O₈")
    );

    public static final Item LABRADORITE_FRAGMENTS = registerItem(
            "labradorite_fragments",
            new Item(new FabricItemSettings())
    );

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Geological.id(name), item);
    }

    public static void registerModItems() {
        Geological.LOGGER.info("Registering Geological items");
    }
}