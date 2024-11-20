package mod.master_bw3.mixin.client;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.revision.RevisionContext;
import dev.enjarai.trickster.screen.SpellPartWidget;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;
import mod.master_bw3.SuggestionLogic;
import mod.master_bw3.pond.CoolerSpellCircleRenderer;
import mod.master_bw3.pond.CoolerSpellPartWidget;
import net.minecraft.client.gui.ParentElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SpellPartWidget.class)
public abstract class SpellPartWidgetMixin implements ParentElement, CoolerSpellPartWidget {
    @Shadow(remap = false)
    private List<Byte> drawingPattern;

    @Shadow(remap = false)
    @Final
    public SpellCircleRenderer renderer;

    @Shadow(remap = false)
    public abstract boolean isDrawing();

    @Shadow(remap = false)
    private boolean isMutable;

    @Shadow(remap = false)
    private SpellPart drawingPart;

    @Shadow(remap = false)
    protected abstract void stopDrawing();

    @Unique
    private List<Pattern> suggestions = List.of();

    @Unique
    int suggestionSelection = 0;

    @Unique
    boolean replaceDrawingWithSuggestion = false;

    //////

    @Inject(method = "selectPattern", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;playSoundToPlayer(Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"), remap = false)
    private void selectPatternMixin(SpellPart part, float x, float y, float size, double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        SuggestionLogic.INSTANCE.selectPattern(this, part, x, y, size, mouseX, mouseY);
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"), remap = false)
    private void betterRenderer(SpellPart spellPart, double x, double y, double size, RevisionContext revisionContext, boolean animated, CallbackInfo ci) {
        ((CoolerSpellCircleRenderer) ((Object) renderer))._setSuggestionSupplier(() -> suggestions);
        ((CoolerSpellCircleRenderer) ((Object) renderer))._setSuggestionSelectionSupplier(() -> suggestionSelection);

    }

    @Inject(method = "mouseReleased", at = @At(value = "INVOKE", target = "Ldev/enjarai/trickster/screen/SpellPartWidget;stopDrawing()V"), remap = false)
    private void handleRightClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if ( button == 1 && isMutable || isDrawing() && this.drawingPart != null) {
            replaceDrawingWithSuggestion = true;
        }
    }

    @WrapOperation(method = "stopDrawing", at = @At(value = "INVOKE", target = "Ldev/enjarai/trickster/spell/Pattern;from(Ljava/util/List;)Ldev/enjarai/trickster/spell/Pattern;"), remap = false)
    private Pattern useSuggestion(List<Byte> current, Operation<Pattern> original) {
        if (replaceDrawingWithSuggestion) {
            replaceDrawingWithSuggestion = false;
            return suggestions.isEmpty() ? original.call(current) : suggestions.get(suggestionSelection);
        } else {
            return original.call(current);
        }
    }

    //////


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257) {
            replaceDrawingWithSuggestion = true;
            stopDrawing();
        } else {
            suggestionSelection = switch (keyCode) {
                case 87 -> Math.max(0, suggestionSelection - 1);
                case 83 -> Math.min(suggestionSelection + 1, suggestions.size() - 1);
                default -> suggestionSelection;
            };
        }

        return ParentElement.super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void setSuggestions(@NotNull List<Pattern> patterns) {
        suggestions = patterns;
    }

    @Override
    public @NotNull List<Pattern> getSuggestions() {
        return suggestions;
    }

    @Override
    public void setDrawingPattern(@Nullable List<Byte> bytes) {
        drawingPattern = bytes;
    }

    @Override
    public @Nullable List<Byte> getDrawingPattern() {
        return drawingPattern;
    }

    @Override
    public void setSuggestionSelection(int i) {
        suggestionSelection = i;
    }

    @Override
    public int getSuggestionSelection() {
        return suggestionSelection;
    }
}
