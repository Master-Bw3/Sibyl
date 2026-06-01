package mod.master_bw3.sibyl

import dev.enjarai.trickster.spell.Pattern
import io.wispforest.owo.braid.widgets.sharedstate.ShareableState

class SuggestionState : ShareableState() {
    var suggestions: List<Pattern> = listOf()

    var suggestionIndex = 0
}