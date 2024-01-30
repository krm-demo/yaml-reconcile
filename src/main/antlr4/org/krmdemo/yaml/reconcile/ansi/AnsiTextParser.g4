parser grammar AnsiTextParser;

options {
    tokenVocab = AnsiTextLexer;
}

// ---------------------------------------------------------
// have to capture spans over lines, where each
// captured line must consist of spans splited with styles
// ---------------------------------------------------------

text : line (CRLF line)* EOF;
line : (span | styleOpen | styleClose)*;

span : CHAR+;
styleOpen : STYLE_OPEN STYLE_CHAR+ (CHAR_SEMICOLON | CHAR_WS)?;
styleClose : STYLE_CLOSE;

//line : (span | styleApply | styleReset)*;
//span : (DOUBLE_PIPE | STYLE_CHAR | CHAR_COMMA | CHAR_WS | CHAR_SEMICOLON | CHAR)+;
//
//styleApply : STYLE_APPLY styleAttr (CHAR_COMMA styleAttr)* (CHAR_WS | CHAR_SEMICOLON )?;
//styleReset : STYLE_RESET;
//
//styleAttr : styleAttrName | styleFG | styleBG;
//styleFG : 'fg(' fgName ')'
//        | 'fg(#' fg256 ')'
//        | 'fg(#' fgRGB ')';
//styleBG : 'bg(' bgName ')'
//        | 'bg(#' bg256 ')'
//        | 'bg(#' bgRGB ')';
//
//styleAttrName : STYLE_CHAR+;
//fgName : STYLE_CHAR+;
//bgName : STYLE_CHAR+;
//
//fg256 : hexDigit hexDigit;
//bg256 : hexDigit hexDigit;
//fgRGB : hexDigit hexDigit hexDigit hexDigit hexDigit hexDigit;
//bgRGB : hexDigit hexDigit hexDigit hexDigit hexDigit hexDigit;
//hexDigit : 'a' | 'b' | 'c' | 'd' | 'e' | 'f'
//         | 'A' | 'B' | 'B' | 'D' | 'E' | 'F'
//         | '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9';
//
//DOUBLE_PIPE : '||';
//STYLE_APPLY : '@|';
//STYLE_RESET : '|@';
//CHAR_PIPE   : '|';   // <-- this symbol is prohibited
//
//STYLE_CHAR : [a-zA-Z0-9()#*_/!];
//CHAR_COMMA : ',';
//CHAR_WS : [ \t];
//CHAR_SEMICOLON : ';';
//
//CRLF : '\r'? '\n' | '\r';
//CHAR : .;
