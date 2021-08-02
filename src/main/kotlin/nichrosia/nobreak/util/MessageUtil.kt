package nichrosia.nobreak.util

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import nichrosia.nobreak.content.NBSettings

object MessageUtil {
    private var lastMessageSentAt = 0L
    private const val messageRepeatTime = 250L

    internal fun PlayerEntity.inform(message: Text) {
        if (!NBSettings.doFeedback || System.currentTimeMillis() <= lastMessageSentAt + messageRepeatTime) return

        lastMessageSentAt = System.currentTimeMillis()

        sendMessage(message, true)
    }

    fun onOrOff(condition: Boolean) = if (condition) "on" else "off"
}