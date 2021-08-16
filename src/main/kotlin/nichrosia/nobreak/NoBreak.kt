package nichrosia.nobreak

import net.fabricmc.api.ClientModInitializer
import nichrosia.nobreak.content.*
import nichrosia.nobreak.content.type.Content

@Suppress("MemberVisibilityCanBePrivate", "unused")
object NoBreak : ClientModInitializer {
    internal val content = arrayOf(
        NBKeyBinds,
        NBEvents
    )

    override fun onInitializeClient() {
        content.forEach(Content::load)

        NBSettings.load()
    }
}