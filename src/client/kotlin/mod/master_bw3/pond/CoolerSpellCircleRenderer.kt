package mod.master_bw3.pond

import dev.enjarai.trickster.spell.Pattern
import dev.enjarai.trickster.spell.SpellPart
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3d
import java.util.function.Function
import java.util.function.Supplier

interface CoolerSpellCircleRenderer {
    fun _setSuggestionSupplier(suggestionsGetter: Supplier<List<Pattern>>)

    fun _setSuggestionSelectionSupplier(suggestionIndexGetter: Supplier<Int>)

    fun _drawGlyph(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        spellPart: SpellPart,
        x: Double,
        y: Double,
        size: Double,
        startingAngle: Double,
        delta: Float,
        alphaGetter: Function<Float, Float>,
        normal: Vec3d
    )

    fun _toLocalSpace(size: Double): Float

    val suggestions: List<Pattern>

    val suggestionSelection: Int

    val inEditor: Boolean

    val drawingPart: SpellPart

    val drawingPattern: List<Byte>
}