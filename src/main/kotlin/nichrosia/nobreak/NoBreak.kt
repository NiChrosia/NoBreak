package nichrosia.nobreak

import net.fabricmc.api.ClientModInitializer
import nichrosia.nobreak.content.*
import nichrosia.nobreak.type.content.Content
import org.apache.logging.log4j.LogManager

@Suppress("MemberVisibilityCanBePrivate", "unused")
object NoBreak : ClientModInitializer {
    private val log = LogManager.getLogger("NoBreak")
    internal val content = arrayOf(NBKeyBinds, NBEvents, NBSettings)

    override fun onInitializeClient() {
        content.forEach(Content::load)

        log.info("Loaded mod successfully.")
    }
}