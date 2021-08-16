package nichrosia.nobreak.gui.screen.description

import io.github.cottonmc.cotton.gui.widget.WBox
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.data.Axis
import io.github.cottonmc.cotton.gui.widget.data.Insets
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.ToolItem
import net.minecraft.text.TranslatableText
import nichrosia.nobreak.content.NBSettings

@Suppress("LeakingThis")
open class PresetScreenDescription(from: BlacklistScreenDescription) : LessLightweightGuiDescription() {
    open val presetBox: WBox

    init {
        val root = WGridPanel().apply {
            setSize(256, 240)
            insets = Insets.ROOT_PANEL
        }

        setRootPanel(root)

        presetBox = WBox(Axis.VERTICAL).apply {
            generatePresets().forEach {
                add(WButton(ItemIcon(it.icon), TranslatableText(it.translationKey)).apply {
                    onClick = Runnable {
                        NBSettings.blacklist = it
                    }
                }, 80, 20)
            }
        }

        root.add(presetBox, 0, 0)

        addBackButton(from, root)
    }

    open fun generatePresets(): Array<BlacklistPreset> {
        return arrayOf(
            BlacklistPreset("blacklist_preset.diamond_plus", ItemStack(Items.DIAMOND_PICKAXE), {
                (it.item as? ToolItem)?.let { toolItem ->
                    toolItem.material.miningLevel >= 3
                } == true
            }),
            BlacklistPreset("blacklist_preset.enchanted", ItemStack(Items.ENCHANTED_BOOK), {
                it.enchantments.isNotEmpty()
            })
        )
    }

    data class BlacklistPreset(val translationKey: String, val icon: ItemStack, val itemFilter: (ItemStack) -> Boolean, val items: () -> Array<ItemStack> = { arrayOf() })
}