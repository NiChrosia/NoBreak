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
import java.io.File
import kotlin.io.path.pathString
import kotlin.properties.Delegates

@Suppress("MemberVisibilityCanBePrivate", "unused")
object NBSettings {
    private val configDir = File(FabricLoader.getInstance().configDir.pathString + "/nobreak")
    private val configFile = File(configDir.path + "/config.dat")
    private val log = LogManager.getLogger("NoBreak")

    val blacklists = object : ArrayList<PresetScreenDescription.BlacklistPreset>() {
        override fun contains(element: PresetScreenDescription.BlacklistPreset): Boolean {
            return map { it.translationKey }.contains(element.translationKey)
        }
    }

    var customBlacklist = PresetScreenDescription.BlacklistPreset.empty
    var notifyUser = true

    var allowAttackingOwnPets = true
    var allowAttackingNeutralMobs = true

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

        write.bool(notifyUser)
        write.bool(allowAttackingOwnPets)
        write.bool(allowAttackingNeutralMobs)
        write.str(
            customBlacklist.items(null).joinToString("|") { Registry.ITEM.getId(it).toString() } +
            "\n" +
            blacklists.joinToString("|") { it.translationKey }
        )
    }

    fun load() {
        if (!configFile.exists()) return

        try {
            val read = DataInputStream(configFile.inputStream())

            notifyUser = read.bool()
            allowAttackingOwnPets = read.bool()
            allowAttackingNeutralMobs = read.bool()

            val (rawCustomBlacklist, rawBlacklists) = read.str().split("\n")

            customBlacklist.addItems(rawCustomBlacklist.split("|").map { Registry.ITEM.get(Identifier(it)) })
            blacklists.addAll(PresetScreenDescription.BlacklistPreset.types.filter { rawBlacklists.split("|").contains(it.translationKey) })
        } catch(e: Exception) {
            log.warn("Invalid config, setting values to default.")
        }
    }
}