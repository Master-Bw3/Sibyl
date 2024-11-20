package mod.master_bw3

import dev.enjarai.trickster.spell.Pattern
import dev.enjarai.trickster.spell.SpellPart
import dev.enjarai.trickster.spell.trick.Trick
import dev.enjarai.trickster.spell.trick.Tricks
import mod.master_bw3.pond.CoolerSpellPartWidget
import net.minecraft.registry.RegistryKey
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object SuggestionLogic {
    fun selectPattern(
        self: CoolerSpellPartWidget,
        part: SpellPart,
        x: Float,
        y: Float,
        size: Float,
        mouseX: Double,
        mouseY: Double,
    ) {
        self.suggestionSelection = 0

        val drawingPattern = self.drawingPattern

        if (drawingPattern != null) {
            val drawn: List<Pattern.PatternEntry> = Pattern.from(drawingPattern).entries()

            self.suggestions = Tricks.REGISTRY
                .map(Trick::getPattern)
                .filter { pattern: Pattern ->
                    if (drawingPattern.isEmpty() || drawn == pattern.entries() || !pattern.entries().containsAll(drawn)) {
                        return@filter false
                    }

                    val cutPattern =
                        pattern.entries().filter { e: Pattern.PatternEntry -> !drawn.contains(e) }.toList()

                    if (!GraphConnected.isConnected(cutPattern)) {
                        return@filter false
                    }

                    //get ordinal of startVertices
                    val ordinals = HashMap<Byte, Int>()
                    for (patternEntry in cutPattern) {
                        ordinals[patternEntry.p1()] = ordinals.getOrDefault(patternEntry.p1(), 0) + 1
                        ordinals[patternEntry.p2()] = ordinals.getOrDefault(patternEntry.p2(), 0) + 1
                    }

                    val startVertices: MutableList<Byte> = ArrayList()

                    if (ordinals.values.all { o: Int -> o % 2 == 0 }) {
                        //all even ordinal
                        startVertices.addAll(ordinals.keys)
                    } else {
                        //has odd ordinal
                        ordinals.entries.filter { e: Map.Entry<Byte, Int> -> e.value % 2 == 1 }
                            .forEach { e: Map.Entry<Byte, Int> -> startVertices.add(e.key) }

                        //must have 0 or 2 odd vertices
                        if (startVertices.size != 2) {
                            return@filter false
                        }
                    }

                    return@filter startVertices.contains(drawingPattern.last())
                }
                .sortedBy { pattern: Pattern -> pattern.entries().size }
                .toList()
        } else {
            self.suggestions = listOf()
        }
    }
}