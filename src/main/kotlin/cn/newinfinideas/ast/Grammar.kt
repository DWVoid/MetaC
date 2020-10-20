package cn.newinfinideas.ast

import cn.newinfinideas.ast.parser.Identifier
import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.parser.Parser

// fast fn for assert
fun syAssert(exp: String) = literalToken(exp)
fun idAssert(exp: String) = literalToken(exp)

// operators
// 1. basic pairs
val blQ = syAssert("?")
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
// names
data class NsName(val parts: Array<Identifier>) {
    override fun equals(other: Any?) = when {
        this === other -> true
        javaClass != other?.javaClass -> false
        else -> parts.contentEquals((other as NsName).parts)
    }

    override fun hashCode() = parts.contentHashCode()

    companion object {
        val parser = (Identifier.parser and zeroOrMore(skip(blSep) and Identifier.parser)) map { (s, l) ->
            NsName(mutableListOf(s).apply { l.mapTo(this) { tk -> tk } }.toTypedArray())
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

