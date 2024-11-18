package mod.master_bw3.mixin.client;

import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellPart;
import io.netty.util.Attribute;
import mod.master_bw3.pond.CoolerSpellCircleRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

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


    @Override
    public void setSuggestionSupplier(Supplier<List<Pattern>> suggestionsGetter) {
        this.suggestionsGetter = suggestionsGetter;
    }

    @Inject(method = "renderPart",
            at = @At(value = "INVOKE", target = "Ldev/enjarai/trickster/render/SpellCircleRenderer;drawGlyph(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Ldev/enjarai/trickster/spell/SpellPart;DDDDFLjava/util/function/Function;Lnet/minecraft/util/math/Vec3d;)V"),
            remap = false)
    private void renderSuggestionPattern(MatrixStack matrices, VertexConsumerProvider vertexConsumers, SpellPart entry, double x, double y, double size, double startingAngle, float delta, Function<Float, Float> alphaGetter, Vec3d normal, CallbackInfo ci) {
        //size -> (float) Math.clamp(1 / (size / context.getScaledWindowHeight() * 3) - 0.2, 0, 1)
        var isDrawing = inEditor && drawingPartGetter.get() == entry;

        if (isDrawing && !suggestionsGetter.get().isEmpty() && drawingPatternGetter.get().size() > 1) {
            var suggestion = suggestionsGetter.get().getFirst();

            drawGlyph(
                    matrices, vertexConsumers, new SpellPart(new PatternGlyph(suggestion)),
                    x, y, size, startingAngle,
                    delta, alphaGetter, normal
            );


            var textRenderer = MinecraftClient.getInstance().textRenderer;

            var width = textRenderer.getWidth(suggestion.asText());

            matrices.push();
            matrices.translate(toLocalSpace(x), toLocalSpace(y), 0);
            matrices.scale(1.2f, 1.2f, 0);

            textRenderer.draw(suggestion.asText(), -width / 2f, (float) (-12e14 * size), 0xffffff, false,
                    matrices.peek().getPositionMatrix(),
                    vertexConsumers, TextRenderer.TextLayerType.NORMAL,
                    0, 0xf000f0);

            matrices.pop();

        }


    }



}
