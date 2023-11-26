package util

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.net.URI
import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

class FileUtil {
    companion object {
//        fun readSingleLineFile(fileURL: URL): String {
////            val resourceFile = this::class.java.getResource(fileName).file
//            val br = BufferedReader(FileReader(fileURL.file))
//            return br.readLine()
//        }

        fun readSingleLineFile(fileURL: URL): String {
            return readMultiLineFile(fileURL).get(0)
        }

        fun readMultiLineFile(fileURL: URL): List<String> {
            val resourcePath = Paths.get( File(fileURL.file).toPath().toUri())
            return Files.readAllLines(resourcePath, StandardCharsets.UTF_8)
        }
    }
}