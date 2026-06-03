package mod.master_bw3.sibyl.mixin.client;


import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.enjarai.trickster.screen.scribing.CircleSoupState;
import dev.enjarai.trickster.screen.scribing.CircleSoupWidget;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellView;
import io.vavr.Function2;
import io.wispforest.owo.braid.core.Color;
import io.wispforest.owo.braid.core.KeyModifiers;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.*;
import io.wispforest.owo.braid.widgets.flex.Row;
import io.wispforest.owo.braid.widgets.sharedstate.SharedState;
import io.wispforest.owo.braid.widgets.stack.Stack;
import mod.master_bw3.sibyl.SibylClient;
import mod.master_bw3.sibyl.compat.MultiKeyTest;
import mod.master_bw3.sibyl.pond.CircleSoupWidgetStateDuck;
import mod.master_bw3.sibyl.widget.SibylEditorState;
import mod.master_bw3.sibyl.widget.SpellInfoSidePanelWidget;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import us.kenny.ModifierManager;

import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(CircleSoupWidget.State.class)
public abstract class CircleSoupWidgetStateMixin extends WidgetState<CircleSoupWidget> implements CircleSoupWidgetStateDuck {

    @Unique
    private SpellView sibyl$focusedSpellView;

    @Unique
    private BuildContext sibyl$buildContext;

    @Shadow
    private SpellView primaryPonder;

    @WrapOperation(method = "build", at = @At(value = "NEW", target = "io/wispforest/owo/braid/widgets/sharedstate/SharedState"))
    private SharedState addSuggestionPanel(Supplier initState, Widget child, Operation<SharedState<CircleSoupState>> original) {
        var screenWidth = MinecraftClient.getInstance().currentScreen.width;
        var screenHeight = MinecraftClient.getInstance().currentScreen.height;
        var widget = child;
        if (((CircleSoupWidgetAccessorMixin) this.widget()).sibyl$isMutable()) {
            widget = new Builder((context) -> {
                sibyl$buildContext = context;

                return new Stack(
                        child,
                        new Transform(new Matrix4f().translate(0, 0, 100),
                                new Row(
                                        new Sized(screenWidth * 0.25, screenHeight, new SpellInfoSidePanelWidget(primaryPonder != null, sibyl$focusedSpellView)),
                                        new Transform(new Matrix4f().translate(-1, 0, 0), new Sized(1, null,
                                                new Box(Color.values(0.6, 0.6, 0.6)))))));
            });
        }

        return original.call(initState, widget);
    }

    @WrapMethod(method = "keyUp")
    private boolean addSibylKeyEvents(int keyCode, KeyModifiers modifiers, Operation<Boolean> original) {
        Function2<KeyBinding, Integer, Boolean> multiKeyTest = (a, b) -> true;
        if (FabricLoader.getInstance().isModLoaded("multi-key-bindings")) {
            multiKeyTest = MultiKeyTest::multiKeyTest;
        }

        if (SibylClient.keyNextSuggestion.matchesKey(keyCode, -1) && multiKeyTest.apply(SibylClient.keyNextSuggestion, keyCode)) {
            SharedState.set(sibyl$buildContext, SibylEditorState.class, (state) ->
                    state.setSuggestionIndex(Math.min(state.getSuggestionIndex() + 1, Math.max(0, state.getSuggestions().size() - 1))));
            return true;
        }

        if (SibylClient.keyPrevSuggestion.matchesKey(keyCode, -1) && multiKeyTest.apply(SibylClient.keyPrevSuggestion, keyCode)) {
            SharedState.set(sibyl$buildContext, SibylEditorState.class, (state) ->
                    state.setSuggestionIndex(Math.max(0, state.getSuggestionIndex() - 1)));
            return true;
        }

        var suggestions = SharedState.get(sibyl$buildContext, SibylEditorState.class).getSuggestions();
        var suggestionIndex = SharedState.get(sibyl$buildContext, SibylEditorState.class).getSuggestionIndex();
        if (SibylClient.keySelectSuggestion.matchesKey(keyCode, -1)
                && ModifierManager.shouldActivate(SibylClient.keySelectSuggestion.getTranslationKey(), InputUtil.Type.KEYSYM.createFromCode(keyCode))
                && SharedState.get(sibyl$buildContext, CircleSoupState.class).drawingIn != null
                && !suggestions.isEmpty())
        {
            SharedState.set(sibyl$buildContext, CircleSoupState.class, (state) -> applySuggestion(state, suggestions.get(suggestionIndex)));
            return true;
        }

        return original.call(keyCode, modifiers);
    }



    @Unique
    void applySuggestion(CircleSoupState state, Pattern suggestion) {
            var drawingIn = state.drawingIn;
            ((CircleWidgetStateAccessorMixin) drawingIn).sibyl$finishDrawing(false, SharedState.get(sibyl$buildContext, CircleSoupState.class));
            ((CircleWidgetAccessorMixin) drawingIn.widget()).getUpdatePattern().accept(suggestion);
    }


    @Override
    public void sibyl$setFocusedSpellView(SpellView spellView) {
        setState(() -> sibyl$focusedSpellView = spellView);
    }

}
