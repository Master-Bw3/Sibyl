package mod.master_bw3

import dev.enjarai.trickster.spell.Pattern
import dev.enjarai.trickster.spell.PatternGlyph
import dev.enjarai.trickster.spell.SpellPart
import mod.master_bw3.pond.CoolerSpellCircleRenderer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.Vec3d
import java.util.function.Function
import kotlin.math.min

object SuggestionRenderer {
    fun renderSuggestionStuff(
        self: CoolerSpellCircleRenderer,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        entry: SpellPart,
        x: Double,
        y: Double,
        size: Double,
        startingAngle: Double,
        delta: Float,
        alphaGetter: Function<Float, Float>,
        normal: Vec3d,
    ) {
        //size -> (float) Math.clamp(1 / (size / context.getScaledWindowHeight() * 3) - 0.2, 0, 1)
        val isDrawing = self.inEditor && self.drawingPart === entry

        val suggestions: List<Pattern> = self.suggestions
        val selectionIndex: Int = self.suggestionSelection

        if (isDrawing && !suggestions.isEmpty() && self.drawingPattern.size > 1) {
            val drawingSet: HashSet<Pattern.PatternEntry> =
                HashSet<Pattern.PatternEntry>(Pattern.from(self.drawingPattern).entries())
            val glyph = PatternGlyph(
                Pattern(
                    suggestions[selectionIndex].entries().stream()
                        .filter { e: Pattern.PatternEntry -> !drawingSet.contains(e) }.toList()
                )
            )

            //pattern
            self._drawGlyph(
                matrices,
                vertexConsumers,
                SpellPart(glyph),
                x,
                y,
                size,
                startingAngle,
                delta,
                alphaGetter,
                normal
            )

            //suggestions
            val textRenderer = MinecraftClient.getInstance().textRenderer

            val height = textRenderer.fontHeight * 1.25f

            matrices.push()
            matrices.translate(self._toLocalSpace(x), self._toLocalSpace(y), 0f)

            val suggestionCount = min(suggestions.size.toDouble(), 5.0).toInt()
            for (i in 0 until suggestionCount) {
                val suggestion = suggestions[i]
                var text: MutableText =
                    PatternGlyph(suggestion).asText().siblings.first().copy().formatted(Formatting.LIGHT_PURPLE)

                var space = 0

                if (i == selectionIndex) {
                    text = text.copy().formatted(Formatting.BOLD)
                    text = Text.literal(">").append(text)
                } else {
                    space = textRenderer.getWidth(">")
                }

                textRenderer.draw(
                    text,
                    (12e14 * size).toFloat() + space,
                    (i * height) - (0.5f * height * suggestionCount),
                    0xffffff,
                    false,
                    matrices.peek().positionMatrix,
                    vertexConsumers,
                    TextRenderer.TextLayerType.NORMAL,
                    0,
                    0xf000f0
                )
            }

            matrices.pop()
        }
    }
}