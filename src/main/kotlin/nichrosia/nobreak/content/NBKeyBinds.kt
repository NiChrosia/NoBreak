package nichrosia.nobreak.content

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import nichrosia.nobreak.type.content.Content
import org.lwjgl.glfw.GLFW

object NBKeyBinds : Content {
    lateinit var toggleCurrentItemBlacklist: KeyBinding
    lateinit var toggleProtectEnchanted: KeyBinding
    lateinit var openBlacklistScreen: KeyBinding

    override fun load() {
        toggleCurrentItemBlacklist = KeyBindingHelper.registerKeyBinding(KeyBinding(
            "key.nobreak.toggle_current_item_blacklist",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.nobreak.keybindings"
        ))

        toggleProtectEnchanted = KeyBindingHelper.registerKeyBinding(KeyBinding(
            "key.nobreak.toggle_current_item_protect_enchanted",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "category.nobreak.keybindings"
        ))

        openBlacklistScreen = KeyBindingHelper.registerKeyBinding(KeyBinding(
            "key.nobreak.open_blacklist_screen",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "category.nobreak.keybindings"
        ))
    }
}