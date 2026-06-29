package geology.discovery.worldgen;

import com.mojang.serialization.Codec;
import geology.discovery.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import geology.discovery.Geological;

public class StrangeGravelFeature extends Feature<DefaultFeatureConfig> {

    public StrangeGravelFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();

        Geological.LOGGER.info("Searching near {}", origin);

        for (int i = 0; i < 256; i++) {
            BlockPos pos = origin.add(
                    context.getRandom().nextInt(17) - 8,
                    context.getRandom().nextInt(9) - 4,
                    context.getRandom().nextInt(17) - 8
            );

            if (!world.getBlockState(pos).isOf(Blocks.GRAVEL))
                continue;

            boolean exposed = false;

            for (Direction direction : Direction.values()) {
                if (world.getBlockState(pos.offset(direction)).isAir()) {
                    exposed = true;
                    break;
                }
            }

            if (!exposed)
                continue;

            Geological.LOGGER.info("Placed exposed Strange Gravel at {}", pos);

            world.setBlockState(pos, ModBlocks.STRANGE_GRAVEL.getDefaultState(), 3);
            return true;
        }

        Geological.LOGGER.info("No gravel found near {}", origin);
        return false;
    }
}