package nichrosia.nobreak.util

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import nichrosia.nobreak.content.NBSettings

object MessageUtilities {
    private var lastMessageSentAt = 0L
    private const val messageRepeatTime = 250L

    fun PlayerEntity.inform(message: Text) {
        if (!NBSettings.notifyUser || System.currentTimeMillis() <= lastMessageSentAt + messageRepeatTime) return

        lastMessageSentAt = System.currentTimeMillis()

        sendMessage(message, true)
    }

    fun onOrOff(condition: Boolean, on: String = "on", off: String = "off") = if (condition) on else off
}