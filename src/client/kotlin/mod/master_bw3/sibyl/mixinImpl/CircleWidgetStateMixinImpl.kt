package mod.master_bw3.sibyl.mixinImpl

import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import dev.enjarai.trickster.spell.Pattern
import io.wispforest.owo.braid.widgets.basic.CustomDraw
import io.wispforest.owo.braid.widgets.basic.Sized
import io.wispforest.owo.braid.widgets.stack.Stack
import mod.master_bw3.sibyl.SuggestionRenderer.renderSuggestionGlyph

object CircleWidgetStateMixinImpl {
    @JvmStatic
    fun addSuggestionGlyph(suggestion: Pattern, width: Double, height: Double, child: Stack, original: Operation<Sized>): Sized {
        val inner = Stack(
            CustomDraw(renderSuggestionGlyph(suggestion)),
                *(child.children).toTypedArray()
        )

        return original.call(width, height, inner)
    }
}