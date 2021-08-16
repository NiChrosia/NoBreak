package nichrosia.nobreak.content

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import nichrosia.nobreak.content.type.Content
import org.lwjgl.glfw.GLFW

object NBKeyBinds : Content {
    lateinit var toggleToolBreakage: KeyBinding
    lateinit var toggleCurrentItemBlacklist: KeyBinding
    lateinit var openBlacklistScreen: KeyBinding

    override fun load() {
        toggleToolBreakage = KeyBindingHelper.registerKeyBinding(KeyBinding(
            "key.nobreak.toggle_tool_breakage",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_Y,
            "category.nobreak.keybindings"
        ))

        toggleCurrentItemBlacklist = KeyBindingHelper.registerKeyBinding(KeyBinding(
            "key.nobreak.toggle_current_item_blacklist",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
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