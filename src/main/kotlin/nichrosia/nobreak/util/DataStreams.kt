package nichrosia.nobreak.util

import java.io.DataInputStream
import java.io.DataOutputStream

object DataStreams {
    fun DataOutputStream.bool(value: Boolean) = writeBoolean(value)
    fun DataOutputStream.str(value: String) = writeUTF(value)

    fun DataInputStream.bool() = readBoolean()
    fun DataInputStream.str(): String = readUTF()
}