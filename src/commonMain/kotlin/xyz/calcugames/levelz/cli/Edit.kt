package xyz.calcugames.levelz.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.FileNotFound
import com.github.ajalt.clikt.core.InvalidFileFormat
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.*
import korlibs.io.file.std.localCurrentDirVfs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.runBlocking
import xyz.calcugames.levelz.Block
import xyz.calcugames.levelz.LevelExporter
import xyz.calcugames.levelz.LevelObject
import xyz.calcugames.levelz.builder.LevelBuilder
import xyz.calcugames.levelz.coord.Coordinate2D
import xyz.calcugames.levelz.coord.Coordinate3D
import xyz.calcugames.levelz.parser.ParseException
import xyz.calcugames.levelz.parser.parseLevel

class Edit : CliktCommand(name = "edit", help = "Edit a LevelZ save file") {
    private val input by argument(help = "The save file to edit")
    private val output by argument(help = "The path to output the save to. If empty, the save will be output to the console")
        .optional()

    private val headers by option("-h", "--header", help = "The headers to edit or create in save file")
        .pair()
        .multiple()

    private val removedHeaders by option("-rh", "--remove-header", help = "The header names to remove from the save file")
        .multiple()

    private val blocks by option("-b", "--block", "--bl", help = "The new blocks to include in the save file")
        .pair()
        .multiple()
        .validate { it.forEach { pair ->
            require(pair.second.startsWith("[") && pair.second.endsWith("]")) { "2nd Parameter must be a valid LevelZ Coordinate" }
        }}

    private val removedBlocks by option("-rb", "--remove-block", "--remove-bl", help = "The blocks to remove from the save file, mapped at coordinates")
        .multiple()

    private val override by option("-o", "--override", help = "Override the output file if it already exists")
        .flag("--no-override")

    override fun run() = runBlocking(Dispatchers.IO) {
        val file = localCurrentDirVfs[input]
        if (!file.exists())
            throw FileNotFound(file.path)

        if (file.isDirectory())
            throw InvalidFileFormat(file.path, "file")

        val save = file.readString().lines()

        try {
            val level = parseLevel(save)
            val dimension = level.dimension
            val builder = if (dimension.is2D) LevelBuilder.create2D() else LevelBuilder.create3D()

            val newHeaders = (level.getHeaders() + headers.toMap())
                .filter { (header, _) -> header !in removedHeaders }

            for ((header, value) in newHeaders)
                builder.header(header, value)

            val newBlocks = level.blocks.toMutableList()
            for ((block, value) in blocks) {
                val coordinate = if (dimension.is2D) Coordinate2D.fromString(value) else Coordinate3D.fromString(value)
                newBlocks.add(LevelObject(Block(block), coordinate))
            }

            for (block in newBlocks.filter { it.block.name !in removedBlocks })
                builder.block(block)

            val newLevel = builder.build()
            val export = LevelExporter.export(newLevel).writeToString()

            if (output == null) {
                echo(export)
                return@runBlocking
            }

            echo("Editing '$input'...")
            val outputFile = localCurrentDirVfs[output!!]
            if (outputFile.exists() && !override)
                throw InvalidFileFormat(outputFile.path, "File already exists: $output (use '-o' flag to override)")

            outputFile.writeString(export)
            echo("Edited '${outputFile.absolutePath}'")
        } catch (e: ParseException) {
            throw InvalidFileFormat(file.path, "Malformed LevelZ save file")
        }
    }

}