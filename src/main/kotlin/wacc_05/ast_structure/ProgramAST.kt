package wacc_05.ast_structure

import wacc_05.SemanticErrorHandler
import wacc_05.symbol_table.SymbolTable
import java.util.*

class ProgramAST(val functionList : ArrayList<FunctionAST>,
                 val statementList : ArrayList<StatementAST>) : AST {

    override fun check(st: SymbolTable, errorHandler: SemanticErrorHandler) {
//        TODO("Not yet implemented")
    }

}