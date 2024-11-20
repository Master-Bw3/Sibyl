package mod.master_bw3.mixin.client;

import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellPart;
import mod.master_bw3.pond.CoolerSpellCircleRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(SpellCircleRenderer.class)
public abstract class SpellCircleRendererMixin implements CoolerSpellCircleRenderer {

    @Final @Shadow private boolean inEditor;
    @Shadow(remap = false) private Supplier<SpellPart> drawingPartGetter;

    @Shadow(remap = false) protected abstract void drawGlyph(MatrixStack matrices, VertexConsumerProvider vertexConsumers, SpellPart parent, double x, double y, double size, double startingAngle, float delta, Function<Float, Float> alphaGetter, Vec3d normal);
    @Shadow(remap = false) protected abstract float toLocalSpace(double size);



    @Shadow(remap = false) private Supplier<List<Byte>> drawingPatternGetter;
    @Shadow @Final private double precisionOffset;
    private Supplier<List<Pattern>> suggestionsGetter = List::of;
    private Supplier<Integer> suggestionSelectionGetter = () -> 0;



    @Override
    public void sibyl$setSuggestionSupplier(Supplier<List<Pattern>> suggestionsGetter) {
        this.suggestionsGetter = suggestionsGetter;
    }

    @Override
    public void sibyl$setSuggestionSelectionSupplier(Supplier<Integer> suggestionIndexGetter) {
        this.suggestionSelectionGetter = suggestionIndexGetter;
    }

    @Inject(method = "renderPart",
            at = @At(value = "INVOKE", target = "Ldev/enjarai/trickster/render/SpellCircleRenderer;drawGlyph(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Ldev/enjarai/trickster/spell/SpellPart;DDDDFLjava/util/function/Function;Lnet/minecraft/util/math/Vec3d;)V"),
            remap = false)
    private void renderSuggestionStuff(MatrixStack matrices, VertexConsumerProvider vertexConsumers, SpellPart entry, double x, double y, double size, double startingAngle, float delta, Function<Float, Float> alphaGetter, Vec3d normal, CallbackInfo ci) {
        //size -> (float) Math.clamp(1 / (size / context.getScaledWindowHeight() * 3) - 0.2, 0, 1)
        var isDrawing = inEditor && drawingPartGetter.get() == entry;

        var suggestions = suggestionsGetter.get();
        int selectionIndex = suggestionSelectionGetter.get();

        if (isDrawing && !suggestions.isEmpty() && drawingPatternGetter.get().size() > 1) {

            //pattern
            drawGlyph(
                    matrices, vertexConsumers, new SpellPart(new PatternGlyph(suggestions.get(selectionIndex))),
                    x, y, size, startingAngle,
                    delta, alphaGetter, normal
            );

            //suggestions
            var textRenderer = MinecraftClient.getInstance().textRenderer;

            var height = textRenderer.fontHeight;

            matrices.push();
            matrices.translate(toLocalSpace(x), toLocalSpace(y), 0);

            int suggestionCount = Math.min(suggestions.size(), 5);
            for (int i = 0; i < suggestionCount; i++) {
                var suggestion = suggestions.get(i);
                var text = suggestion.asText();

                if (i == selectionIndex)
                    text = text.copy().formatted(Formatting.BOLD);

                textRenderer.draw(text, (float) (12e14 * size), (suggestionCount - i) * height - height * suggestionCount, 0xffffff, false,
                        matrices.peek().getPositionMatrix(),
                        vertexConsumers, TextRenderer.TextLayerType.NORMAL,
                        0, 0xf000f0);
            }

            matrices.pop();

        }


    }

}
