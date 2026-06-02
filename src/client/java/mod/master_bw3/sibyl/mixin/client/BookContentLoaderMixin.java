package mod.master_bw3.sibyl.mixin.client;

import io.wispforest.lavender.book.BookContentLoader;
import mod.master_bw3.sibyl.TrickDescriptions;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BookContentLoader.class)
public class BookContentLoaderMixin {

    @Inject(method = "reloadContents", at=@At("TAIL"))
    private static void populateTrickDescriptions(ResourceManager manager, CallbackInfo ci) {
        TrickDescriptions.reload();
    }
}
