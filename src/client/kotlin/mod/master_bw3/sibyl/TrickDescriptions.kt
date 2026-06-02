package mod.master_bw3.sibyl

import io.wispforest.lavender.Lavender
import io.wispforest.lavender.book.BookLoader
import io.wispforest.lavender.md.features.ItemTagFeature
import io.wispforest.lavender.md.features.OwoUIModelFeature
import io.wispforest.lavender.md.features.PageBreakFeature
import io.wispforest.lavender.md.features.RecipeFeature
import io.wispforest.lavendermd.MarkdownProcessor
import io.wispforest.lavendermd.compiler.OwoUICompiler
import io.wispforest.lavendermd.feature.BlockStateFeature
import io.wispforest.lavendermd.feature.EntityFeature
import io.wispforest.lavendermd.feature.ImageFeature
import io.wispforest.lavendermd.feature.ItemStackFeature
import io.wispforest.lavendermd.feature.KeybindFeature
import io.wispforest.lavendermd.feature.OwoUITemplateFeature
import io.wispforest.lavendermd.feature.TranslationsFeature
import io.wispforest.owo.ui.core.Component
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier

object TrickDescriptions {

    val descriptions: MutableMap<Identifier, Component> = mutableMapOf()

    val processor = MarkdownProcessor.richText(0)
        .copyWith { OwoUICompiler() }
        .copyWith(
            ImageFeature(),
            BlockStateFeature(),
            ItemStackFeature(MinecraftClient.getInstance().world?.getRegistryManager()), EntityFeature(),
            KeybindFeature(),
            ItemTagFeature(), OwoUIModelFeature(), TranslationsFeature()
        )

    @JvmStatic
    fun reload() {
        descriptions.clear()

        BookLoader.get(Identifier.of("trickster", "tome_of_tomfoolery"))?.entries()?.forEach { entry ->
            val regex = Regex(
                """<\|[^|]+@trickster:templates\|trick-id=([^,|>]+).*?\|>\s*(.*?)(?=<\|[^|]+@trickster:templates\||\z)""",
                setOf(RegexOption.DOT_MATCHES_ALL)
            )

            for (match in regex.findAll(entry.content)) {
                val trickId = Identifier.of(match.groupValues[1])

                val description = match.groupValues[2].replace(";;;;;", "")
                val component = processor.process(description)

                descriptions[trickId] = component
            }
        }
    }
}