package mod.master_bw3.sibyl.mixin.client;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.enjarai.trickster.screen.scribing.CircleSoupState;
import dev.enjarai.trickster.screen.scribing.CircleWidget;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import io.wispforest.owo.braid.core.KeyModifiers;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.framework.widget.WidgetSetupCallback;
import io.wispforest.owo.braid.widgets.basic.MouseArea;
import io.wispforest.owo.braid.widgets.basic.Sized;
import io.wispforest.owo.braid.widgets.sharedstate.SharedState;
import io.wispforest.owo.braid.widgets.stack.Stack;
import mod.master_bw3.sibyl.SuggestionLogic;
import mod.master_bw3.sibyl.widget.SibylEditorState;
import mod.master_bw3.sibyl.mixinImpl.CircleWidgetStateMixinImpl;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CircleWidget.State.class)
public abstract class CircleWidgetStateMixin extends WidgetState<CircleWidget> {

    @Unique
    private BuildContext sibyl$buildContext;

    @Shadow
    private @Nullable List<Byte> drawingPattern;

    @Inject(method = "build", at = @At("HEAD"))
    private void exposeBuildContext(BuildContext context, CallbackInfoReturnable<Widget> cir) {
        sibyl$buildContext = context;
    }

    @WrapOperation(method = "build", at = @At(value = "NEW", target = "io/wispforest/owo/braid/widgets/basic/Sized", ordinal = 0))
    private Sized addSuggestionGlyph(double width, double height, Widget child, Operation<Sized> original) {
        var suggestions = SharedState.get(sibyl$buildContext, SibylEditorState.class).getSuggestions();
        var suggestionIndex = SharedState.get(sibyl$buildContext, SibylEditorState.class).getSuggestionIndex();

        if (drawingPattern != null && !suggestions.isEmpty()) {
            var clampedSuggestionIndex = Math.min(suggestionIndex, suggestions.size() - 1);
            var suggestion = suggestions.get(clampedSuggestionIndex);

            return CircleWidgetStateMixinImpl.addSuggestionGlyph(suggestion, width, height, (Stack) child, original);
        } else {
            return original.call(width, height, child);
        }
    }

    @WrapOperation(method = "build", at = @At(value = "NEW", target = "io/wispforest/owo/braid/widgets/basic/MouseArea"))
    private MouseArea addMouseUpEvent(WidgetSetupCallback<MouseArea> setupCallback, Widget child, Operation<MouseArea> original) {
        return original.call(setupCallback.andThen((m) -> m.releaseCallback(onRelease(m.releaseCallback()))), child);
    }

    @Inject(method = "mouseMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getInstance()Lnet/minecraft/client/MinecraftClient;"))
    private void updateDrawingStateInMouseMove(double x, double y, CallbackInfo ci) {
        setState(this::sibyl$updateSuggestions);
    }

    @Inject(method = "startDrawing", at = @At("TAIL"))
    private void updateDrawingStateInStartDrawing(CircleSoupState state, CallbackInfoReturnable<List<Byte>> cir) {
        setState(this::sibyl$updateSuggestions);
    }


    @Inject(method = "finishDrawing", at = @At("TAIL"))
    private void updateDrawingStateInFinishDrawing(boolean apply, CircleSoupState state, CallbackInfo ci) {
        setState(this::sibyl$updateSuggestions);
    }

    @Unique
    MouseArea.ReleaseCallback onRelease(MouseArea.ReleaseCallback  oldReleaseCallback) {
        return (double x, double y, int button, KeyModifiers modifiers) -> {
            var suggestions = SharedState.get(sibyl$buildContext, SibylEditorState.class).getSuggestions();
            var suggestionIndex = SharedState.get(sibyl$buildContext, SibylEditorState.class).getSuggestionIndex();

            if (button == 1 && !suggestions.isEmpty() && SharedState.get(sibyl$buildContext, CircleSoupState.class).drawingIn != null) {
                SharedState.set(sibyl$buildContext, CircleSoupState.class, (state) -> applySuggestion(state, suggestions.get(suggestionIndex)));
                return true;
            }

            return oldReleaseCallback.onRelease(x, y, button, modifiers);
        };
    }

    @Unique
    private void sibyl$updateSuggestions() {
        SharedState.set(sibyl$buildContext, SibylEditorState.class, (state) -> {
                    state.setSuggestionIndex(0);
                    if (drawingPattern == null || drawingPattern.size() <= 1) {
                        state.setSuggestions(List.of());
                    } else {
                        state.setSuggestions(SuggestionLogic.getSuggestions(drawingPattern));
                    }
                }
        );

    }

    @Unique
    void applySuggestion(CircleSoupState state, Pattern suggestion) {
        var drawingIn = state.drawingIn;
        ((CircleWidgetStateAccessorMixin) drawingIn).sibyl$finishDrawing(false, SharedState.get(sibyl$buildContext, CircleSoupState.class));
        ((CircleWidgetAccessorMixin) drawingIn.widget()).sibyl$getPartView().replaceGlyph(new PatternGlyph(suggestion));
    }
}

