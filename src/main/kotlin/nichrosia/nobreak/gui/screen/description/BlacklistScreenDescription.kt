package nichrosia.nobreak.gui.screen.description

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.*
import io.github.cottonmc.cotton.gui.widget.data.Axis
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.text.TranslatableText
import nichrosia.nobreak.content.NBSettings
import nichrosia.nobreak.gui.screen.PresetScreen

@Suppress("MemberVisibilityCanBePrivate", "LeakingThis")
open class BlacklistScreenDescription : LightweightGuiDescription() {
    open val elementsPerRow = 5
    open val blacklistedItemBox: WBoxOpen

    init {
        val root = WGridPanel().apply {
            setSize(256, 240)
            insets = Insets.ROOT_PANEL
        }

        setRootPanel(root)

        blacklistedItemBox = WBoxOpen(Axis.VERTICAL)
        populateBlacklistBox()

        root.add(blacklistedItemBox, 0, 0)

        val clearButton = WButton(TranslatableText("button.nobreak.reset_blacklist")).apply {
            onClick = Runnable {
                NBSettings.toolBlacklist.clear()
                blacklistedItemBox.clearChildren()

                populateBlacklistBox()
            }
        }

        root.add(clearButton, 8, 0, 5, 1)

        val presetButton = WButton(TranslatableText("button.nobreak.open_presets")).apply {
            onClick = Runnable {
                MinecraftClient.getInstance().setScreen(PresetScreen(this@BlacklistScreenDescription))
            }
        }

        root.add(presetButton, 8, 1, 5, 1)
    }

    open fun populateBlacklistBox() {
        var rowBox = WBox(Axis.HORIZONTAL)

        NBSettings.toolBlacklist.forEachIndexed { index, item ->
            rowBox.add(WItem(ItemStack(item)))

            if ((index + 1) % elementsPerRow == 0) {
                blacklistedItemBox.add(rowBox)

                rowBox = WBox(Axis.HORIZONTAL)
            }
        }

        if (NBSettings.toolBlacklist.size % elementsPerRow != 0) blacklistedItemBox.add(rowBox)
    }

    open class WBoxOpen(axis: Axis) : WBox(axis) {
        open fun clearChildren() = children.clear()
    }
}