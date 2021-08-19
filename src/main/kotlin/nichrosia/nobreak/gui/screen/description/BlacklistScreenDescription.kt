package nichrosia.nobreak.gui.screen.description

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.*
import io.github.cottonmc.cotton.gui.widget.data.Axis
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.minecraft.client.MinecraftClient
import net.minecraft.enchantment.Enchantments
import net.minecraft.item.ItemStack
import net.minecraft.text.TranslatableText
import nichrosia.nobreak.content.NBSettings
import nichrosia.nobreak.content.NBSettings.sumByOr
import nichrosia.nobreak.gui.screen.PresetScreen

@Suppress("MemberVisibilityCanBePrivate", "LeakingThis")
open class BlacklistScreenDescription : LightweightGuiDescription() {
    open val elementsPerRow = 5
    open val rowsPerColumn = 10
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
                NBSettings.customBlacklist = PresetScreenDescription.BlacklistPreset.empty
                NBSettings.blacklists.clear()
                blacklistedItemBox.clearChildren()

                populateBlacklistBox()
            }
        }

        root.add(clearButton, 8, 0, 5, 1)

        val presetButton = WButton(TranslatableText("button.nobreak.open_presets")).apply {
            onClick = Runnable {
                MinecraftClient.getInstance().setScreen(PresetScreen())
            }
        }

        root.add(presetButton, 8, 1, 5, 1)
    }

    open fun populateBlacklistBox() {
        var rowBox = WBox(Axis.HORIZONTAL)

        run forEach@{
            val items = (NBSettings.customBlacklist.items(MinecraftClient.getInstance().player) +
                         NBSettings.blacklists.map { it.items(MinecraftClient.getInstance().player) }.flatten())

            items.forEachIndexed { index, item ->
                val stack = ItemStack(item)

                if (NBSettings.customBlacklist.isEnchanted(stack) || NBSettings.blacklists.map { it.isEnchanted(stack) }.sumByOr()) {
                    stack.addEnchantment(Enchantments.MENDING, 1)
                }

                rowBox.add(WItem(stack))

                if ((index + 1) % elementsPerRow == 0) {
                    blacklistedItemBox.add(rowBox)

                    rowBox = WBox(Axis.HORIZONTAL)

                    val row = (index + 1) / elementsPerRow
                    if (row >= rowsPerColumn) return@forEach
                }
            }

            if (items.size % elementsPerRow != 0) blacklistedItemBox.add(rowBox)
        }
    }

    open class WBoxOpen(axis: Axis) : WBox(axis) {
        open fun clearChildren() = children.clear()
    }
}