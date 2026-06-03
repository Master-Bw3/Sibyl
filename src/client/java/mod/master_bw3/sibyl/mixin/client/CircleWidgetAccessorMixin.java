package mod.master_bw3.sibyl.mixin.client;

import dev.enjarai.trickster.screen.scribing.CircleWidget;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Consumer;

@Mixin(CircleWidget.class)
public interface CircleWidgetAccessorMixin {
    @Accessor("updatePattern")
    Consumer<Pattern> getUpdatePattern();
}
