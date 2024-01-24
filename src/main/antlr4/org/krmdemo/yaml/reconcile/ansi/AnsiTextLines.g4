grammar AnsiTextLines;

// --------------------------------------------------
// capture the lines divided by line-feed separator:
// --------------------------------------------------

text : (line CRLF)* line EOF;
line : CHAR*;

CHAR : ~ ('\r'|'\n');
CRLF : '\r'? '\n' | '\r';
