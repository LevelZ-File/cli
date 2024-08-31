package xyz.calcugames.levelz.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class LevelZ : CliktCommand(name = "levelz") {
    override fun run() = Unit
}

fun main(args: Array<String>) = LevelZ()
    .subcommands(
        Validate(),
        Create(),
        Edit()
    )
    .main(args)