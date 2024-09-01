package xyz.calcugames.levelz.cli

import com.github.ajalt.clikt.testing.test
import kotlin.test.Test
import kotlin.test.assertTrue

class TestCreate {

    @Test
    fun testCreate2D() {
        val command = Create()
        val result = command.test("-d 2 -h spawn [-1,1] -h scroll vertical-up -b grass [0,0] -b grass [0,1]")
        println(result.stdout)

        assertTrue(result.stdout.contains("@type 2"))
        assertTrue(result.stdout.contains("@spawn [-1,1]"))
        assertTrue(result.stdout.contains("@scroll vertical-up"))
        assertTrue(result.stdout.contains("grass: [0, 0]*[0, 1]"))
    }

    @Test
    fun testCreate3D() {
        val command = Create()
        val result = command.test("-d 3 -h spawn [-3,-4,5.5] -b grass [0,0,0] -b grass [0,1,0] -b grass [0,0,1]")
        println(result.stdout)

        assertTrue(result.stdout.contains("@type 3"))
        assertTrue(result.stdout.contains("@spawn [-3,-4,5.5]"))
        assertTrue(result.stdout.contains("grass: [0, 0, 0]*[0, 1, 0]*[0, 0, 1]"))
    }

}