package mod.master_bw3.sibyl.mixin.client;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.enjarai.trickster.screen.scribing.CircleSoupElement;
import dev.enjarai.trickster.screen.scribing.CircleSoupState;
import dev.enjarai.trickster.screen.scribing.CircleSoupWidget;
import dev.enjarai.trickster.spell.SpellView;
import io.wispforest.owo.braid.core.Color;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.Box;
import io.wispforest.owo.braid.widgets.basic.Sized;
import io.wispforest.owo.braid.widgets.basic.Transform;
import io.wispforest.owo.braid.widgets.flex.Row;
import io.wispforest.owo.braid.widgets.focus.Focusable;
import io.wispforest.owo.braid.widgets.sharedstate.SharedState;
import io.wispforest.owo.braid.widgets.stack.Stack;
import mod.master_bw3.sibyl.pond.CircleSoupWidgetStateDuck;
import mod.master_bw3.sibyl.pond.CircleStateDuck;
import mod.master_bw3.sibyl.widget.SibylEditorState;
import mod.master_bw3.sibyl.widget.SpellInfoSidePanelWidget;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(CircleSoupElement.State.class)
public abstract class CircleSoupElementStateMixin extends WidgetState<CircleSoupElement> {

    @Inject(
            method = "lambda$build$5",
            at = @At("HEAD")
    )
    private static void onMove(
            @Coerce CircleStateDuck c, BuildContext context1, double toX, double toY, CallbackInfo ci
    ) {
        if ( Focusable.isFocused(context1)) {
            SharedState.set(context1, SibylEditorState.class, (state) -> state.setHoveredSpellView(c.sibyl$getSpellView()));
        }
    }

    @Inject(
            method = "lambda$build$6",
            at = @At("HEAD")
    )
    private static void onExit(
            @Coerce CircleStateDuck c, BuildContext context1, CallbackInfo ci
    ) {
        if ( Focusable.isFocused(context1)) {
            SharedState.set(context1, SibylEditorState.class, (state) -> state.setHoveredSpellView(null));
        }
    }

}
