package mod.master_bw3

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW


object SibylClient : ClientModInitializer {
	@JvmField
	var keySelectSuggestion: KeyBinding? = null;

	@JvmField
	var keyNextSuggestion: KeyBinding? = null;

	@JvmField
	var keyPrevSuggestion: KeyBinding? = null;

	override fun onInitializeClient() {
		keySelectSuggestion = KeyBindingHelper.registerKeyBinding(
			KeyBinding(
				"sibyl.keys.gui.select_suggestion",  // The translation key of the keybinding's name
				InputUtil.Type.KEYSYM,  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_ENTER,  // The keycode of the key
				"sibyl.keys.gui" // The translation key of the keybinding's category.
			)
		)

		keyNextSuggestion = KeyBindingHelper.registerKeyBinding(
			KeyBinding(
				"sibyl.keys.gui.next_suggestion",  // The translation key of the keybinding's name
				InputUtil.Type.KEYSYM,  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_S,  // The keycode of the key
				"sibyl.keys.gui" // The translation key of the keybinding's category.
			)
		)

		keyPrevSuggestion = KeyBindingHelper.registerKeyBinding(
			KeyBinding(
				"sibyl.keys.gui.previous_suggestion",  // The translation key of the keybinding's name
				InputUtil.Type.KEYSYM,  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_W,  // The keycode of the key
				"sibyl.keys.gui" // The translation key of the keybinding's category.
			)
		)

		
	}
}