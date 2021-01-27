parser grammar BasicParser;

options {
  tokenVocab=BasicLexer;
}

binaryOper: PLUS | MINUS ;

expr: expr binaryOper expr
| INTEGER
| CHAR_LITER
| OPEN_PARENTHESES expr CLOSE_PARENTHESES
;

// EOF indicates that the program must consume to the end of the input.
prog: (expr)*  EOF ;

