package wacc_05.ast_structure.assignment_ast

import wacc_05.SemanticErrors
import wacc_05.ast_structure.AST
import wacc_05.ast_structure.ExprAST
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.IdentifierObject
import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import wacc_05.symbol_table.identifier_objects.VariableIdentifier

class AssignLHSAST(private val ident: String?) : AST {

    private var arrElem: ExprAST.ArrayElemAST? = null
    private var pairElem: PairElemAST? = null
    private lateinit var type: TypeIdentifier

    constructor(arrElem: ExprAST.ArrayElemAST) : this(null) {
        this.arrElem = arrElem
    }

    constructor(pairElem: PairElemAST) : this(null) {
        this.pairElem = pairElem
    }

    override fun check(st: SymbolTable, errorHandler: SemanticErrors) {
        if (arrElem != null) {
            arrElem!!.check(st, errorHandler)
            type = arrElem!!.getType()
        } else if (pairElem != null) {
            pairElem!!.check(st, errorHandler)
            type = pairElem!!.getType()
        } else {
            val identInST: IdentifierObject? = st.lookupAll(ident!!)
            if (identInST == null) {
                errorHandler.invalidIdentifier(ident)
                // Not sure what to set the type to here
            } else {
                type = (identInST as VariableIdentifier).getType()
            }
        }
    }

    fun getType() : TypeIdentifier {
        return type
    }
}