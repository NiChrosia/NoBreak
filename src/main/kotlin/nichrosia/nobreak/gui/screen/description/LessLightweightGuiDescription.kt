package nichrosia.nobreak.gui.screen.description

import io.github.cottonmc.cotton.gui.GuiDescription
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import net.minecraft.client.MinecraftClient
import net.minecraft.text.TranslatableText
import nichrosia.nobreak.gui.screen.BlacklistScreen

open class LessLightweightGuiDescription : LightweightGuiDescription() {
    open lateinit var backButton: WButton

    open fun addBackButton(from: GuiDescription, root: WGridPanel, buttonKey: String = "button.nobreak.back") {
        backButton = WButton(TranslatableText(buttonKey)).apply {
            onClick = Runnable {
                MinecraftClient.getInstance().setScreen(BlacklistScreen(from))
            }
        }

        root.add(backButton, 4, 10, 5, 1)
    }
}