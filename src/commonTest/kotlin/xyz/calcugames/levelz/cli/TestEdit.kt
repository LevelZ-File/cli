package xyz.calcugames.levelz.cli

import com.github.ajalt.clikt.testing.test
import kotlin.test.Test
import kotlin.test.assertTrue

class TestEdit {

    @Test
    fun testEdit2D() {
        val command = Edit()
        val result = command.test("../../test-resources/input-2D.lvlz -h spawn [2,3] -h scroll horizontal-left -b grass [-1,0] -b grass [1,1]")
        println(result.output)

        assertTrue(result.stdout.contains("@type 2"))
        assertTrue(result.stdout.contains("@spawn [2,3]"))
        assertTrue(result.stdout.contains("@scroll horizontal-left"))
        assertTrue(result.stdout.contains("grass: [0, 0]*[0, 1]*[-1, 0]*[1, 1]"))
    }

    @Test
    fun testEdit3D() {
        val command = Edit()
        val result = command.test("../../test-resources/input-3D.lvlz -h spawn [2,6.75,1] -b grass [2.5,0,0] -b grass [0,1,0] -b grass [0,0,1]")
        println(result.output)

        assertTrue(result.stdout.contains("@type 3"))
        assertTrue(result.stdout.contains("@spawn [2,6.75,1]"))
    }

}