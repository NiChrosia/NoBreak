package nichrosia.nobreak.content

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterials
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import kotlin.io.path.pathString

object NBSettings {
    private val configDir = File(FabricLoader.getInstance().configDir.pathString + "/nobreak")
    private val configFile = File(configDir.path + "/config.dat")

    var doBreak = false
    var doFeedback = true

    var diamondPlusToolsCanBreak = false
    var enchantedToolsCanBreak = false

    fun isBlacklisted(itemStack: ItemStack): Boolean {
        val material = (itemStack.item as ToolItem).material

        return ((material == ToolMaterials.DIAMOND ||
                material == ToolMaterials.NETHERITE ||
                material.miningLevel >= 3 && !diamondPlusToolsCanBreak) ||
                (itemStack.hasEnchantments() && !enchantedToolsCanBreak))
    }

    private fun createFiles() {
        configDir.mkdirs()
        configFile.createNewFile()
    }

    fun save() {
        if (!configFile.exists()) createFiles()

        val w = DataOutputStream(configFile.outputStream())

        w.writeBoolean(doBreak)
        w.writeBoolean(doFeedback)
        w.writeBoolean(diamondPlusToolsCanBreak)
        w.writeBoolean(enchantedToolsCanBreak)
    }

    fun load() {
        if (!configFile.exists()) return

        val r = DataInputStream(configFile.inputStream())

        doBreak = r.readBoolean()
        doFeedback = r.readBoolean()
        diamondPlusToolsCanBreak = r.readBoolean()
        enchantedToolsCanBreak = r.readBoolean()
    }
}