package mod.master_bw3.sibyl.mixin.client;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.enjarai.trickster.screen.scribing.CircleSoupState;
import dev.enjarai.trickster.screen.scribing.CircleSoupWidget;
import io.wispforest.owo.braid.core.Color;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.Box;
import io.wispforest.owo.braid.widgets.basic.Sized;
import io.wispforest.owo.braid.widgets.basic.Transform;
import io.wispforest.owo.braid.widgets.flex.Row;
import io.wispforest.owo.braid.widgets.sharedstate.SharedState;
import io.wispforest.owo.braid.widgets.stack.Stack;
import mod.master_bw3.sibyl.SuggestionState;
import mod.master_bw3.sibyl.widget.SpellInfoSidePanelWidget;
import mod.master_bw3.sibyl.widget.SuggestionsWidget;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

@Mixin(CircleSoupWidget.State.class)
public abstract class CircleSoupWidgetStateMixin extends WidgetState<CircleSoupWidget> {

    @WrapOperation(method = "build", at = @At(value = "NEW", target = "io/wispforest/owo/braid/widgets/sharedstate/SharedState"))
    private SharedState addSuggestionPanel(Supplier initState, Widget child, Operation<SharedState<CircleSoupState>> original) {
        var screenWidth = MinecraftClient.getInstance().currentScreen.width;
        var widget = child;
        if (((CircleSoupWidgetAccessorMixin) this.widget()).sibyl$isMutable()) {
            widget = new Stack(
                    child,
                    new Transform(new Matrix4f().translate(0, 0, 100),
                            new Row(
                                    new Sized(screenWidth * 0.25, null, new SpellInfoSidePanelWidget()),
                                    new Transform(new Matrix4f().translate(-1, 0, 0), new Sized(1, null,
                                            new Box(Color.values(0.6, 0.6, 0.6)))))));
        }

        return original.call(initState, new SharedState<>(SuggestionState::new, widget));
    }
}
