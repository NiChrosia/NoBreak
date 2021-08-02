package nichrosia.nobreak.content

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

object NBKeyBinds : NBContent {
    lateinit var toggleToolBreakage: KeyBinding
    lateinit var toggleCurrentItemBlacklist: KeyBinding

    override fun register() {
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
    }
}