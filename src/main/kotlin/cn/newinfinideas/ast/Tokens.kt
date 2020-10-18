package cn.newinfinideas.ast

import com.github.h0tk3y.betterParse.lexer.DefaultTokenizer
import com.github.h0tk3y.betterParse.lexer.regexToken

private const val escapeSequence = """(?:\\(?:[^\dxuU]|(?:\d{1,7})|(?:x[\da-eA-E]{1,2})|(?:[uU][\da-eA-E]{1,6})))"""
private const val universalCharacter = """[\u00A8\u00AA\u00AD\u00AF\u2054\u00B2-\u00B5\u00B7-\u00BA\u00BC-\u00BE\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u00FF\u0100-\u167F\u1681-\u180D\u180F-\u1FFF\u200B-\u200D\u202A-\u202E\u203F-\u2040\u2060-\u206F\u2070-\u218F\u2460-\u24FF\u2776-\u2793\u2C00-\u2DFF\u2E80-\u2FFF\u3004-\u3007\u3021-\u302F\u3031-\u303F\u3040-\uD7FF\uF900-\uFD3D\uFD40-\uFDCF\uFDF0-\uFE44\uFE47-\uFFFD]"""
private const val reducedSymbolicOpen = "\${}\\[\\]#\\(\\)<>%:;\\.\\?\\*\\+\\-\\/\\^&\\|~!=,"
private const val cChar = "[\\w\"${reducedSymbolicOpen}]"
private const val sChar = "[\\w\'${reducedSymbolicOpen}]"
private const val rawString = "(?:\"\"\"[\\s\\S]*?\"\"\")"

val numeric = regexToken("nm","""\w[\wxXaAbBcCdDeEfF'.]+""")
val symbolic = regexToken("sy","""[$reducedSymbolicOpen]""")
val character = regexToken("ch","'(?:$escapeSequence|$universalCharacter|$cChar)'")
val string = regexToken("st","$rawString|(?:\"(?:$escapeSequence|$universalCharacter|$sChar)*\")")
val identifier = regexToken("id", "(?:[a-zA-Z_]|$universalCharacter$)(?:\\w|$universalCharacter\$)*")
val linefeed = regexToken("lf", "[\\r\\n]\\s*")
val spacing = regexToken("ws","[^\\S\\r\\n]+", ignore = true)
val comment = regexToken("co", "(?:/\\*[\\s\\S]*?\\*/)|(?://.*)", ignore = true)

val tokenizer = DefaultTokenizer(listOf(linefeed, comment, spacing, identifier, numeric, symbolic, character, string))

fun regexPrint() {
    println("$rawString|(?:\"(?:$escapeSequence|$universalCharacter|$sChar)*\")")
}

val test = """
val lineEnd = regexToken("eol", "[\\r\\n]+")
val whiteSpace = regexToken("[^\\S\\r\\n]+", ignore = true)
val comment = regexToken("comment", "/\\*[\\s\\S]*\\*/"/*this is a inline comment*/)
/*this is a multiline
comment*/
// this is a line trailing comment
val tokenizer = DefaultTokenizer(listOf(identifier, numeric, symbolic, character, string, lineEnd, comment, whiteSpace))

fun regexPrint() {
    println("xxxxxx")
}
"""

fun test() {
    tokenizer.tokenize(test).forEach {
        println(it)
    }
}