package mod.master_bw3.pond;

import dev.enjarai.trickster.spell.Pattern;

import java.util.List;
import java.util.function.Supplier;

public interface CoolerSpellCircleRenderer {
    void sibyl$setSuggestionSupplier(Supplier<List<Pattern>> suggestionsGetter);

    void sibyl$setSuggestionSelectionSupplier(Supplier<Integer> suggestionIndexGetter);
}
