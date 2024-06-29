package org.krmdemo.yaml.reconcile.ansi;

import org.krmdemo.yaml.reconcile.ansi.LayoutBuilder.BuildContext;

import java.util.*;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.krmdemo.yaml.reconcile.ansi.Layout.emptyLayout;
import static org.krmdemo.yaml.reconcile.ansi.Layout.horizontalBar;
import static org.krmdemo.yaml.reconcile.ansi.Layout.symbol;
import static org.krmdemo.yaml.reconcile.ansi.Layout.verticalBar;

/**
 * An interface that represents the structure of border over single, multiple and table-like
 * collections of {@link Layout}. Predefined enumeration implements {@link AnsiBorder} and
 * the default implementation {@link #NONE} represents the absence of any borders (inner and outer).
 * <p/>
 * The idea and the list of predefined implementations are taken from python-library
 * <a href="https://rich.readthedocs.io/en/stable/index.html">Rich</a>.
 * <p/>
 *
 * @see <a href="https://rich.readthedocs.io/en/stable/tables.html#">Rich: Tables</a> for a description
 * @see <a href="https://github.com/Textualize/rich/blob/master/rich/box.py">...rich/box.py</a> for the source code
 * @see <a href="https://github.com/Textualize/rich/blob/master/examples/table.py">...rich/examples/table.py</a> for a sample usage
 */
public interface AnsiBorder {

    /**
     * Absence of any inner and outer borders
     */
    AnsiBorder NONE = new AnsiBorder(){};

    default int borderWidth() { return 0; }
    default int borderHeight() { return 0; }

    default boolean isAscii() { return true; }

    /**
     * Predefined implementations of {@link AnsiBorder} interface.
     */
    enum Kind implements AnsiBorder {

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

