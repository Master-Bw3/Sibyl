package mod.master_bw3.sibyl.mixin.client;

import dev.enjarai.trickster.screen.scribing.CircleWidget;
import dev.enjarai.trickster.spell.SpellView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CircleWidget.class)
public interface CircleWidgetAccessorMixin {
    @Accessor("partView")
    SpellView sibyl$getPartView();

}
