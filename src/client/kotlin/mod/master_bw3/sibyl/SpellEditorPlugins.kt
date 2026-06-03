package mod.master_bw3.sibyl

import dev.enjarai.trickster.screen.scribing.CircleSoupState
import dev.enjarai.trickster.screen.scribing.CircleWidget
import dev.enjarai.trickster.api.event.CircleSoupWidgetEvents
import dev.enjarai.trickster.api.event.CircleWidgetEvents
import dev.enjarai.trickster.spell.Pattern
import io.wispforest.owo.braid.core.Color
import io.wispforest.owo.braid.widgets.basic.Box
import io.wispforest.owo.braid.widgets.basic.CustomDraw
import io.wispforest.owo.braid.widgets.basic.Sized
import io.wispforest.owo.braid.widgets.basic.Transform
import io.wispforest.owo.braid.widgets.flex.Row
import io.wispforest.owo.braid.widgets.focus.Focusable
import io.wispforest.owo.braid.widgets.sharedstate.SharedState
import mod.master_bw3.sibyl.SuggestionRenderer.renderSuggestionGlyph
import mod.master_bw3.sibyl.compat.multiKeyTest
import mod.master_bw3.sibyl.mixin.client.CircleWidgetAccessorMixin
import mod.master_bw3.sibyl.widget.SibylEditorState
import mod.master_bw3.sibyl.widget.SpellInfoSidePanelWidget
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import org.joml.Matrix4f
import kotlin.math.max
import kotlin.math.min

