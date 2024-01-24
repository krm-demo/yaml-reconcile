grammar AnsiTextLines;

// --------------------------------------------------
// capture the lines divided by line-feed separator:
// --------------------------------------------------

text : (lineLF)* lineOpen EOF;

lineLF    : lineOpen CRLF;
lineOpen  : CHAR*;

CHAR : ~ ('\r'|'\n');
CRLF : '\r'? '\n' | '\r';
