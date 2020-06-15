package ru.liqvid.util

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest

object File {

    fun isExistDirectory(dirPath: String): Boolean {
        var ret = false
        val dir = File(dirPath)
        if (dir.exists() && dir.isDirectory)
            ret = true
        return ret
    }

    fun isExistFile(dirPath: String): Boolean {
        var ret = false
        val dir = File(dirPath)
        if (dir.exists())
            ret = true
        return ret
    }

    private const val CHECKSUM_NAME_ALGORITHM = "MD5"

    @Throws(Throwable::class)
    fun calculateMD5(updateFile: File): String? {
        val digest = MessageDigest.getInstance(CHECKSUM_NAME_ALGORITHM)
        val inputStream = FileInputStream(updateFile)
        val buffer = ByteArray(8192)
        var read: Int
        return try {
            while (inputStream.read(buffer).also { read = it } > 0) {
                digest.update(buffer, 0, read)
            }
            val md5sum = digest.digest()
            val bigInt = BigInteger(1, md5sum)
            var output: String = bigInt.toString(16)
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0')
            output
        } catch (e: IOException) {
            throw IOException("Unable to process file for MD5", e)
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                throw IOException( "Exception on closing MD5 input stream", e)
            }
        }
    }

}