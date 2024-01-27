package org.krmdemo.yaml.reconcile.ansi;


import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static java.lang.String.format;

/**
 * Single attribute of ansi-style
 */
@Slf4j
public class AnsiStyleAttr {

    private final String name;
    private final Integer ansiCode;
    private final String formatStringErr;

    private AnsiStyleAttr(String name, Integer ansiCode) {
        this.name = name;
        this.ansiCode = ansiCode;
        this.formatStringErr = null;
    }

    private AnsiStyleAttr(String formatStringErr) {
        this.name = null;
        this.ansiCode = null;
        this.formatStringErr = formatStringErr;
    }

    public Optional<String> name() {
        return Optional.ofNullable(name);
    }

    public Optional<Integer> ansiCode() {
        return Optional.ofNullable(ansiCode);
    }

    public String ansiCodeStr() {
        return ansiCode().map(String::valueOf).orElse("");
    }

    public String dump() {
        return name().orElse("??\"" + formatStringErr + "\"");
    }

    public static AnsiStyleAttr unknown(String formatString) {
        return new AnsiStyleAttr(formatString);
    }

    public static AnsiStyleAttr RESET_ALL = new AnsiStyleAttr("!!", 0);

    public static AnsiStyleAttr RESET_BOLD = new AnsiStyleAttr("!bold", 22);
    public static AnsiStyleAttr RESET_DIM = new AnsiStyleAttr("!dim", 22);
    public static AnsiStyleAttr RESET_ITALIC = new AnsiStyleAttr("!italic", 23);
    public static AnsiStyleAttr RESET_UNDERLINE = new AnsiStyleAttr("!underline", 24);
    public static AnsiStyleAttr RESET_BLINKING = new AnsiStyleAttr("!blinking", 24);
    public static AnsiStyleAttr RESET_INVERSE = new AnsiStyleAttr("!inverse", 27);
    public static AnsiStyleAttr RESET_HIDDEN = new AnsiStyleAttr("!hidden", 28);
    public static AnsiStyleAttr RESET_STRIKETHROUGH = new AnsiStyleAttr("!strikethrough", 29);

    public static AnsiStyleAttr RESET_FG = new AnsiStyleAttr("!fg", 39);
    public static AnsiStyleAttr RESET_BG = new AnsiStyleAttr("!bg", 49);

    public static AnsiStyleAttr APPLY_BOLD = new AnsiStyleAttr("bold", 1);
    public static AnsiStyleAttr APPLY_DIM = new AnsiStyleAttr("dim", 2);
    public static AnsiStyleAttr APPLY_ITALIC = new AnsiStyleAttr("italic", 3);
    public static AnsiStyleAttr APPLY_UNDERLINE = new AnsiStyleAttr("underline", 4);
    public static AnsiStyleAttr APPLY_BLINKING = new AnsiStyleAttr("blinking", 5);
    public static AnsiStyleAttr APPLY_INVERSE = new AnsiStyleAttr("inverse", 7);
    public static AnsiStyleAttr APPLY_HIDDEN = new AnsiStyleAttr("hidden", 8);
    public static AnsiStyleAttr APPLY_STRIKETHROUGH = new AnsiStyleAttr("strikethrough", 9);


    public enum Color {

        BLACK(30, 40, "black"),
        RED(31, 41, "red"),
        GREEN(32, 42, "green"),
        YELLOW(33, 43, "yellow"),
        BLUE(34, 44, "blue"),
        MAGENTA(35, 45, "magenta"),
        CYAN(36, 46, "cyan"),
        WHITE(37, 47, "white"),

        BRIGHT_BLACK(90, 100, "^black"),
        BRIGHT_RED(91, 101, "^red"),
        BRIGHT_GREEN(92, 102, "^green"),
        BRIGHT_YELLOW(93, 103, "^yellow"),
        BRIGHT_BLUE(94, 104, "^blue"),
        BRIGHT_MAGENTA(95, 105, "^magenta"),
        BRIGHT_CYAN(96, 106, "^cyan"),
        BRIGHT_WHITE(97, 107, "^white");

