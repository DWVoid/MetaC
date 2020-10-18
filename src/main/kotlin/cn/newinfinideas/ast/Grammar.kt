package cn.newinfinideas.ast

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.lexer.LiteralToken
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.h0tk3y.betterParse.lexer.TokenMatchesSequence
import com.github.h0tk3y.betterParse.parser.*

class TokenAssert(private val inner: Parser<TokenMatch>, exp: String) : Parser<TokenMatch> {
    private val expect = LiteralToken("", exp)
    override fun tryParse(tokens: TokenMatchesSequence, fromPosition: Int): ParseResult<TokenMatch> {
        return when (val innerResult = inner.tryParse(tokens, fromPosition)) {
            is ErrorResult -> innerResult
            is Parsed -> if (innerResult.value.text == expect.text) innerResult else MismatchedToken(
                expect,
                innerResult.value
            )
        }
    }
}

// fast fn for assert
fun syAssert(exp: String) = TokenAssert(symbolic, exp)
fun idAssert(exp: String) = TokenAssert(identifier, exp)

// operators
// 1. basic pairs
val blSep = syAssert(".")
val blIdxL = syAssert("[")
val blIdxR = syAssert("]")
val blScpL = syAssert("{")
val blScpR = syAssert("}")
val blBrcL = syAssert("(")
val blBrcR = syAssert(")")
val blL = syAssert("<")
val blG = syAssert(">")

// 2. op components
val blEq = syAssert("=")
val blSub = syAssert("-")
val blAdd = syAssert("+")
val blAst = syAssert("*")
val blDiv = syAssert("/")
val blRem = syAssert("%")
val blAmp = syAssert("&")
val blVert = syAssert("|")
val blNeg = syAssert("!")
val blHat = syAssert("^")
val blQ = syAssert("?")

// 3. multi-sym ops
val blAnd = blAmp and blAmp
val blOr = blVert and blVert
val blShl = blL and blL
val blShr = blG and blG
val blLeq = blL and blEq
val blGeq = blG and blEq

// 4. self-mutation operator
val blEqSub = blSub and blEq
val blEqAdd = blAdd and blEq
val blEqMul = blAst and blEq
val blEqDiv = blDiv and blEq
val blEqRem = blRem and blEq
val blEqAmp = blAmp and blEq
val blEqVert = blVert and blEq
val blEqHat = blHat and blEq
val blEqAnd = blAnd and blEq
val blEqOr = blOr and blEq
val blEqShl = blShl and blEq
val blEqShr = blShr and blEq

// names
data class NsName(val parts: Array<String>) {
    override fun equals(other: Any?) = when {
        this === other -> true
        javaClass != other?.javaClass -> false
        else -> parts.contentEquals((other as NsName).parts)
    }

    override fun hashCode() = parts.contentHashCode()

    companion object {
        val parser = (identifier and zeroOrMore(skip(blSep) and identifier)) map { (s, l) ->
            NsName(mutableListOf(s.text).apply { l.mapTo(this) { tk -> tk.text } }.toTypedArray())
        }
    }
}

// package && primary ns control
data class PkgDecl(val name: NsName) {
    companion object {
        val parser = (skip(idAssert("package")) and NsName.parser and skip(linefeed)) map { PkgDecl(it) }
    }
}

data class ExplodeDecl(val name: NsName) {
    companion object {
        val parser = (skip(idAssert("explode")) and NsName.parser and skip(linefeed)) map { ExplodeDecl(it) }
    }
}

data class AliasDecl(val origin: NsName, val alias: NsName) {
    companion object {
        val parser = (skip(idAssert("alias")) and NsName.parser and NsName.parser and skip(linefeed)) map { (a, b) ->
            AliasDecl(a, b)
        }
    }
}

// Literal Values
open class NumericLiteral

data class IntegralLiteral(val value: Long) : NumericLiteral() {
    companion object {
        val parser = numeric map { IntegralLiteral(0) }// TODO(do a actual reparse)
    }
}

// Type Decl
open class TypeDecl {
    companion object {
        val parser: Parser<TypeDecl> = NamedTypeDecl.parser or
                NullableTypeDecl.parser or
                FixedArrayTypeDecl.parser or
                DynArrayTypeDecl.parser or
                ConstRefTypeDecl.parser or
                MutableRefTypeDecl.parser
    }
}

data class NamedTypeDecl(val name: NsName) : TypeDecl() {
    companion object {
        val parser = NsName.parser map { NamedTypeDecl(it) }
    }
}

data class NullableTypeDecl(val type: TypeDecl) : TypeDecl() {
    companion object {
        val parser = (skip(blQ) and TypeDecl.parser) map { NullableTypeDecl(it) }
    }
}

data class ConstRefTypeDecl(val type: TypeDecl) : TypeDecl() {
    companion object {
        val parser = (skip(idAssert("ref")) and TypeDecl.parser) map { ConstRefTypeDecl(it) }
    }
}

data class MutableRefTypeDecl(val type: TypeDecl) : TypeDecl() {
    companion object {
        val parser = (skip(idAssert("mut")) and TypeDecl.parser) map { MutableRefTypeDecl(it) }
    }
}

data class DynArrayTypeDecl(val type: TypeDecl) : TypeDecl() {
    companion object {
        val parser = (skip(blIdxL) and skip(blIdxR) and TypeDecl.parser) map { DynArrayTypeDecl(it) }
    }
}

data class FixedArrayTypeDecl(val length: IntegralLiteral, val type: TypeDecl) : TypeDecl() {
    companion object {
        val parser = (skip(blIdxL) and IntegralLiteral.parser and skip(blIdxR) and TypeDecl.parser) map { (a, b) ->
            FixedArrayTypeDecl(a, b)
        }
    }
}

