package nichrosia.nobreak.content

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import nichrosia.nobreak.gui.screen.description.PresetScreenDescription
import nichrosia.nobreak.util.DataStreams.bool
import nichrosia.nobreak.util.DataStreams.str
import org.apache.logging.log4j.LogManager
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import java.io.File
import kotlin.io.path.pathString

object NBSettings {
    private val configDir = File(FabricLoader.getInstance().configDir.pathString + "/nobreak")
    private val configFile = File(configDir.path + "/config.dat")
    private val log = LogManager.getLogger("NoBreak")

    var blacklist = PresetScreenDescription.BlacklistPreset.empty
    var notifyUser = true

    var allowAttackingOwnPets = true
    var allowAttackingNeutralMobs = true

    fun breakingAllowedFor(itemStack: ItemStack, player: PlayerEntity): Boolean {
        return (blacklist.items(player).contains(itemStack.item) ||
                blacklist.isBreakingAllowed(itemStack)) ||
                !itemStack.isDamageable ||
                (if (itemStack.hasEnchantments()) blacklist.isEnchanted(itemStack) else false)

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

        write.bool(notifyUser)
        write.bool(allowAttackingOwnPets)
        write.bool(allowAttackingNeutralMobs)
        write.str(blacklist.items(null).joinToString("|") { Registry.ITEM.getId(it).toString() })
    }

    fun load() {
        if (!configFile.exists()) return

        try {
            val read = DataInputStream(configFile.inputStream())

            notifyUser = read.bool()
            allowAttackingOwnPets = read.bool()
            allowAttackingNeutralMobs = read.bool()
            blacklist.addItems(read.str().split("|").map { Registry.ITEM.get(Identifier(it)) })
        } catch(e: EOFException) {
            log.warn("Invalid config, setting values to default.")
        }
    }
}