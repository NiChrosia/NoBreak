package nichrosia.nobreak.type.data.blacklist

import kotlinx.serialization.json.*
import net.minecraft.item.*
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import nichrosia.nobreak.NoBreak
import nichrosia.nobreak.type.annotation.data.JsonReadable
import nichrosia.nobreak.type.annotation.data.JsonWritable
import nichrosia.nobreak.util.id
import nichrosia.nobreak.util.mapOf

@Suppress("MemberVisibilityCanBePrivate", "NestedLambdaShadowedImplicitParameter")
data class BlacklistPreset(
    val ID: Identifier,
    var translationKey: String = "",
    var icon: ItemStack = ItemStack.EMPTY,
    var isBreakingAllowed: (ItemStack) -> Boolean = { false },
    var items: () -> List<Item> = { listOf() },
    var protectEnchantedItems: MutableMap<Item, Boolean> = mutableMapOf()
) : JsonWritable<JsonObject> {
    init {
        try {
            types.filter { it.ID == ID }.forEach {
                translationKey = it.translationKey
                icon = it.icon
                isBreakingAllowed = it.isBreakingAllowed
                items = it.items
            }
        } catch(e: Exception) {}
    }

    override fun toJson(): JsonObject {
        return JsonObject(mutableMapOf(
            "items" to JsonArray(items().distinctBy { it.id }.map { JsonPrimitive(it.id.toString()) }),
            "protected_enchanted_items" to JsonObject(mapOf(protectEnchantedItems.map { (item, toggled) ->
                item.id.toString() to JsonPrimitive(toggled)
            }.distinctBy { it.first })),
            "id" to JsonPrimitive(ID.toString())
        ))
    }

    fun addItems(items: List<Item>) {
        val previousItems = items()

        this.items = { (previousItems + items) }
    }

    fun addItem(item: Item) {
        if (items().contains(item)) return

        addItems(listOf(item))
    }

    fun removeItem(item: Item) {
        val previousItems = items()

        items = { previousItems.toMutableList().apply { remove(item) } }
    }

    fun toggleProtectEnchant(item: Item, default: Boolean = true) {
        protectEnchantedItems[item] = protectEnchantedItems[item]?.not() ?: default
    }

    fun toggleProtectEnchanted() {
        items().forEach {
            toggleProtectEnchant(it, true)
        }
    }

    fun breakingAllowedFor(stack: ItemStack): Boolean {
        val (blacklistContainsIt, breakingAllowed, stackNotDamageable, protectEnchantedItems) = arrayOf(
            items().contains(stack.item),
            isBreakingAllowed(stack),
            !stack.isDamageable,
            if (protectEnchantedItems[stack.item] == true) !stack.hasEnchantments() else false
        )

        return (blacklistContainsIt && breakingAllowed) || stackNotDamageable || protectEnchantedItems
    }

    @Suppress("unused")
    companion object : JsonReadable<BlacklistPreset, JsonObject> {
        override fun fromJson(json: JsonObject): BlacklistPreset {
            val (idNamespace, idPath) = json["id"]?.jsonPrimitive?.content?.split(":")!!
            val preset = BlacklistPreset(Identifier(idNamespace, idPath))

            json["items"]?.jsonArray?.forEach {
                it.jsonPrimitive.contentOrNull?.let {
                    val (namespace, path) = it.split(":")

                    preset.addItem(Registry.ITEM.get(Identifier(namespace, path)))
                }
            }

            preset.protectEnchantedItems.putAll(mapOf(json["protected_enchanted_items"]?.jsonObject?.map { (item, toggled) ->
                val (namespace, path) = item.split(":")

                Registry.ITEM.get(Identifier(namespace, path)) to toggled.jsonPrimitive.boolean
            } ?: listOf()))

            return preset
        }

        val custom = BlacklistPreset(NoBreak.idOf("custom"), "blacklist_preset.empty", ItemStack.EMPTY, { false }, { listOf() })

        val wood = BlacklistPreset(NoBreak.idOf("wood"), "blacklist_preset.wood", ItemStack(Items.WOODEN_PICKAXE), items = {
            Registry.ITEM.filter { (it as? ToolItem)?.let { it.material.miningLevel == 0 } == true }
        })

        val stone = BlacklistPreset(NoBreak.idOf("stone"), "blacklist_preset.stone", ItemStack(Items.STONE_PICKAXE), items = {
            Registry.ITEM.filter { (it as? ToolItem)?.let { it.material.miningLevel == 1 } == true }
        })

        val iron = BlacklistPreset(NoBreak.idOf("iron"), "blacklist_preset.iron", ItemStack(Items.IRON_PICKAXE), items = {
            Registry.ITEM.filter { (it as? ToolItem)?.let { it.material.miningLevel == 2 } == true }
        })

        val diamond = BlacklistPreset(NoBreak.idOf("diamond"), "blacklist_preset.diamond", ItemStack(Items.DIAMOND_PICKAXE), items = {
            Registry.ITEM.filter { (it as? ToolItem)?.let { it.material.miningLevel == 3 } == true }
        })

        val throwable = BlacklistPreset(NoBreak.idOf("throwable"), "blacklist_preset.throwable", ItemStack(Items.ARROW), items = {
            Registry.ITEM.filterIsInstance<RangedWeaponItem>() + Registry.ITEM.filterIsInstance<TridentItem>()
        })

        val types = mutableListOf(
            custom,
            wood,
            stone,
            iron,
            diamond,
            throwable
        )
    }
}