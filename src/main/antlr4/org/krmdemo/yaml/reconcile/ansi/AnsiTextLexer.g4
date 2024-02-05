lexer grammar AnsiTextLexer;

@lexer::header{
    // ----------------------------------------------
    //          manually injected imports:
    // ----------------------------------------------
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import java.util.stream.Stream;

    import static java.util.Arrays.stream;
    import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.*;
    import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.Color.*;
    // ----------------------------------------------
}

@lexer::members{
    // ----------------------------------------------
    //    manually injected members of lexer:
    // ----------------------------------------------
    private final static Logger log = org.slf4j.LoggerFactory.getLogger(AnsiTextLexer.class);
    static {
        if (log.isTraceEnabled()) {
            log.debug("in static initializing:");
            Stream.of(
                RESET_ALL, RESET_FG, RESET_BG,
                RESET_BOLD, RESET_DIM, RESET_ITALIC, RESET_UNDERLINE,
                RESET_BLINKING, RESET_INVERSE, RESET_HIDDEN, RESET_STRIKETHROUGH,
                APPLY_BOLD, APPLY_DIM, APPLY_ITALIC, APPLY_UNDERLINE,
                APPLY_BLINKING, APPLY_INVERSE, APPLY_HIDDEN, APPLY_STRIKETHROUGH
            ).map(a -> a.ansiCode() + ":" + a.name()).forEach(log::debug);
            stream(Color.values()).map(c -> c.toString()).forEach(log::debug);
        }
    }
    // ----------------------------------------------
}

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

ESC_SEQ_RESET_ALL : '0' { setText(RESET_ALL.name()); };

ESC_SEQ_RESET_BOLD_DIM :      '22' { setText(RESET_BOLD.name()); };
ESC_SEQ_RESET_ITALIC :        '23' { setText(APPLY_ITALIC.name()); };
ESC_SEQ_RESET_UNDERLINE :     '24' { setText(APPLY_UNDERLINE.name()); };
ESC_SEQ_RESET_BLINKING :      '25' { setText(APPLY_BLINKING.name()); };
ESC_SEQ_RESET_INVERSE :       '27' { setText(APPLY_INVERSE.name()); };
ESC_SEQ_RESET_HIDDEN :        '28' { setText(APPLY_HIDDEN.name()); };
ESC_SEQ_RESET_STRIKETHROUGH : '29' { setText(RESET_STRIKETHROUGH.name()); };

ESC_SEQ_RESET_FG_COLOR : '39' { setText(RESET_FG.name()); };
ESC_SEQ_RESET_BG_COLOR : '49' { setText(RESET_BG.name()); };

ESC_SEQ_APPLY_FG_BLACK :   '30' { setText(fg(BLACK).name()); };
ESC_SEQ_APPLY_FG_RED :     '31' { setText(fg(RED).name()); };
ESC_SEQ_APPLY_FG_GREEN :   '32' { setText(fg(GREEN).name()); };
ESC_SEQ_APPLY_FG_YELLOW :  '33' { setText(fg(YELLOW).name()); };
ESC_SEQ_APPLY_FG_BLUE :    '34' { setText(fg(BLUE).name()); };
ESC_SEQ_APPLY_FG_MAGENTA : '35' { setText(fg(MAGENTA).name()); };
ESC_SEQ_APPLY_FG_CYAN :    '36' { setText(fg(CYAN).name()); };
ESC_SEQ_APPLY_FG_WHITE :   '37' { setText(fg(WHITE).name()); };

ESC_SEQ_APPLY_BG_BLACK :   '40' { setText(bg(BLACK).name()); };
ESC_SEQ_APPLY_BG_RED :     '41' { setText(bg(RED).name()); };
ESC_SEQ_APPLY_BG_GREEN :   '42' { setText(bg(GREEN).name()); };
ESC_SEQ_APPLY_BG_YELLOW :  '43' { setText(bg(YELLOW).name()); };
ESC_SEQ_APPLY_BG_BLUE :    '44' { setText(bg(BLUE).name()); };
ESC_SEQ_APPLY_BG_MAGENTA : '45' { setText(bg(MAGENTA).name()); };
ESC_SEQ_APPLY_BG_CYAN :    '46' { setText(bg(CYAN).name()); };
ESC_SEQ_APPLY_BG_WHITE :   '47' { setText(bg(WHITE).name()); };

