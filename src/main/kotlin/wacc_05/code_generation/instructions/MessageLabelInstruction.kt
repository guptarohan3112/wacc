package wacc_05.code_generation.instructions

import java.awt.Label

class MessageLabelInstruction(private val name: String, private val string: String) : LabelInstruction(name) {
    companion object {
        private var currentLabel: Int = 0

        public fun getUniqueLabel(string: String): MessageLabelInstruction {
            return MessageLabelInstruction("msg_${currentLabel++}", string)
        }
    }

    private var length: Int = 0

    init {
        length = string.length
    }

    override fun toString(): String {
        return "$name:\n\t\t.word $length\n\t\t.ascii \"$string\""
    }

}