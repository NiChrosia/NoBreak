package nichrosia.nobreak.type.gui.widget

import io.github.cottonmc.cotton.gui.widget.WBox
import io.github.cottonmc.cotton.gui.widget.WWidget
import io.github.cottonmc.cotton.gui.widget.data.Axis

open class WBoxOpen(axis: Axis) : WBox(axis) {
    val children: MutableList<WWidget>
        get() = children
}