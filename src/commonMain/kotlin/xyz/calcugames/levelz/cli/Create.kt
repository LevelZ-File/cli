package xyz.calcugames.levelz.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.restrictTo
import korlibs.io.file.std.localCurrentDirVfs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.runBlocking
import xyz.calcugames.levelz.LevelExporter
import xyz.calcugames.levelz.builder.LevelBuilder
import xyz.calcugames.levelz.coord.Coordinate2D
import xyz.calcugames.levelz.coord.Coordinate3D

class Create : CliktCommand(name = "create", help = "Generates a new LevelZ save file") {
    private val output by argument(help = "The path to output the save to. If empty, the save will be output to the console")
        .optional()

    private val dimension by option("-d", "--dimension", help = "The dimension of the save file, either 2 or 3")
        .int()
        .restrictTo(2..3)
        .required()

    private val headers by option("-h", "--header", help = "The headers to include in the save file")
        .pair()
        .multiple()

    private val blocks by option("-b", "--block", "--bl", help = "The blocks to include in the save file")
        .pair()
        .multiple()
        .validate { it.forEach { pair ->
            require(pair.second.startsWith("[") && pair.second.endsWith("]")) { "2nd Parameter must be a valid LevelZ Coordinate" }
        }}

    private val override by option("-o", "--override", help = "Override the file if it already exists")
        .flag("--no-override")

    override fun run() = runBlocking(Dispatchers.IO) {
        val builder = if (dimension == 2) LevelBuilder.create2D() else LevelBuilder.create3D()

        for ((header, value) in headers)
            builder.header(header, value)

        for ((block, value) in blocks) {
            val coordinate = if (dimension == 2) Coordinate2D.fromString(value) else Coordinate3D.fromString(value)
            builder.block(block, coordinate)
        }

        val level = builder.build()
        val export = LevelExporter.export(level).writeToString()

        if (output == null) {
            echo(export)
            return@runBlocking
        }

        echo("Creating '$output'...")
        val file = localCurrentDirVfs[output!!]
        if (file.exists() && !override)
            throw PrintMessage("File already exists: $output (use '-o' flag to override)", 1, true)

        file.writeString(export)
        echo("Created '${file.absolutePath}'")
    }
}