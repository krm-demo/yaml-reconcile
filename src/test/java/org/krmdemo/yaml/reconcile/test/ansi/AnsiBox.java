package org.krmdemo.yaml.reconcile.test.ansi;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Supplier;

/**
 * Wrap the text to ansi-characters to output the rectangular block of specified border and background.
 * The idea of implementation is taken from python-library <a href="https://rich.readthedocs.io/en/stable/index.html">"Rich"</a>.
 * <p/>
 * Implemented in a builder-pattern way, where content becomes unmodifiable after ...
 * <p/>
 * TODO: parametrize {@link #MAX_HEIGHT} and {@link #MAX_WIDTH} somehow
 *
 * @see <a href="https://rich.readthedocs.io/en/stable/tables.html#">Rich: Tables</a> for a description
 * @see <a href="https://github.com/Textualize/rich/blob/master/rich/box.py">...rich/box.py</a> for the source code
 * @see <a href="https://github.com/Textualize/rich/blob/master/examples/table.py">...rich/examples/table.py</a> for a sample usage
 */
public class AnsiBox {

    public interface Style {

        default boolean isAscii() { return true; }

        default Optional<String> top() { return Optional.empty(); }
        default Optional<String> head() { return Optional.empty(); }
        default Optional<String> headRow() { return Optional.empty(); }
        default Optional<String> mid() { return Optional.empty(); }
        default Optional<String> row() { return Optional.empty(); }
        default Optional<String> footRow() { return Optional.empty(); }
        default Optional<String> foot() { return Optional.empty(); }
        default Optional<String> bottom() { return Optional.empty(); }

        default String left(Supplier<Optional<String>> borderHorizontalLine) {
            return borderHorizontalLine.get()
                .map(str -> String.valueOf(str.charAt(0)))
                .orElse("");
        }

        default String pad(Supplier<Optional<String>> borderHorizontalLine) {
            return borderHorizontalLine.get()
                .map(str -> String.valueOf(str.charAt(1)))
                .orElse("");
        }

        default String connect(Supplier<Optional<String>> borderHorizontalLine) {
            return borderHorizontalLine.get()
                .map(str -> String.valueOf(str.charAt(2)))
                .orElse("");
        }

        default String right(Supplier<Optional<String>> borderHorizontalLine) {
            return borderHorizontalLine.get()
                .map(str -> String.valueOf(str.charAt(3)))
                .orElse("");
        }

        default String borderTop(int width) {
            return left(this::top) + StringUtils.repeat(pad(this::top), width) + right(this::top);
        }

        default String borderBottom(int width) {
            return left(this::top) + StringUtils.repeat(pad(this::top), width) + right(this::top);
        }
    }

    public enum StyleKind implements Style {
        ASCII (new String[] {
            "+--+",
            "| ||",
            "|-+|",
            "| ||",
            "|-+|",
            "|-+|",
            "| ||",
            "+--+",
        }, true),

        ASCII2 (new String[] {
            "+-++",
            "| ||",
            "+-++",
            "| ||",
            "+-++",
            "+-++",
            "| ||",
            "+-++",
        }, true),

        ASCII_DOUBLE_HEAD (new String[] {
            "+-++",
            "| ||",
            "+=++",
            "| ||",
            "+-++",
            "+-++",
            "| ||",
            "+-++",
        }, true),

        SQUARE (new String[] {
            "┌─┬┐",
            "│ ││",
            "├─┼┤",
            "│ ││",
            "├─┼┤",
            "├─┼┤",
            "│ ││",
            "└─┴┘",
        }),

        SQUARE_DOUBLE_HEAD (new String[] {
            "┌─┬┐",
            "│ ││",
            "╞═╪╡",
            "│ ││",
            "├─┼┤",
            "├─┼┤",
            "│ ││",
            "└─┴┘",
        }),

        MINIMAL (new String[] {
            "  ╷ ",
            "  │ ",
            "╶─┼╴",
            "  │ ",
            "╶─┼╴",
            "╶─┼╴",
            "  │ ",
            "  ╵ ",
        }),

        MINIMAL_HEAVY_HEAD (new String[] {
            "  ╷ ",
            "  │ ",
            "╺━┿╸",
            "  │ ",
            "╶─┼╴",
            "╶─┼╴",
            "  │ ",
            "  ╵ ",
        }),

        MINIMAL_DOUBLE_HEAD (new String[] {
            "  ╷ ",
            "  │ ",
            " ═╪ ",
            "  │ ",
            " ─┼ ",
            " ─┼ ",
            "  │ ",
            "  ╵ ",
        }),

        SIMPLE (new String[] {
            "    ",
            "    ",
            " ── ",
            "    ",
            "    ",
            " ── ",
            "    ",
            "    ",
        }),

        SIMPLE_HEAD (new String[] {
            "    ",
            "    ",
            " ── ",
            "    ",
            "    ",
            "    ",
            "    ",
            "    ",
        }),

