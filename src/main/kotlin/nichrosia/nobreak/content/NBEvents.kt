package nichrosia.nobreak.content

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ShieldItem
import net.minecraft.text.TranslatableText
import nichrosia.nobreak.type.content.Content
import nichrosia.nobreak.type.gui.description.BlacklistScreenDescription
import nichrosia.nobreak.type.gui.screen.BlacklistScreen
import nichrosia.nobreak.util.*

@Suppress("NestedLambdaShadowedImplicitParameter", "MemberVisibilityCanBePrivate")
object NBEvents : Content {
    override fun load() {
        PlayerBlockBreakEvents.BEFORE.register Before@ { _, playerEntity, _, _, _ ->
            val stack = playerEntity.getStackInHand(playerEntity.activeHand)

            if (stack.hasUsableDurability() || stack.isEmpty) return@Before true
            if (NBSettings.allBlacklists.any { it.breakingAllowedFor(stack) }) return@Before true

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
                if (NBKeyBinds.toggleCurrentItemBlacklist.wasPressed()) run toggleCurrent@ {
                    player.mainHandStack.item.let { item ->
                        if (!item.isDamageable) return@toggleCurrent

                        if (item is ShieldItem) {
                            player.inform(TranslatableText("text.nobreak.shields_not_supported"))

                            return@toggleCurrent
                        }

                        when {
                            NBSettings.customBlacklist.items().contains(item) -> NBSettings.customBlacklist.removeItem(item)

                            NBSettings.blacklists.any { it.items().contains(item) } -> {
                                player.inform(TranslatableText("text.nobreak.cannot_remove_item_from_preset"))
                                return@toggleCurrent
                            }

                            else -> {
                                NBSettings.customBlacklist.addItem(item)
                            }
                        }
                    }

                    player.inform(TranslatableText(
                        "text.nobreak.toggled_item_blacklist",
                        onOrOff(NBSettings.allBlacklists.any { it.items().contains(player.mainHandStack.item) }),
                    player.mainHandStack.item.name))
                }

                if (NBKeyBinds.toggleProtectEnchanted.wasPressed()) run protectEnchanted@ {
                    player.mainHandStack.item.let { item ->
                        if (!item.isDamageable) return@protectEnchanted

                        if (item is ShieldItem) {
                            player.inform(TranslatableText("text.nobreak.shields_not_supported"))

                            return@protectEnchanted
                        }

                        NBSettings.allBlacklists.forEach { it.toggleProtectEnchant(item) }

                        player.inform(TranslatableText(
                            "text.nobreak.toggled_protecting_enchanted_item",
                            onOrOff(NBSettings.customBlacklist.protectEnchantedItems[item]!!),
                            item.name
                        ))
                    }
                }
            }

            if (NBKeyBinds.openBlacklistScreen.wasPressed()) {
                MinecraftClient.getInstance().setScreen(BlacklistScreen(BlacklistScreenDescription()))
            }
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            NBSettings.save()
        }
    }
}