package org.krmdemo.yaml.reconcile.ansi;


import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Arrays.stream;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyleAttr.Family.*;

/**
 * Single attribute of ansi-style
 */
@Slf4j
public class AnsiStyleAttr implements Comparable<AnsiStyleAttr> {

    public final static Comparator<AnsiStyleAttr> COMPARATOR =
        Comparator.comparing(AnsiStyleAttr::family)
            .thenComparing(AnsiStyleAttr::familyName)
            .thenComparing(AnsiStyleAttr::name);

    @Override
    public int compareTo(AnsiStyleAttr that) {
        return COMPARATOR.compare(this, that);
    }

    public enum Operation {
        apply,
        reset
    }

    public enum Family {

        BOLD("bold"),
        DIM("dim"),
        ITALIC("italic"),
        UNDERLINE("underline"),
        BLINKING("blinking"),
        INVERSE("inverse"),
        HIDDEN("hidden"),
        STRIKETHROUGH("strikethrough"),

        FOREGROUND("fg", "fg(%s)"),
        BACKGROUND("bg", "bg(%s)"),

        ALL("!");

        private final String familyName;
        private final String formatString;

        Family(String familyName) {
            this(familyName, null);
        }

        Family(String familyName, String formatString) {
            this.familyName = familyName == null ? name() : familyName;
            this.formatString = formatString;
        }

        public String formatName(String styleName) {
            if (isBlank(styleName)) {
                return familyName;
            } else if (isBlank(formatString)) {
                return styleName;
            } else {
                return format(formatString, styleName);
            }
        }
    }

    private final Operation operation;
    private final Family family;
    private final String name;
    private final int ansiCode;

    private AnsiStyleAttr(Operation operation, Family family, int ansiCode) {
        this(operation, family, family.familyName, ansiCode);
    }

    private AnsiStyleAttr(Operation operation, Family family, String name, int ansiCode) {
        this.operation = operation;
        this.family = family;
        this.name = name;
        this.ansiCode = ansiCode;
    }

    private static AnsiStyleAttr ansiStyleAttr(Operation operation, Family family, String name, int ansiCode) {
        return new AnsiStyleAttr(operation, family, name, ansiCode);
    }

    private static AnsiStyleAttr createAndRegister(Operation operation, Family family, int ansiCode) {
        return createAndRegister(operation, family, family.familyName, ansiCode);
    }

    private static AnsiStyleAttr createAndRegister(Operation operation, Family family, String name, int ansiCode) {
        AnsiStyleAttr styleAttr = ansiStyleAttr(operation, family, name, ansiCode);
        AnsiStyleAttr existing = attrByName.put(styleAttr.name(), styleAttr);
        if (existing != null) {
            log.warn(format("register in 'attrByName' twice - %s over %s", styleAttr, existing));
        }
        if (operation == Operation.reset) {
            AnsiStyleAttr existingReset = resetByFamily.put(family, styleAttr);
            if (existingReset != null) {
                log.warn(format("register in 'resetByFamily' twice - %s over %s", styleAttr, existingReset));
            }
        }
        return styleAttr;
    }

    private final static Map<String, AnsiStyleAttr> attrByName = new TreeMap<>();
    private final static EnumMap<Family, AnsiStyleAttr> resetByFamily = new EnumMap<>(Family.class);

    public static Optional<AnsiStyleAttr> lookupByName(String styleAttrName) {
        AnsiStyleAttr styleAttr = attrByName.get(styleAttrName);
        if (styleAttr == null) {
            log.warn("could not lookup attribute by name '" + styleAttrName + "'");
//            log.warn("available attributes are: \n" + attrByName.entrySet().stream()
//                .map(e -> format("'%s' ---> %s", e.getKey(), e.getValue()))
//                .collect(Collectors.joining(lineSeparator())));
        }
        return Optional.ofNullable(styleAttr);
    }

    public static Optional<AnsiStyleAttr> lookupResetFamily(Family family) {
        AnsiStyleAttr styleAttr = resetByFamily.get(family);
        if (styleAttr == null) {
            log.warn("could not lookup reset-attribute by family '" + family + "'");
        }
        return Optional.ofNullable(styleAttr);
    }

    public Operation operation() {
        return this.operation;
    }

    public String operationPrefix() {
        return operation() == Operation.reset ? "!" : "";
    }

    public Family family() {
        return this.family;
    }

    public String familyName() {
        return this.family.familyName;
    }

    public String name() {
        return operationPrefix() + family().formatName(this.name);
    }

    @Override
    public String toString() {
        return name();
    }

    public Integer ansiCode() {
        return this.ansiCode;
    }

    public String ansiCodeSeq() {
        return valueOf(ansiCode());
    }

    public static AnsiStyleAttr RESET_ALL =
        new AnsiStyleAttr(Operation.reset, Family.ALL, 0);

    private static AnsiStyleAttr resetAttr(Family family, int ansiCode) {
        return createAndRegister(Operation.reset, family, ansiCode);
    }

    public static AnsiStyleAttr RESET_BOLD = resetAttr(BOLD, 22);
    public static AnsiStyleAttr RESET_DIM = resetAttr(DIM, 22);
    public static AnsiStyleAttr RESET_ITALIC = resetAttr(ITALIC, 23);
    public static AnsiStyleAttr RESET_UNDERLINE = resetAttr(UNDERLINE, 24);
    public static AnsiStyleAttr RESET_BLINKING = resetAttr(BLINKING, 24);
    public static AnsiStyleAttr RESET_INVERSE = resetAttr(INVERSE, 27);
    public static AnsiStyleAttr RESET_HIDDEN = resetAttr(HIDDEN, 28);
    public static AnsiStyleAttr RESET_STRIKETHROUGH = resetAttr(STRIKETHROUGH, 29);