ESC_SEQ_APPLY_FG_BRIGHT_BLACK :   '90' { setText(fg(BRIGHT_BLACK).name()); };
ESC_SEQ_APPLY_FG_BRIGHT_RED :     '91' { setText(fg(BRIGHT_RED).name()); };
ESC_SEQ_APPLY_FG_BRIGHT_GREEN :   '92' { setText(fg(BRIGHT_GREEN).name()); };
ESC_SEQ_APPLY_FG_BRIGHT_YELLOW :  '93' { setText(fg(BRIGHT_YELLOW).name()); };
ESC_SEQ_APPLY_FG_BRIGHT_BLUE :    '94' { setText(fg(BRIGHT_BLUE).name()); };
ESC_SEQ_APPLY_FG_BRIGHT_MAGENTA : '95' { setText(fg(BRIGHT_MAGENTA).name()); };
ESC_SEQ_APPLY_FG_BRIGHT_CYAN :    '96' { setText(fg(BRIGHT_CYAN).name()); };
ESC_SEQ_APPLY_FG_BRIGHT_WHITE :   '97' { setText(fg(BRIGHT_WHITE).name()); };

ESC_SEQ_APPLY_BG_BRIGHT_BLACK :   '100' { setText(bg(BRIGHT_BLACK).name()); };
ESC_SEQ_APPLY_BG_BRIGHT_RED :     '101' { setText(bg(BRIGHT_RED).name()); };
ESC_SEQ_APPLY_BG_BRIGHT_GREEN :   '102' { setText(bg(BRIGHT_GREEN).name()); };
ESC_SEQ_APPLY_BG_BRIGHT_YELLOW :  '103' { setText(bg(BRIGHT_YELLOW).name()); };
ESC_SEQ_APPLY_BG_BRIGHT_BLUE :    '104' { setText(bg(BRIGHT_BLUE).name()); };
ESC_SEQ_APPLY_BG_BRIGHT_MAGENTA : '105' { setText(bg(BRIGHT_MAGENTA).name()); };
ESC_SEQ_APPLY_BG_BRIGHT_CYAN :    '106' { setText(bg(BRIGHT_CYAN).name()); };
ESC_SEQ_APPLY_BG_BRIGHT_WHITE :   '107' { setText(bg(BRIGHT_WHITE).name()); };

ESC_SEQ_APPLY_BOLD :          '1' { setText(APPLY_BOLD.name()); };
ESC_SEQ_APPLY_DIM :           '2' { setText(APPLY_DIM.name()); };
ESC_SEQ_APPLY_ITALIC :        '3' { setText(APPLY_ITALIC.name()); };
ESC_SEQ_APPLY_UNDERLINE :     '4' { setText(APPLY_UNDERLINE.name()); };
ESC_SEQ_APPLY_BLINKING :      '5' { setText(APPLY_BLINKING.name()); };
ESC_SEQ_APPLY_INVERSE :       '7' { setText(APPLY_INVERSE.name()); };
ESC_SEQ_APPLY_HIDDEN :        '8' { setText(APPLY_HIDDEN.name()); };
ESC_SEQ_APPLY_STRIKETHROUGH : '9' { setText(APPLY_STRIKETHROUGH.name()); };

ESC_SEQ_FG_256 : '38;5;' -> pushMode(ESC_SEQ_COLOR_256_MODE);
ESC_SEQ_BG_256 : '48;5;' -> pushMode(ESC_SEQ_COLOR_256_MODE);

ESC_SEQ_FG_RGB : '38;2;' -> pushMode(ESC_SEQ_COLOR_RGB_MODE);
ESC_SEQ_BG_RGB : '48;2;' -> pushMode(ESC_SEQ_COLOR_RGB_MODE);

// --------- ESC-Color-256 mode: -----------

mode ESC_SEQ_COLOR_256_MODE;
ESC_SEQ_INTEGER : [0-9]+ -> popMode;

// --------- ESC-Color-256 mode: -----------

mode ESC_SEQ_COLOR_RGB_MODE;
ESC_SEQ_RGB : [0-9]+ ';' [0-9]+ ';' [0-9]+ -> popMode;
