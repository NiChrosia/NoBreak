package nichrosia.nobreak.content

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.mob.Angerable
import net.minecraft.entity.passive.TameableEntity
import net.minecraft.item.ShieldItem
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import nichrosia.nobreak.content.type.Content
import nichrosia.nobreak.gui.screen.BlacklistScreen
import nichrosia.nobreak.util.MessageUtilities.inform
import nichrosia.nobreak.util.MessageUtilities.onOrOff
import kotlin.math.absoluteValue

object NBEvents : Content {
    override fun load() {
        PlayerBlockBreakEvents.BEFORE.register Before@ { _, playerEntity, _, _, _ ->
            val stack = playerEntity.getStackInHand(playerEntity.activeHand)

            if (NBSettings.shouldSucceed(stack)) return@Before true
            if (NBSettings.breakingAllowedFor(stack, playerEntity)) return@Before true

            playerEntity.inform(
                TranslatableText(
                    "text.nobreak.tool_break_prevented",
                    NBKeyBinds.toggleCurrentItemBlacklist.boundKeyLocalizedText
                )
            )

            return@Before false
        }

//        AttackEntityCallback.EVENT.register Before@ { player, _, hand, victim, _ ->
//            val stack = player.getStackInHand(hand)
//
//            if (victim is TameableEntity) {
//                if (victim.ownerUuid == player.uuid) {
//                    if (NBSettings.allowAttackingOwnPets) {
//                        return@Before ActionResult.PASS
//                    } else {
//                        return@Before ActionResult.FAIL
//                    }
//                }
//            }
//
//            if (victim is Angerable) {
//                if (victim.angryAt != player.uuid) {
//                    if (NBSettings.allowAttackingNeutralMobs) {
//                        return@Before ActionResult.PASS
//                    } else {
//                        return@Before ActionResult.FAIL
//                    }
//                }
//            }
//
//            if (NBSettings.shouldSucceed(stack)) return@Before ActionResult.SUCCESS
//            if (NBSettings.breakingAllowedFor(stack, player)) return@Before ActionResult.SUCCESS
//
//            player.inform(TranslatableText("text.nobreak.tool_break_prevented", NBKeyBinds.toggleCurrentItemBlacklist.boundKeyLocalizedText))
//
//            return@Before ActionResult.FAIL
//        }
//
//        UseBlockCallback.EVENT.register Before@ { player, _, hand, _ ->
//            val stack = player.getStackInHand(hand)
//
//            if (NBSettings.shouldSucceed(stack)) return@Before ActionResult.SUCCESS
//            if (NBSettings.breakingAllowedFor(stack, player)) return@Before ActionResult.PASS
//
//            player.inform(TranslatableText("text.nobreak.tool_break_prevented", NBKeyBinds.toggleCurrentItemBlacklist.boundKeyLocalizedText))
//
//            return@Before ActionResult.FAIL
//        }

        ClientTickEvents.END_CLIENT_TICK.register {
            it.player?.let tick@{ player ->
                if (NBKeyBinds.toggleCurrentItemBlacklist.wasPressed()) {
                    player.mainHandStack.item.let { item ->
                        if (!item.isDamageable) return@tick

                        if (item is ShieldItem) {
                            player.inform(TranslatableText("text.nobreak.shields_not_supported"))

                            return@tick
                        }

                        val currentBlacklist = NBSettings.blacklist.copy()

                        NBSettings.blacklist = if (NBSettings.blacklist.items(player).contains(item)) {
                             NBSettings.blacklist.copy(items = { currentBlacklist.items(player) - item })
                        } else {
                            NBSettings.blacklist.copy(items = { currentBlacklist.items(player) + item })
                        }
                    }

                    player.inform(TranslatableText(
                        "text.nobreak.toggled_item_blacklist",
                        onOrOff(NBSettings.blacklist.items(player).contains(player.mainHandStack.item)),
                    player.mainHandStack.item.name))
                }
            }

            if (NBKeyBinds.openBlacklistScreen.wasPressed()) {
                MinecraftClient.getInstance().setScreen(BlacklistScreen())
            }
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            NBSettings.save()
        }
    }
}