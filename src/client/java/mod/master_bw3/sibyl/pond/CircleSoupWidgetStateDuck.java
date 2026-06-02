package mod.master_bw3.sibyl.pond;

import dev.enjarai.trickster.spell.SpellView;
import io.wispforest.owo.braid.framework.BuildContext;
import org.jetbrains.annotations.Nullable;

public interface CircleSoupWidgetStateDuck {
    BuildContext sibyl$getBuildContext();

    void sibyl$setFocusedSpellView(@Nullable SpellView spellView);

}
