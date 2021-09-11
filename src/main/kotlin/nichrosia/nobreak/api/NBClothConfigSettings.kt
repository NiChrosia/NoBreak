package nichrosia.nobreak.api

import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.ConfigCategory
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.TranslatableText
import nichrosia.nobreak.content.NBSettings

object NBClothConfigSettings {
    lateinit var builder: ConfigBuilder

    private fun addCategories() {
        val general = builder.getOrCreateCategory(TranslatableText("category.nobreak.general"))

        addEntries(general)
    }

    private fun addEntries(general: ConfigCategory) {
        val entryBuilder = builder.entryBuilder()

        general.addEntry(entryBuilder.startBooleanToggle(
            TranslatableText("setting.nobreak.do_feedback"),
            NBSettings.notifyUser)
            .setSaveConsumer { NBSettings.notifyUser = it }
            .build())
    }

    fun build(parent: Screen): Screen {
        builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(TranslatableText("title.nobreak.config"))
            .setSavingRunnable(NBSettings::save)

        addCategories()

        return builder.build()
    }
}