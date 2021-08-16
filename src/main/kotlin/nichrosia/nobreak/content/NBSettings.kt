package nichrosia.nobreak.content

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import nichrosia.nobreak.gui.screen.description.PresetScreenDescription
import nichrosia.nobreak.util.DataStreams
import nichrosia.nobreak.util.DataStreams.bool
import nichrosia.nobreak.util.DataStreams.readItemArr
import nichrosia.nobreak.util.DataStreams.writeItemArr
import java.io.*
import kotlin.io.path.pathString

object NBSettings {
    private val configDir = File(FabricLoader.getInstance().configDir.pathString + "/nobreak")
    private val configFile = File(configDir.path + "/config.dat")

    val toolBlacklist = mutableListOf<Item>()
    lateinit var blacklist: PresetScreenDescription.BlacklistPreset

    var allowBreakage = false
    var notifyUser = true

    fun isBlacklisted(itemStack: ItemStack): Boolean {
        return toolBlacklist.contains(itemStack.item)
    }

    private fun createFiles() {
        configDir.mkdirs()
        configFile.createNewFile()
    }

    fun save() {
        if (!configFile.exists()) createFiles()

        val write = DataOutputStream(configFile.outputStream())

        write.bool(allowBreakage)
        write.bool(notifyUser)

        write.writeItemArr(toolBlacklist)
    }

    fun load() {
        if (!configFile.exists()) return

        val read = DataInputStream(configFile.inputStream())

        allowBreakage = read.bool()
        notifyUser = read.bool()

        toolBlacklist.addAll(read.readItemArr())
    }
}