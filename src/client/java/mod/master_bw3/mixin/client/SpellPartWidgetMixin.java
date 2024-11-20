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
import net.minecraft.client.gui.ParentElement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Mixin(SpellPartWidget.class)
public abstract class SpellPartWidgetMixin implements ParentElement {
    @Shadow(remap = false)
    private List<Byte> drawingPattern;

    @Shadow(remap = false)
    @Final
    public SpellCircleRenderer renderer;

    @Unique
    private List<Pattern> suggestions = List.of();

    @Unique
    int suggestionSelection = 0;

    @Inject(method = "selectPattern", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;playSoundToPlayer(Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"), remap = false)
    private void selectPatternMixin(SpellPart part, float x, float y, float size, double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        suggestionSelection = 0;

        if (drawingPattern != null) {

            List<Pattern.PatternEntry> drawn = Pattern.from(drawingPattern).entries();


            suggestions = Tricks.REGISTRY.getEntrySet().stream()
                    .filter(entry -> {
                        List<Pattern.PatternEntry> pattern = entry.getValue().getPattern().entries();


                        //get ordinal of vertices
                        HashMap<Byte, Integer> ordinals = new HashMap<>();
                        for (Pattern.PatternEntry patternEntry : pattern) {
                            ordinals.put(patternEntry.p1(), ordinals.getOrDefault(patternEntry.p1(), 0) + 1);
                            ordinals.put(patternEntry.p2(), ordinals.getOrDefault(patternEntry.p2(), 0) + 1);
                        }

                        List<Byte> vertices = new ArrayList<>();

                        if (ordinals.values().stream().allMatch(o -> o % 2 == 0)) {
                            //all even ordinal
                            vertices.addAll(ordinals.keySet());
                        } else {
                            //has odd ordinal
                            ordinals.entrySet().stream().filter(e -> e.getValue() % 2 == 1).forEach(e -> vertices.add(e.getKey()));
                        }

                        return !drawingPattern.isEmpty()
                                && !drawn.equals(pattern)
                                && pattern.containsAll(drawn)
                                && (vertices.contains(drawingPattern.getFirst()));


                        // return !drawn.equals(pattern) && pattern.containsAll(drawn);
                        //return !drawn.equals(pattern) &&
                        //(drawingPattern.isEmpty() || GraphExtension.canExtendPathFrom(new GraphExtension.Graph(drawn), new GraphExtension.Graph(pattern), drawingPattern.getLast()));
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
        ((CoolerSpellCircleRenderer) ((Object) renderer)).sibyl$setSuggestionSupplier(() -> suggestions);
        ((CoolerSpellCircleRenderer) ((Object) renderer)).sibyl$setSuggestionSelectionSupplier(() -> suggestionSelection);

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 87) {
            suggestionSelection = Math.max(0, suggestionSelection - 1);
        }
        if (keyCode == 83) {
            suggestionSelection = Math.min(suggestionSelection + 1, suggestions.size() - 1);
        }
        Sibyl.logger.info("{} {}", keyCode, suggestionSelection);

        return ParentElement.super.keyPressed(keyCode, scanCode, modifiers);
    }
}
