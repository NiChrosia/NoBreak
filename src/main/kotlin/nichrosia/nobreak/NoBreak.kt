package nichrosia.nobreak

import net.fabricmc.api.ClientModInitializer
import nichrosia.nobreak.content.*

@Suppress("MemberVisibilityCanBePrivate")
object NoBreak : ClientModInitializer {
    internal val content = arrayOf(
        NBKeyBinds,
        NBEvents
    )

    override fun onInitializeClient() {
        content.forEach(NBContent::register)

        NBSettings.load()
    }
}