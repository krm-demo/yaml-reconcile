parser grammar AnsiTextParser;

options {
    tokenVocab = AnsiTextLexer;
}

// --------------------- dividing the text on lines: --------------------------

text : line (CRLF line)* EOF;
line : (span | styleOpen | styleClose | escSeq)*;

// ------ dividing the line on spans, ansi-styles and escape-sequences: -------

span : (CHAR | AT_PIPE_PIPE | PIPE_PIPE_AT)+;
styleOpen : STYLE_OPEN styleAttr (CHAR_COMMA styleAttr)* (CHAR_SEMICOLON | CHAR_WS)?;
styleClose : STYLE_CLOSE;

// ---------------------recognizing ansi-style attributes: --------------------

styleAttr : styleAttrName | styleFG | styleBG;
styleAttrName : STYLE_CHAR+;

styleFG : FG_OPEN (fgColorName | fgColor256 | fgColorRGB) CLOSE_BRACKET;
styleBG : BG_OPEN (bgColorName | bgColor256 | bgColorRGB) CLOSE_BRACKET;

fgColorName : STYLE_CHAR+;
fgColor256  : HEX_256;
fgColorRGB  : HEX_RGB;

bgColorName : STYLE_CHAR+;
bgColor256  : HEX_256;
bgColorRGB  : HEX_RGB;

// ------------------- recognizing escape-sequences: --------------------------

escSeq : ESC_SEQ_OPEN escSeqAttr (ESC_SEQ_SEP escSeqAttr)* ESC_SEQ_CLOSE;
escSeqAttr : escSeqAttrCode | fgEscColor256 | bgEscColor256 | fgEscColorRGB | bgEscColorRGB;

escSeqAttrCode : ESC_SEQ_RESET_ALL | ESC_SEQ_RESET_FG_COLOR | ESC_SEQ_RESET_BG_COLOR
        | ESC_SEQ_APPLY_BOLD | ESC_SEQ_APPLY_DIM | ESC_SEQ_APPLY_ITALIC |ESC_SEQ_APPLY_UNDERLINE
        | ESC_SEQ_APPLY_BLINKING | ESC_SEQ_APPLY_INVERSE | ESC_SEQ_APPLY_HIDDEN | ESC_SEQ_APPLY_STRIKETHROUGH
        | ESC_SEQ_RESET_BOLD_DIM | ESC_SEQ_RESET_ITALIC  | ESC_SEQ_RESET_UNDERLINE
        | ESC_SEQ_RESET_BLINKING | ESC_SEQ_RESET_INVERSE | ESC_SEQ_RESET_HIDDEN | ESC_SEQ_RESET_STRIKETHROUGH
        | ESC_SEQ_APPLY_FG_BLACK | ESC_SEQ_APPLY_FG_RED | ESC_SEQ_APPLY_FG_GREEN | ESC_SEQ_APPLY_FG_YELLOW
        | ESC_SEQ_APPLY_FG_BLUE  | ESC_SEQ_APPLY_FG_MAGENTA | ESC_SEQ_APPLY_FG_CYAN | ESC_SEQ_APPLY_FG_WHITE
        | ESC_SEQ_APPLY_BG_BLACK | ESC_SEQ_APPLY_BG_RED | ESC_SEQ_APPLY_BG_GREEN | ESC_SEQ_APPLY_BG_YELLOW
        | ESC_SEQ_APPLY_BG_BLUE  | ESC_SEQ_APPLY_BG_MAGENTA | ESC_SEQ_APPLY_BG_CYAN | ESC_SEQ_APPLY_BG_WHITE
        ;


fgEscColor256 : ESC_SEQ_FG_256 ESC_SEQ_INTEGER;
bgEscColor256 : ESC_SEQ_BG_256 ESC_SEQ_INTEGER;

fgEscColorRGB : ESC_SEQ_FG_RGB
                red=ESC_SEQ_INTEGER   ESC_SEQ_SEP
                green=ESC_SEQ_INTEGER ESC_SEQ_SEP
                blue=ESC_SEQ_INTEGER;

bgEscColorRGB : ESC_SEQ_BG_RGB
                red=ESC_SEQ_INTEGER   ESC_SEQ_SEP
                green=ESC_SEQ_INTEGER ESC_SEQ_SEP
                blue=ESC_SEQ_INTEGER;
