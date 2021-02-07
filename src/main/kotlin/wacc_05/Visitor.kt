package wacc_05

import antlr.WaccParser
import antlr.WaccParserBaseVisitor
import wacc_05.ast_structure.*
import wacc_05.ast_structure.assignment_ast.*

class Visitor : WaccParserBaseVisitor<AST>() {

    /* Function: visitProg()
        ----------------------------
        Generates a SkipAST node without referencing the context as the context is not required.
     */
    override fun visitProg(ctx: WaccParser.ProgContext): ProgramAST {
        val funcs: MutableList<FunctionAST> = ArrayList()
        for (funcCtx in ctx.func()) {
            funcs.add(visitFunc(funcCtx))
        }
        return ProgramAST(funcs as ArrayList<FunctionAST>, visitStat(ctx.stat()), )
    }

    /* Function: visitProg()
        ----------------------------
        Generates a SkipAST node without referencing the context as the context is not required.
     */
    override fun visitFunc(ctx: WaccParser.FuncContext): FunctionAST {
        // TODO: visit param list, get return type, get statement body etc. Currently just to avoid other errors!
        return FunctionAST("", ctx.IDENT().text, ParamListAST(ArrayList()), StatementAST.SkipAST)
    }

    /* Function: visitStat()
       ---------------------
       A function to be used when we have a StatContext but do not know its subtype (StatSkip, ... etc.)
       Param ctx - the context we want to visit but do not know its specific type
       Returns the StatementAST resulting from visiting the given ctx
     */
    private fun visitStat(ctx: WaccParser.StatContext): StatementAST {
        // visit() would return AST! but since we override all stat methods here we can safely cast to StatementAST.
        return visit(ctx) as StatementAST
    }

    /* Function: visitStatSkip()
    ----------------------------
        Generates a SkipAST node without referencing the context as the context is not required.
     */
    override fun visitStatSkip(ctx: WaccParser.StatSkipContext): StatementAST {
        return StatementAST.SkipAST
    }

    /* Function: visitStatDeclaration()
        -------------------------------
        Generates a DeclAST node using the type, IDENT and assignRHS from the context and relevant visitX() calls
        where necessary
     */
    override fun visitStatDeclaration(ctx: WaccParser.StatDeclarationContext): StatementAST {
        return StatementAST.DeclAST(visitType(ctx.type()), ctx.IDENT().text, visitAssignRHS(ctx.assignRHS()))
    }

    /* Function: visitStatAssign()
       ----------------------------
       Generates an AssignAST node using the assignLHS and assignRHS from the context and relevant visitX() calls
       where necessary.
     */
    override fun visitStatAssign(ctx: WaccParser.StatAssignContext): StatementAST {
        return StatementAST.AssignAST(visitAssignLHS(ctx.assignLHS()), visitAssignRHS(ctx.assignRHS()))
    }

    /* Function: visitStatRead()
       -------------------------
       Generates a ReadAST node with the child being the AssignLHSAST resulting from calling visitAssignLHS on the
       context's assignLHS child.
     */
    override fun visitStatRead(ctx: WaccParser.StatReadContext): StatementAST {
        return StatementAST.ReadAST(visitAssignLHS(ctx.assignLHS()))
    }

    /* Function: visitStatFree()
       -------------------------
       Generates a FreeAST node with the child being the ExprAST resulting from calling visitExpr() on the
       context's expr child.
     */
    override fun visitStatFree(ctx: WaccParser.StatFreeContext): StatementAST {
        return StatementAST.FreeAST(visitExpr(ctx.expr()))
    }

    /* Function: visitStatReturn()
       -------------------------
       Generates a ReturnAST node with the child being the ExprAST resulting from calling visitExpr() on ctx's
       expr child.
     */
    override fun visitStatReturn(ctx: WaccParser.StatReturnContext): StatementAST {
        return StatementAST.ReturnAST(visitExpr(ctx.expr()))
    }

    /* Function: visitStatExit()
       -------------------------
       Generates an ExitAST node with the child being the ExprAST resulting from calling visitExpr() on ctx's
       expr child.
     */
    override fun visitStatExit(ctx: WaccParser.StatExitContext): StatementAST {
        return StatementAST.ExitAST(visitExpr(ctx.expr()))
    }

    /* Function: visitStatPrint()
       -------------------------
       Generates a PrintAST node with the child being the ExprAST resulting from calling visitExpr() on ctx's
       expr child.
     */
    override fun visitStatPrint(ctx: WaccParser.StatPrintContext): StatementAST {
        return StatementAST.PrintAST(visitExpr(ctx.expr()), false)
    }

