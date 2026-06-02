package mod.master_bw3.sibyl.widget

import dev.enjarai.trickster.spell.PatternGlyph
import dev.enjarai.trickster.spell.trick.Trick
import dev.enjarai.trickster.spell.trick.Tricks
import io.wispforest.owo.braid.core.Alignment
import io.wispforest.owo.braid.core.Insets
import io.wispforest.owo.braid.framework.BuildContext
import io.wispforest.owo.braid.framework.proxy.WidgetState
import io.wispforest.owo.braid.framework.widget.StatefulWidget
import io.wispforest.owo.braid.framework.widget.Widget
import io.wispforest.owo.braid.widgets.basic.Align
import io.wispforest.owo.braid.widgets.basic.EmptyWidget
import io.wispforest.owo.braid.widgets.basic.Padding
import io.wispforest.owo.braid.widgets.flex.Column
import io.wispforest.owo.braid.widgets.label.Label
import io.wispforest.owo.braid.widgets.sharedstate.SharedState
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.math.max

class SuggestionsWidget : StatefulWidget() {
    override fun createState(): WidgetState<*> = State()

    class State : WidgetState<SuggestionsWidget>() {
        override fun build(context: BuildContext): Widget {
            val suggestionState = SharedState.get(context, SibylEditorState::class.java)
            val suggestions = suggestionState.suggestions
            val suggestionIndex = suggestionState.suggestionIndex

            val start = max(suggestionIndex - 5, 0)
            val end = start + 6

            val suggestionTrickNames = mutableListOf<Text>()

            var selectedTrick: Trick<*>? = null

            for (i in start until end) {
                if (suggestions.size <= i) break

                val suggestion = suggestions[i]
                val trick = Tricks.lookup(suggestion)!!

                val color = if (i == suggestionIndex) Formatting.LIGHT_PURPLE else Formatting.DARK_PURPLE
                suggestionTrickNames.add(PatternGlyph(suggestion).asText().siblings.first().copy().formatted(color))

                if (i == suggestionIndex) {
                    selectedTrick = trick
                }
            }


            val widgets = suggestionTrickNames.map<Text, Widget> {
                Padding(Insets.vertical(2.0), Align(Alignment.LEFT, Label(it)))
            }.toMutableList()

            val trickWidget = if (selectedTrick != null) TrickInfoWidget(selectedTrick) else EmptyWidget.INSTANCE

            return Column(
                trickWidget,
                Column(widgets)
            )
        }
    }
}