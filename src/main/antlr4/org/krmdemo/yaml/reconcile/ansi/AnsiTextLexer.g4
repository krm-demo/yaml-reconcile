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
    // ----------------------------------------------
}

@lexer::members{
    // ----------------------------------------------
    //    manually injected members of lexer:
    // ----------------------------------------------
    private final static Logger log = org.slf4j.LoggerFactory.getLogger(AnsiTextLexer.class);
    static {
        if (log.isDebugEnabled()) {
            log.debug("in static initializing:");
            Stream.of(
                RESET_ALL,
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

ESC_SEQ_RESET_FG_COLOR : '39' { setText(RESET_STRIKETHROUGH.name()); };
ESC_SEQ_RESET_BG_COLOR : '49' { setText(RESET_STRIKETHROUGH.name()); };

ESC_SEQ_APPLY_BOLD :          '1' { setText(APPLY_BOLD.name()); };
ESC_SEQ_APPLY_DIM :           '2' { setText(APPLY_DIM.name()); };
ESC_SEQ_APPLY_ITALIC :        '3' { setText(APPLY_ITALIC.name()); };
ESC_SEQ_APPLY_UNDERLINE :     '4' { setText(APPLY_UNDERLINE.name()); };
ESC_SEQ_APPLY_BLINKING :      '5' { setText(APPLY_BLINKING.name()); };
ESC_SEQ_APPLY_INVERSE :       '7' { setText(APPLY_INVERSE.name()); };
ESC_SEQ_APPLY_HIDDEN :        '8' { setText(APPLY_HIDDEN.name()); };
ESC_SEQ_APPLY_STRIKETHROUGH : '9' { setText(APPLY_STRIKETHROUGH.name()); };

ESC_SEQ_FG_256 : '38;5;';
ESC_SEQ_BG_256 : '48;5;';

ESC_SEQ_FG_RGB : '38;2;';
ESC_SEQ_BG_RGB : '48;2;';

ESC_SEQ_INTEGER : [0-9]+;
