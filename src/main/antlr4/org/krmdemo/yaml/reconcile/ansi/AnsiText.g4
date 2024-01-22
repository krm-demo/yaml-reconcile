grammar AnsiText;

text     : (lineLF)* lineOpen EOF;
lineLF   : lineOpen CRLF;
lineOpen : CHAR*;

CHAR : ~ ('\r'|'\n');
CRLF : '\r'? '\n' | '\r';
