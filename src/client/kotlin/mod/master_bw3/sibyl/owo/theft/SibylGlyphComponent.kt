package mod.master_bw3.sibyl.owo.theft

import dev.enjarai.trickster.Trickster
import dev.enjarai.trickster.render.CircleRenderer
import dev.enjarai.trickster.spell.Pattern
import dev.enjarai.trickster.spell.trick.Trick
import dev.enjarai.trickster.spell.trick.Tricks
import io.wispforest.owo.ui.core.Color
import io.wispforest.owo.ui.base.BaseComponent
import io.wispforest.owo.ui.core.OwoUIDrawContext
import io.wispforest.owo.ui.core.Sizing
import io.wispforest.owo.ui.parsing.UIModelParsingException
import io.wispforest.owo.ui.parsing.UIParsing
import org.w3c.dom.Element
import java.util.*
import java.util.stream.IntStream


class SibylGlyphComponent(var pattern: Pattern, var size: Int, val color: Color) : BaseComponent() {
    var patternList: MutableList<Int?> = pattern
        .entries().stream()
        .flatMapToInt { e: Pattern.PatternEntry? -> IntStream.of(e!!.p1().toInt(), e.p2().toInt()) }
        .distinct()
        .boxed()
        .toList()

    constructor(trick: Trick<*>, size: Int) : this(trick.getPattern(), size, Color.BLACK)

    override fun draw(context: OwoUIDrawContext, mouseX: Int, mouseY: Int, partialTicks: Float, delta: Float) {
        val patternSize = size / 2

        val dotTerminalStatus = pattern.dotTerminalStatus()

        for (i in 0..8) {
            val pos = CircleRenderer.getPatternDotPosition(
                (x + patternSize + 4).toFloat(),
                (y + patternSize + 4).toFloat(),
                i,
                patternSize.toFloat()
            )

            val isLinked = patternList.contains(i)
            val dotSize = 1

            var r = color.red
            var g = color.green
            var b = color.blue

            if (dotTerminalStatus[i] && Trickster.CONFIG.dotEmphasis()) {
                r = Trickster.CONFIG.dotEmphasisColor().red()
                g = Trickster.CONFIG.dotEmphasisColor().green()
                b = Trickster.CONFIG.dotEmphasisColor().blue()
            }

            CircleRenderer.drawFlatPolygon(
                context.getMatrices(), context.getVertexConsumers(),
                pos.x - dotSize, pos.y - dotSize,
                pos.x - dotSize, pos.y + dotSize,
                pos.x + dotSize, pos.y + dotSize,
                pos.x + dotSize, pos.y - dotSize,
                0f, r, g, b, if (isLinked) 0.9f else 0.5f, true
            )
        }

        for (line in pattern.entries()) {
            val r = color.red
            val g = color.green
            val b = color.blue

            val now = CircleRenderer.getPatternDotPosition(
                (x + patternSize + 4).toFloat(),
                (y + patternSize + 4).toFloat(),
                line.p1().toInt(),
                patternSize.toFloat()
            )
            val last = CircleRenderer.getPatternDotPosition(
                (x + patternSize + 4).toFloat(),
                (y + patternSize + 4).toFloat(),
                line.p2().toInt(),
                patternSize.toFloat()
            )
            CircleRenderer.drawGlyphLine(
                context.getMatrices(), context.getVertexConsumers(), last, now,
                1f, false, 1f, r, g, b, 0.9f,
                false, 0
            )
        }
    }

    override fun determineHorizontalContentSize(sizing: Sizing?): Int {
        return size + 8
    }

    override fun determineVerticalContentSize(sizing: Sizing?): Int {
        return size + 8
    }

    companion object {
        fun parseTrick(element: Element): SibylGlyphComponent {
            UIParsing.expectAttributes(element, "trick-id")
            UIParsing.expectAttributes(element, "size")

            val trickId = UIParsing.parseIdentifier(element.getAttributeNode("trick-id"))
            val trick = Tricks.REGISTRY.get(trickId)

            if (trick == null) {
                throw UIModelParsingException("Not a valid trick: " + trickId)
            }

            val size = UIParsing.parseUnsignedInt(element.getAttributeNode("size"))

            return SibylGlyphComponent(trick, size)
        }

        fun parseList(element: Element): SibylGlyphComponent {
            UIParsing.expectAttributes(element, "pattern")
            UIParsing.expectAttributes(element, "size")

            val patternString = element.getAttributeNode("pattern").getTextContent()

            val pattern = Pattern.from(
                Arrays.stream<String>(patternString.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                    .map<Byte?> { s: String? -> s!!.toByte(10) }.toList()
            )

            val size = UIParsing.parseUnsignedInt(element.getAttributeNode("size"))

            return SibylGlyphComponent(pattern, size, Color.WHITE)
        }
    }
}