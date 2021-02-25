package wacc_05.ast_structure

import antlr.WaccParser
import wacc_05.ast_structure.assignment_ast.AssignRHSAST
import wacc_05.code_generation.*
import wacc_05.code_generation.instructions.*
import wacc_05.symbol_table.SymbolTable
import wacc_05.symbol_table.identifier_objects.*

sealed class ExprAST : AssignRHSAST() {

    var dest: Register? = null

    data class IntLiterAST(private val sign: String, private val value: String) : ExprAST() {

        override fun getType(st: SymbolTable): TypeIdentifier {
            return TypeIdentifier.INT_TYPE
        }

        fun getValue(): Int {
            return (sign + value).toInt()
        }


        override fun translate(): ArrayList<Instruction> {
            val intValue = Integer.parseInt(sign+value)
            val register = Registers.allocate()
            val mode: AddressingMode = AddressingMode.AddressingMode2(register, Immediate(intValue))
//            this.dest = register
            return arrayListOf(LoadInstruction(register, mode))
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitIntLiterAST(this)
        }
    }

    data class BoolLiterAST(private val value: String) : ExprAST() {

        override fun getType(st: SymbolTable): TypeIdentifier {
            return TypeIdentifier.BOOL_TYPE
        }

        override fun translate(): ArrayList<Instruction> {
            val intValue = if (value == "true") 1 else 0
            return arrayListOf(MoveInstruction(Registers.allocate(), Immediate(intValue)))
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitBoolLiterAST(this)
        }
    }

    data class CharLiterAST(private val value: String) : ExprAST() {

        override fun getType(st: SymbolTable): TypeIdentifier {
            return TypeIdentifier.CHAR_TYPE
        }

        override fun translate(): ArrayList<Instruction> {
            return if (value == "'\\0'") {
                arrayListOf(MoveInstruction(Registers.allocate(), Immediate(0)))
            } else {
                arrayListOf(MoveInstruction(Registers.allocate(), ImmediateChar(value)))
            }
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitCharLiterAST(this)
        }
    }

    data class StrLiterAST(val value: String) : ExprAST() {

        override fun getType(st: SymbolTable): TypeIdentifier {
            return TypeIdentifier.STRING_TYPE
        }

        override fun translate(): ArrayList<Instruction> {
            // TODO: ADD LABEL FOR MSG
//            return arrayListOf(LoadInstruction(regs.allocate(), //load instruction)
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitStrLiterAST(this)
        }
    }

    object PairLiterAST : ExprAST() {

        override fun getType(st: SymbolTable): TypeIdentifier {
            return TypeIdentifier.PAIR_LIT_TYPE
        }

        override fun translate(): ArrayList<Instruction> {
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitPairLiterAST(this)
        }
    }

    data class IdentAST(val ctx: WaccParser.ExprContext, val value: String) : ExprAST() {

        override fun getType(st: SymbolTable): TypeIdentifier {
            val type = st.lookupAll(value)
            return when (type) {
                null -> {
                    TypeIdentifier.GENERIC
                }
                else -> {
                    type.getType()
                }
            }
        }

        override fun translate(): ArrayList<Instruction> {
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitIdentAST(this)
        }
    }

    data class ArrayElemAST(
        val ctx: WaccParser.ArrayElemContext,
        val ident: String,
        val exprs: ArrayList<ExprAST>
    ) : ExprAST() {

        override fun getType(st: SymbolTable): TypeIdentifier {
            val type = st.lookupAll(ident)
            return if (type == null) {
                TypeIdentifier.GENERIC
            } else {
                val typeIdent = type.getType()
                if (typeIdent !is TypeIdentifier.ArrayIdentifier) {
                    return TypeIdentifier.GENERIC
                }
                return typeIdent.getType()
            }
        }

        override fun translate(): ArrayList<Instruction> {
            return ArrayList()
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitArrayElemAST(this)
        }
    }

    data class UnOpAST(
        val ctx: WaccParser.UnaryOperContext,
        val expr: ExprAST,
        val operator: String
    ) : ExprAST() {

        override fun getType(st: SymbolTable): TypeIdentifier {
            // Will need to get unaryOpIdentifier from st (can I if it an invalid operator) and get its return type
            return when (operator) {
                "-" -> TypeIdentifier.INT_TYPE
                "!" -> TypeIdentifier.BOOL_TYPE
                "len" -> TypeIdentifier.INT_TYPE
                "ord" -> TypeIdentifier.INT_TYPE
                "chr" -> TypeIdentifier.CHAR_TYPE
                else -> TypeIdentifier()
            }
        }

        override fun translate(): ArrayList<Instruction> {
            return when (operator) {
                "-" -> translateNeg()
                else -> ArrayList()
            }
        }

        fun translateNeg(): ArrayList<Instruction> {
            val results: ArrayList<Instruction> = ArrayList()

            results.addAll(expr.translate())
            val dest: Register = expr.dest!!
            results.add(LoadInstruction(dest, AddressingMode.AddressingMode2(Registers.sp, null)))
            results.add(ReverseSubtractInstruction(dest, dest, Immediate(0)))
            return results
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitUnOpAST(this)
        }
    }

    data class BinOpAST(
        val ctx: WaccParser.ExprContext,
        val expr1: ExprAST,
        val expr2: ExprAST,
        val operator: String
    ) : ExprAST() {

        companion object {
            val intIntFunctions = hashSetOf("*", "/", "+", "-", "%")

            val intCharFunctions = hashSetOf(">", ">=", "<", "<=")

            val boolBoolFunctions = hashSetOf("&&", "||")
        }

        override fun getType(st: SymbolTable): TypeIdentifier {
            return when (operator) {
                // Need valid min and max integers to put here
                "+", "%", "/", "*", "-" -> TypeIdentifier.INT_TYPE
                else -> TypeIdentifier.BoolIdentifier
            }
        }

        override fun translate(): ArrayList<Instruction> {
            return when (operator) {
//                "+" -> translateAdd(regs)
                else -> ArrayList()
            }
        }

        fun translateAdd(regs: Registers) {
            when {
                expr1 is IntLiterAST -> {
                    expr2.translate()
                    val dest: Register = expr2.dest!!
                    AssemblyRepresentation.addMainInstr(AddInstruction(dest, dest, Immediate(expr1.getValue())))

                    this.dest = dest
                }
                expr2 is IntLiterAST -> {
                    expr1.translate()
                    val dest: Register = expr2.dest!!
                    AssemblyRepresentation.addMainInstr(AddInstruction(dest, dest, Immediate(expr2.getValue())))

                    this.dest = dest
                }
                else -> {
                    expr1.translate()
                    expr2.translate()

                    val dest1: Register = expr1.dest!!
                    val dest2: Register = expr2.dest!!

                    AssemblyRepresentation.addMainInstr(AddInstruction(dest1, dest1, dest2))

                    regs.free(dest2)
                    this.dest = dest1
                }
            }
        }

        override fun <T> accept(visitor: ASTVisitor<T>): T {
            return visitor.visitBinOpAST(this)
        }
    }
}