    /* Function: visitStatPrintln()
       -------------------------
       Generates a PrintAST node with the child being the ExprAST resulting from calling visitExpr() on ctx's
       expr child.
     */
    override fun visitStatPrintln(ctx: WaccParser.StatPrintlnContext): StatementAST {
        return StatementAST.PrintAST(visitExpr(ctx.expr()), true)
    }

    /* Function: visitStatIf()
        ----------------------
        Returns an IfAST node with children corresponding to calls to visitExpr() and visitStat(). Assumes there are
        2 stat children (one for the if branch and one for the else branch.)
     */
    override fun visitStatIf(ctx: WaccParser.StatIfContext): StatementAST {
        return StatementAST.IfAST(visitExpr(ctx.expr()), visitStat(ctx.stat(0)), visitStat(ctx.stat(1)))
    }

    /* Function: visitStatWhile()
       --------------------------
       Returns a WhileAST node with children corresponding to calls to visitExpr() and visitStat().
     */
    override fun visitStatWhile(ctx: WaccParser.StatWhileContext): StatementAST {
        return StatementAST.WhileAST(visitExpr(ctx.expr()), visitStat(ctx.stat()))
    }

    /* Function: visitStatBeginEnd()
       -----------------------------
       Returns a BeginAST node which has one child, corresponding to a call to visitStat().
     */
    override fun visitStatBeginEnd(ctx: WaccParser.StatBeginEndContext): StatementAST {
        return StatementAST.BeginAST(visitStat(ctx.stat()))
    }

    /* Function: visitStatSequential()
       -------------------------------
       Returns a SequentialAST node with children corresponding to two calls to visitStat(). Assumes that ctx has
       two stat children.
     */
    override fun visitStatSequential(ctx: WaccParser.StatSequentialContext): StatementAST {
        return StatementAST.SequentialAST(visitStat(ctx.stat(0)), visitStat(ctx.stat(1)))
    }

    /* Function: visitAssignRHS()
       --------------------------
       Returns an AssignRHSAST node, by matching the context with each possible type of assignRHS
       or throws an error if this fails
     */
    override fun visitAssignRHS(ctx: WaccParser.AssignRHSContext): AssignRHSAST {
        // we use the when statement to check which type of assignRHS we have and generate a child AST
        // node accordingly
        return when {
            ctx.expr() != null -> {
                visitExpr(ctx.expr())
            }
            ctx.arrayLit() != null -> {
                visitArrayLit(ctx.arrayLit())
            }
            ctx.newPair() != null -> {
                visitNewPair(ctx.newPair())
            }
            ctx.pairElem() != null -> {
                visitPairElem(ctx.pairElem())
            }
            ctx.funcCall() != null -> {
                visitFuncCall(ctx.funcCall())
            }

            // TODO - throw suitable error
            else -> throw Exception()
        }
    }

    /* Function: visitAssignLHS()
        -------------------------
        Generates an AssignLHSAST node by matching the context with each possible type of assignLHS, or
        throws an error if this fails.
     */
    override fun visitAssignLHS(ctx: WaccParser.AssignLHSContext): AssignLHSAST {
        // we use the when statement to check which type of assignLHS we have and generate a child AST
        // node accordingly
        return when {
            ctx.IDENT() != null -> {
                AssignLHSAST(ident = ctx.IDENT().text)
            }
            ctx.pairElem() != null -> {
                AssignLHSAST(pairElem = visitPairElem(ctx.pairElem()))
            }
            ctx.arrayElem() != null -> {
                AssignLHSAST(arrElem = visitArrayElem(ctx.arrayElem()))
            }

            // TODO - throw suitable error
            else -> throw Exception()
        }

    }

    override fun visitType(ctx: WaccParser.TypeContext): TypeAST {
        // TODO - return purely for compilation purposes
        return TypeAST.BaseTypeAST("")
    }

    /* Function: visitNewPair()
        ------------------------
        Returns a NewPairAST node, by matching the context with each of the two expr children. It assumes these
        two children exist.
     */
    override fun visitIntLit(ctx: WaccParser.IntLitContext): ExprAST.IntLiterAST {
        var sign = ""
        if (ctx.PLUS() != null) {
            sign = ctx.PLUS().text
        }
        if (ctx.MINUS() != null) {
            sign = ctx.MINUS().text
        }
        return ExprAST.IntLiterAST(sign, ctx.INT_LIT().text)
    }

    /* Function: visitBoolLit()
        ------------------------
        Returns a BoolLiterAST node with the value of the literal as a string
     */
    override fun visitBoolLit(ctx: WaccParser.BoolLitContext): ExprAST.BoolLiterAST {
        return ExprAST.BoolLiterAST(ctx.BOOL_LIT().text)
    }

    /* Function: visitCharLit()
        ------------------------
        Returns a CharLiterAST node with the value of the literal as a string
     */
    override fun visitCharLit(ctx: WaccParser.CharLitContext): ExprAST.CharLiterAST {
        return ExprAST.CharLiterAST(ctx.CHAR_LIT().text)
    }

