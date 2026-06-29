package geology.discovery.worldgen;

import geology.discovery.Geological;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class ModFeatures {

    public static final Feature<DefaultFeatureConfig> STRANGE_GRANITE =
            Registry.register(
                    Registries.FEATURE,
                    Geological.id("strange_granite"),
                    new StrangeGraniteFeature(DefaultFeatureConfig.CODEC)
            );
    public static final Feature<DefaultFeatureConfig> STRANGE_DIORITE =
            Registry.register(
                    Registries.FEATURE,
                    Geological.id("strange_diorite"),
                    new StrangeDioriteFeature(DefaultFeatureConfig.CODEC)
            );
    public static final Feature<DefaultFeatureConfig> STRANGE_ANDESITE =
            Registry.register(
                    Registries.FEATURE,
                    Geological.id("strange_andesite"),
                    new StrangeAndesiteFeature(DefaultFeatureConfig.CODEC)
            );
    public static final Feature<DefaultFeatureConfig> STRANGE_STONE =
            Registry.register(
                    Registries.FEATURE,
                    Geological.id("strange_stone"),
                    new StrangeStoneFeature(DefaultFeatureConfig.CODEC)
            );
    public static final Feature<DefaultFeatureConfig> STRANGE_TUFF =
            Registry.register(
                    Registries.FEATURE,
                    Geological.id("strange_tuff"),
                    new StrangeTuffFeature(DefaultFeatureConfig.CODEC)
            );

    public static void register() {
        Geological.LOGGER.info("Registering features");
        Geological.LOGGER.info("Feature ID: {}", Registries.FEATURE.getId(STRANGE_GRANITE));
        Geological.LOGGER.info("Feature ID: {}", Registries.FEATURE.getId(STRANGE_DIORITE));
        Geological.LOGGER.info("Feature ID: {}", Registries.FEATURE.getId(STRANGE_ANDESITE));
        Geological.LOGGER.info("Feature ID: {}", Registries.FEATURE.getId(STRANGE_STONE));
        Geological.LOGGER.info("Feature ID: {}", Registries.FEATURE.getId(STRANGE_TUFF));
        }
    }
