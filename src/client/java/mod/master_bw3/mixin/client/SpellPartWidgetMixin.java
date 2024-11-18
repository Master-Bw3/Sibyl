package mod.master_bw3.mixin.client;


import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.revision.RevisionContext;
import dev.enjarai.trickster.screen.SpellPartWidget;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.trick.Tricks;
import mod.master_bw3.GraphExtension;
import mod.master_bw3.Sibyl;
import mod.master_bw3.pond.CoolerSpellCircleRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.List;

@Mixin(SpellPartWidget.class)
public class SpellPartWidgetMixin {
    @Shadow(remap = false)
    private List<Byte> drawingPattern;

    @Shadow(remap = false)
    @Final
    public SpellCircleRenderer renderer;
    private List<Pattern> suggestions = List.of();

    @Inject(method = "selectPattern", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;playSoundToPlayer(Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"), remap = false)
    private void selectPatternMixin(SpellPart part, float x, float y, float size, double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        if (drawingPattern != null) {

            var drawn = Pattern.from(drawingPattern).entries();


            suggestions = Tricks.REGISTRY.getEntrySet().stream()
                    .filter(entry -> {
                        var pattern = entry.getValue().getPattern().entries();


                        // return !drawn.equals(pattern) && pattern.containsAll(drawn);
                        return !drawn.equals(pattern) &&
                                (drawingPattern.isEmpty() || GraphExtension.canExtendPathFrom(new GraphExtension.Graph(drawn), new GraphExtension.Graph(pattern), drawingPattern.getLast()));
                    })
                    .sorted(Comparator.comparingInt(entry -> entry.getValue().getPattern().entries().size()))
                    .map(entry -> entry.getValue().getPattern())
                    .toList();

            if (!drawn.isEmpty()) {
                Sibyl.logger.info("{} | {} | {}", drawn, drawingPattern.getLast(), suggestions.size());
            }

        } else {
            suggestions = List.of();
        }
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"), remap = false)
    private void betterRenderer(SpellPart spellPart, double x, double y, double size, RevisionContext revisionContext, boolean animated, CallbackInfo ci) {
        ((CoolerSpellCircleRenderer) ((Object) renderer)).setSuggestionSupplier(() -> suggestions);
    }


}
