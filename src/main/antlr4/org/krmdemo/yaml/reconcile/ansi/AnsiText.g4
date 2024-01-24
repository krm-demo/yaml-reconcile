grammar AnsiText;

// --------------------------------------------------
// have to capture spans over lines, where each
// captured line must consist of splited spans
// --------------------------------------------------

text : ANY_CHAR*? EOF;

ANY_CHAR : .;
