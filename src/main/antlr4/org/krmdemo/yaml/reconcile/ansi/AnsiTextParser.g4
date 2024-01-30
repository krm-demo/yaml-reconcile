parser grammar AnsiTextParser;

options {
    tokenVocab = AnsiTextLexer;
}

text : line (CRLF line)* EOF;
line : (span | styleOpen | styleClose)*;

span : CHAR+;
styleOpen : STYLE_OPEN styleAttr (CHAR_COMMA styleAttr)* (CHAR_SEMICOLON | CHAR_WS)?;
styleClose : STYLE_CLOSE;

styleAttr : styleAttrName; // | styleFG | styleBG;
styleAttrName : STYLE_CHAR+;

//styleFG : FG_OPEN (fgColorName | fgColor256 | fgColorRGB) CLOSE_BRACKET;
//styleBG : BG_OPEN (bgColorName | bgColor256 | bgColorRGB) CLOSE_BRACKET;

//fgColorName : STYLE_CHAR+;
//fgColor256  : HEX_256;
//fgColorRGB  : HEX_RGB;

//bgColorName : STYLE_CHAR+;
//bgColor256  : HEX_256;
//bgColorRGB  : HEX_RGB;
