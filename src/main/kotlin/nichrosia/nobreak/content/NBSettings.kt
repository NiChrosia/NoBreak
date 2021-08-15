package nichrosia.nobreak.content

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import nichrosia.nobreak.util.DataStreams.readItemArr
import nichrosia.nobreak.util.DataStreams.writeItemArr
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import kotlin.io.path.pathString

object NBSettings {
    private val configDir = File(FabricLoader.getInstance().configDir.pathString + "/nobreak")
    private val configFile = File(configDir.path + "/config.dat")

    val toolBlacklist = mutableListOf<Item>()

    var allowBreakage = false
    var notifyUser = true

    var diamondPlusToolsCanBreak = false
    var enchantedToolsCanBreak = false

    fun isBlacklisted(itemStack: ItemStack): Boolean {
        return toolBlacklist.contains(itemStack.item)
    }

    private fun createFiles() {
        configDir.mkdirs()
        configFile.createNewFile()
    }

    fun save() {
        if (!configFile.exists()) createFiles()

        val w = DataOutputStream(configFile.outputStream())

        w.writeBoolean(allowBreakage)
        w.writeBoolean(notifyUser)
        w.writeBoolean(diamondPlusToolsCanBreak)
        w.writeBoolean(enchantedToolsCanBreak)

        w.writeItemArr(toolBlacklist)
    }

    fun load() {
        if (!configFile.exists()) return

        val r = DataInputStream(configFile.inputStream())

        allowBreakage = r.readBoolean()
        notifyUser = r.readBoolean()
        diamondPlusToolsCanBreak = r.readBoolean()
        enchantedToolsCanBreak = r.readBoolean()

        toolBlacklist.addAll(r.readItemArr())
    }
}