        private final AnsiStyleAttr fg;
        private final AnsiStyleAttr bg;

        private String colorName;

        Color(int fgCode, int bgCode, String colorName) {
            this.colorName = colorName;
            this.fg = new AnsiStyleAttr(format("fg(%s)", colorName), fgCode);
            this.bg = new AnsiStyleAttr(format("bg(%s)", colorName), bgCode);
        }
    }

    public static AnsiStyleAttr bg(Color color) {
        return color.bg;
    }
    public static AnsiStyleAttr fg(Color color) {
        return color.fg;
    }

    public static AnsiStyleAttr fg(int color256) {
        String codeStr = intToByte(color256, "fg(#%02X)", "fg(?'#%1$X~%1$d')",
            "The value of foreground 256-color must be in range [0..255], but it was %d");
        if (codeStr.contains("'")) {
            return new AnsiStyleAttr(codeStr);
        } else {
            return new AnsiStyleAttr(codeStr, 38) {
                @Override
                public String ansiCodeStr() {
                    return super.ansiCodeStr() + ";5;" + color256;
                }
            };
        }
    }

    public static AnsiStyleAttr bg(int color256) {
        String codeStr = intToByte(color256, "bg(#%02X)", "bg(?'#%1$X~%1$d')",
            "The value of background 256-color must be in range [0..255], but it was %d");
        if (codeStr.contains("?")) {
            return new AnsiStyleAttr(codeStr);
        } else {
            return new AnsiStyleAttr(codeStr, 48) {
                @Override
                public String ansiCodeStr() {
                    return super.ansiCodeStr() + ";5;" + color256;
                }
            };
        }
    }

    public static AnsiStyleAttr fg(int red, int green, int blue) {
        String redStr = intToByte(red, "%02X", "?'%1$X~%1$d'",
            "Red part of foreground true-color must be in range [0..255], but it was %d");
        String greenStr = intToByte(green, "%02X", "?'%1$X~%1$d'",
            "Green part of foreground true-color must be in range [0..255], but it was %d");
        String blueStr = intToByte(blue, "%02X", "?'%1$X~%1$d'",
            "Blue part of foreground true-color must be in range [0..255], but it was %d");
        String codeStr = format("fg(#%s%s%s)", redStr, greenStr, blueStr);
        if (codeStr.contains("?")) {
            return new AnsiStyleAttr(codeStr);
        } else {
            return new AnsiStyleAttr(codeStr, 38) {
                @Override
                public String ansiCodeStr() {
                    return super.ansiCodeStr() + ";2;" + red + ";" + green + ";" + blue;
                }
            };
        }
    }


    public static AnsiStyleAttr bg(int red, int green, int blue) {
        String redStr = intToByte(red, "%02X", "?'%$1X~#%$1d'",
            "Red part of background true-color must be in range [0..255], but it was %d");
        String greenStr = intToByte(green, "%02X", "?'%$1X~#%$1d'",
            "Green part of background true-color must be in range [0..255], but it was %d");
        String blueStr = intToByte(blue, "%02X", "?'%$1X~#%$1d'",
            "Blue part of background true-color must be in range [0..255], but it was %d");
        String codeStr = format("bg(#%s%s%s)", redStr, greenStr, blueStr);
        if (codeStr.contains("?")) {
            return new AnsiStyleAttr(codeStr);
        } else {
            return new AnsiStyleAttr(codeStr, 48) {
                @Override
                public String ansiCodeStr() {
                    return super.ansiCodeStr() + ";2;" + red + ";" + green + ";" + blue;
                }
            };
        }
    }

    private static String intToByte(int intValue, String fmtOK, String fmtErr, String fmtErrMsg) {
        if (intValue < 0x00 || intValue > 0xFF) {
            log.error(format(fmtErrMsg, intValue));
            return format(fmtErr, intValue);
        } else {
            return format(fmtOK, intValue);
        }
    }
}
