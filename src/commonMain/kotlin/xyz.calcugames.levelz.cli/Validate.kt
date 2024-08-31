package xyz.calcugames.levelz.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.FileNotFound
import com.github.ajalt.clikt.core.InvalidFileFormat
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.arguments.argument
import korlibs.io.file.std.localCurrentDirVfs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.runBlocking
import xyz.calcugames.levelz.parser.ParseException
import xyz.calcugames.levelz.parser.parseLevel

class Validate : CliktCommand(name = "validate", help = "Validate a LevelZ save file") {
    private val input by argument(help = "The save file to validate")

    override fun run() = runBlocking(Dispatchers.IO) {
        println("Validating $input...")

        val file = localCurrentDirVfs[input]
        if (!file.exists())
            throw FileNotFound(file.path)

        if (file.isDirectory())
            throw InvalidFileFormat(file.path, "file")

        val save = file.readString().lines()

        try {
            parseLevel(save)
        } catch (e: ParseException) {
            throw PrintMessage(e.message ?: "Unknown Error", 1, true)
        }

        Unit
    }
}