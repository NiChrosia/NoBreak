package nichrosia.nobreak.util

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import nichrosia.nobreak.content.NBSettings

object MessageUtilities {
    fun PlayerEntity.inform(message: Text) {
        sendMessage(message, true)
    }

    fun onOrOff(condition: Boolean, on: String = "on", off: String = "off") = if (condition) on else off
}