package geology.discovery.item;

import geology.discovery.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SieveItem extends Item {
    public SieveItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        Block block = world.getBlockState(pos).getBlock();

        if (block != ModBlocks.STRANGE_GRAVEL) {
            return ActionResult.PASS;
        }

        if (!world.isClient && context.getPlayer() instanceof ServerPlayerEntity player) {
            siftStrangeGravel(player, world, pos, context.getHand());
        }

        return ActionResult.SUCCESS;
    }

    private void siftStrangeGravel(ServerPlayerEntity player, World world, BlockPos pos, Hand hand) {
        ItemStack sieveStack = player.getStackInHand(hand);

        world.setBlockState(pos, Blocks.GRAVEL.getDefaultState(), 3);

        ItemStack reward = getSiftingReward(player);
        Block.dropStack(world, pos, reward);

        world.playSound(
                null,
                pos,
                SoundEvents.BLOCK_GRAVEL_BREAK,
                SoundCategory.BLOCKS,
                1.0f,
                0.85f
        );

        sieveStack.damage(1, player, brokenPlayer -> brokenPlayer.sendToolBreakStatus(hand));
    }

    private ItemStack getSiftingReward(ServerPlayerEntity player) {
        int roll = player.getRandom().nextInt(100);

        if (roll < 40) {
            return getRandomNugget(player);
        }

        if (roll < 80) {
            return getRandomFragments(player);
        }

        if (roll < 95) {
            return new ItemStack(Items.GRAVEL);
        }

        return getRandomSeeds(player);
    }

    private ItemStack getRandomNugget(ServerPlayerEntity player) {
        ItemStack stack;

        if (player.getRandom().nextBoolean()) {
            stack = new ItemStack(Items.IRON_NUGGET);
        } else {
            stack = new ItemStack(Items.GOLD_NUGGET);
        }

        stack.setCount(1 + player.getRandom().nextInt(3));
        return stack;
    }

    private ItemStack getRandomFragments(ServerPlayerEntity player) {
        Item[] fragments = {
                ModItems.AZURITE_FRAGMENTS,
                ModItems.CALCITE_FRAGMENTS,
                ModItems.GARNET_FRAGMENTS,
                ModItems.HEMATITE_FRAGMENTS,
                ModItems.LABRADORITE_FRAGMENTS,
                ModItems.MAGNETITE_FRAGMENTS,
                ModItems.MALACHITE_FRAGMENTS,
                ModItems.PYRITE_FRAGMENTS,
                ModItems.SCHEELITE_FRAGMENTS,
                ModItems.TIGERS_EYE_FRAGMENTS
        };

        ItemStack stack = new ItemStack(fragments[player.getRandom().nextInt(fragments.length)]);
        stack.setCount(1 + player.getRandom().nextInt(3));

        return stack;
    }

    private ItemStack getRandomSeeds(ServerPlayerEntity player) {
        Item[] seeds = {
                Items.WHEAT_SEEDS,
                Items.PUMPKIN_SEEDS,
                Items.MELON_SEEDS,
                Items.BEETROOT_SEEDS
        };

        ItemStack stack = new ItemStack(seeds[player.getRandom().nextInt(seeds.length)]);
        stack.setCount(1);

        return stack;
    }
}