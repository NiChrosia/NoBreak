package nichrosia.nobreak.content

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolItem
import net.minecraft.item.ToolMaterials
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
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

    val toolBlacklist = mutableListOf<Item>()

    fun isBlacklisted(itemStack: ItemStack): Boolean {
        val material = (itemStack.item as ToolItem).material

        return ((material == ToolMaterials.DIAMOND ||
                material == ToolMaterials.NETHERITE ||
                material.miningLevel >= 3 && !diamondPlusToolsCanBreak) ||
                (itemStack.hasEnchantments() && !enchantedToolsCanBreak) ||
                toolBlacklist.contains(itemStack.item))
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

        w.writeItemArr(toolBlacklist)
    }

    fun load() {
        if (!configFile.exists()) return

        val r = DataInputStream(configFile.inputStream())

        doBreak = r.readBoolean()
        doFeedback = r.readBoolean()
        diamondPlusToolsCanBreak = r.readBoolean()
        enchantedToolsCanBreak = r.readBoolean()

        toolBlacklist.addAll(r.readItemArr())
    }

    private fun DataOutputStream.writeItemArr(arr: Iterable<Item>) {
        writeUTF(arr.joinToString(separator = "|") {
            Registry.ITEM.getId(it).toString()
        })
    }

    private fun DataInputStream.readItemArr(): MutableList<Item> {
        println("Reading item array.")

        val output = mutableListOf<Item>()
        val items = readUTF().split("|")

        println("Raw items: $items")

        for (it in items) {
            println("Raw item: $it")
            if (!it.contains(":")) continue

            val (namespace, path) = it.split(":")
            val identity = Identifier(namespace, path)

            println("Proper item: $identity")

            println("Adding item '${Registry.ITEM.get(identity)}' to output.")
            output.add(Registry.ITEM.get(identity))
        }

        return output
    }
}