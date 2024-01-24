grammar AnsiTextSpans;

// -----------------------------------------------------
// capture the spans devided by ansi-style expressions:
// -----------------------------------------------------

text : (span ANSI_EXPR)* span EOF;
span : NON_ANSI*;

NON_ANSI  : ~'|';
ANSI_EXPR : '|';
