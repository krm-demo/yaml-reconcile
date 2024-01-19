package org.krmdemo.yaml.reconcile.test.ansi;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

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
public enum AnsiBox {

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


    public static final int MAX_HEIGHT = 10_000;

    public static final int MAX_WIDTH = 256;

    public static final int DEFAULT_WIDTH = 24;

    public String top() { return boxLines[0]; }
    public String head() { return boxLines[1]; }
    public String headRow() { return boxLines[2]; }
    public String mid() { return boxLines[3]; }
    public String row() { return boxLines[3]; }
    public String footRow() { return boxLines[4]; }
    public String foot() { return boxLines[5]; }
    public String bottom() { return boxLines[6]; }

    private final String[] boxLines;
    private final boolean ascii;

    /**
     * Default to non-ascii box.
     *
     * @param boxLines characters making up box.
     */
    AnsiBox(String[] boxLines) {
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
    AnsiBox(String[] boxLines, boolean ascii) {
        if (boxLines.length != 8) {
            throw new IllegalArgumentException("there must be exactly 8 box-lines");
    }
        this.boxLines = boxLines;
        this.ascii = ascii;
    }

    public Builder builder() {
        return new Builder(this);
    }

    public static class RenderableBox {

        private final AnsiBox ansiBox;

        private final int width;

        private boolean skipTopBorder;
        private boolean skipLeftBorder;
        private boolean skipRightBorder;
        private boolean skipBottomBorder;

        private List<String> contentLines = new ArrayList<>();

        private List<String> columnHeaders = new ArrayList();

        private RenderableBox(AnsiBox ansiBox, int width) {
            this.ansiBox = ansiBox;
            this.width = width;
        }

        public String render() {
            StringBuilder sb = new StringBuilder();
            if (!skipTopBorder) {
                if (!skipLeftBorder) {
                    sb.append(ansiBox.top().charAt(0));
                }
                sb.append(StringUtils.repeat(ansiBox.top().charAt(1), width));
            }
            return sb.toString();
        }
    }

    public static class Builder {

        private final AnsiBox ansiBox;

        private Integer width = null;

        private boolean skipTopBorder;
        private boolean skipLeftBorder;
        private boolean skipRightBorder;
        private boolean skipBottomBorder;

        private List<String> contentLines = new ArrayList<>();

        private List<String> columnHeaders = new ArrayList();

        private Builder(AnsiBox ansiBox) {
            this.ansiBox = ansiBox;
        }

        public RenderableBox build() {
            RenderableBox box = new RenderableBox(ansiBox, effectiveWidth());
            box.contentLines = this.contentLines;
            box.columnHeaders = this.columnHeaders;
            return box;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Optional<Integer> getWidth() {
            return Optional.ofNullable(width);
        }

        public int effectiveWidth() {
            return getWidth().orElse(contentLines.stream()
                .map(String::length)
                .max(Integer::compareTo)
                .orElse(DEFAULT_WIDTH));
        }
    }

    public void appendCharInt(int charInt) {

    }

    public void appendCharEsc(Character c) {
     }

    public void appendString(String str) {

    }

    public void appendLine(String line) {

    }
}