    private static AnsiStyleAttr applyAttr(Family family, int ansiCode) {
        return createAndRegister(Operation.apply, family, ansiCode);
    }

    public static AnsiStyleAttr APPLY_BOLD = applyAttr(BOLD, 1);
    public static AnsiStyleAttr APPLY_DIM = applyAttr(DIM, 2);
    public static AnsiStyleAttr APPLY_ITALIC = applyAttr(ITALIC, 3);
    public static AnsiStyleAttr APPLY_UNDERLINE = applyAttr(UNDERLINE, 4);
    public static AnsiStyleAttr APPLY_BLINKING = applyAttr(BLINKING, 5);
    public static AnsiStyleAttr APPLY_INVERSE = applyAttr(INVERSE, 7);
    public static AnsiStyleAttr APPLY_HIDDEN = applyAttr(HIDDEN, 8);
    public static AnsiStyleAttr APPLY_STRIKETHROUGH = applyAttr(STRIKETHROUGH, 9);

    public static AnsiStyleAttr RESET_FG =
        createAndRegister(Operation.reset, FOREGROUND, "", 39);
    public static AnsiStyleAttr RESET_BG =
        createAndRegister(Operation.reset, BACKGROUND, "", 49);

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

        private final String colorName;

        Color(int fgCode, int bgCode, String colorName) {
            this.colorName = colorName;
            this.fg = createAndRegister(Operation.apply, FOREGROUND, colorName, fgCode);
            this.bg = createAndRegister(Operation.apply, BACKGROUND, colorName, bgCode);
        }
    }

    public static AnsiStyleAttr bg(Color color) {
        return color.bg;
    }
    public static AnsiStyleAttr fg(Color color) {
        return color.fg;
    }

    private static final String DELIMITER_ANSI_COLOR = ";";
    private static final String SUB_PREFIX_COLOR_256 = DELIMITER_ANSI_COLOR + 5 + DELIMITER_ANSI_COLOR;
    private static final String SUB_PREFIX_COLOR_RGB = DELIMITER_ANSI_COLOR + 2 + DELIMITER_ANSI_COLOR;

    public static AnsiStyleAttr fg(int color256) {
        String codeStr = byteHex(color256, "The value of foreground 256-color must be in range [0..255], but it was %d");
        return new AnsiStyleAttr(Operation.apply, FOREGROUND, "#" + codeStr, 38) {
            @Override
            public String ansiCodeSeq() {
                return super.ansiCodeSeq() + SUB_PREFIX_COLOR_256 + color256;
            }
        };
    }

    public static AnsiStyleAttr bg(int color256) {
        String codeStr = byteHex(color256, "The value of background 256-color must be in range [0..255], but it was %d");
        return new AnsiStyleAttr(Operation.apply, BACKGROUND, "#" + codeStr, 48) {
            @Override
            public String ansiCodeSeq() {
                return super.ansiCodeSeq() + SUB_PREFIX_COLOR_256 + color256;
            }
        };
    }

    public static AnsiStyleAttr fg(int red, int green, int blue) {
        String redStr = byteHex(red, "Red part of foreground true-color must be in range [0..255], but it was %d");
        String greenStr = byteHex(green, "Green part of foreground true-color must be in range [0..255], but it was %d");
        String blueStr = byteHex(blue, "Blue part of foreground true-color must be in range [0..255], but it was %d");
        String nameHex = format("#%s%s%s", redStr, greenStr, blueStr);
        String colorSeq = String.join(DELIMITER_ANSI_COLOR, valueOf(red), valueOf(green), valueOf(blue));
        return new AnsiStyleAttr(Operation.apply, FOREGROUND, nameHex, 38) {
            @Override
            public String ansiCodeSeq() {
                return super.ansiCodeSeq() + SUB_PREFIX_COLOR_RGB + colorSeq;
            }
        };
    }

    public static AnsiStyleAttr bg(int red, int green, int blue) {
        String redStr = byteHex(red, "Red part of background true-color must be in range [0..255], but it was %d");
        String greenStr = byteHex(green, "Green part of background true-color must be in range [0..255], but it was %d");
        String blueStr = byteHex(blue, "Blue part of background true-color must be in range [0..255], but it was %d");
        String nameHex = format("#%s%s%s", redStr, greenStr, blueStr);
        String colorSeq = String.join(DELIMITER_ANSI_COLOR, valueOf(red), valueOf(green), valueOf(blue));
        return new AnsiStyleAttr(Operation.apply, BACKGROUND, nameHex, 48) {
            @Override
            public String ansiCodeSeq() {
                return super.ansiCodeSeq() + SUB_PREFIX_COLOR_RGB + colorSeq;
            }
        };
    }

    private static String byteHex(int intValue, String fmtErrMsg) {
        if (intValue < 0x00 || intValue > 0xFF) {
            throw new IllegalArgumentException(format(fmtErrMsg, intValue));
        }
        return format("%02X", intValue);
    }

    static {
        stream(Color.values()).forEach(color -> {
            attrByName.put(color.colorName, color.fg);
        });
    }
}
