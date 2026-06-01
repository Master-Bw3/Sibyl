package mod.master_bw3.sibyl

import dev.enjarai.trickster.spell.PatternGlyph
import dev.enjarai.trickster.spell.trick.Tricks
import io.wispforest.owo.braid.core.Color
import io.wispforest.owo.braid.framework.BuildContext
import io.wispforest.owo.braid.framework.proxy.WidgetState
import io.wispforest.owo.braid.framework.widget.StatefulWidget
import io.wispforest.owo.braid.framework.widget.Widget
import io.wispforest.owo.braid.widgets.basic.Box
import io.wispforest.owo.braid.widgets.flex.Column
import io.wispforest.owo.braid.widgets.label.Label
import io.wispforest.owo.braid.widgets.owoui.OwoUIWidget
import io.wispforest.owo.braid.widgets.sharedstate.SharedState
import io.wispforest.owo.braid.widgets.stack.Stack
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.core.Sizing
import mod.master_bw3.sibyl.owo.theft.SibylTrickOverviewComponent
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import kotlin.math.max

class SuggestionsWidget : StatefulWidget() {
    override fun createState(): WidgetState<*> = State()

//    val uiModel = BaseUIModelScreen.DataSource.asset(Identifier.of("trickster", "templates")).get()

    class State : WidgetState<SuggestionsWidget>() {
        override fun build(context: BuildContext): Widget {
            val suggestionState = SharedState.get(context, SuggestionState::class.java)
            val suggestions = suggestionState.suggestions
            val suggestionIndex = suggestionState.suggestionIndex

            val start = max(suggestionIndex - 5, 0)
            val end = start + 6

            val suggestionTrickNames = mutableListOf<Text>()
            var suggestionComponent: Component? = null

            for (i in start until end) {
                if (suggestions.size <= i) break

                val suggestion = suggestions[i]
                val trick = Tricks.lookup(suggestion)!!

                val color = if (i == suggestionIndex) Formatting.LIGHT_PURPLE else Formatting.DARK_PURPLE
                suggestionTrickNames.add(PatternGlyph(suggestion).asText().siblings.first().copy().formatted(color))

                if (i == suggestionIndex) {
//                    suggestionComponent = widget().uiModel?.expandTemplate(
//                        Component::class.java,
//                        "trick",
//                        mapOf(
//                            "trick-id" to trick.toString(),
//                            "book-texture" to "trickster:textures/gui/white_book.png"
//                        )
//                    )
                    suggestionComponent = SibylTrickOverviewComponent.of(trick, null,
                        Identifier.of("sibyl", "textures/gui/white_book.png"))
                }
            }

            val widgets = suggestionTrickNames.map<Text, Widget>(::Label).toMutableList()

            suggestionComponent?.let {
                widgets.addFirst(OwoUIWidget {
                    Containers.verticalFlow(Sizing.content(), Sizing.content())
                        .child(it)
                })
            }

            return Stack(
                Box(Color.BLACK.withA(0.5)),
                Column(widgets)
            )
        }
    }
}