package geology.discovery.item;

import geology.discovery.Geological;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {
    public static final Item GEOLOGICAL_HAMMER = registerItem(
            "geological_hammer",
            new GeologicalHammerItem(new FabricItemSettings().maxDamage(250))
    );

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Geological.id(name), item);
    }

    public static void registerModItems() {
        Geological.LOGGER.info("Registering Geological items");
    }
}