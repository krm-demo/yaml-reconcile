package org.krmdemo.yaml.reconcile.ansi;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.krmdemo.yaml.reconcile.ansi.AnsiSize.sum;
import static org.krmdemo.yaml.reconcile.ansi.Layout.emptyLayout;
import static org.krmdemo.yaml.reconcile.ansi.Layout.horizontalBar;

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

        @Override public boolean isAscii() { return this.ascii; }

        @Override public Optional<String>  top() { return Optional.of(boxLines[0]); }
        @Override public Optional<String>  head() { return Optional.of(boxLines[1]); }
        @Override public Optional<String>  headRow() { return Optional.of(boxLines[2]); }
        @Override public Optional<String>  mid() { return Optional.of(boxLines[3]); }
        @Override public Optional<String>  row() { return Optional.of(boxLines[4]); }
        @Override public Optional<String>  footRow() { return Optional.of(boxLines[5]); }
        @Override public Optional<String>  foot() { return Optional.of(boxLines[6]); }
        @Override public Optional<String>  bottom() { return Optional.of(boxLines[7]); }

        private class TopBar extends Layout.Blank {
            private TopBar(int width) {
                super(1, width);
                this.paddingChar = top().map(str -> str.charAt(1)).orElseThrow();
            }
            @Override public Layout leftFrame() { return topLeft(); }
            @Override public Layout rightFrame() { return topRight(); }
            @Override public String dump() { return format("TopBar(w:%d'%s')", width(), paddingChar()); }
        }

        private class MiddleBar extends Layout.Blank {
            private MiddleBar(int width) {
                super(1, width);
                this.paddingChar = row().map(str -> str.charAt(1)).orElseThrow();
            }
            @Override public Layout leftFrame() { return leftLink(); }
            @Override public Layout rightFrame() { return rightLink(); }
            @Override public String dump() { return format("MiddleBar(w:%d'%s')", width(), paddingChar()); }
        }

        private class BottomBar extends Layout.Blank {
            private BottomBar(int width) {
                super(1, width);
                this.paddingChar = bottom().map(str -> str.charAt(1)).orElseThrow();
            }
            @Override public Layout leftFrame() { return bottomLeft(); }
            @Override public Layout rightFrame() { return bottomRight(); }
            @Override public String dump() { return format("BottomBar(w:%d'%s')", width(), paddingChar()); }
        }

        @Override public Layout topBar(int width) { return new TopBar(width); }
        @Override public Layout middleBar(int width) { return new MiddleBar(width); }
        @Override public Layout bottomBar(int width) { return new BottomBar(width); }

        private class LeftBar extends Layout.Blank {
            private LeftBar(int height) {
                super(height, 1);
                this.paddingChar = mid().map(str -> str.charAt(0)).orElseThrow();
            }
            @Override public Layout topFrame() { return topLeft(); }
            @Override public Layout bottomFrame() { return bottomLeft(); }
            @Override public String dump() { return format("LeftBar(h:%d'%s')", width(), paddingChar()); }
        }

        private class CenterBar extends Layout.Blank {
            private CenterBar(int height) {
                super(height, 1);
                this.paddingChar = mid().map(str -> str.charAt(2)).orElseThrow();
            }
            @Override public Layout topFrame() { return topLink(); }
            @Override public Layout bottomFrame() { return bottomLink(); }
            @Override public String dump() { return format("CenterBar(h:%d'%s')", width(), paddingChar()); }
        }

        private class RightBar extends Layout.Blank {
            private RightBar(int height) {
                super(height, 1);
                this.paddingChar = mid().map(str -> str.charAt(3)).orElseThrow();
            }
            @Override public Layout topFrame() { return topRight(); }
            @Override public Layout bottomFrame() { return bottomRight(); }
            @Override public String dump() { return format("RightBar(h:%d'%s')", width(), paddingChar()); }
        }

        @Override public Layout leftBar(int height) { return new LeftBar(height); }
        @Override public Layout centerBar(int height) { return new CenterBar(height); }
        @Override public Layout rightBar(int height) { return new RightBar(height); }

        private class TopLeft extends Layout.Blank {
            private TopLeft() {
                super(1, 1);
                this.paddingChar = top().map(str -> str.charAt(0)).orElseThrow();
            }
            @Override
            public String dump() {
                return format("TopLeft('%s')", paddingChar());
            }
        }

        private class TopRight extends Layout.Blank {
            private TopRight() {
                super(1, 1);
                this.paddingChar = top().map(str -> str.charAt(3)).orElseThrow();
            }
            @Override
            public String dump() {
                return format("TopRight('%s')", paddingChar());
            }
        }

        private class BottomLeft extends Layout.Blank {
            private BottomLeft() {
                super(1, 1);
                this.paddingChar = bottom().map(str -> str.charAt(0)).orElseThrow();
            }
            @Override
            public String dump() {
                return format("BottomLeft('%s')", paddingChar());
            }
        }

        private class BottomRight extends Layout.Blank {
            private BottomRight() {
                super(1, 1);
                this.paddingChar = bottom().map(str -> str.charAt(3)).orElseThrow();
            }
            @Override
            public String dump() {
                return format("BottomRight('%s')", paddingChar());
            }
        }

        @Override public Layout topLeft() { return new TopLeft(); }
        @Override public Layout topRight() { return new TopRight(); }
        @Override public Layout bottomLeft() { return new BottomLeft(); }
        @Override public Layout bottomRight() { return new BottomRight(); }

        private class TopLink extends Layout.Blank {
            private TopLink() {
                super(1, 1);
                this.paddingChar = top().map(str -> str.charAt(2)).orElseThrow();
            }
            @Override
            public String dump() {
                return format("TopLink('%s')", paddingChar());
            }
        }

        private class BottomLink extends Layout.Blank {
            private BottomLink() {
                super(1, 1);
                this.paddingChar = bottom().map(str -> str.charAt(2)).orElseThrow();
            }
            @Override
            public String dump() {
                return format("BottomLink('%s')", paddingChar());
            }
        }

        private class LeftLink extends Layout.Blank {
            private LeftLink() {
                super(1, 1);
                this.paddingChar = row().map(str -> str.charAt(0)).orElseThrow();
            }
            @Override
            public String dump() {
                return format("LeftLink('%s')", paddingChar());
            }
        }

        private class RightLink extends Layout.Blank {
            private RightLink() {
                super(1, 1);
                this.paddingChar = row().map(str -> str.charAt(3)).orElseThrow();
            }
            @Override
            public String dump() {
                return format("RightLink('%s')", paddingChar());
            }
        }

        @Override public Layout topLink() { return new TopLink(); }
        @Override public Layout bottomLink() { return new BottomLink(); }
        @Override public Layout leftLink() { return new LeftLink(); }
        @Override public Layout rightLink() { return new RightLink(); }
    }

    default Optional<String> top() { return Optional.empty(); }
    default Optional<String> head() { return Optional.empty(); }
    default Optional<String> headRow() { return Optional.empty(); }
    default Optional<String> mid() { return Optional.empty(); }
    default Optional<String> row() { return Optional.empty(); }
    default Optional<String> footRow() { return Optional.empty(); }
    default Optional<String> foot() { return Optional.empty(); }
    default Optional<String> bottom() { return Optional.empty(); }

    default Layout topBar(int width) { return emptyLayout(); }
    default Layout middleBar(int width) { return emptyLayout(); }
    default Layout bottomBar(int width) { return emptyLayout(); }

    default Layout leftBar(int height) { return emptyLayout(); }
    default Layout centerBar(int height) { return emptyLayout(); }
    default Layout rightBar(int height) { return emptyLayout(); }

    default Layout topLeft() { return emptyLayout(); }
    default Layout topRight() { return emptyLayout(); }
    default Layout bottomLeft() { return emptyLayout(); }
    default Layout bottomRight() { return emptyLayout(); }

    default Layout topLink() { return emptyLayout(); }
    default Layout bottomLink() { return emptyLayout(); }
    default Layout leftLink() { return emptyLayout(); }
    default Layout rightLink() { return emptyLayout(); }
}
