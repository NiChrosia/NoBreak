package nichrosia.nobreak.content

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.minecraft.item.ToolItem
import net.minecraft.text.TranslatableText
import nichrosia.nobreak.util.MessageUtil.inform

object NBEvents : NBContent {
    override fun register() {
        PlayerBlockBreakEvents.BEFORE.register(PlayerBlockBreakEvents.Before { _, playerEntity, _, _, _ ->
            if (NBSettings.doBreak) return@Before true

            val stack = playerEntity.getStackInHand(playerEntity.activeHand)

            if (stack.item !is ToolItem ||
                stack.maxDamage - stack.damage != 1 ||
                !NBSettings.isBlacklisted(stack)) return@Before true

            playerEntity.inform(TranslatableText("text.nobreak.tool_break_prevented", NBKeyBinds.toggleToolBreakage.boundKeyLocalizedText))

            return@Before false
        })

        ClientTickEvents.END_CLIENT_TICK.register {
            var hasToggled = false

            while (NBKeyBinds.toggleToolBreakage.wasPressed() && !hasToggled) {
                NBSettings.doBreak = !NBSettings.doBreak

                it.player?.inform(TranslatableText("text.nobreak.toggled_tool_breakage", if (NBSettings.doBreak) "on" else "off"))

                hasToggled = true
            }
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            NBSettings.save()
        }
    }
}