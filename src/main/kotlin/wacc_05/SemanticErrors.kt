package wacc_05

import wacc_05.symbol_table.identifier_objects.TypeIdentifier
import kotlin.system.exitProcess

interface SemanticErrors {

    fun invalidIdentifier(name: String)

    fun invalidType(typeName: String)

    fun invalidFunction(funcName: String)

    fun repeatVariableDeclaration(varName: String)

    fun typeMismatch(expected: TypeIdentifier, actual: TypeIdentifier)

    fun argNumberError(fName: String, expected: Int, actual: Int)

    fun invalidReturnType()
}