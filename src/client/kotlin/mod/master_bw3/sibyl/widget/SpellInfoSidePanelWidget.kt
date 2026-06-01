package mod.master_bw3.sibyl.widget

import io.wispforest.owo.braid.core.Color
import io.wispforest.owo.braid.core.Insets
import io.wispforest.owo.braid.framework.BuildContext
import io.wispforest.owo.braid.framework.proxy.WidgetState
import io.wispforest.owo.braid.framework.widget.StatefulWidget
import io.wispforest.owo.braid.framework.widget.Widget
import io.wispforest.owo.braid.widgets.basic.Box
import io.wispforest.owo.braid.widgets.basic.Padding
import io.wispforest.owo.braid.widgets.stack.Stack

class SpellInfoSidePanelWidget : StatefulWidget() {
    override fun createState(): WidgetState<*> = State()


    class State : WidgetState<SuggestionsWidget>() {
        override fun build(context: BuildContext?): Widget? {
            return Stack(
                Box(Color.BLACK.withA(0.5)),
                Padding(Insets.of(5.0, 0.0, 5.0, 0.0),
                    SuggestionsWidget()
                )
            )
        }
    }
}