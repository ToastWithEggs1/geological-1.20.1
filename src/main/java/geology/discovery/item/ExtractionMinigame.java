package geology.discovery.item;

import geology.discovery.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ExtractionMinigame {
    private static final int BAR_LENGTH = 11;

    private static final int FIRST_ATTEMPT_SUCCESS_WIDTH = 4;
    private static final int SECOND_ATTEMPT_SUCCESS_WIDTH = 3;
    private static final int THIRD_ATTEMPT_SUCCESS_WIDTH = 2;

    private static final int TICKS_PER_MOVE = 1;
    private static final int MAX_ATTEMPTS = 3;

    private static final int EXTRACTION_SLOWNESS_DURATION = 8;
    private static final int EXTRACTION_SLOWNESS_AMPLIFIER = 3;

    private static final Map<UUID, ExtractionSession> ACTIVE_SESSIONS = new HashMap<>();

    public static boolean isStrangeBlock(Block block) {
        return block == ModBlocks.STRANGE_STONE
                || block == ModBlocks.STRANGE_GRANITE
                || block == ModBlocks.STRANGE_DIORITE
                || block == ModBlocks.STRANGE_ANDESITE
                || block == ModBlocks.STRANGE_DEEPSLATE
                || block == ModBlocks.STRANGE_TUFF
                || block == ModBlocks.STRANGE_BASALT
                || block == ModBlocks.STRANGE_SANDSTONE
                || block == ModBlocks.STRANGE_RED_SANDSTONE;
    }

    public static void handleHammerClick(ServerPlayerEntity player, World world, BlockPos pos) {
        UUID playerId = player.getUuid();

        ExtractionSession session = ACTIVE_SESSIONS.get(playerId);

        if (session == null) {
            startNewSession(player, world, pos);
            return;
        }

        if (!session.worldKey.equals(world.getRegistryKey()) || !session.pos.equals(pos)) {
            startNewSession(player, world, pos);
            return;
        }

        boolean success = session.markerPosition >= session.successStart
                && session.markerPosition <= session.successEnd;

        session.attempts++;

        if (success) {
            session.hits++;
            player.sendMessage(Text.literal("HIT!").formatted(Formatting.GREEN, Formatting.BOLD), true);
        } else {
            player.sendMessage(Text.literal("MISS!").formatted(Formatting.RED, Formatting.BOLD), true);
        }

        if (session.attempts >= MAX_ATTEMPTS) {
            clearExtractionEffects(player);
            finishExtraction(player, session);
            ACTIVE_SESSIONS.remove(playerId);
            return;
        }

        session.markerPosition = 0;
        session.direction = 1;
        session.tickCounter = 0;

        int nextSuccessWidth = getSuccessWidthForAttempt(session.attempts);
        session.setSuccessZone(randomSuccessStart(player, nextSuccessWidth), nextSuccessWidth);
    }

    private static void startNewSession(ServerPlayerEntity player, World world, BlockPos pos) {
        int successWidth = getSuccessWidthForAttempt(0);
        Block strangeBlock = world.getBlockState(pos).getBlock();

        ACTIVE_SESSIONS.put(
                player.getUuid(),
                new ExtractionSession(
                        world.getRegistryKey(),
                        pos,
                        strangeBlock,
                        randomSuccessStart(player, successWidth),
                        successWidth,
                        player.getPos(),
                        getHammerHand(player)
                )
        );

        applyExtractionZoom(player);
    }

    public static void tick(MinecraftServer server) {
        Iterator<Map.Entry<UUID, ExtractionSession>> iterator = ACTIVE_SESSIONS.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, ExtractionSession> entry = iterator.next();
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            ExtractionSession session = entry.getValue();

            if (player == null) {
                iterator.remove();
                continue;
            }

            if (!player.getWorld().getRegistryKey().equals(session.worldKey)) {
                clearExtractionEffects(player);
                iterator.remove();
                continue;
            }

            if (player.getPos().squaredDistanceTo(session.startPlayerPos) > 0.0001) {
                clearExtractionEffects(player);
                iterator.remove();
                continue;
            }

            applyExtractionZoom(player);

            session.tickCounter++;

            if (session.tickCounter >= TICKS_PER_MOVE) {
                session.tickCounter = 0;

                session.markerPosition += session.direction;

                if (session.markerPosition >= BAR_LENGTH - 1) {
                    session.markerPosition = BAR_LENGTH - 1;
                    session.direction = -1;
                } else if (session.markerPosition <= 0) {
                    session.markerPosition = 0;
                    session.direction = 1;
                }
            }

            player.sendMessage(createBarText(session), true);
        }
    }

    private static void finishExtraction(ServerPlayerEntity player, ExtractionSession session) {
        World world = player.getWorld();
        Block currentBlock = world.getBlockState(session.pos).getBlock();

        if (currentBlock != session.strangeBlock || !isStrangeBlock(currentBlock)) {
            return;
        }

        MineralDrop mineralDrop = chooseMineralDrop(world, session.pos, currentBlock);

        boolean fragmented = shouldFragment(player, session.hits);

        if (fragmented) {
            dropFragments(world, session.pos, mineralDrop);
            removeStrangeBlock(world, session.pos);
            playFragmentedSound(world, session.pos);
            player.sendMessage(Text.literal("Fragmented").formatted(Formatting.RED), false);
        } else {
            ExtractionQuality quality = getQualityFromHits(session.hits);

            dropSpecimen(world, session.pos, mineralDrop, quality);
            replaceWithVanillaCounterpart(world, session.pos, currentBlock);
            playExtractionSound(world, session.pos);
            player.sendMessage(Text.literal(quality.displayName).formatted(quality.color), false);
        }

        damageHammer(player, session);
    }

    private static boolean shouldFragment(ServerPlayerEntity player, int hits) {
        if (hits <= 0) {
            return true;
        }

        double fractureChance = getFractureChance(hits);
        return player.getRandom().nextDouble() < fractureChance;
    }

    private static double getFractureChance(int hits) {
        if (hits >= 3) {
            return 0.03;
        } else if (hits == 2) {
            return 0.09;
        } else if (hits == 1) {
            return 0.18;
        }

        return 1.0;
    }

    private static ExtractionQuality getQualityFromHits(int hits) {
        if (hits >= 3) {
            return ExtractionQuality.PRISTINE;
        } else if (hits == 2) {
            return ExtractionQuality.INTACT;
        } else {
            return ExtractionQuality.CHIPPED;
        }
    }

    private static MineralDrop chooseMineralDrop(World world, BlockPos pos, Block strangeBlock) {
        // Temporary placeholder:
        // Later this can become rock-based, biome-based, or both.
        return new MineralDrop(ModItems.PYRITE, ModItems.PYRITE_FRAGMENTS);
    }

    private static void dropSpecimen(World world, BlockPos pos, MineralDrop mineralDrop, ExtractionQuality quality) {
        ItemStack stack = new ItemStack(mineralDrop.specimenItem);
        stack.getOrCreateNbt().putString("Quality", quality.nbtValue);

        Block.dropStack(world, pos, stack);
    }

    private static void dropFragments(World world, BlockPos pos, MineralDrop mineralDrop) {
        ItemStack stack = new ItemStack(mineralDrop.fragmentsItem);
        Block.dropStack(world, pos, stack);
    }

    private static void replaceWithVanillaCounterpart(World world, BlockPos pos, Block strangeBlock) {
        Block vanillaBlock = getVanillaCounterpart(strangeBlock);
        world.setBlockState(pos, vanillaBlock.getDefaultState(), 3);
    }

    private static void removeStrangeBlock(World world, BlockPos pos) {
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
    }

    private static Block getVanillaCounterpart(Block strangeBlock) {
        if (strangeBlock == ModBlocks.STRANGE_STONE) {
            return Blocks.STONE;
        } else if (strangeBlock == ModBlocks.STRANGE_GRANITE) {
            return Blocks.GRANITE;
        } else if (strangeBlock == ModBlocks.STRANGE_DIORITE) {
            return Blocks.DIORITE;
        } else if (strangeBlock == ModBlocks.STRANGE_ANDESITE) {
            return Blocks.ANDESITE;
        } else if (strangeBlock == ModBlocks.STRANGE_DEEPSLATE) {
            return Blocks.DEEPSLATE;
        } else if (strangeBlock == ModBlocks.STRANGE_TUFF) {
            return Blocks.TUFF;
        } else if (strangeBlock == ModBlocks.STRANGE_BASALT) {
            return Blocks.BASALT;
        } else if (strangeBlock == ModBlocks.STRANGE_SANDSTONE) {
            return Blocks.SANDSTONE;
        } else if (strangeBlock == ModBlocks.STRANGE_RED_SANDSTONE) {
            return Blocks.RED_SANDSTONE;
        }

        return Blocks.STONE;
    }

    private static void playExtractionSound(World world, BlockPos pos) {
        world.playSound(
                null,
                pos,
                SoundEvents.BLOCK_STONE_HIT,
                SoundCategory.BLOCKS,
                1.0f,
                0.75f
        );
    }

    private static void playFragmentedSound(World world, BlockPos pos) {
        world.playSound(
                null,
                pos,
                SoundEvents.BLOCK_STONE_BREAK,
                SoundCategory.BLOCKS,
                1.0f,
                0.7f
        );
    }

    private static void damageHammer(ServerPlayerEntity player, ExtractionSession session) {
        Hand hand = session.hand;
        ItemStack stack = player.getStackInHand(hand);

        if (!stack.isOf(ModItems.GEOLOGICAL_HAMMER)) {
            hand = getHammerHand(player);
            stack = player.getStackInHand(hand);
        }

        if (!stack.isOf(ModItems.GEOLOGICAL_HAMMER)) {
            return;
        }

        Hand finalHand = hand;
        stack.damage(1, player, brokenPlayer -> brokenPlayer.sendToolBreakStatus(finalHand));
    }

    private static Hand getHammerHand(ServerPlayerEntity player) {
        if (player.getMainHandStack().isOf(ModItems.GEOLOGICAL_HAMMER)) {
            return Hand.MAIN_HAND;
        }

        return Hand.OFF_HAND;
    }

    private static void applyExtractionZoom(ServerPlayerEntity player) {
        player.addStatusEffect(
                new StatusEffectInstance(
                        StatusEffects.SLOWNESS,
                        EXTRACTION_SLOWNESS_DURATION,
                        EXTRACTION_SLOWNESS_AMPLIFIER,
                        false,
                        false,
                        false
                )
        );
    }

    private static void clearExtractionEffects(ServerPlayerEntity player) {
        player.removeStatusEffect(StatusEffects.SLOWNESS);
        player.sendMessage(Text.literal(""), true);
    }

    private static int getSuccessWidthForAttempt(int attempts) {
        if (attempts == 0) {
            return FIRST_ATTEMPT_SUCCESS_WIDTH;
        } else if (attempts == 1) {
            return SECOND_ATTEMPT_SUCCESS_WIDTH;
        } else {
            return THIRD_ATTEMPT_SUCCESS_WIDTH;
        }
    }

    private static int randomSuccessStart(ServerPlayerEntity player, int successWidth) {
        return 1 + player.getRandom().nextInt(BAR_LENGTH - successWidth - 1);
    }

    private static MutableText createBarText(ExtractionSession session) {
        MutableText text = Text.literal("[").formatted(Formatting.DARK_GRAY);

        for (int i = 0; i < BAR_LENGTH; i++) {
            if (i == session.markerPosition) {
                text.append(Text.literal("o").formatted(Formatting.GOLD, Formatting.BOLD));
            } else if (i >= session.successStart && i <= session.successEnd) {
                text.append(Text.literal("=").formatted(Formatting.GREEN));
            } else {
                text.append(Text.literal("=").formatted(Formatting.DARK_GRAY));
            }
        }

        text.append(Text.literal("]").formatted(Formatting.DARK_GRAY));

        return text;
    }

    private enum ExtractionQuality {
        PRISTINE("Pristine", "pristine", Formatting.AQUA),
        INTACT("Intact", "intact", Formatting.GREEN),
        CHIPPED("Chipped", "chipped", Formatting.GOLD);

        private final String displayName;
        private final String nbtValue;
        private final Formatting color;

        ExtractionQuality(String displayName, String nbtValue, Formatting color) {
            this.displayName = displayName;
            this.nbtValue = nbtValue;
            this.color = color;
        }
    }

    private static class MineralDrop {
        private final Item specimenItem;
        private final Item fragmentsItem;

        private MineralDrop(Item specimenItem, Item fragmentsItem) {
            this.specimenItem = specimenItem;
            this.fragmentsItem = fragmentsItem;
        }
    }

    private static class ExtractionSession {
        private final RegistryKey<World> worldKey;
        private final BlockPos pos;
        private final Block strangeBlock;
        private final Vec3d startPlayerPos;
        private final Hand hand;

        private int markerPosition = 0;
        private int direction = 1;
        private int tickCounter = 0;
        private int attempts = 0;
        private int hits = 0;

        private int successStart;
        private int successEnd;
        private int successWidth;

        private ExtractionSession(
                RegistryKey<World> worldKey,
                BlockPos pos,
                Block strangeBlock,
                int successStart,
                int successWidth,
                Vec3d startPlayerPos,
                Hand hand
        ) {
            this.worldKey = worldKey;
            this.pos = pos;
            this.strangeBlock = strangeBlock;
            this.startPlayerPos = startPlayerPos;
            this.hand = hand;
            setSuccessZone(successStart, successWidth);
        }

        private void setSuccessZone(int successStart, int successWidth) {
            this.successStart = successStart;
            this.successWidth = successWidth;
            this.successEnd = successStart + successWidth - 1;
        }
    }
}