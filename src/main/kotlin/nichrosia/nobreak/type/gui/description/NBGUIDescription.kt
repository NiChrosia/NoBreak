package nichrosia.nobreak.type.gui.description

import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import net.minecraft.client.MinecraftClient
import net.minecraft.text.TranslatableText

open class NBGUIDescription : LightweightGuiDescription() {
    open lateinit var backButton: WButton

    open fun addBackButton(root: WGridPanel, buttonKey: String = "button.nobreak.back", from: () -> CottonClientScreen) {
        backButton = WButton(TranslatableText(buttonKey)).apply {
            onClick = Runnable {
                MinecraftClient.getInstance().setScreen(from())
            }
        }

        root.add(backButton, 4, 11, 5, 1)
    }
}