package geology.discovery.item;

import net.minecraft.item.PickaxeItem;

public class GeologicalHammerItem extends PickaxeItem {
    public GeologicalHammerItem(Settings settings) {
        super(
                GeologicalToolMaterials.GEOLOGICAL_HAMMER,
                1,
                -2.3f,
                settings
        );
    }
}