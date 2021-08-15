package nichrosia.nobreak.content

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.minecraft.item.ToolItem
import net.minecraft.text.TranslatableText
import nichrosia.nobreak.util.MessageUtil.inform
import nichrosia.nobreak.util.MessageUtil.onOrOff

object NBEvents : NBContent {
    override fun register() {
        PlayerBlockBreakEvents.BEFORE.register(PlayerBlockBreakEvents.Before { _, playerEntity, _, _, _ ->
            if (NBSettings.allowBreakage) return@Before true

            val stack = playerEntity.getStackInHand(playerEntity.activeHand)

            if (stack.item !is ToolItem ||
                stack.maxDamage - stack.damage != 1 ||
                !NBSettings.isBlacklisted(stack)) return@Before true

            playerEntity.inform(TranslatableText("text.nobreak.tool_break_prevented", NBKeyBinds.toggleToolBreakage.boundKeyLocalizedText))

            return@Before false
        })

        ClientTickEvents.END_CLIENT_TICK.register {
            it.player?.let { player ->
                var hasToggled = false

                while (NBKeyBinds.toggleToolBreakage.wasPressed() && !hasToggled) {
                    NBSettings.allowBreakage = !NBSettings.allowBreakage

                    player.inform(
                        TranslatableText(
                            "text.nobreak.toggled_tool_breakage",
                            onOrOff(NBSettings.allowBreakage)
                        )
                    )

                    hasToggled = true
                }
            }
        }

        ClientTickEvents.END_CLIENT_TICK.register {
            it.player?.let { player ->
                var hasToggled = false

                while (NBKeyBinds.toggleCurrentItemBlacklist.wasPressed() && !hasToggled) {
                    player.mainHandStack.item.let { item ->
                        if (NBSettings.toolBlacklist.contains(item)) {
                            NBSettings.toolBlacklist.remove(item)
                        } else {
                            NBSettings.toolBlacklist.add(item)
                        }
                    }

                    player.inform(
                        TranslatableText(
                            "text.nobreak.toggled_item_blacklist",
                            onOrOff(NBSettings.toolBlacklist.contains(player.mainHandStack.item)),
                            player.mainHandStack.item.name
                        )
                    )

                    hasToggled = true
                }
            }
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            NBSettings.save()
        }
    }
}