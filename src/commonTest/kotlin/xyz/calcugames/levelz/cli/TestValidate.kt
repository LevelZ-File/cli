package xyz.calcugames.levelz.cli

import com.github.ajalt.clikt.testing.test
import kotlin.test.Test
import kotlin.test.assertEquals

class TestValidate {

    @Test
    fun testValidate2D() {
        val command = Validate()
        val result = command.test("../../test-resources/input-2D.lvlz -d 2 -mb 5 -sp [2,2]")
        println(result.output)

        assertEquals(0, result.statusCode)
    }

    @Test
    fun testValidate3D() {
        val command = Validate()
        val result = command.test("../../test-resources/input-3D.lvlz -d 3 -mb 5 -sp [-3,4,5]")
        println(result.output)

        assertEquals(0, result.statusCode)
    }

}