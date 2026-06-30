package geology.discovery.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MineralItem extends Item {
    private final String mineralClass;
    private final String formula;

    public MineralItem(String mineralClass, String formula) {
        super(new FabricItemSettings().maxCount(1));
        this.mineralClass = mineralClass;
        this.formula = formula;
    }

    @Override
    public void appendTooltip(
            ItemStack stack,
            @Nullable World world,
            List<Text> tooltip,
            TooltipContext context
    ) {
        addQualityTooltip(stack, tooltip);

        if (mineralClass != null && !mineralClass.isEmpty()) {
            tooltip.add(
                    Text.literal("Class: ")
                            .formatted(Formatting.GRAY)
                            .append(Text.literal(mineralClass).formatted(Formatting.DARK_PURPLE))
            );
        }

        if (formula != null && !formula.isEmpty()) {
            tooltip.add(
                    Text.literal("Formula: ")
                            .formatted(Formatting.GRAY)
                            .append(Text.literal(formula).formatted(Formatting.LIGHT_PURPLE))
            );
        }
    }

    private void addQualityTooltip(ItemStack stack, List<Text> tooltip) {
        NbtCompound nbt = stack.getNbt();

        if (nbt == null || !nbt.contains("Quality")) {
            tooltip.add(
                    Text.literal("Quality: ")
                            .formatted(Formatting.GRAY)
                            .append(Text.literal("Unknown").formatted(Formatting.DARK_GRAY))
            );
            return;
        }

        String quality = nbt.getString("Quality");

        MutableText qualityText = switch (quality) {
            case "pristine" -> Text.literal("Pristine").formatted(Formatting.AQUA);
            case "intact" -> Text.literal("Intact").formatted(Formatting.GREEN);
            case "chipped" -> Text.literal("Chipped").formatted(Formatting.GOLD);
            default -> Text.literal("Unknown").formatted(Formatting.DARK_GRAY);
        };

        tooltip.add(
                Text.literal("Quality: ")
                        .formatted(Formatting.GRAY)
                        .append(qualityText)
        );
    }
}