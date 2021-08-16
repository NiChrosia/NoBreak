package nichrosia.nobreak.gui.screen.description

import io.github.cottonmc.cotton.gui.widget.WBox
import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WLabel
import io.github.cottonmc.cotton.gui.widget.data.Axis
import io.github.cottonmc.cotton.gui.widget.data.Insets
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.*
import net.minecraft.text.TranslatableText
import net.minecraft.util.registry.Registry
import nichrosia.nobreak.content.NBSettings
import nichrosia.nobreak.gui.screen.BlacklistScreen
import nichrosia.nobreak.gui.screen.description.type.LessLightweightGuiDescription

@Suppress("LeakingThis")
open class PresetScreenDescription : LessLightweightGuiDescription() {
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
                    val button = WButton(ItemIcon(it.icon), TranslatableText(it.translationKey)).apply {
                        onClick = Runnable {
                            NBSettings.blacklist = it
                        }
                    }

                    add(button, 100, 20)

                    val description = WLabel(TranslatableText("${it.translationKey}.desc"))

                    add(description)
                }

                add(rowBox)
            }
        }

        root.add(presetBox, 0, 0)

        addBackButton(root) { BlacklistScreen(BlacklistScreenDescription()) }
    }

    open fun generatePresets(): Array<BlacklistPreset> {
        return arrayOf(
            BlacklistPreset.empty,
            BlacklistPreset.ironMinus
        )
    }

    @Suppress("MemberVisibilityCanBePrivate", "NestedLambdaShadowedImplicitParameter")
    data class BlacklistPreset(
        val translationKey: String,
        val icon: ItemStack,
        val isBreakingAllowed: (ItemStack) -> Boolean = { false },
        var items: (PlayerEntity?) -> List<Item> = { listOf() },
        val isEnchanted: (ItemStack) -> Boolean = { false }
    ) {
        fun addItems(items: Iterable<Item>) {
            val previousItems = items(null)

            this.items = { (previousItems + items) }
        }

        companion object {
            val PlayerInventory.all: List<ItemStack>
                get() = main + armor + offHand

            val empty = BlacklistPreset("blacklist_preset.empty", ItemStack.EMPTY, { false }, { listOf() }) { false }

            val ironMinus = BlacklistPreset("blacklist_preset.iron_minus", ItemStack(Items.IRON_PICKAXE), items = {
                Registry.ITEM.filter { (it as? ToolItem)?.let { it.material.miningLevel <= 2 } == true }
            })
        }
    }
}