package wacc_05.ast_structure

import wacc_05.SemanticErrorHandler
import wacc_05.symbol_table.SymbolTable

class ParamAST(val type: String,
               val name: String) : AST {

    override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
//        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return name;
    }
}
