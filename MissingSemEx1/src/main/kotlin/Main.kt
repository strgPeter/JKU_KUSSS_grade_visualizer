package org.example

import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {

    var isC = false
    var isT = false
    var isH = false
    var cPath: String? = null
    var hPath: String? = null

    val tempDir = System.getProperty("java.io.tmpdir")

    // Parse arguments
    var i = 0
    while (i < args.size) {
        when (args[i]) {
            "-t" -> {
                isT = true
            }
            "-c" -> {
                isC = true
                if (i + 1 < args.size && !args[i + 1].startsWith("-")) {
                    cPath = validatePath(args[i + 1])
                    if (cPath == null) return
                    i++ // Skip the path argument
                }
            }
            "-h" -> {
                isH = true
                if (i + 1 < args.size && !args[i + 1].startsWith("-")) {
                    hPath = validatePath(args[i + 1])
                    if (hPath == null) return
                    i++ // Skip the path argument
                }
            }
            else -> {
                println("Error: Unknown argument '${args[i]}'")
                return
            }
        }
        i++
    }


    // If no paths are provided for -c and -h, use the default temp directory
    if (isC && cPath == null) cPath = tempDir
    if (isH && hPath == null) hPath = tempDir

    // Set -t to true only if no other flags (-c or -h) are set
    if (!isC && !isH) {
        isT = true
    }

    // Output for demonstration purposes
    /*
    println("Flags set:")
    println("-t: $isT")
    println("-c: $isC, cPath: $cPath")
    println("-h: $isH, hPath: $hPath")
     */

    // Logic implementation
    val gt = GradeTable()
    if (gt.table_.isNotEmpty()){
        if (isT) gt.renderTxt()

        if (isH) {
            println("Saving HTML to path: $hPath")
            gt.saveAsHtml(hPath ?: tempDir)
        }
        if (isC) {
            println("Saving CSV to path: $cPath")
            gt.saveAsCsv(cPath ?: tempDir)
        }
    }


}

/**
 * Validates a given file path string.
 *
 * @param input the string representation of the file path to validate.
 * @return the normalized absolute path as a string if the input is valid and points to a directory,
 *         or `null` if the path is invalid or not a directory.
 */
fun validatePath(input: String): String? {
    return try {
        val path = Paths.get(input)

        // Check if the path exists
        if (!Files.exists(path)) {
            println("Error: Path does not exist: $input")
            return null
        }

        // Check if the path is a directory
        if (!Files.isDirectory(path)) {
            println("Error: Path is not a directory: $input")
            return null
        }

        // Normalize and return the absolute path
        path.toAbsolutePath().toString()
    } catch (e: Exception) {
        println("Error: Invalid path: $input")
        null
    }
}