    /* Function: visitStrLot()
        ------------------------
        Returns a StrLiterAST node with the value of the literal as a string
     */
    override fun visitStrLit(ctx: WaccParser.StrLitContext): ExprAST.StrLiterAST {
        return ExprAST.StrLiterAST(ctx.STR_LIT().text)
    }

    /* Function: visitNewPair()
       ------------------------
       Returns a NewPairAST node, by matching the context with each of the two expr children. It assumes these
       two children exist.
     */
    override fun visitNewPair(ctx: WaccParser.NewPairContext): NewPairAST {
        return NewPairAST(visitExpr(ctx.expr(0)), visitExpr(ctx.expr(1)))
    }

    /* Function: visitPairElem()
        -------------------------
        Generates a PairElemAST, which has expr child generated by calling visitExpr on the context's expr child.
     */
    override fun visitPairElem(ctx: WaccParser.PairElemContext): PairElemAST {
        return PairElemAST(visitExpr(ctx.expr()))
    }


    /* Function: visitArrayLit()
        ------------------------
        Returns a ArrayLiterAST node, by matching each of the expression children in the context and adding to internal
        ArrayList.
     */
    override fun visitArrayLit(ctx: WaccParser.ArrayLitContext): ArrayLiterAST {
        val exprs: MutableList<ExprAST> = ArrayList()
        for (exprCtx in ctx.expr()) {
            exprs.add(visitExpr(exprCtx))
        }
        return ArrayLiterAST(exprs as ArrayList<ExprAST>)
    }

    /* Function: VisitArrayElem()
        ------------------------
        Returns a ArrayElemAST node, by matching each of the expression children in the context and adding to internal
        ArrayList. Also matches IDENT token to get id of the ArrayElem
     */

    override fun visitArrayElem(ctx: WaccParser.ArrayElemContext): ExprAST.ArrayElemAST {
        val exprs: MutableList<ExprAST> = ArrayList()
        for (exprCtx in ctx.expr()) {
            exprs.add(visitExpr(exprCtx))
        }
        return ExprAST.ArrayElemAST(ctx.IDENT().text, exprs as ArrayList<ExprAST>)
    }

    /* Function: VisitFuncCall()
        ------------------------
        Generated a FuncCallAST with function name following ctx's IDENT token, and the args are either null (ctx's
        argsList is null) or generated by calling visitArgList.
     */
    override fun visitFuncCall(ctx: WaccParser.FuncCallContext): FuncCallAST {
        val argList = if (ctx.argList() == null) {
            null
        } else {
            visitArgList(ctx.argList())
        }
        return FuncCallAST(ctx.IDENT().text, argList)
    }

    /* Function: visitArgList()
        -----------------------
        Generates an ArgListAST for the 1(+) args (expressions) in the context by calling visitExpr() on these.
     */
    override fun visitArgList(ctx: WaccParser.ArgListContext): ArgListAST {
        val exprs: MutableList<ExprAST> = ArrayList()
        for (exprCtx in ctx.expr()) {
            exprs.add(visitExpr(exprCtx))
        }
        return ArgListAST(exprs as ArrayList<ExprAST>)
    }

    /* Function: visitExpr()
        -----------------------
        Generates an ExprAST node depending on the type of the context. Calls respective visitor of each type to
        produce AST node, except for in simple cases like IDENT, where the AST node can be generated directly.
     */
    override fun visitExpr(ctx: WaccParser.ExprContext): ExprAST {
        // we use the when statement to check which type of expr we have and generate a child AST
        // node accordingly
        return when {
            ctx.intLit() != null -> {
                visitIntLit(ctx.intLit())
            }
            ctx.boolLit() != null -> {
                visitBoolLit(ctx.boolLit())
            }
            ctx.charLit() != null -> {
                visitCharLit(ctx.charLit())
            }
            ctx.strLit() != null -> {
                visitStrLit(ctx.strLit())
            }
            ctx.PAIR_LIT() != null -> {
                ExprAST.PairLiterAST(value = ctx.PAIR_LIT().text)
            }
            ctx.IDENT() != null -> {
                ExprAST.IdentAST(value = ctx.IDENT().text)
            }
            ctx.arrayElem() != null -> {
                visitArrayElem(ctx.arrayElem())
            }
            ctx.unaryOper() != null -> {
                // TODO : this is a hacky solution, why is ctx.expr a list in the first place?
                ExprAST.UnOpAST(visitExpr(ctx.expr()[0]), ctx.unaryOper().text)
            }
            ctx.OPEN_PARENTHESES() != null -> {
                // TODO: See above
                visitExpr(ctx.expr()[0])
            }
            // TODO - throw suitable error
            else -> throw Exception()
        }
    }
}