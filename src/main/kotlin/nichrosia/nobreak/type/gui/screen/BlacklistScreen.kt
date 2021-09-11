package nichrosia.nobreak.type.gui.screen

import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import nichrosia.nobreak.type.gui.description.BlacklistScreenDescription

open class BlacklistScreen<T>(guiDescription: T) : CottonClientScreen(guiDescription) where T : BlacklistScreenDescription