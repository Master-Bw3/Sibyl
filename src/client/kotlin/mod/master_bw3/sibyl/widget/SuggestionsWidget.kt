package mod.master_bw3.sibyl.widget

import dev.enjarai.trickster.spell.PatternGlyph
import dev.enjarai.trickster.spell.trick.Tricks
import io.wispforest.owo.braid.core.Alignment
import io.wispforest.owo.braid.core.Color
import io.wispforest.owo.braid.core.Insets
import io.wispforest.owo.braid.framework.BuildContext
import io.wispforest.owo.braid.framework.proxy.WidgetState
import io.wispforest.owo.braid.framework.widget.Key
import io.wispforest.owo.braid.framework.widget.StatefulWidget
import io.wispforest.owo.braid.framework.widget.Widget
import io.wispforest.owo.braid.widgets.basic.Align
import io.wispforest.owo.braid.widgets.basic.Box
import io.wispforest.owo.braid.widgets.basic.Padding
import io.wispforest.owo.braid.widgets.flex.Column
import io.wispforest.owo.braid.widgets.label.Label
import io.wispforest.owo.braid.widgets.owoui.OwoUIWidget
import io.wispforest.owo.braid.widgets.sharedstate.SharedState
import io.wispforest.owo.braid.widgets.stack.Stack
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.Sizing
import mod.master_bw3.sibyl.SuggestionState
import mod.master_bw3.sibyl.owo.theft.SibylTrickOverviewComponent
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import kotlin.math.max

class SuggestionsWidget : StatefulWidget() {
    override fun createState(): WidgetState<*> = State()

    class State : WidgetState<SuggestionsWidget>() {
        override fun build(context: BuildContext): Widget {
            val suggestionState = SharedState.get(context, SuggestionState::class.java)
            val suggestions = suggestionState.suggestions
            val suggestionIndex = suggestionState.suggestionIndex

            val start = max(suggestionIndex - 5, 0)
            val end = start + 6

            val suggestionTrickNames = mutableListOf<Text>()
            var suggestionComponent: Component? = null

            var selectedTrickName = ""

            for (i in start until end) {
                if (suggestions.size <= i) break

                val suggestion = suggestions[i]
                val trick = Tricks.lookup(suggestion)!!

                val color = if (i == suggestionIndex) Formatting.WHITE else Formatting.GRAY
                suggestionTrickNames.add(PatternGlyph(suggestion).asText().siblings.first().copy().formatted(color))

                if (i == suggestionIndex) {
                    suggestionComponent = SibylTrickOverviewComponent.Companion.of(
                        trick, null,
                        Identifier.of("sibyl", "textures/gui/white_book.png")
                    )

                    selectedTrickName = trick.name.string
                }
            }


            val widgets = suggestionTrickNames.map<Text, Widget>{
                Padding(Insets.vertical(2.0), Align(Alignment.LEFT, Label(it)))
            }.toMutableList()

            suggestionComponent?.let {
                widgets.addFirst(
                    Padding(
                        Insets.vertical(5.0),
                        OwoUIWidget {
                            Containers.stack(Sizing.content(), Sizing.content())
                                .child(suggestionComponent)
                        }.key(Key.of(selectedTrickName))
                    )
                )
            }

//            widgets.add(MessageButton(
//                Text.literal("inspector"),
//                { AppState.of(context).activateInspector( )}))

            return Column(widgets)

        }
    }
}