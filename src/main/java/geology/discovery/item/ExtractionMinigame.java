package geology.discovery.item;

import geology.discovery.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
            int successWidth = getSuccessWidthForAttempt(0);

            ACTIVE_SESSIONS.put(
                    playerId,
                    new ExtractionSession(
                            world.getRegistryKey(),
                            pos,
                            randomSuccessStart(player, successWidth),
                            successWidth,
                            player.getPos()
                    )
            );

            applyExtractionZoom(player);
            return;
        }

        if (!session.worldKey.equals(world.getRegistryKey()) || !session.pos.equals(pos)) {
            int successWidth = getSuccessWidthForAttempt(0);

            ACTIVE_SESSIONS.put(
                    playerId,
                    new ExtractionSession(
                            world.getRegistryKey(),
                            pos,
                            randomSuccessStart(player, successWidth),
                            successWidth,
                            player.getPos()
                    )
            );

            applyExtractionZoom(player);
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

    private static void finishExtraction(PlayerEntity player, ExtractionSession session) {
        if (session.hits == 3) {
            player.sendMessage(Text.literal("Perfect extraction").formatted(Formatting.GREEN), false);
        } else if (session.hits == 2) {
            player.sendMessage(Text.literal("Good extraction").formatted(Formatting.YELLOW), false);
        } else if (session.hits == 1) {
            player.sendMessage(Text.literal("Poor extraction").formatted(Formatting.RED), false);
        } else {
            player.sendMessage(Text.literal("Failed extraction").formatted(Formatting.DARK_RED), false);
        }
    }

    private static class ExtractionSession {
        private final RegistryKey<World> worldKey;
        private final BlockPos pos;
        private final Vec3d startPlayerPos;

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
                int successStart,
                int successWidth,
                Vec3d startPlayerPos
        ) {
            this.worldKey = worldKey;
            this.pos = pos;
            this.startPlayerPos = startPlayerPos;
            setSuccessZone(successStart, successWidth);
        }

        private void setSuccessZone(int successStart, int successWidth) {
            this.successStart = successStart;
            this.successWidth = successWidth;
            this.successEnd = successStart + successWidth - 1;
        }
    }
};