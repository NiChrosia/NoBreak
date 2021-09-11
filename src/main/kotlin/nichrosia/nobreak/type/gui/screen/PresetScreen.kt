package nichrosia.nobreak.type.gui.screen

import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import nichrosia.nobreak.type.gui.description.PresetScreenDescription

open class PresetScreen<T>(guiDescription: T) : CottonClientScreen(guiDescription) where T : PresetScreenDescription