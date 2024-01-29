package org.krmdemo.yaml.reconcile.ansi;


import lombok.extern.slf4j.Slf4j;

import static java.lang.String.format;
import static java.lang.String.valueOf;

/**
 * Single attribute of ansi-style
 */
@Slf4j
public class AnsiStyleAttr {

    public enum Family {

        SINGLE,

        FOREGROUND("fg(%s)"),
        BACKGROUND("bg(%s)"),

        ALL;

        private final String formatString;

        Family() {
            this("%s");
        }

        Family(String formatString) {
            this.formatString = formatString;
        }

        public String formatName(String styleName) {
            return format(formatString, styleName);
        }
    }

    public enum Operation {
        apply,
        reset
    }

    private final Operation operation;
    private final Family family;
    private final String name;
    private final int ansiCode;

    private AnsiStyleAttr(Operation operation, Family family, String name, int ansiCode) {
        this.operation = operation;
        this.family = family;
        this.name = name;
        this.ansiCode = ansiCode;
    }

    public Operation operation() {
        return this.operation;
    }

    public Family family() {
        return this.family;
    }

    public String name() {
        return this.name;
    }

    public Integer ansiCode() {
        return this.ansiCode;
    }

    public String ansiCodeSeq() {
        return valueOf(ansiCode());
    }

    public String dump() {
        return family().formatName(name());
    }

    public static AnsiStyleAttr RESET_ALL =
        new AnsiStyleAttr(Operation.reset, Family.ALL, "!!", 0);

    private static AnsiStyleAttr resetAttr(String name, int ansiCode) {
        return new AnsiStyleAttr(Operation.reset, Family.SINGLE, name, ansiCode);
    }

    public static AnsiStyleAttr RESET_BOLD = resetAttr("!bold", 22);
    public static AnsiStyleAttr RESET_DIM = resetAttr("!dim", 22);
    public static AnsiStyleAttr RESET_ITALIC = resetAttr("!italic", 23);
    public static AnsiStyleAttr RESET_UNDERLINE = resetAttr("!underline", 24);
    public static AnsiStyleAttr RESET_BLINKING = resetAttr("!blinking", 24);
    public static AnsiStyleAttr RESET_INVERSE = resetAttr("!inverse", 27);
    public static AnsiStyleAttr RESET_HIDDEN = resetAttr("!hidden", 28);
    public static AnsiStyleAttr RESET_STRIKETHROUGH = resetAttr("!strikethrough", 29);

    private static AnsiStyleAttr applyAttr(String name, int ansiCode) {
        return new AnsiStyleAttr(Operation.apply, Family.SINGLE, name, ansiCode);
    }

    public static AnsiStyleAttr APPLY_BOLD = applyAttr("bold", 1);
    public static AnsiStyleAttr APPLY_DIM = applyAttr("dim", 2);
    public static AnsiStyleAttr APPLY_ITALIC = applyAttr("italic", 3);
    public static AnsiStyleAttr APPLY_UNDERLINE = applyAttr("underline", 4);
    public static AnsiStyleAttr APPLY_BLINKING = applyAttr("blinking", 5);
    public static AnsiStyleAttr APPLY_INVERSE = applyAttr("inverse", 7);
    public static AnsiStyleAttr APPLY_HIDDEN = applyAttr("hidden", 8);
    public static AnsiStyleAttr APPLY_STRIKETHROUGH = applyAttr("strikethrough", 9);

    public static AnsiStyleAttr RESET_FG =
        new AnsiStyleAttr(Operation.reset, Family.FOREGROUND, "!fg", 39);
    public static AnsiStyleAttr RESET_BG =
        new AnsiStyleAttr(Operation.reset, Family.BACKGROUND, "!bg", 49);

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

        Color(int fgCode, int bgCode, String colorName) {
            this.fg = new AnsiStyleAttr(Operation.apply, Family.FOREGROUND, colorName, fgCode);
            this.bg = new AnsiStyleAttr(Operation.apply, Family.BACKGROUND, colorName, bgCode);
        }
    }

    public static AnsiStyleAttr bg(Color color) {
        return color.bg;
    }
    public static AnsiStyleAttr fg(Color color) {
        return color.fg;
    }

    public static AnsiStyleAttr fg(int color256) {
        String codeStr = byteHex(color256, "The value of foreground 256-color must be in range [0..255], but it was %d");
        return new AnsiStyleAttr(Operation.apply, Family.FOREGROUND, "#" + codeStr, 38) {
            @Override
            public String ansiCodeSeq() {
                return super.ansiCodeSeq() + ";5;" + color256;
            }
        };
    }

    public static AnsiStyleAttr bg(int color256) {
        String codeStr = byteHex(color256, "The value of background 256-color must be in range [0..255], but it was %d");
        return new AnsiStyleAttr(Operation.apply, Family.BACKGROUND, "#" + codeStr, 48) {
            @Override
            public String ansiCodeSeq() {
                return super.ansiCodeSeq() + ";5;" + color256;
            }
        };
    }

    public static AnsiStyleAttr fg(int red, int green, int blue) {
        String redStr = byteHex(red, "Red part of foreground true-color must be in range [0..255], but it was %d");
        String greenStr = byteHex(green, "Green part of foreground true-color must be in range [0..255], but it was %d");
        String blueStr = byteHex(blue, "Blue part of foreground true-color must be in range [0..255], but it was %d");
        String nameHex = format("#%s%s%s", redStr, greenStr, blueStr);
        String colorSeq = String.join(";", "2", valueOf(red), valueOf(green), valueOf(blue));
        return new AnsiStyleAttr(Operation.apply, Family.FOREGROUND, nameHex, 38) {
            @Override
            public String ansiCodeSeq() {
                return super.ansiCodeSeq() + ";" + colorSeq;
            }
        };
    }

    public static AnsiStyleAttr bg(int red, int green, int blue) {
        String redStr = byteHex(red, "Red part of background true-color must be in range [0..255], but it was %d");
        String greenStr = byteHex(green, "Green part of background true-color must be in range [0..255], but it was %d");
        String blueStr = byteHex(blue, "Blue part of background true-color must be in range [0..255], but it was %d");
        String nameHex = format("#%s%s%s", redStr, greenStr, blueStr);
        String colorSeq = String.join(";", "2", valueOf(red), valueOf(green), valueOf(blue));
        return new AnsiStyleAttr(Operation.apply, Family.BACKGROUND, nameHex, 48) {
            @Override
            public String ansiCodeSeq() {
                return super.ansiCodeSeq() + ";" + colorSeq;
            }
        };
    }

    private static String byteHex(int intValue, String fmtErrMsg) {
        if (intValue < 0x00 || intValue > 0xFF) {
            throw new IllegalArgumentException(format(fmtErrMsg, intValue));
        }
        return format("%02X", intValue);
    }
}
