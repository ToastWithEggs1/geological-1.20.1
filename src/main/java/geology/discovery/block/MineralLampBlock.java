package geology.discovery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class MineralLampBlock extends Block {
    private static final int SCARE_RADIUS = 8;
    private static final int SCARE_TICK_INTERVAL = 5;
    private static final double FLEE_SPEED = 1.35;

    private static final VoxelShape SHAPE = VoxelShapes.union(
            // Base: 6x6x1
            Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 1.0, 11.0),

            // Small support: 4x4x2
            Block.createCuboidShape(6.0, 1.0, 6.0, 10.0, 3.0, 10.0),

            // Main body: 6x6x7
            Block.createCuboidShape(5.0, 3.0, 5.0, 11.0, 10.0, 11.0)
    );

    public MineralLampBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos belowPos = pos.down();
        BlockState belowState = world.getBlockState(belowPos);

        return belowState.isSideSolidFullSquare(world, belowPos, Direction.UP);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            world.scheduleBlockTick(pos, this, SCARE_TICK_INTERVAL);
        }

        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
            return;
        }

        scareNearbyMonsters(world, pos);

        world.scheduleBlockTick(pos, this, SCARE_TICK_INTERVAL);
    }

    @Override
    public void neighborUpdate(
            BlockState state,
            World world,
            BlockPos pos,
            Block sourceBlock,
            BlockPos sourcePos,
            boolean notify
    ) {
        if (!world.isClient && !state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
            return;
        }

        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    @Override
    public BlockState getStateForNeighborUpdate(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            WorldAccess world,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            world.scheduleBlockTick(pos, this, 1);
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    private void scareNearbyMonsters(ServerWorld world, BlockPos pos) {
        Box scareBox = new Box(pos).expand(SCARE_RADIUS);

        for (HostileEntity monster : world.getEntitiesByClass(
                HostileEntity.class,
                scareBox,
                MineralLampBlock::isScaredByMineralLamp
        )) {
            scareMonsterAway(world, monster, pos);
        }
    }

    private static boolean isScaredByMineralLamp(HostileEntity monster) {
        EntityType<?> type = monster.getType();

        return type == EntityType.ZOMBIE
                || type == EntityType.CREEPER
                || type == EntityType.HUSK
                || type == EntityType.DROWNED
                || type == EntityType.ZOMBIFIED_PIGLIN
                || type == EntityType.SPIDER
                || type == EntityType.CAVE_SPIDER
                || type == EntityType.SLIME
                || type == EntityType.MAGMA_CUBE
                || type == EntityType.SILVERFISH
                || type == EntityType.ENDERMITE
                || type == EntityType.ENDERMAN
                || type == EntityType.VINDICATOR
                || type == EntityType.RAVAGER
                || type == EntityType.HOGLIN
                || type == EntityType.ZOGLIN
                || type == EntityType.PIGLIN_BRUTE;
    }

    private static void scareMonsterAway(ServerWorld world, HostileEntity monster, BlockPos lampPos) {
        monster.setTarget(null);
        monster.setAttacking(false);

        Vec3d fleeTarget = findFleeTarget(world, monster, lampPos);

        if (fleeTarget != null) {
            monster.getNavigation().startMovingTo(
                    fleeTarget.x,
                    fleeTarget.y,
                    fleeTarget.z,
                    FLEE_SPEED
            );
            return;
        }

        pushMonsterAway(monster, lampPos);
    }

    private static Vec3d findFleeTarget(ServerWorld world, HostileEntity monster, BlockPos lampPos) {
        Vec3d lampCenter = Vec3d.ofCenter(lampPos);
        Vec3d monsterPos = monster.getPos();

        Vec3d away = monsterPos.subtract(lampCenter);

        if (away.lengthSquared() < 0.0001) {
            away = new Vec3d(0.0, 0.0, 1.0);
        }

        away = away.normalize();

        Vec3d sideways = new Vec3d(-away.z, 0.0, away.x);
        Random random = monster.getRandom();

        for (int attempt = 0; attempt < 12; attempt++) {
            double forwardDistance = 6.0 + random.nextDouble() * 6.0;
            double sidewaysDistance = (random.nextDouble() - 0.5) * 5.0;
            int verticalOffset = random.nextInt(5) - 2;

            Vec3d rawTarget = monsterPos
                    .add(away.multiply(forwardDistance))
                    .add(sideways.multiply(sidewaysDistance))
                    .add(0.0, verticalOffset, 0.0);

            BlockPos targetPos = BlockPos.ofFloored(rawTarget);
            BlockPos groundPos = findValidGroundPosition(world, targetPos);

            if (groundPos == null) {
                continue;
            }

            if (groundPos.getSquaredDistance(lampPos) <= monster.getBlockPos().getSquaredDistance(lampPos)) {
                continue;
            }

            if (monster.getNavigation().findPathTo(groundPos, 0) == null) {
                continue;
            }

            return Vec3d.ofBottomCenter(groundPos);
        }

        return null;
    }

    private static BlockPos findValidGroundPosition(ServerWorld world, BlockPos targetPos) {
        for (int yOffset = 3; yOffset >= -5; yOffset--) {
            BlockPos feetPos = targetPos.add(0, yOffset, 0);
            BlockPos belowPos = feetPos.down();
            BlockPos headPos = feetPos.up();

            BlockState belowState = world.getBlockState(belowPos);
            BlockState feetState = world.getBlockState(feetPos);
            BlockState headState = world.getBlockState(headPos);

            boolean solidGround = belowState.isSideSolidFullSquare(world, belowPos, Direction.UP);
            boolean feetClear = feetState.getCollisionShape(world, feetPos).isEmpty();
            boolean headClear = headState.getCollisionShape(world, headPos).isEmpty();

            if (solidGround && feetClear && headClear) {
                return feetPos;
            }
        }

        return null;
    }

    private static void pushMonsterAway(HostileEntity monster, BlockPos lampPos) {
        Vec3d lampCenter = Vec3d.ofCenter(lampPos);
        Vec3d monsterPos = monster.getPos();

        Vec3d away = monsterPos.subtract(lampCenter);

        if (away.lengthSquared() < 0.0001) {
            away = new Vec3d(0.0, 0.0, 1.0);
        }

        Vec3d direction = away.normalize();

        monster.getNavigation().stop();

        monster.addVelocity(
                direction.x * 0.28,
                0.03,
                direction.z * 0.28
        );

        monster.velocityModified = true;
    }
}