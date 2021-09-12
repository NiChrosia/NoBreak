package nichrosia.nobreak.type.mod

import net.minecraft.util.Identifier

interface IdentifiedModInit {
    val modID: String

    fun idOf(path: String) = Identifier(modID, path)
}