grammar AnsiTextSpans;

// -----------------------------------------------------
// capture the spans devided by ansi-style expressions:
// -----------------------------------------------------

text : (spanAnsiExpr)* spanOpen EOF;

spanAnsiExpr : spanOpen ANSI_EXPR;
spanOpen     : NON_ANSI*;

NON_ANSI  : ~'|';
ANSI_EXPR : '|';
