package nichrosia.nobreak.content

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import net.fabricmc.loader.api.FabricLoader
import nichrosia.nobreak.type.content.SavableContent
import nichrosia.nobreak.type.data.blacklist.BlacklistPreset
import org.apache.logging.log4j.LogManager
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import kotlin.io.path.pathString

@Suppress("MemberVisibilityCanBePrivate", "unused", "NestedLambdaShadowedImplicitParameter")
object NBSettings : SavableContent {
    private val configDir = File(FabricLoader.getInstance().configDir.pathString + "/nobreak")
    private val configFile = File(configDir.path + "/config.json")
    private val log = LogManager.getLogger("NoBreak")
    private val jsonFormat = Json { prettyPrint = true }

    val blacklists = object : ArrayList<BlacklistPreset>() {
        override fun contains(element: BlacklistPreset): Boolean {
            return map { it.translationKey }.contains(element.translationKey)
        }
    }

    val allBlacklists: List<BlacklistPreset>
        get() = blacklists + customBlacklist

    var customBlacklist = BlacklistPreset.custom.copy()
    var notifyUser = true

    var allowAttackingOwnPets = true
    var allowAttackingNeutralMobs = true

    var json: JsonObject
        get() = JsonObject(mapOf(
            "custom_blacklist" to customBlacklist.toJson(),
            "notify_user" to JsonPrimitive(notifyUser),
            "blacklists" to JsonArray(blacklists.map(BlacklistPreset::toJson))
        ))
        set(value) {
            try {
                value["custom_blacklist"]?.jsonObject?.let {
                    customBlacklist = BlacklistPreset.fromJson(it)
                }

                value["notify_user"]?.jsonPrimitive?.boolean?.let {
                    notifyUser = it
                }

                value["blacklists"]?.jsonArray?.forEach {
                    blacklists.add(BlacklistPreset.fromJson(it.jsonObject))
                }
            } catch(e: IllegalStateException) {
                log.error("Cannot load JSON config, resetting to default values. (Stacktrace: $e)")
            }
        }

    private fun createFiles() {
        configDir.mkdirs()
        configFile.createNewFile()
    }

    override fun save() {
        if (!configFile.exists()) createFiles()

        val write = DataOutputStream(configFile.outputStream())

        write.write(jsonFormat.encodeToString(json.toMap()).toByteArray())

        write.close()
    }

    override fun load() {
        if (!configFile.exists()) return

        val read = DataInputStream(configFile.inputStream())

        json = jsonFormat.parseToJsonElement(read.readBytes().decodeToString()).jsonObject

        read.close()

        log.info("Configuration parsed successfully.")
    }
}