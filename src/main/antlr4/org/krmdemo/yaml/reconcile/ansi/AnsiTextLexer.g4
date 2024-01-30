lexer grammar AnsiTextLexer;

STYLE_OPEN : '@|' -> pushMode(STYLE_MODE);
STYLE_CLOSE : '|@';

CRLF : [\r]? [\n] | [\r];
CHAR : .;

mode STYLE_MODE;

FG_OPEN : 'fg(';
BG_OPEN : 'bg(';
CLOSE_BRACKET : ')';

HEX_256 : '#' HEX_DIGIT HEX_DIGIT;
HEX_RGB : '#' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT;
fragment HEX_DIGIT : [0-9a-fA-F];

CHAR_WS : [ \t] -> popMode;
CHAR_COMMA : ',';
CHAR_SEMICOLON : ';' -> popMode;
STYLE_CHAR : [0-9a-zA-Z()*_/!];
