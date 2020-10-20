package cn.newinfinideas.ast.parser

import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.parser.Parser

open class ValueLiteral: ExpressionNode() {
    companion object {
        val parser: Parser<ValueLiteral> = BooleanLiteral.parser or
                IntegerLiteral.parser or
                FloatLiteral.parser or
                CharLiteral.parser or
                StringLiteral.parser
    }
}