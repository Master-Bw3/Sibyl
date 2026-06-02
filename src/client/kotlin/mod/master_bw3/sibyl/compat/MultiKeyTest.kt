@file:JvmName("MultiKeyTest")

package mod.master_bw3.sibyl.compat

import mod.master_bw3.sibyl.SibylClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import us.kenny.ModifierManager

fun multiKeyTest(binding: KeyBinding, keyCode: Int): Boolean {
   return ModifierManager.shouldActivate(binding.getTranslationKey(), InputUtil.Type.KEYSYM.createFromCode(keyCode))
}