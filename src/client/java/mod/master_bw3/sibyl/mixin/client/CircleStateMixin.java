package mod.master_bw3.sibyl.mixin.client;

import dev.enjarai.trickster.screen.scribing.CircleSoupWidget;
import dev.enjarai.trickster.spell.SpellView;
import mod.master_bw3.sibyl.pond.CircleSoupWidgetStateDuck;
import mod.master_bw3.sibyl.pond.CircleStateDuck;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.enjarai.trickster.screen.scribing.CircleSoupWidget$State$CircleState")
public abstract class CircleStateMixin implements CircleStateDuck {

    @Shadow(aliases = "this$0")
    private CircleSoupWidget.State outer;

    @Final
    @Shadow
    SpellView partView;

    @Inject(method = "tryPonder", at = @At(value = "INVOKE", target = "Ldev/enjarai/trickster/screen/scribing/CircleSoupWidget$State;setState(Ljava/lang/Runnable;)V"))
    private void addSuggestionPanel(CallbackInfoReturnable<Boolean> cir) {
        var state = ((CircleSoupWidgetStateDuck) outer);

        state.sibyl$setFocusedSpellView(partView);
    }

    @Override
    public SpellView sibyl$getSpellView() {
        return partView;
    }
}