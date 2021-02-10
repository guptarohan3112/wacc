package wacc_05.symbol_table.identifier_objects

open class TypeIdentifier : IdentifierObject() {

    companion object {
        const val BOOLEAN = "bool"
        const val CHARACTER = "char"
        const val INTEGER = "int"
        const val STRING = "string"
        const val PAIR = "pair"
        const val ARRAY = "array"

        // static type definitions to use globally instead of redefining on each use
        val INT_TYPE = IntIdentifier(Int.MIN_VALUE, Int.MAX_VALUE)
        val INT_TYPE_CHAR = IntIdentifier(0, 256) // an int strictly within the range of a char
        val CHAR_TYPE = CharIdentifier
        val BOOL_TYPE = BoolIdentifier
        val STRING_TYPE = StringIdentifier
    }

    // This identifier is set as the type when a semantic error has been found (eg: identifier does not currently exist in the symbol table)
    object NullIdentifier : TypeIdentifier() {
        override fun toString(): String {
            return "null"
        }
    }

    object BoolIdentifier : TypeIdentifier() {
        override fun toString(): String {
            return BOOLEAN
        }
    }

    object CharIdentifier : TypeIdentifier() {
        override fun toString(): String {
            return CHARACTER
        }
    }

    object StringIdentifier : TypeIdentifier() {
        override fun toString(): String {
            return STRING
        }
    }

    data class IntIdentifier(private val min: Int = Int.MIN_VALUE, private val max: Int = Int.MAX_VALUE) :
        TypeIdentifier() {

        fun valid(value: Int): Boolean {
            return value in min until max
        }

        override fun toString(): String {
            return INTEGER
        }

    }

    data class ArrayIdentifier(private val elemType: TypeIdentifier, private val size: Int) : TypeIdentifier() {
        override fun toString(): String {
            return ARRAY
        }
    }

    data class PairIdentifier(private val fstType: TypeIdentifier, private val sndType: TypeIdentifier) :
        TypeIdentifier() {
        override fun toString(): String {
            return PAIR
        }

        fun getFirstType() : TypeIdentifier {
            return fstType
        }

        fun getSecondType() : TypeIdentifier {
            return sndType
        }
    }

    object PairLiterIdentifier : TypeIdentifier() {
        override fun toString(): String {
            return PAIR
        }
    }
}