package geology.discovery.worldgen;

import geology.discovery.Geological;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

public class ModWorldGeneration {
    public static final RegistryKey<PlacedFeature> STRANGE_GRANITE_PLACED_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Geological.id("strange_granite"));

    public static final RegistryKey<PlacedFeature> STRANGE_DIORITE_PLACED_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Geological.id("strange_diorite"));

    public static final RegistryKey<PlacedFeature> STRANGE_ANDESITE_PLACED_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Geological.id("strange_andesite"));

    public static final RegistryKey<PlacedFeature> STRANGE_STONE_PLACED_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Geological.id("strange_stone"));

    public static final RegistryKey<PlacedFeature> STRANGE_TUFF_PLACED_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Geological.id("strange_tuff"));

    public static final RegistryKey<PlacedFeature> STRANGE_DEEPSLATE_PLACED_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Geological.id("strange_deepslate"));

    public static final RegistryKey<PlacedFeature> STRANGE_GRAVEL_PLACED_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Geological.id("strange_gravel"));

    public static final RegistryKey<PlacedFeature> STRANGE_SANDSTONE_PLACED_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Geological.id("strange_sandstone"));

    public static final RegistryKey<PlacedFeature> STRANGE_RED_SANDSTONE_PLACED_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Geological.id("strange_red_sandstone"));

    public static final RegistryKey<PlacedFeature> STRANGE_BASALT_PLACED_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Geological.id("strange_basalt"));

    public static void generateModWorldGen() {
        Geological.LOGGER.info("Registering Geological world generation");

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                STRANGE_GRANITE_PLACED_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                STRANGE_DIORITE_PLACED_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                STRANGE_ANDESITE_PLACED_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                STRANGE_STONE_PLACED_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                STRANGE_TUFF_PLACED_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                STRANGE_DEEPSLATE_PLACED_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                STRANGE_GRAVEL_PLACED_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                STRANGE_SANDSTONE_PLACED_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                STRANGE_RED_SANDSTONE_PLACED_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInTheNether(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                STRANGE_BASALT_PLACED_KEY
        );
    }
}