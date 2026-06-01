package mod.master_bw3.sibyl.owo.theft

import dev.enjarai.trickster.screen.owo.ManaCostComponent
import dev.enjarai.trickster.spell.Pattern
import dev.enjarai.trickster.spell.revision.Revision
import dev.enjarai.trickster.spell.revision.Revisions
import dev.enjarai.trickster.spell.trick.Trick
import dev.enjarai.trickster.spell.trick.Tricks
import dev.enjarai.trickster.spell.type.Signature
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Color
import io.wispforest.owo.ui.core.HorizontalAlignment
import io.wispforest.owo.ui.core.Insets
import io.wispforest.owo.ui.core.Sizing
import io.wispforest.owo.ui.core.VerticalAlignment
import io.wispforest.owo.ui.parsing.UIModelParsingException
import io.wispforest.owo.ui.parsing.UIParsing
import net.minecraft.client.MinecraftClient
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import org.w3c.dom.Element
import java.util.Arrays
import java.util.function.Consumer
import java.util.function.UnaryOperator

class SibylTrickOverviewComponent private constructor(
    pattern: Pattern,
    title: Text?,
    content: MutableText,
    costCalculation: String?,
    bookTexture: Identifier?
) : FlowLayout(Sizing.fill(100), Sizing.content(), Algorithm.VERTICAL) {
    init {
        alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)

        child(
            Components.label(title)
                .color(Color.ofFormatting(Formatting.WHITE))
                .horizontalTextAlignment(HorizontalAlignment.CENTER)
                .margins(Insets.of(2, 2, 0, 0))
                .sizing(Sizing.fill(100), Sizing.content())
        )
        child(SibylGlyphComponent(pattern, 50, Color.WHITE))

        child(
            Components.label(content.styled(UnaryOperator { s: Style? ->
                s!!
                    .withFormatting(Formatting.WHITE)
                    .withFont(MinecraftClient.UNICODE_FONT_ID)
            }))
                .horizontalTextAlignment(HorizontalAlignment.LEFT)
                .horizontalSizing(Sizing.fill(100))
                .margins(Insets.of(0, 5, 3, 3))
        )

        if (costCalculation != null) {
            child(ManaCostComponent(costCalculation, bookTexture))
        } else {
            child(
                Components.texture(
                    bookTexture, 54, 183, 109,
                    3, 512, 256
                )
                    .blend(true)
            )
        }

        allowOverflow(true)
    }

    companion object {
        fun of(trick: Trick<*>, costCalculation: String?, bookTexture: Identifier?): SibylTrickOverviewComponent {
            val pattern = trick.getPattern()

            val name = Text.empty()
            trick.getName().getSiblings()
                .forEach(Consumer { s: Text? -> name.append(s!!.copy().formatted(Formatting.WHITE)) })

            var signatures = Text.empty()
            val iterator = trick.getSignatures().iterator()
            while (iterator.hasNext()) {
                val signature: Signature<out Trick<*>?> = iterator.next()
                signatures = signatures.append(signature.asText())
                if (iterator.hasNext()) {
                    signatures = signatures.append("\n")
                }
            }

            return SibylTrickOverviewComponent(pattern, name, signatures, costCalculation, bookTexture)
        }

        fun of(rev: Revision, bookTexture: Identifier?): SibylTrickOverviewComponent {
            val pattern = rev.pattern()

            val name = Text.empty()
            rev.getName().getSiblings()
                .forEach(Consumer { s: Text? -> name.append(s!!.copy().formatted(Formatting.WHITE)) })

            return SibylTrickOverviewComponent(pattern, name, Text.translatable("trickster.revision"), null, bookTexture)
        }

        fun of(
            pattern: Pattern,
            title: Text?,
            content: MutableText,
            costCalculation: String?,
            bookTexture: Identifier?
        ): SibylTrickOverviewComponent {
            return SibylTrickOverviewComponent(pattern, title, content, costCalculation, bookTexture)
        }

        fun parse(element: Element): SibylTrickOverviewComponent {
            UIParsing.expectAttributes(element, "texture")
            val texture = UIParsing.parseIdentifier(element.getAttributeNode("texture"))

            val trickIdAttribute = element.getAttributeNode("trick-id")
            if (trickIdAttribute != null) {
                val trickId = UIParsing.parseIdentifier(trickIdAttribute)
                val trick = Tricks.REGISTRY.get(trickId)

                if (trick == null) {
                    throw UIModelParsingException("Not a valid trick: " + trickId)
                }

                var costCalculation: String? = null
                if (element.hasAttribute("cost")) {
                    costCalculation = element.getAttribute("cost")
                }

                return of(trick, costCalculation, texture)
            }

            val revisionIdAttribute = element.getAttributeNode("revision-id")
            if (revisionIdAttribute != null) {
                val revisionId = UIParsing.parseIdentifier(revisionIdAttribute)
                val revision = Revisions.REGISTRY.get(revisionId)

                if (revision == null) {
                    throw UIModelParsingException("Not a valid revision: " + revisionId)
                }

                return of(revision, texture)
            }

            UIParsing.expectAttributes(element, "pattern", "title")
            val title = Text.literal(element.getAttributeNode("title").getTextContent())
            val patternString = element.getAttributeNode("pattern").getTextContent()
            val pattern = Pattern.from(
                Arrays.stream<String>(patternString.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                    .map<Byte?> { s: String? -> s!!.toByte(10) }.toList()
            )
            val content = Text.literal(element.getAttributeNode("content").getTextContent())

            return of(pattern, title, content, null, texture)
        }
    }
}