        /**
         * Default to non-ascii box.
         *
         * @param boxLines characters making up box.
         */
        Kind(String[] boxLines) {
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
        Kind(String[] boxLines, boolean ascii) {
            if (boxLines.length != 8) {
                throw new IllegalArgumentException("there must be exactly 8 box-lines");
            }
            this.boxLines = boxLines;
            this.ascii = ascii;
        }

        private final String[] boxLines;
        private final boolean ascii;

        @Override public int borderWidth() { return 1; }
        @Override public int borderHeight() { return 1; }

        @Override public boolean isAscii() { return this.ascii; }

        @Override public Optional<String>  top() { return Optional.of(boxLines[0]); }
        @Override public Optional<String>  head() { return Optional.of(boxLines[1]); }
        @Override public Optional<String>  headRow() { return Optional.of(boxLines[2]); }
        @Override public Optional<String>  mid() { return Optional.of(boxLines[3]); }
        @Override public Optional<String>  row() { return Optional.of(boxLines[4]); }
        @Override public Optional<String>  footRow() { return Optional.of(boxLines[5]); }
        @Override public Optional<String>  foot() { return Optional.of(boxLines[6]); }
        @Override public Optional<String>  bottom() { return Optional.of(boxLines[7]); }

        private Layout hb(BuildContext ctx, String itemName, int width,
                          Supplier<Optional<String>> boxLine, int charAt) {
            char ch = boxLine.get().map(str -> str.charAt(charAt)).orElseThrow();
            return horizontalBar(ctx, width, ch, format("%s<%s>", itemName, name()));
        }

        private Layout vb(BuildContext ctx, String itemName, int height,
                          Supplier<Optional<String>> boxLine, int charAt) {
            char ch = boxLine.get().map(str -> str.charAt(charAt)).orElseThrow();
            return verticalBar(ctx, height, ch, format("%s<%s>", itemName, name()));
        }

        private Layout smb(BuildContext ctx, String itemName,
                           Supplier<Optional<String>> boxLine, int charAt) {
            char ch = boxLine.get().map(str -> str.charAt(charAt)).orElseThrow();
            return symbol(ctx, ch, format("%s<%s>", itemName, name()));
        }

        @Override
        public Layout topFrameBar(BuildContext ctx, int width) {
            return hb(ctx, "top-frame-bar", width, this::top, 1);
        }

        @Override
        public Layout horzGridBar(BuildContext ctx, int width) {
            return hb(ctx, "horz-grid-bar", width, this::row, 1);
        }

        @Override
        public Layout bottomFrameBar(BuildContext ctx, int width) {
            return hb(ctx, "bottom-frame-bar", width, this::bottom, 1);
        }

        @Override
        public Layout leftFrameBar(BuildContext ctx, int height) {
            return vb(ctx, "left-frame-bar", height, this::mid, 0);
        }

        @Override
        public Layout vertGridBar(BuildContext ctx, int height) {
            return vb(ctx, "vert-grid-bar", height, this::mid, 2);
        }

        @Override
        public Layout rightFrameBar(BuildContext ctx, int height) {
            return vb(ctx, "right-frame-bar", height, this::mid, 3);
        }

        @Override public Layout topLeftCorner(BuildContext ctx) {
            return smb(ctx, "top-left-corner", this::top, 0);
        }

        @Override public Layout topRightCorner(BuildContext ctx) {
            return smb(ctx, "top-right-corner", this::top, 3);
        }

        @Override public Layout bottomLeftCorner(BuildContext ctx) {
            return smb(ctx, "bottom-left-corner", this::bottom, 0);
        }

        @Override public Layout bottomRightCorner(BuildContext ctx) {
            return smb(ctx, "bottom-right-corner", this::bottom, 3);
        }

        @Override public Layout topFrameLink(BuildContext ctx) {
            return smb(ctx, "top-frame-link", this::top, 2);
        }

        @Override public Layout bottomFrameLink(BuildContext ctx) {
            return smb(ctx, "bottom-frame-link", this::bottom, 2);
        }

        @Override public Layout leftFrameLink(BuildContext ctx) {
            return smb(ctx, "left-frame-link", this::row, 0);
        }

        @Override public Layout rightFrameLink(BuildContext ctx) {
            return smb(ctx, "right-frame-link", this::row, 3);
        }
    }

    default Optional<String> top() { return Optional.empty(); }
    default Optional<String> head() { return Optional.empty(); }
    default Optional<String> headRow() { return Optional.empty(); }
    default Optional<String> mid() { return Optional.empty(); }
    default Optional<String> row() { return Optional.empty(); }
    default Optional<String> footRow() { return Optional.empty(); }
    default Optional<String> foot() { return Optional.empty(); }
    default Optional<String> bottom() { return Optional.empty(); }

    default Layout topFrameBar(BuildContext ctx, int width) { return emptyLayout(); }
    default Layout horzGridBar(BuildContext ctx, int width) { return emptyLayout(); }
    default Layout bottomFrameBar(BuildContext ctx, int width) { return emptyLayout(); }

    default Layout leftFrameBar(BuildContext ctx, int height) { return emptyLayout(); }
    default Layout vertGridBar(BuildContext ctx, int height) { return emptyLayout(); }
    default Layout rightFrameBar(BuildContext ctx, int height) { return emptyLayout(); }

    default Layout topLeftCorner(BuildContext ctx) { return emptyLayout(); }
    default Layout topRightCorner(BuildContext ctx) { return emptyLayout(); }
    default Layout bottomLeftCorner(BuildContext ctx) { return emptyLayout(); }
    default Layout bottomRightCorner(BuildContext ctx) { return emptyLayout(); }

    default Layout topFrameLink(BuildContext ctx) { return emptyLayout(); }
    default Layout bottomFrameLink(BuildContext ctx) { return emptyLayout(); }
    default Layout leftFrameLink(BuildContext ctx) { return emptyLayout(); }
    default Layout rightFrameLink(BuildContext ctx) { return emptyLayout(); }
}
