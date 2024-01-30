lexer grammar AnsiTextLexer;

STYLE_OPEN : '@|' -> pushMode(STYLE_MODE);
STYLE_CLOSE : '|@';

CRLF : '\r'? '\n' | '\r';
CHAR : .;

mode STYLE_MODE;
STYLE_CHAR : [a-zA-Z0-9()#*_/!];
CHAR_WS : [ \t] -> popMode;
CHAR_SEMICOLON : ';' -> popMode;
