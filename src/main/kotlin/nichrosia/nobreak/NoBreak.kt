package nichrosia.nobreak

import net.fabricmc.api.ClientModInitializer
import nichrosia.nobreak.content.*
import nichrosia.nobreak.content.type.Content
import nichrosia.nobreak.type.mod.IdentifiedModInit
import org.apache.logging.log4j.LogManager

@Suppress("MemberVisibilityCanBePrivate", "unused")
object NoBreak : ClientModInitializer, IdentifiedModInit {
    override val modID = "nobreak"
    private val log = LogManager.getLogger("NoBreak")

    internal val content = arrayOf(
        NBKeyBinds,
        NBEvents
    )

    override fun onInitializeClient() {
        content.forEach(Content::load)

        NBSettings.load()

        log.info("Loaded mod '$modID' successfully.")
    }
}