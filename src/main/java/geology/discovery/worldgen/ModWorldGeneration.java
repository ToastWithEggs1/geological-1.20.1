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
    }
}