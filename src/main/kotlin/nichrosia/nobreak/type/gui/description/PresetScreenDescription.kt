package nichrosia.nobreak.type.gui.description

import io.github.cottonmc.cotton.gui.widget.WBox
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WLabel
import io.github.cottonmc.cotton.gui.widget.data.Axis
import io.github.cottonmc.cotton.gui.widget.data.Insets
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon
import net.minecraft.item.Items
import net.minecraft.text.TranslatableText
import nichrosia.nobreak.content.NBSettings
import nichrosia.nobreak.type.data.blacklist.BlacklistPreset
import nichrosia.nobreak.type.gui.screen.BlacklistScreen

@Suppress("LeakingThis")
open class PresetScreenDescription : NBGUIDescription() {
    open val presetBox: WBox

    init {
        val root = WGridPanel().apply {
            setSize(256, 240)
            insets = Insets.ROOT_PANEL
        }

        setRootPanel(root)

        presetBox = WBox(Axis.VERTICAL).apply {
            generatePresets().forEach {
                val rowBox = WBox(Axis.HORIZONTAL)

                rowBox.apply {
                    val presetButton = WButton(ItemIcon(it.icon), TranslatableText(it.translationKey)).apply {
                        onClick = Runnable {
                            if (!NBSettings.blacklists.contains(it)) NBSettings.blacklists.add(it)
                        }
                    }

                    add(presetButton, 100, 20)

                    val toggleEnchantmentButton = WButton(ItemIcon(Items.ENCHANTED_BOOK)).apply {
                        onClick = Runnable {
                            if (NBSettings.blacklists.contains(it)) it.toggleProtectEnchanted()
                        }
                    }

                    add(toggleEnchantmentButton, 20, 20)

                    val description = WLabel(TranslatableText("${it.translationKey}.desc"))

                    add(description)
                }

                add(rowBox)
            }
        }

        root.add(presetBox, 0, 0)

        addBackButton(root) { BlacklistScreen(BlacklistScreenDescription()) }
    }

    open fun generatePresets(): List<BlacklistPreset> {
        return BlacklistPreset.types
    }
}