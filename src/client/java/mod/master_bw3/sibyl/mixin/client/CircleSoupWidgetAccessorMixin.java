package mod.master_bw3.sibyl.mixin.client;

import dev.enjarai.trickster.screen.scribing.CircleSoupWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CircleSoupWidget.class)
public interface CircleSoupWidgetAccessorMixin {
    @Accessor("mutable")
    boolean sibyl$isMutable();
}
