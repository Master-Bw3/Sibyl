package mod.master_bw3.sibyl.mixin.client;

import dev.enjarai.trickster.screen.scribing.CircleSoupState;
import dev.enjarai.trickster.screen.scribing.CircleWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;


@Mixin(CircleWidget.State.class)
public interface CircleWidgetStateAccessorMixin {
    @Invoker("finishDrawing")
    void sibyl$finishDrawing(boolean apply, CircleSoupState state);
}
