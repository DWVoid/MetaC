package cn.newinfinideas.ast.parser

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken

// TODO(uChar beyond \uFFFF will not work because of JVM limitation under trivial implementation)

private const val reducedSymbolicOpen = "\${}\\[\\]#\\(\\)<>%:;\\.\\?\\*\\+\\-\\/\\^&\\|~!=,"
private const val cChar = "\\w\"$reducedSymbolicOpen"
private const val sChar = "\\w\'$reducedSymbolicOpen"
private const val uChar = """[\u00A8\u00AA\u00AD\u00AF\u2054\u00B2-\u00B5\u00B7-\u00BA\u00BC-\u00BE\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u00FF\u0100-\u167F\u1681-\u180D\u180F-\u1FFF\u200B-\u200D\u202A-\u202E\u203F-\u2040\u2060-\u206F\u2070-\u218F\u2460-\u24FF\u2776-\u2793\u2C00-\u2DFF\u2E80-\u2FFF\u3004-\u3007\u3021-\u302F\u3031-\u303F\u3040-\uD7FF\uF900-\uFD3D\uFD40-\uFDCF\uFDF0-\uFE44\uFE47-\uFFFD]"""

private val ucChar = regexToken("[$cChar$uChar]") map { it.text }

private val usChars = regexToken("[$sChar$uChar]*") map { it.text }

private val uEChar =
    (skip(regexToken("\\\\[uU]")) and regexToken("[\\da-eA-E]{1,6}")) map {
        it.text.toInt(16).toChar().toString()
    }

private val aXEChar =
    (skip(literalToken("\\x")) and regexToken("[\\da-eA-E]{2}")) map {
        it.text.toInt(16).toChar().toString()
    }

private val aOEChar =
    (skip(literalToken("\\")) and regexToken("[0-7]{3}")) map {
        it.text.toInt(8).toChar().toString()
    }

private val aEChar =
    (skip(literalToken("\\")) and regexToken("['\"?\\\\abfnrtv]")) map {
        when (it.text[0]) {
            'a' -> "\u0007"
            'b' -> "\u0008"
            'f' -> "\u000c"
            'n' -> "\u000a"
            'r' -> "\u000d"
            't' -> "\u0009"
            'v' -> "\u000b"
            else -> it.text // never happens, just to make the compiler happy
        }
    }

private val eChar = uEChar or aXEChar or aOEChar or aEChar

private val sQuote = literalToken("'")

private val dQuote = literalToken("\"")

private val rString = regexToken("(?:\"\"\"[\\s\\S]*?\"\"\")") map { it.text.substring(3, it.text.length - 3) }

private val nString = skip(sQuote) and zeroOrMore(usChars or eChar) and skip(sQuote) map {
    StringBuilder().apply { for(s in it) this.append(s) }.toString()
}

private val character = skip(sQuote) and (ucChar or eChar) and skip(sQuote)

private val string = rString or nString

class CharLiteral(val value: String): ValueLiteral() {
    companion object {
        val parser = character map { CharLiteral(it) }
    }
}

data class StringLiteral(val value: String): ValueLiteral() {
    companion object {
        val parser = string map { StringLiteral(it) }
    }
}

data class Identifier(val value: String) {
    companion object {
        val parser = regexToken("[a-zA-Z_$uChar][\\w$uChar]*") map { Identifier(it.text) }
    }
}