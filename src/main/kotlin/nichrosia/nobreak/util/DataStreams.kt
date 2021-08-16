package nichrosia.nobreak.util

import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.io.DataInputStream
import java.io.DataOutputStream

object DataStreams {
    fun DataOutputStream.writeItemArr(arr: Iterable<Item>) {
        str(arr.joinToString(separator = "|") {
            Registry.ITEM.getId(it).toString()
        })
    }

    fun DataInputStream.readItemArr(): MutableList<Item> {
        val output = mutableListOf<Item>()
        val items = str().split("|")

        for (it in items) {
            if (!it.contains(":")) continue

            val (namespace, path) = it.split(":")
            val identity = Identifier(namespace, path)
            output.add(Registry.ITEM.get(identity))
        }

        return output
    }

    fun DataOutputStream.bool(value: Boolean) = writeBoolean(value)
    fun DataOutputStream.str(value: String) = writeUTF(value)

    fun DataInputStream.bool() = readBoolean()
    fun DataInputStream.str(): String = readUTF()
}