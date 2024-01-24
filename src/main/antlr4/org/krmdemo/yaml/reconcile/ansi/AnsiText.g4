grammar AnsiText;

// --------------------------------------------------
// have to capture spans over lines, where each
// captured line must consist of splited spans
// --------------------------------------------------

text : (line CRLF)* line EOF;
line : (span | styleApply | styleReset)*;
span : (CHAR | WS | DOUBLE_PIPE | DOUBLE_AT);

styleApply : '@|' styleAttr (',' styleAttr)* (WS | ';' )?;
styleAttr  : STYLE_CHAR+;
styleReset : '|@';

STYLE_CHAR : [a-zA-Z0-9()#*_/!]; // ????
WS : [ \t];

DOUBLE_PIPE : '||';
DOUBLE_AT   : '@@';

CHAR : ~ [\r\n|@ \t];
CRLF : '\r'? '\n' | '\r';
