package nichrosia.nobreak.ext

import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.ConfigCategory
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.TranslatableText
import nichrosia.nobreak.content.NBSettings

object NBClothConfigSettings {
    fun build(parent: Screen): Screen {
        val builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(TranslatableText("title.nobreak.config"))
            .setSavingRunnable {

            }

        addCategories(builder)

        return builder.build()
    }

    private fun addCategories(builder: ConfigBuilder) {
        val general = builder.getOrCreateCategory(TranslatableText("category.nobreak.general"))

        addEntries(builder, general)
    }

    private fun addEntries(builder: ConfigBuilder, general: ConfigCategory) {
        val entryBuilder = builder.entryBuilder()

        general.addEntry(entryBuilder.startBooleanToggle(
            TranslatableText("setting.nobreak.do_break"),
            NBSettings.allowBreakage)
            .setSaveConsumer { NBSettings.allowBreakage = it }
            .build())

        general.addEntry(entryBuilder.startBooleanToggle(
            TranslatableText("setting.nobreak.do_feedback"),
            NBSettings.notifyUser)
            .setSaveConsumer { NBSettings.notifyUser = it }
            .build())

        general.addEntry(entryBuilder.startBooleanToggle(
            TranslatableText("setting.nobreak.diamond+_tools_can_break"),
            NBSettings.diamondPlusToolsCanBreak)
            .setSaveConsumer { NBSettings.diamondPlusToolsCanBreak = it }
            .build())

        general.addEntry(entryBuilder.startBooleanToggle(
            TranslatableText("setting.nobreak.enchanted_tools_can_break"),
            NBSettings.enchantedToolsCanBreak)
            .setSaveConsumer { NBSettings.enchantedToolsCanBreak = it }
            .build())
    }
}