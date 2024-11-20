package mod.master_bw3.pond

import dev.enjarai.trickster.spell.Pattern

interface CoolerSpellPartWidget {
    var suggestionSelection: Int

    var drawingPattern: List<Byte>?

    var suggestions: List<Pattern>
}