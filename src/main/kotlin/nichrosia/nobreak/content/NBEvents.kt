package nichrosia.nobreak.content

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ShieldItem
import net.minecraft.text.TranslatableText
import nichrosia.nobreak.content.type.Content
import nichrosia.nobreak.gui.screen.BlacklistScreen
import nichrosia.nobreak.util.MessageUtilities.inform
import nichrosia.nobreak.util.MessageUtilities.onOrOff

@Suppress("NestedLambdaShadowedImplicitParameter", "MemberVisibilityCanBePrivate")
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

        /* (Discontinued.)
        AttackEntityCallback.EVENT.register Before@ { player, _, hand, victim, _ ->
            val stack = player.getStackInHand(hand)

            if (victim is TameableEntity) {
                if (victim.ownerUuid == player.uuid) {
                    if (NBSettings.allowAttackingOwnPets) {
                        return@Before ActionResult.PASS
                    } else {
                        if (NBSettings.shouldSucceed(stack)) {
                            return@Before ActionResult.PASS
                        } else {
                            if (NBSettings.breakingAllowedFor(stack, player)) {
                                return@Before ActionResult.SUCCESS
                            }
                        }
                    }
                }
            }

            if (victim is Angerable) {
                if (victim.angryAt != player.uuid) {
                    if (NBSettings.allowAttackingNeutralMobs) {
                        return@Before ActionResult.PASS
                    } else {
                        if (NBSettings.shouldSucceed(stack)) {
                            return@Before ActionResult.PASS
                        } else {
                            if (NBSettings.breakingAllowedFor(stack, player)) {
                                return@Before ActionResult.SUCCESS
                            }
                        }
                    }
                }
            }

            if (NBSettings.shouldSucceed(stack)) return@Before ActionResult.PASS
            if (NBSettings.breakingAllowedFor(stack, player)) return@Before ActionResult.SUCCESS

            player.inform(TranslatableText("text.nobreak.tool_break_prevented", NBKeyBinds.toggleCurrentItemBlacklist.boundKeyLocalizedText))

            return@Before ActionResult.FAIL
        }

        UseBlockCallback.EVENT.register Before@ { player, _, hand, _ ->
            val stack = player.getStackInHand(hand)

            if (NBSettings.shouldSucceed(stack)) return@Before ActionResult.PASS
            if (NBSettings.breakingAllowedFor(stack, player)) return@Before ActionResult.PASS

            player.inform(TranslatableText("text.nobreak.tool_break_prevented", NBKeyBinds.toggleCurrentItemBlacklist.boundKeyLocalizedText))

            return@Before ActionResult.FAIL
        } */

        ClientTickEvents.END_CLIENT_TICK.register {
            it.player?.let tick@{ player ->
                if (NBKeyBinds.toggleCurrentItemBlacklist.wasPressed()) {
                    player.mainHandStack.item.let { item ->
                        if (!item.isDamageable) return@tick

                        if (item is ShieldItem) {
                            player.inform(TranslatableText("text.nobreak.shields_not_supported"))

                            return@tick
                        }

                        if (NBSettings.customBlacklist.items(player).contains(item)) {
                            NBSettings.customBlacklist.removeItem(item)
                        } else {
                            NBSettings.customBlacklist.addItem(item)
                        }
                    }

                    player.inform(TranslatableText(
                        "text.nobreak.toggled_item_blacklist",
                        onOrOff(NBSettings.customBlacklist.items(player).contains(player.mainHandStack.item)),
                    player.mainHandStack.item.name))
                }

                /* Location for adding support for disabling armor, if that were to happen in the future

                [...]

                */
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