grammar AnsiText;

// ---------------------------------------------------------
// have to capture spans over lines, where each
// captured line must consist of spans splited with styles
// ---------------------------------------------------------

text : line (CRLF line)*;
line : (span | styleApply | styleReset)*;
span : (DOUBLE_PIPE | STYLE_CHAR | CHAR_COMMA | CHAR_WS | CHAR_SEMICOLON)+;

styleApply : STYLE_APPLY styleAttr (CHAR_COMMA styleAttr)* (CHAR_WS | CHAR_SEMICOLON )?;
styleAttr  : STYLE_CHAR+;
styleReset : STYLE_RESET;

DOUBLE_PIPE : '||';
STYLE_APPLY : '@|';
STYLE_RESET : '|@';
CHAR_PIPE   : '|';   // <-- this symbol is prohibited

STYLE_CHAR : [a-zA-Z0-9()#*_/!];
CHAR_COMMA : ',';
CHAR_WS : [ \t];
CHAR_SEMICOLON : ';';

CRLF : '\r'? '\n' | '\r';
CHAR : .;
