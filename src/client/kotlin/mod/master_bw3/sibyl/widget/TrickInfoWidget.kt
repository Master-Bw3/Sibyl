package mod.master_bw3.sibyl.widget

import dev.enjarai.trickster.spell.trick.Trick
import dev.enjarai.trickster.spell.trick.Tricks
import io.wispforest.owo.braid.core.Alignment
import io.wispforest.owo.braid.core.Insets
import io.wispforest.owo.braid.framework.BuildContext
import io.wispforest.owo.braid.framework.widget.Key
import io.wispforest.owo.braid.framework.widget.StatelessWidget
import io.wispforest.owo.braid.framework.widget.Widget
import io.wispforest.owo.braid.widgets.basic.Align
import io.wispforest.owo.braid.widgets.basic.Padding
import io.wispforest.owo.braid.widgets.basic.Sized
import io.wispforest.owo.braid.widgets.flex.Column
import io.wispforest.owo.braid.widgets.flex.Flexible
import io.wispforest.owo.braid.widgets.owoui.OwoUIWidget
import io.wispforest.owo.braid.widgets.scroll.Scrollable
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.core.Sizing
import mod.master_bw3.sibyl.TrickDescriptions
import mod.master_bw3.sibyl.owo.theft.SibylTrickOverviewComponent
import net.minecraft.util.Identifier

class TrickInfoWidget(val trick: Trick<*>) : StatelessWidget() {
    override fun build(context: BuildContext): Widget {
        val trickID = Tricks.REGISTRY.getId(trick)

        val trickOverview = SibylTrickOverviewComponent.of(
            trick, null,
            Identifier.of("sibyl", "textures/gui/white_book.png")
        )

        val widgets = mutableListOf<Widget>()


        widgets.add(
            Padding(
                Insets.vertical(5.0),
                OwoUIWidget {
                    Containers.stack(Sizing.content(), Sizing.content())
                        .child(trickOverview)
                }.key(Key.of(trickID.toString()))
            )
        )

        TrickDescriptions.descriptions[trickID]?.let { description ->
            widgets.add(
                Flexible(
                    Padding(
                        Insets.vertical(5.0),
                        Scrollable(
                            false,
                            true,
                            null,
                            null,
                            null,
                            OwoUIWidget {
                                Containers.stack(Sizing.content(), Sizing.content())
                                    .child(description)
                            }.key(Key.of(trickID.toString()))
                        )
                    )
                )
            )
        }

        return Column(
            widgets
        )
    }
}