        SIMPLE_HEAVY (new String[] {
            "    ",
            "    ",
            " ━━ ",
            "    ",
            "    ",
            " ━━ ",
            "    ",
            "    ",
        }),

        HORIZONTALS (new String[] {
            " ── ",
            "    ",
            " ── ",
            "    ",
            " ── ",
            " ── ",
            "    ",
            " ── ",
        }),

        ROUNDED (new String[] {
            "╭─┬╮",
            "│ ││",
            "├─┼┤",
            "│ ││",
            "├─┼┤",
            "├─┼┤",
            "│ ││",
            "╰─┴╯",
        }),

        HEAVY (new String[] {
            "┏━┳┓",
            "┃ ┃┃",
            "┣━╋┫",
            "┃ ┃┃",
            "┣━╋┫",
            "┣━╋┫",
            "┃ ┃┃",
            "┗━┻┛",
        }),

        HEAVY_EDGE (new String[] {
            "┏━┯┓",
            "┃ │┃",
            "┠─┼┨",
            "┃ │┃",
            "┠─┼┨",
            "┠─┼┨",
            "┃ │┃",
            "┗━┷┛",
        }),

        HEAVY_HEAD (new String[] {
            "┏━┳┓",
            "┃ ┃┃",
            "┡━╇┩",
            "│ ││",
            "├─┼┤",
            "├─┼┤",
            "│ ││",
            "└─┴┘",
        }),

        DOUBLE (new String[] {
            "╔═╦╗",
            "║ ║║",
            "╠═╬╣",
            "║ ║║",
            "╠═╬╣",
            "╠═╬╣",
            "║ ║║",
            "╚═╩╝",
        }),

        DOUBLE_EDGE (new String[] {
            "╔═╤╗",
            "║ │║",
            "╟─┼╢",
            "║ │║",
            "╟─┼╢",
            "╟─┼╢",
            "║ │║",
            "╚═╧╝",
        }),

        MARKDOWN (new String[] {
            "    ",
            "| ||",
            "|-||",
            "| ||",
            "|-||",
            "|-||",
            "| ||",
            "    ",
        }, true);

        String[] THICK_INNER = new String[] {
            "▄▄▄▄",  // "▁▁▁▁"
            "█ ┃█",  // "▌ ┃▐
            "█━╋█",
            "█ ┃█",
            "█━╋█",
            "█━╋█",
            "█ ┃█",  // "▌ ┃▐"
            "▀▀▀▀",  // "▔▔▔▔
        };

        String[] THICK_OUTER = new String[] {
            "▄▀▀▄",  // "█▀▀█"  // "▛▀▀▜"  // "▞▔▔▚"  // "▞▀▀▚"
            "█  █",  // "█  █"  // "▌  ▐"
            "█━┳█",  //  ....
            "█ ┃█",  //  ....
            "█━╋█",  //  ....
            "█━┻█",  //  ....
            "█  █",  //  ....
            "▀▄▄▀",  // "█▄▄█"
        };

        @Override
        public boolean isAscii() { return this.ascii; }

        public Optional<String>  top() { return Optional.of(boxLines[0]); }
        public Optional<String>  head() { return Optional.of(boxLines[1]); }
        public Optional<String>  headRow() { return Optional.of(boxLines[2]); }
        public Optional<String>  mid() { return Optional.of(boxLines[3]); }
        public Optional<String>  row() { return Optional.of(boxLines[4]); }
        public Optional<String>  footRow() { return Optional.of(boxLines[5]); }
        public Optional<String>  foot() { return Optional.of(boxLines[6]); }
        public Optional<String>  bottom() { return Optional.of(boxLines[7]); }

        /**
         * Default to non-ascii box.
         *
         * @param boxLines characters making up box.
         */
        StyleKind(String[] boxLines) {
            this(boxLines, false);
        }

        /**
         * Defines characters to render boxes:<pre>
         *   ┌─┬┐ top
         *   │ ││ head
         *   ├─┼┤ head_row
         *   │ ││ mid
         *   ├─┼┤ row
         *   ├─┼┤ foot_row
         *   │ ││ foot
         *   └─┴┘ bottom
         * </pre>
         * @param boxLines characters making up box.
         * @param ascii <code>true</code> if this box uses ascii characters only.
         */
        StyleKind(String[] boxLines, boolean ascii) {
            if (boxLines.length != 8) {
                throw new IllegalArgumentException("there must be exactly 8 box-lines");
            }
            this.boxLines = boxLines;
            this.ascii = ascii;
        }

        private final String[] boxLines;
        private final boolean ascii;
    }

    public enum Horizontal {
        LEFT,

        CENTER,

        RIGHT
    };

    public enum Vertical {
        TOP,
        MIDDLE,
        BOTTOM
    }

    public static final Style EMPTY_BORDER = new Style(){};

    public static final int MAX_HEIGHT = 10_000;

    public static final int MAX_WIDTH = 256;

    public static final int DEFAULT_WIDTH = 24;

    private Horizontal horizontal;

    private Vertical vertical;
}