internal fun registerSpellEditorFunctions() {

    // add shared state used by sidebar
    CircleSoupWidgetEvents.ADD_SHARABLE_STATE.register { shareableStates -> shareableStates.accept(::SibylEditorState) }

    // add spell info sidebar
    CircleSoupWidgetEvents.ADD_OVERLAY.register { state, context, widgetAdder ->
        if (state.isMutable) {
            val screenWidth = MinecraftClient.getInstance().currentScreen!!.width
            val screenHeight = MinecraftClient.getInstance().currentScreen!!.height

            widgetAdder.accept(
                Transform(
                    Matrix4f().translate(0f, 0f, 100f),
                    Row(
                        Sized(
                            screenWidth * 0.25,
                            screenHeight.toDouble(),
                            SpellInfoSidePanelWidget(state.primaryPonder != null, state.primaryPonder)
                        ),
                        Transform(
                            Matrix4f().translate(-1f, 0f, 0f), Sized(
                                1, null,
                                Box(Color.values(0.6, 0.6, 0.6))
                            )
                        )
                    )
                )
            )
        }
    }

    // update suggestions
    CircleWidgetEvents.MOUSE_MOVE.register { state, context, x, y ->
        val prevDrawingPattern = SharedState.get(context, SibylEditorState::class.java).drawingPattern
        val drawingPattern = state.drawingPattern

        if (drawingPattern != prevDrawingPattern) {
            SharedState.set(context, SibylEditorState::class.java) { sharedState ->
                sharedState.drawingPattern = drawingPattern
                sharedState.suggestionIndex = 0

                if (drawingPattern != null && drawingPattern.size >= 2) {
                    sharedState.suggestions = SuggestionLogic.getSuggestions(drawingPattern)
                }
            }
        }

        val notDrawing = SharedState.get(context, CircleSoupState::class.java).drawingIn == null

        if (notDrawing && state.isMouseInside && Focusable.isFocused(context)) {
            SharedState.set(context, SibylEditorState::class.java) { sharedState ->
                sharedState.hoveredSpellView = state.partView
            }
        }
    }

    CircleWidgetEvents.MOUSE_LEAVE.register { state, context ->
        val hoveredSpellView = SharedState.get(context, SibylEditorState::class.java).hoveredSpellView

        if (state.partView === hoveredSpellView) {
            SharedState.set(context, SibylEditorState::class.java) { sharedState ->
                sharedState.hoveredSpellView = null
            }
        }

    }

    // display suggestion glyph
    CircleWidgetEvents.ADD_OVERLAY.register { state, context, widgetAdder ->
        val suggestions: List<Pattern> = SharedState.get(context, SibylEditorState::class.java).suggestions
        val suggestionIndex = SharedState.get(context, SibylEditorState::class.java).suggestionIndex

        if (state.drawingPattern != null && !suggestions.isEmpty()) {
            val clampedSuggestionIndex = min(suggestionIndex, suggestions.size - 1)
            val suggestion = suggestions[clampedSuggestionIndex]

            widgetAdder.accept(
                Sized(
                    CircleWidget.DIAMETER.toDouble(), CircleWidget.DIAMETER.toDouble(),
                    CustomDraw(renderSuggestionGlyph(suggestion))
                )
            )
        }
    }

    // apply suggestion with right click
    CircleWidgetEvents.MOUSE_CLICK.register { state, context, x, y, button, modifiers ->
        when {
            button == 0 && state.drawingPattern != null -> {
                SharedState.set(context, SibylEditorState::class.java) { sharedState ->
                    sharedState.suggestionIndex = 0
                    sharedState.suggestions = listOf()
                }

                false
            }

            button == 1 && state.drawingPattern != null -> {
                val suggestions: List<Pattern> = SharedState.get(context, SibylEditorState::class.java).suggestions
                val suggestionIndex = SharedState.get(context, SibylEditorState::class.java).suggestionIndex

                state.circleWidgetState.finishDrawing(
                    false,
                    SharedState.get(context, CircleSoupState::class.java)
                )
                state.updatePattern.accept(suggestions[suggestionIndex])

                SharedState.set(context, SibylEditorState::class.java) { sharedState ->
                    sharedState.suggestionIndex = 0
                    sharedState.suggestions = listOf()
                }

                true
            }

            else -> false
        }
    }

    // apply suggestion with keybind
    CircleSoupWidgetEvents.KEY_DOWN.register { stateApiAccess, context, keyCode, modifiers ->
        val multiKeyTest =
            if (FabricLoader.getInstance().isModLoaded("multi-key-bindings")) {
                { binding: KeyBinding, keyCode: Int -> multiKeyTest(binding, keyCode) }
            } else {
                { _: KeyBinding, _: Int -> true }
            }

        val suggestions = SharedState.get(context, SibylEditorState::class.java).suggestions
        val suggestionIndex = SharedState.get(context, SibylEditorState::class.java).suggestionIndex

        if (SibylClient.keySelectSuggestion.matchesKey(keyCode, -1)
            && multiKeyTest(SibylClient.keySelectSuggestion, keyCode)
            && SharedState.get(context, CircleSoupState::class.java).drawingIn != null
            && !suggestions.isEmpty()
        ) {
            SharedState.set(
                context,
                CircleSoupState::class.java
            ) { state: CircleSoupState ->
                val drawingIn = state.drawingIn
                drawingIn.finishDrawing(
                    false,
                    SharedState.get(context, CircleSoupState::class.java)
                )
                (drawingIn.widget() as CircleWidgetAccessorMixin).getUpdatePattern()
                    .accept(suggestions[suggestionIndex])
            }
            return@register true
        }

        if (SibylClient.keyNextSuggestion.matchesKey(keyCode, -1)
            && multiKeyTest(SibylClient.keyNextSuggestion, keyCode)
        ) {
            SharedState.set(
                context,
                SibylEditorState::class.java
            ) { state ->
                state.suggestionIndex = min(state.suggestionIndex + 1, max(0, state.suggestions.size - 1))
            }
            return@register true
        }

        if (SibylClient.keyPrevSuggestion.matchesKey(keyCode, -1)
            && multiKeyTest(SibylClient.keyPrevSuggestion, keyCode)
        ) {
            SharedState.set(
                context,
                SibylEditorState::class.java
            ) { state -> state.suggestionIndex = max(0, state.suggestionIndex - 1) }
            return@register true
        }

        return@register false
    }

}
