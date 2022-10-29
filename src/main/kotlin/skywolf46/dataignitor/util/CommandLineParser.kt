package skywolf46.dataignitor.util

import java.util.concurrent.atomic.AtomicInteger

class CommandLineParser {
    private val registeredActions = mutableMapOf<String, (Array<String>) -> Boolean>()

    fun registerBlockableAction(vararg action: String, unit: (Array<String>) -> Boolean): CommandLineParser {
        for (x in action)
            registeredActions[x] = unit
        return this
    }

    fun registerAction(vararg action: String, unit: (Array<String>) -> Unit): CommandLineParser {
        return registerBlockableAction(*action) {
            unit(it)
            true
        }
    }

    fun execute(commandLine: String) = execute(commandLine.split(" ").toTypedArray())

    fun execute(commandLines: Array<String>): Array<String> {
        val index = AtomicInteger(0)
        val pureCommandArgs = mutableListOf<String>()
        while (index.get() < commandLines.size) {
            val arg = commandLines[index.get()]
            if (arg.startsWith("-")) {
                val actionArgs = mutableListOf<String>()
                index.incrementAndGet()
                while (index.get() < commandLines.size) {
                    val actionArg = parseArg(commandLines, index)
                    if (!actionArg.isQuoted && actionArg.arg.startsWith('-')) {
                        index.decrementAndGet()
                        break
                    }
                    actionArgs += actionArg.arg
                    index.incrementAndGet()
                }
                val result = registeredActions[arg.substring(1)]?.invoke(actionArgs.toTypedArray())
                    ?: throw IllegalStateException("Cannot execute arg $arg: Unknown action arg")
                if (!result) {
                    return emptyArray()
                }
            } else {
                pureCommandArgs += parseArg(commandLines, index).arg
            }
            index.incrementAndGet()
        }
        return pureCommandArgs.toTypedArray()
    }

    private fun parseArg(args: Array<String>, index: AtomicInteger): ArgResult {
        if (!args[index.get()].startsWith('\"'))
            return ArgResult(args[index.get()], false)
        val quotedString = mutableListOf<String>()
        while (index.get() < args.size) {
            val arg = args[index.get()]
            if (arg.endsWith('\"')) {
                quotedString += arg.substring(0, arg.length - 1)
                return ArgResult(quotedString.joinToString(" "), true)
            } else {
                quotedString += arg.let {
                    if (it.startsWith('\"'))
                        it.substring(1)
                    else it
                }
                index.incrementAndGet()
            }
        }
        throw IllegalStateException("Failed to parse quoted string: Quote not closed")
    }


    data class ArgResult(val arg: String, val isQuoted: Boolean)
}