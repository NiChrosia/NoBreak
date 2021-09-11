package nichrosia.nobreak.type.data.blacklist

import kotlinx.serialization.json.*
import net.minecraft.item.*
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import nichrosia.nobreak.type.annotation.data.JsonReadable
import nichrosia.nobreak.type.annotation.data.JsonWritable
import nichrosia.nobreak.util.mapOf

@Suppress("MemberVisibilityCanBePrivate", "NestedLambdaShadowedImplicitParameter")
data class BlacklistPreset(
    var translationKey: String = "",
    var icon: ItemStack = ItemStack.EMPTY,
    var isBreakingAllowed: (ItemStack) -> Boolean = { false },
    var items: () -> List<Item> = { listOf() },
    var protectEnchantedItems: MutableMap<Item, Boolean> = mutableMapOf()
) : JsonWritable<JsonObject> {
    var id: Int
        get() = types.indexOf(this)
        set(value) {
            if (value > 0) types[value].let {
                translationKey = it.translationKey
                icon = it.icon
                items = it.items
            }
        }

    override fun toJson(): JsonObject {
        return JsonObject(mutableMapOf(
            "items" to JsonArray(items().map { JsonPrimitive(Registry.ITEM.getId(it).toString()) }),
            "protected_enchanted_items" to JsonObject(mapOf(protectEnchantedItems.map { (item, toggled) ->
                Registry.ITEM.getId(item).toString() to JsonPrimitive(toggled)
            }))
        ).apply { if (id != -1) put("id", JsonPrimitive(id)) })
    }

    fun addItems(items: Iterable<Item>) {
        val previousItems = items()

        this.items = { (previousItems + items) }
    }

    fun addItem(item: Item) = addItems(listOf(item))

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
            val preset = BlacklistPreset()

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

            json["id"]?.jsonPrimitive?.intOrNull?.let { preset.id = it }

            return preset
        }

        val custom = BlacklistPreset("blacklist_preset.empty", ItemStack.EMPTY, { false }, { listOf() })

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