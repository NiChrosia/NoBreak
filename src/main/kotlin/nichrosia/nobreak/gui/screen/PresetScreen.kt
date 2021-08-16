package nichrosia.nobreak.gui.screen

import io.github.cottonmc.cotton.gui.GuiDescription
import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import nichrosia.nobreak.gui.screen.description.BlacklistScreenDescription
import nichrosia.nobreak.gui.screen.description.PresetScreenDescription

open class PresetScreen(from: BlacklistScreenDescription, guiDescription: GuiDescription = PresetScreenDescription(from)) : CottonClientScreen(guiDescription)