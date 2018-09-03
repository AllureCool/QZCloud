package com.smile.qzclould.utils

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
object ConverUtils {
    fun toString(`is`: InputStream): String {
        return toString(`is`, "utf-8")
    }

    private fun toString(`is`: InputStream, charset: String): String {
        val sb = StringBuilder()
        try {
            val reader = BufferedReader(InputStreamReader(`is`, charset))
            while (true) {
                val line = reader.readLine()
                if (line == null) {
                    break
                } else {
                    sb.append(line).append("\n")
                }
            }
            reader.close()
            `is`.close()
        } catch (e: IOException) {
            e.stackTrace
        }

        return sb.toString()
    }
}
