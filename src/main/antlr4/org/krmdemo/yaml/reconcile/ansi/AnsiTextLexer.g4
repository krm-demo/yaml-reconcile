lexer grammar AnsiTextLexer;

// ------------- default mode: ------------------

STYLE_OPEN : '@|' -> pushMode(STYLE_MODE);
STYLE_CLOSE : '|@';

AT_PIPE_PIPE : '@||' { setText("@|"); };
PIPE_PIPE_AT : '||@' { setText("|@"); };

ESC_SEQ_OPEN : '\u001B[' -> pushMode(ESC_SEQ_MODE);

CRLF : [\r]? [\n] | [\r];
CHAR : .;

// --------- ansi-text-style mode: --------------

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
STYLE_CHAR : [0-9a-zA-Z()*_/!^];

// ----------- ESC-sequence mode: ---------------

mode ESC_SEQ_MODE;

ESC_SEQ_CLOSE : 'm' -> popMode;
ESC_SEQ_SEP : ';';

ESC_SEQ_RESET_ALL : '0';

ESC_SEQ_BOLD : '1';
ESC_SEQ_DIM : '2';
ESC_SEQ_ITALIC : '3';
ESC_SEQ_UNDERLINE : '4';
ESC_SEQ_BLINKING : '5';
ESC_SEQ_INVERSE : '7';
ESC_SEQ_HIDDEN : '8';
ESC_SEQ_STRIKETHROUGH : '9';

ESC_SEQ_RESET_BOLD_DIM : '22';
ESC_SEQ_RESET_ITALIC : '23';
ESC_SEQ_RESET_UNDERLINE : '24';
ESC_SEQ_RESET_BLINKING : '25';
ESC_SEQ_RESET_INVERSE : '27';
ESC_SEQ_RESET_HIDDEN : '28';
ESC_SEQ_RESET_STRIKETHROUGH : '29';

ESC_SEQ_FG_256 : '38;5;';
ESC_SEQ_BG_256 : '48;5;';

ESC_SEQ_FG_RGB : '38;2;';
ESC_SEQ_BG_RGB : '48;2;';

ESC_SEQ_INTEGER : [0-9]+;
