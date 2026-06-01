package mod.master_bw3.sibyl

import dev.enjarai.trickster.spell.Pattern
import dev.enjarai.trickster.spell.trick.Trick
import dev.enjarai.trickster.spell.trick.Tricks

object SuggestionLogic {
    @JvmStatic
    fun getSuggestions(
        drawingPattern: List<Byte>
    ): List<Pattern> {
            val drawn: List<Pattern.PatternEntry> = Pattern.from(drawingPattern).entries()

            return Tricks.REGISTRY
                .map(Trick<*>::getPattern)
                .filter { pattern: Pattern ->
                    if (drawingPattern.isEmpty() || drawn == pattern.entries() || !pattern.entries()
                            .containsAll(drawn)
                    ) {
                        return@filter false
                    }

                    val cutPattern =
                        pattern.entries().filter { e: Pattern.PatternEntry -> !drawn.contains(e) }.toList()

                    if (!GraphConnected.isConnected(cutPattern)) {
                        return@filter false
                    }

                    //get ordinal of startVertices
                    val ordinals = mutableMapOf<Byte, Int>()
                    for (patternEntry in cutPattern) {
                        ordinals.merge(patternEntry.p1(), 1, Int::plus)
                        ordinals.merge(patternEntry.p2(), 1, Int::plus)
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

    }
}