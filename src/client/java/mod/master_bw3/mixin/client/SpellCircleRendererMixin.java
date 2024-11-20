package mod.master_bw3.mixin.client;

import dev.enjarai.trickster.render.SpellCircleRenderer;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;
import mod.master_bw3.SuggestionRenderer;
import mod.master_bw3.pond.CoolerSpellCircleRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
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

    @Final
    @Shadow(remap = false)
    private boolean inEditor;
    @Shadow(remap = false)
    private Supplier<SpellPart> drawingPartGetter;

    @Shadow(remap = false)
    protected abstract void drawGlyph(MatrixStack matrices, VertexConsumerProvider vertexConsumers, SpellPart parent, double x, double y, double size, double startingAngle, float delta, Function<Float, Float> alphaGetter, Vec3d normal);

    @Shadow(remap = false)
    protected abstract float toLocalSpace(double size);

    @Shadow(remap = false)
    private Supplier<List<Byte>> drawingPatternGetter;

    private Supplier<List<Pattern>> suggestionsGetter = List::of;

    private Supplier<Integer> suggestionSelectionGetter = () -> 0;

    //////

    @Inject(method = "renderPart", at = @At(value = "INVOKE", target = "Ldev/enjarai/trickster/render/SpellCircleRenderer;drawGlyph(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Ldev/enjarai/trickster/spell/SpellPart;DDDDFLjava/util/function/Function;Lnet/minecraft/util/math/Vec3d;)V"))
    private void renderSuggestionStuff(MatrixStack matrices, VertexConsumerProvider vertexConsumers, SpellPart entry, double x, double y, double size, double startingAngle, float delta, Function<Float, Float> alphaGetter, Vec3d normal, CallbackInfo ci) {
        SuggestionRenderer.INSTANCE.renderSuggestionStuff(this, matrices, vertexConsumers, entry, x, y, size, startingAngle, delta, alphaGetter, normal);
    }

    //////

    @Override
    public void _setSuggestionSupplier(Supplier<List<Pattern>> suggestionsGetter) {
        this.suggestionsGetter = suggestionsGetter;
    }

    @Override
    public void _setSuggestionSelectionSupplier(Supplier<Integer> suggestionIndexGetter) {
        this.suggestionSelectionGetter = suggestionIndexGetter;
    }

    @Override
    public void _drawGlyph(@NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vertexConsumers, @NotNull SpellPart spellPart, double x, double y, double size, double startingAngle, float delta, @NotNull Function<Float, Float> alphaGetter, @NotNull Vec3d normal) {
        drawGlyph(matrices, vertexConsumers, spellPart, x, y, size, startingAngle, delta, alphaGetter, normal);
    }

    @Override
    public float _toLocalSpace(double size) {
        return toLocalSpace(size);
    }

    @Override
    public boolean getInEditor() {
        return inEditor;
    }

    @Override
    public @NotNull List<Pattern> getSuggestions() {
        return suggestionsGetter.get();
    }

    @Override
    public int getSuggestionSelection() {
        return suggestionSelectionGetter.get();
    }

    @Override
    public @NotNull SpellPart getDrawingPart() {
        return drawingPartGetter.get();
    }

    @Override
    public @NotNull List<Byte> getDrawingPattern() {
        return drawingPatternGetter.get();
    }
}
