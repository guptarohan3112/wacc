package wacc_05.ast_structure

class ParamAST(val type: String,
               val name: String) : AST {

    override fun check() {
//        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return name;
    }
}
