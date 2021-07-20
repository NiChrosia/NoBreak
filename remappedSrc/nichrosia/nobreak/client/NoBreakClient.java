package nichrosia.nobreak.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;

import java.io.File;

@Environment(EnvType.CLIENT)
@SuppressWarnings("unused")
public class NoBreakClient implements ClientModInitializer {
    private static KeyBinding keyBinding;
    private boolean doBreak = false;
    private final TranslatableText on = new TranslatableText("text.nobreak.on");
    private final TranslatableText off = new TranslatableText("text.nobreak.off");
    private static long lastMessage = 0;
    private static final int MESSAGE_REPEAT_TIME = 1000;
    private static NoBreakConfig config;

    @Override
    public void onInitializeClient() {
        // Create config object from JSON
        config = NoBreakConfig.loadConfig(new File(FabricLoader.getInstance().getConfigDir() + "/htm_config.json"));
        doBreak = config.isBreaking();

        PlayerBlockBreakEvents.BEFORE.register(((world, playerEntity, blockPos, blockState, blockEntity) -> {
            if (doBreak) return true;

            ItemStack stack = playerEntity.getStackInHand(playerEntity.getActiveHand());

            if (stack.getItem() instanceof ToolItem) {
                if (stack.getMaxDamage() - stack.getDamage() == 1) {
                    informPlayer(playerEntity);
                    return false;
                }
            }

            return true;
        }));

        ClientLifecycleEvents.CLIENT_STOPPING.register((MinecraftClient client) ->
            config.saveConfig(new File(FabricLoader.getInstance().getConfigDir() + "/htm_config.json"))
        );

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.nobreak.togglebreak",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Y,
                "category.nobreak.title"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                doBreak = !doBreak;
                if (client.player != null) client.player.sendMessage(new TranslatableText("text.nobreak.toggle", doBreak ? on : off), true);
            }
        });
    }

    private void informPlayer(PlayerEntity player) {
        if (!config.isFeedback() || System.currentTimeMillis() < lastMessage + MESSAGE_REPEAT_TIME) { return; }

        lastMessage = System.currentTimeMillis();
        Text message;

        message = KeyBindingHelper.getBoundKeyOf(keyBinding).equals(GLFW.GLFW_KEY_UNKNOWN) ?
                new TranslatableText("text.nobreak.prevented") :
                new TranslatableText("text.nobreak.enableby", KeyBindingHelper.getBoundKeyOf(keyBinding).getLocalizedText());

        player.sendMessage(message, true);
    }
}
