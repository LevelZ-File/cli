package xyz.calcugames.levelz.cli

import com.github.ajalt.clikt.core.CliktCommand

class LevelZ : CliktCommand(name = "levelz") {
    override fun run() = Unit
}

fun main(args: Array<String>) = LevelZ()
    .main(args)