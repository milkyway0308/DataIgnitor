package skywolf46.dataignitor.test

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import skywolf46.dataignitor.util.CommandLineParser

class CommandLineParserTest {
    val sharedParser = CommandLineParser()

    @Test
    fun onSimpleCommandArgTest() {
        val command = "hello world!"
        assertArrayEquals(
            arrayOf("hello", "world!"),
            sharedParser.execute(command)
        )
    }

    @Test
    fun onQuotedArgTest() {
        val command = "\"hello world!\""
        assertArrayEquals(
            arrayOf("hello world!"),
            sharedParser.execute(command)
        )
    }

    @Test
    fun onIllegalActionTest() {
        val command = "-nonexistscommand Hello World"
        assertThrows(IllegalStateException::class.java) {
            sharedParser.execute(command)
        }
    }

    @Test
    fun onActionTest() {
        sharedParser.registerAction("test") {
            assertArrayEquals(arrayOf("This is", "test."), it)
        }
        assertArrayEquals(
            arrayOf("test", "1234"),
            sharedParser.execute("test 1234 -test \"This is\" test.")
        )
    }


    @Test
    fun onMultipleActionTest() {
        sharedParser.registerAction("test") {
            assertArrayEquals(arrayOf("This is", "test."), it)
        }

        sharedParser.registerAction("test2") {
            assertArrayEquals(arrayOf("Lorem Ipsum"), it)
        }

        sharedParser.registerAction("test3") {
            assertArrayEquals(arrayOf("Hello", "World"), it)
        }

        assertArrayEquals(
            arrayOf("test", "1234"),
            sharedParser.execute("test 1234 -test3 Hello World -test \"This is\" test. -test2 \"Lorem Ipsum\"")
        )
    }


    @Test
    fun onActionTestWithUnExistsCommand() {
        sharedParser.registerAction("test") {
            assertArrayEquals(arrayOf("This is", "test."), it)
        }
        assertThrows(IllegalStateException::class.java) {
            assertArrayEquals(
                arrayOf("test", "1234"),
                sharedParser.execute("test 1234 -test \"This is\" test. --nonexistscommand")
            )
        }
    }
}