package mod.master_bw3.sibyl.widget

import dev.enjarai.trickster.spell.Pattern
import dev.enjarai.trickster.spell.SpellView
import io.wispforest.owo.braid.widgets.sharedstate.ShareableState

class SibylEditorState : ShareableState() {
    var suggestions: List<Pattern> = listOf()

    var suggestionIndex = 0

    var hoveredSpellView: SpellView? = null
}