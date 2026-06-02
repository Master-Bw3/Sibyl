package mod.master_bw3.sibyl.widget

import dev.enjarai.trickster.screen.scribing.CircleSoupState
import dev.enjarai.trickster.spell.PatternGlyph
import dev.enjarai.trickster.spell.SpellView
import dev.enjarai.trickster.spell.trick.Tricks
import io.wispforest.owo.braid.core.Color
import io.wispforest.owo.braid.core.Insets
import io.wispforest.owo.braid.framework.BuildContext
import io.wispforest.owo.braid.framework.proxy.WidgetState
import io.wispforest.owo.braid.framework.widget.StatefulWidget
import io.wispforest.owo.braid.framework.widget.Widget
import io.wispforest.owo.braid.widgets.basic.Box
import io.wispforest.owo.braid.widgets.basic.EmptyWidget
import io.wispforest.owo.braid.widgets.basic.Padding
import io.wispforest.owo.braid.widgets.sharedstate.SharedState
import io.wispforest.owo.braid.widgets.stack.Stack

class SpellInfoSidePanelWidget(val isPondering: Boolean, val focusedSpellView: SpellView?, val hoveredSpellView: SpellView?) : StatefulWidget() {
    override fun createState(): WidgetState<*> = State()


    class State : WidgetState<SpellInfoSidePanelWidget>() {
        override fun build(context: BuildContext?): Widget {
            val pondering = widget().isPondering
            val focusedGlyph = widget().focusedSpellView?.part?.glyph
            val hoveredGlyph = widget().hoveredSpellView?.part?.glyph
            val isDrawing = SharedState.get(context, CircleSoupState::class.java).drawingIn != null

            print(isDrawing)

            val panel = when {
                isDrawing ->
                    SuggestionsWidget()

                pondering && focusedGlyph is PatternGlyph ->
                    Tricks.lookup(focusedGlyph.pattern)
                        ?.let(::TrickInfoWidget)

                !pondering && hoveredGlyph is PatternGlyph ->
                    Tricks.lookup(hoveredGlyph.pattern)
                        ?.let(::TrickInfoWidget)

                else ->
                    null
            } ?: EmptyWidget.INSTANCE

            return Stack(
                Box(Color.BLACK.withA(0.5)),
                Padding(
                    Insets.of(5.0, 0.0, 5.0, 5.0),
                    panel
                )
            )
        }
    }
}