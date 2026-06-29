package geology.discovery.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.PickaxeItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GeologicalHammerItem extends PickaxeItem {
    public GeologicalHammerItem(Settings settings) {
        super(
                GeologicalToolMaterials.GEOLOGICAL_HAMMER,
                1,
                -2.0f,
                settings
        );
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        Block block = world.getBlockState(pos).getBlock();

        if (!ExtractionMinigame.isStrangeBlock(block)) {
            return super.useOnBlock(context);
        }

        if (!world.isClient && context.getPlayer() instanceof ServerPlayerEntity player) {
            ExtractionMinigame.handleHammerClick(player, world, pos);
        }

        return ActionResult.SUCCESS;
    }
}