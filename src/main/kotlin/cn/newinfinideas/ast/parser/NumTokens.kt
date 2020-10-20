package cn.newinfinideas.ast.parser

import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken

class IntegerLiteral(val value: String): ValueLiteral() {
    companion object {
        val parser = regexToken("(?:[1-9]\\d*)|(?:0[0-7]*|(?:[xX][\\da-fA-F]+)|(?:[bB][01]+))") map {
            FloatLiteral(it.text)
        }
    }
}

class BooleanLiteral(val value: Boolean): ValueLiteral()  {
    companion object {
        val parser = ((literalToken("true") map { BooleanLiteral(true) }) or
                        (literalToken("false") map { BooleanLiteral(false) }))
    }
}

class FloatLiteral(val value: String): ValueLiteral() {
    companion object {
        val parser = regexToken("(?:[1-9]\\d*)|(?:0[0-7]*|(?:[xX][\\da-fA-F]+)|(?:[bB][01]+))") map {
            FloatLiteral(it.text)
        }
    }
}
