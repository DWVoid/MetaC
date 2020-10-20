package cn.newinfinideas.ast.parser

import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.lexer.regexToken

private fun constructRegex(array: Array<String>): String {
    val builder = StringBuilder("(?:")
    for ((i, x) in array.sortedArrayDescending().withIndex()) {
        if (i > 0) builder.append('|')
        builder.append(x)
    }
    return builder.append(')').toString()
}

private fun regexOptSet(array: Array<String>) = regexToken(constructRegex(array))

class BuiltInUnaryOptLiteral(val value: String): UnaryOperatorLiteral() {
    // positive, negative, not, negate
    companion object {
        private val operators = arrayOf("++", "--", "+", "-", "!", "~")
        val parser = regexOptSet(operators) map { BuiltInUnaryOptLiteral(it.text) }
    }
}

class BuiltInBinaryOptLiteral(val value: String) {
    companion object {
        private val operators = arrayOf(
            // arithmetic comparison
            "==", "<", ">", "<=", ">=", "<=>",
            // logical
            "&&", "||",
            // bitwise
            "&", "|", "^",
            // arithmetic
            "+", "-", "*", "/", "%",
            // bit-manipulation
            "<<", ">>"
        )
        val parser = regexOptSet(operators) map { BuiltInBinaryOptLiteral(it.text) }
    }
}

class BuiltInAssignOptLiteral(val value: String) {
    companion object {
        private val operators = arrayOf(
            // logical
            "&&=", "||=",
            // bitwise
            "&=", "|=", "^=",
            // arithmetic
            "+=", "-=", "*=", "/=", "%=",
            // bit-manipulation
            "<<=", ">>="
        )
        val parser = regexOptSet(operators) map { BuiltInAssignOptLiteral(it.text) }
    }
}
