package geology.discovery;

import geology.discovery.block.ModBlocks;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import geology.discovery.item.ModItemGroups;
import geology.discovery.worldgen.ModFeatures;
import geology.discovery.worldgen.ModWorldGeneration;
import geology.discovery.item.ModItems;

public class Geological implements ModInitializer {
	public static final String MOD_ID = "geological";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.registerModBlocks();
		ModItemGroups.registerItemGroups();
		ModFeatures.register();
		ModItems.registerModItems();
		ModWorldGeneration.generateModWorldGen();
		LOGGER.info("Hello Fabric world!");
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
