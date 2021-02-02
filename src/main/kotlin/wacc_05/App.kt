package wacc_05

import antlr.BasicLexer
import antlr.BasicParser
import org.antlr.v4.runtime.*

object App {

    @JvmStatic
    fun main(args: Array<String>) {
        println("YAYY")
        val inputStream = System.`in`
        val input = CharStreams.fromStream(inputStream)
        val lexer = BasicLexer(input)
        val tokens = CommonTokenStream(lexer)
        val parser = BasicParser(tokens)
        val tree = parser.prog()   //`prog` is the entry point we defined in our grammar
        println(tree.toStringTree(parser))
        println("FINISHED")
    }

}