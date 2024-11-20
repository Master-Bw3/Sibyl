package mod.master_bw3

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object Sibyl : ModInitializer {
    @JvmField
	val logger = LoggerFactory.getLogger("sibyl")

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
	}
}