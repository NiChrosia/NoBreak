package nichrosia.nobreak.type.gui.description

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WBox
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItem
import io.github.cottonmc.cotton.gui.widget.data.Axis
import io.github.cottonmc.cotton.gui.widget.data.Insets
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon
import net.minecraft.client.MinecraftClient
import net.minecraft.enchantment.Enchantments
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.TranslatableText
import nichrosia.nobreak.content.NBSettings
import nichrosia.nobreak.type.gui.screen.PresetScreen
import nichrosia.nobreak.type.data.blacklist.BlacklistPreset
import nichrosia.nobreak.type.gui.widget.WBoxOpen

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

        val protectEnchantedButton = WButton(ItemIcon(Items.ENCHANTED_BOOK)).apply {
            onClick = Runnable {
                NBSettings.allBlacklists.forEach { it.toggleProtectEnchanted() }

                repopulateBlacklistBox()
            }
        }

        root.add(protectEnchantedButton, 7, 0, 1, 2)

        val clearButton = WButton(TranslatableText("button.nobreak.reset_blacklist")).apply {
            onClick = Runnable {
                NBSettings.customBlacklist = BlacklistPreset.custom.copy()
                NBSettings.blacklists.clear()
                repopulateBlacklistBox()
            }
        }

        root.add(clearButton, 8, 0, 5, 1)

        val presetButton = WButton(TranslatableText("button.nobreak.open_presets")).apply {
            onClick = Runnable {
                MinecraftClient.getInstance().setScreen(PresetScreen(PresetScreenDescription()))
            }
        }

        root.add(presetButton, 8, 1, 5, 1)
    }

    open fun populateBlacklistBox() {
        var rowBox = WBox(Axis.HORIZONTAL)
        val items = (NBSettings.customBlacklist.items() +
                     NBSettings.blacklists.map { it.items() }.flatten())

        items.forEachIndexed { index, item ->
            val stack = ItemStack(item)

            if (NBSettings.allBlacklists.any { it.protectEnchantedItems[item] == true && it.items().contains(item) }) {
                stack.addEnchantment(Enchantments.MENDING, 1)
            }

            rowBox.add(WItem(stack))

            if ((index + 1) % elementsPerRow == 0) {
                blacklistedItemBox.add(rowBox)

                rowBox = WBox(Axis.HORIZONTAL)

                val row = (index + 1) / elementsPerRow
                if (row >= rowsPerColumn) return@forEachIndexed
            }
        }

        if (items.size % elementsPerRow != 0) blacklistedItemBox.add(rowBox)
    }

    open fun repopulateBlacklistBox() {
        blacklistedItemBox.children.clear()

        populateBlacklistBox()

        blacklistedItemBox.layout()
    }
}