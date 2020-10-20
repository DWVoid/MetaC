package cn.newinfinideas.ast.parser

import cn.newinfinideas.ast.NsName
import cn.newinfinideas.ast.blScpL
import cn.newinfinideas.ast.blScpR
import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.parser.Parser

open class StatementNode {
    companion object {
        val parser: Parser<StatementNode> = ExpressionNode.parser or
                AssignmentStatementNode.parser or
                CompoundStatementNode.parser
    }
}

open class AssignmentStatementNode(
    val left: ExpressionNode,
    val right: ExpressionNode,
    val operator: BuiltInAssignOptLiteral
) : StatementNode() {
    companion object {
        val parser =
            (ExpressionNode.parser and BuiltInAssignOptLiteral.parser and ExpressionNode.parser) map { (l, o, r) ->
                AssignmentStatementNode(l, r, o)
            }
    }
}

open class CompoundStatementNode(val nodes: List<StatementNode>) : StatementNode() {
    companion object {
        val parser = (skip(blScpL) and zeroOrMore(StatementNode.parser) and skip(blScpR)) map {
            CompoundStatementNode(it)
        }
    }
}

open class UnaryOperatorLiteral {
    companion object {
        val parser: Parser<UnaryOperatorLiteral> = BuiltInUnaryOptLiteral.parser
    }
}

open class ExpressionNode : StatementNode() {
    companion object {
        val parser: Parser<ExpressionNode> = ValueLiteral.parser or
                NameExpressionNode.parser
    }
}

class NameExpressionNode(val name: NsName) : ExpressionNode() {
    companion object {
        val parser = NsName.parser map { NameExpressionNode(it) }
    }
}

class UnaryExpressionNode(val target: ExpressionNode, val operator: UnaryOperatorLiteral) {
    companion object {
        val parser = UnaryOperatorLiteral.parser and ExpressionNode.parser map { (o, t) -> UnaryExpressionNode(t, o) }
    }
}