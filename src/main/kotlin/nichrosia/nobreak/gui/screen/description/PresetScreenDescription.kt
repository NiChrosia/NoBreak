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
                            if (!NBSettings.blacklists.contains(it)) NBSettings.blacklists.add(it)
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
        return BlacklistPreset.types.toTypedArray()
    }

    @Suppress("MemberVisibilityCanBePrivate", "NestedLambdaShadowedImplicitParameter")
    data class BlacklistPreset(
        val translationKey: String,
        val icon: ItemStack,
        val isBreakingAllowed: (ItemStack) -> Boolean = { false },
        var items: (PlayerEntity?) -> List<Item> = { listOf() },
        val isEnchanted: (ItemStack) -> Boolean = { false }
    ) {
        init {
            types += this
        }

        fun addItems(items: Iterable<Item>): BlacklistPreset {
            val previousItems = items(null)

            this.items = { (previousItems + items) }

            return this
        }

        fun addItem(item: Item) = addItems(listOf(item))

        fun <T> MutableList<T>.removeAndReturn(item: T): MutableList<T> {
            remove(item)

            return this
        }

        fun removeItem(item: Item): BlacklistPreset {
            val previousItems = items(null)

            items = { previousItems.toMutableList().removeAndReturn(item) }

            return this
        }

        @Suppress("unused")
        companion object {
            val types = mutableListOf<BlacklistPreset>()

            val empty = BlacklistPreset("blacklist_preset.empty", ItemStack.EMPTY, { false }, { listOf() }) { false }

            val wood = BlacklistPreset("blacklist_preset.wood", ItemStack(Items.WOODEN_PICKAXE), items = {
                Registry.ITEM.filter { (it as? ToolItem)?.let { it.material.miningLevel == 0 } == true }
            })

            val stone = BlacklistPreset("blacklist_preset.stone", ItemStack(Items.STONE_PICKAXE), items = {
                Registry.ITEM.filter { (it as? ToolItem)?.let { it.material.miningLevel == 1 } == true }
            })

            val iron = BlacklistPreset("blacklist_preset.iron", ItemStack(Items.IRON_PICKAXE), items = {
                Registry.ITEM.filter { (it as? ToolItem)?.let { it.material.miningLevel == 2 } == true }
            })

            val diamond = BlacklistPreset("blacklist_preset.diamond", ItemStack(Items.DIAMOND_PICKAXE), items = {
                Registry.ITEM.filter { (it as? ToolItem)?.let { it.material.miningLevel == 3 } == true }
            })

            val throwable = BlacklistPreset("blacklist_preset.throwable", ItemStack(Items.ARROW), items = {
                Registry.ITEM.filterIsInstance<RangedWeaponItem>() + Registry.ITEM.filterIsInstance<TridentItem>()
            })
        }
    }
}