package xyz.calcugames.levelz.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.FileNotFound
import com.github.ajalt.clikt.core.InvalidFileFormat
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import korlibs.io.file.std.localCurrentDirVfs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.runBlocking
import xyz.calcugames.levelz.coord.Coordinate2D
import xyz.calcugames.levelz.coord.Coordinate3D
import xyz.calcugames.levelz.parser.ParseException
import xyz.calcugames.levelz.parser.parseLevel

class Validate : CliktCommand(name = "validate", help = "Validate a LevelZ save file") {
    private val input by argument(help = "The save file to validate")

    private val dimension by option("-d", "--dimension", help = "The dimension to validate the save file against")
        .int()
        .restrictTo(2..3)
        .default(-1)

    private val minBlocks by option("-mb", "--min-blocks", help = "The minimum block count to validate the save file against")
        .int()
        .restrictTo(0..Int.MAX_VALUE)
        .default(-1)

    private val spawn by option("-sp", "--spawn", "--spawnpoint", help = "The spawn point to validate the save file against")
        .validate { require(it.startsWith("[") && it.endsWith("]")) { "Spawn point must be a valid LevelZ Coordinate" } }

    private val headers by option("-h", "--header", help = "Validates a header value in the save file")
        .pair()
        .multiple()

    override fun run() = runBlocking(Dispatchers.IO) {
        echo("Validating $input...")

        val file = localCurrentDirVfs[input]
        if (!file.exists())
            throw FileNotFound(file.path)

        if (file.isDirectory())
            throw InvalidFileFormat(file.path, "file")

        val save = file.readString().lines()

        try {
            val level = parseLevel(save)

            if (dimension != -1 && level.dimension.asNumber != dimension)
                throw PrintMessage("Dimension mismatch: Expected $dimension, got ${level.dimension.asNumber}", 1, true)

            if (minBlocks != -1 && level.blocks.size < minBlocks)
                throw PrintMessage("Minimum coordinates mismatch: Expected $minBlocks+, got ${level.blocks.size}", 1, true)

            if (spawn != null) {
                val spawn0 = if (level.dimension.is2D) Coordinate2D.fromString(spawn!!) else Coordinate3D.fromString(spawn!!)

                if (level.spawn != spawn0)
                    throw PrintMessage("Spawn point mismatch: Expected '$spawn0', got '${level.spawn}'", 1, true)
            }

            val actualHeaders = level.getHeaders()
            for ((header, value) in headers) {
                if (actualHeaders[header] != value)
                    throw PrintMessage("Header mismatch for '$header': Expected '$value', got '${actualHeaders[header]}'", 1, true)
            }
        } catch (e: ParseException) {
            throw PrintMessage(e.message ?: "Unknown Error", 1, true)
        }

        echo("Validation successful!")
    }
}