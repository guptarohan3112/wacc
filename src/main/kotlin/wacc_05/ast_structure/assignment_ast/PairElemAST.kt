package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrorHandler
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable

class PairElemAST(private val elem: ExprAST) : AssignRHSAST() {

    override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
        TODO("Not yet implemented")
    }

}