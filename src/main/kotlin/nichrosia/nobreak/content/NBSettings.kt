package nichrosia.nobreak.content

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import nichrosia.nobreak.gui.screen.description.PresetScreenDescription
import nichrosia.nobreak.util.DataStreams.bool
import nichrosia.nobreak.util.DataStreams.str
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import kotlin.io.path.pathString

object NBSettings {
    private val configDir = File(FabricLoader.getInstance().configDir.pathString + "/nobreak")
    private val configFile = File(configDir.path + "/config.dat")

    var blacklist = PresetScreenDescription.BlacklistPreset.empty
    var notifyUser = true

    fun breakingAllowedFor(itemStack: ItemStack, player: PlayerEntity): Boolean {
        println("Item blacklist contains item: ${blacklist.items(player).contains(itemStack.item)}")
        println("Breaking allowed for item stack: ${blacklist.isBreakingAllowed(itemStack)}")
        println("ItemStack not damageable: ${!itemStack.isDamageable}")
        println("Enchantment predicate: ${(if (itemStack.hasEnchantments()) blacklist.isEnchanted(itemStack) else false)}")
        println("Total: ${blacklist.items(player).contains(itemStack.item)} | ${blacklist.isBreakingAllowed(itemStack)} | ${!itemStack.isDamageable} | ${(if (itemStack.hasEnchantments()) blacklist.isEnchanted(itemStack) else false)} = ${(blacklist.items(player).contains(itemStack.item) ||
                blacklist.isBreakingAllowed(itemStack)) ||
                (if (itemStack.hasEnchantments()) blacklist.isEnchanted(itemStack) else false)}\n\n")

        return (blacklist.items(player).contains(itemStack.item) ||
                blacklist.isBreakingAllowed(itemStack)) ||
                !itemStack.isDamageable ||
                (if (itemStack.hasEnchantments()) blacklist.isEnchanted(itemStack) else false)

    }

    fun shouldSucceed(stack: ItemStack): Boolean {
        println("Stack empty: ${stack.isEmpty}")
        println("Stack damageable: ${stack.isDamageable}")
        println("Stack damage: ${stack.maxDamage - stack.damage}/${stack.maxDamage}")
        println("Total: ${stack.isEmpty} | ${stack.isDamageable} & ${stack.maxDamage - stack.damage != 1} = ${stack.isEmpty || stack.isDamageable && ((stack.maxDamage - stack.damage) != 1)}\n")

        return stack.isEmpty || stack.isDamageable && ((stack.maxDamage - stack.damage) != 1)
    }

    private fun createFiles() {
        configDir.mkdirs()
        configFile.createNewFile()
    }

    fun save() {
        if (!configFile.exists()) createFiles()

        val write = DataOutputStream(configFile.outputStream())

        write.bool(notifyUser)
        write.str(blacklist.items(null).joinToString("|") { Registry.ITEM.getId(it).toString() })
    }

    fun load() {
        if (!configFile.exists()) return

        val read = DataInputStream(configFile.inputStream())

        notifyUser = read.bool()
        blacklist.addItems(read.str().split("|").map { Registry.ITEM.get(Identifier(it)) })
    }
}