package mod.master_bw3.sibyl

import io.wispforest.lavender.book.BookLoader
import io.wispforest.lavender.md.features.ItemTagFeature
import io.wispforest.lavender.md.features.OwoUIModelFeature
import io.wispforest.lavendermd.MarkdownProcessor
import io.wispforest.lavendermd.compiler.OwoUICompiler
import io.wispforest.lavendermd.feature.*
import io.wispforest.owo.ui.core.Component
import io.wispforest.owo.ui.parsing.UIModel
import io.wispforest.owo.ui.parsing.UIModelLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier


object TrickDescriptions {

    val descriptions: MutableMap<Identifier, Component> = mutableMapOf()

    val template = object : OwoUITemplateFeature.TemplateProvider {
        override fun <C : Component> template(
            model: Identifier,
            expectedClass: Class<C>,
            templateName: String,
            templateParams: Map<String, String>
        ): C {
            val params = HashMap<String, String>()
            params["book-texture"] = Identifier.of("sibyl", "textures/gui/white_book.png").toString()
            params.putAll(templateParams)

            return UIModelLoader.get(model)!!.expandTemplate<C>(expectedClass, templateName, params)
        }
    }

    val processor = MarkdownProcessor.richText(0)
        .copyWith { OwoUICompiler() }
        .copyWith(
            ImageFeature(),
            BlockStateFeature(),
            ItemStackFeature(MinecraftClient.getInstance().world?.getRegistryManager()), EntityFeature(),
            KeybindFeature(),
            ItemTagFeature(), OwoUIModelFeature(), TranslationsFeature(),
            OwoUITemplateFeature(template)
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