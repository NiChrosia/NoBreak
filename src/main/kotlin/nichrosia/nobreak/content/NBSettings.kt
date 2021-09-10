package nichrosia.nobreak.content

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import nichrosia.nobreak.gui.screen.description.PresetScreenDescription
import org.apache.logging.log4j.LogManager
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import kotlin.io.path.pathString
import kotlin.properties.Delegates

@Suppress("MemberVisibilityCanBePrivate", "unused", "NestedLambdaShadowedImplicitParameter")
object NBSettings {
    private val configDir = File(FabricLoader.getInstance().configDir.pathString + "/nobreak")
    private val configFile = File(configDir.path + "/config.json")
    private val log = LogManager.getLogger("NoBreak")
    private val jsonFormat = Json { prettyPrint = true }

    val blacklists = object : ArrayList<PresetScreenDescription.BlacklistPreset>() {
        override fun contains(element: PresetScreenDescription.BlacklistPreset): Boolean {
            return map { it.translationKey }.contains(element.translationKey)
        }
    }

    var customBlacklist = PresetScreenDescription.BlacklistPreset.empty
    var notifyUser = true

    var allowAttackingOwnPets = true
    var allowAttackingNeutralMobs = true

    var json: JsonObject
        get() = JsonObject(mapOf(
            "custom_blacklist" to JsonArray(customBlacklist.items(null).map { JsonPrimitive(Registry.ITEM.getId(it).toString()) }),
            "notify_user" to JsonPrimitive(notifyUser)
        ))
        set(value) {
            try {
                value["custom_blacklist"]?.jsonArray?.map { it.jsonPrimitive.content }?.let {
                    customBlacklist.addItems(it.map {
                        Registry.ITEM.get(it.run {
                            val (namespace, path) = split(":")

                            Identifier(namespace, path)
                        })
                    })
                }

                value["notify_user"]?.jsonPrimitive?.boolean?.let {
                    notifyUser = it
                }
            } catch(e: IllegalStateException) {
                log.error("Cannot load JSON config, resetting to default values. (Stacktrace: $e)")
            }
        }

    fun List<Boolean>.sumByOr(): Boolean {
        var value by Delegates.notNull<Boolean>()

        forEachIndexed { i, element ->
            value = if (i == 0) element else value || element
        }

        return if (size > 0) value else false
    }

    fun List<Boolean>.sumByAnd(): Boolean {
        var value by Delegates.notNull<Boolean>()

        forEachIndexed { i, element ->
            value = if (i == 0) element else value && element
        }

        return if (size > 0) value else false
    }

    fun List<PresetScreenDescription.BlacklistPreset>.sumByBreakingAllowedFor(stack: ItemStack, player: PlayerEntity): Boolean {
        return map {
            it.breakingAllowedFor(stack, player)
        }.sumByOr()
    }

    fun PresetScreenDescription.BlacklistPreset.breakingAllowedFor(stack: ItemStack, player: PlayerEntity): Boolean {
        return (items(player).contains(stack.item) ||
                isBreakingAllowed(stack)) ||
                !stack.isDamageable ||
                (if (stack.hasEnchantments()) isEnchanted(stack) else false)
    }

    fun breakingAllowedFor(stack: ItemStack, player: PlayerEntity): Boolean {
        return customBlacklist.breakingAllowedFor(stack, player) || blacklists.sumByBreakingAllowedFor(stack, player)
    }

    fun shouldSucceed(stack: ItemStack): Boolean {
        return stack.isEmpty || stack.isDamageable && ((stack.maxDamage - stack.damage) != 1)
    }

    private fun createFiles() {
        configDir.mkdirs()
        configFile.createNewFile()
    }

    fun save() {
        if (!configFile.exists()) createFiles()

        val write = DataOutputStream(configFile.outputStream())

        write.write(jsonFormat.encodeToString(json.toMap()).toByteArray())

        write.close()
    }

    fun load() {
        if (!configFile.exists()) return

        val read = DataInputStream(configFile.inputStream())

        json = jsonFormat.parseToJsonElement(read.readBytes().decodeToString()).jsonObject

        read.close()

        log.info("Configuration parsed successfully.")
    }
}