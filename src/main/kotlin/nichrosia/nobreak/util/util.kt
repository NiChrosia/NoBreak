package nichrosia.nobreak.util

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import nichrosia.nobreak.content.NBSettings
import kotlin.reflect.KMutableProperty0

fun PlayerEntity.inform(message: Text) {
    sendMessage(message, true)
}

fun onOrOff(condition: Boolean, on: String = "on", off: String = "off") = if (condition) on else off

fun ItemStack.hasUsableDurability(): Boolean {
    return isDamageable && ((maxDamage - damage) > 1)
}

fun <K, V> mapOf(list: List<Pair<K, V>>): Map<K, V> {
    return mapOf(*list.toTypedArray())
}