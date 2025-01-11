package mod.master_bw3

import dev.enjarai.trickster.Trickster
import dev.enjarai.trickster.spell.Pattern
import dev.enjarai.trickster.spell.SpellPart
import dev.enjarai.trickster.spell.trick.Trick
import dev.enjarai.trickster.spell.trick.Tricks
import io.wispforest.lavender.book.BookLoader
import io.wispforest.lavendermd.MarkdownProcessor
import io.wispforest.lavendermd.compiler.OwoUICompiler
import mod.master_bw3.pond.CoolerSpellPartWidget
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object SuggestionLogic {
    val tricksterBook = BookLoader.loadedBooks().first { it.id() == Trickster.id("tome_of_tomfoolery") }


    fun selectPattern(
        widget: CoolerSpellPartWidget,
        part: SpellPart,
        x: Float,
        y: Float,
        size: Float,
        mouseX: Double,
        mouseY: Double,
    ) {
        val drawingPattern = widget.drawingPattern

        var containsSelf = false

        if (drawingPattern != null) {
            val drawn: List<Pattern.PatternEntry> = Pattern.from(drawingPattern).entries()

            widget.suggestions = Tricks.REGISTRY
                .map(Trick::getPattern)
                .filter { pattern: Pattern ->
                    if (drawingPattern.isEmpty() || !pattern.entries().containsAll(drawn)) {
                        return@filter false
                    }
                    if (drawn == pattern.entries()) {
                        containsSelf = true
                        return@filter true
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
        } else {
            widget.suggestions = listOf()
        }

        if (containsSelf && widget.suggestions.size > 1)
            widget.suggestionSelection = 1
        else
            widget.suggestionSelection = 0
    }

    fun getDescription(pattern: Pattern): Text? {
        val id = Tricks.REGISTRY.entrySet.firstOrNull { it.value.pattern == pattern }?.key?.value
            ?: return null

        val entry = tricksterBook.entries()
            .firstOrNull { entry ->
                entry.content.contains("trick-id=$id")
            } ?: return null

        val text = extractDescription(entry.content, id)
            ?: return null

        val processor = MarkdownProcessor.text();
        val formatted = processor.process(text);

        return formatted
    }

    private fun extractDescription(input: String, trickId: Identifier): String? {
        val startTag = "<|glyph@trickster:templates|trick-id=$trickId"
        val startIndex = input.indexOf(startTag)

        if (startIndex == -1) return null


        val descriptionStart = input.indexOf("\n", startIndex)
        if (descriptionStart == -1) return null

        val endIndex = input.indexOf(";;;;;", descriptionStart).takeIf { it != -1 }
            ?: input.indexOf("<|glyph@trickster", descriptionStart)
        if (endIndex == -1) return null

        return input.substring(descriptionStart, endIndex)
            .trim()
            .lines()
            .filterNot { it.trim() == "---" || it.trimStart().startsWith("<|cost-rule") }
            .joinToString("\n")
    }
}