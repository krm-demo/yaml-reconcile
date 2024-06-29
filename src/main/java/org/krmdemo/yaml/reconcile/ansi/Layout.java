package org.krmdemo.yaml.reconcile.ansi;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;
import static org.krmdemo.yaml.reconcile.ansi.AnsiSize.max;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.emptyStyle;
import static org.krmdemo.yaml.reconcile.ansi.LayoutBuilder.BuildContext.defaultContext;

/**
 * This class tiles the sequence of {@link AnsiBlock} either horizontally or vertically
 * according to alignment and surround them with borders (described by predefined {@link AnsiBorder.Kind}.
 */
public abstract class Layout implements LayoutBuilder, AnsiSize, AnsiStyle.Holder, AnsiLine.Provider, Renderable {

    protected Supplier<Layout> parent = NO_PARENT;
    protected AnsiStyle style = emptyStyle();

    @Override
    public Layout build() {
        return this;
    }

    @Override
    public Optional<AnsiStyle.Holder> parent() {
        return Optional.ofNullable(parent.get());
    }

    @Override
    public Optional<AnsiStyle> style() {
        return Optional.of(style);
    }

    public abstract List<AnsiLine> layoutRowAt(int rowNum);

    @Override
    public int linesCount() {
        return height();
    }

    @Override
    public int maxWidth() {
        return width();
    }

    @Override
    public AnsiLine lineAt(int lineNum) {
        List<AnsiLine> row = layoutRowAt(lineNum);
//        System.out.println("lineAt(" + lineNum + ") contains " + row.size() + " lines:");
//        row.forEach(System.out::println);
        return AnsiLine.create(row.stream().flatMap(AnsiLine::spans));
    }

    /**
     * @return sequence of ansi-styles that open each span
     */
    public Stream<AnsiStyle> spanStylesOpen() {
        return lines().flatMap(AnsiLine::spans).map(AnsiSpan::styleOpen);
    }

    @Override
    public String content() {
        return lines().map(AnsiLine::content).collect(joining(lineSeparator()));
    }

    @Override
    public String renderAnsi() {
        return lines().map(AnsiLine::renderAnsi).collect(joining(lineSeparator()));
    }

    @Override
    public String toString() {
        return dump();
    }

    private final static Layout LAYOUT_EMPTY = new Layout() {
        @Override public int height() { return 0; }
        @Override public int width() { return 0; }
        @Override public List<AnsiLine> layoutRowAt(int rowNum) { return emptyList(); }
        @Override public String dump() { return "EMPTY"; }
    };

    public static Layout emptyLayout() {
        return LAYOUT_EMPTY;
    }

    static class Blank extends Layout {
        private final AnsiLine blankLine;
        private final List<AnsiLine> blankRow;
        private final int height;
        private final int width;

        Blank(BuildContext ctx, AnsiStyle style, int height, int width) {
            if (height <= 0 || width <= 0) {
                throw new IllegalArgumentException("Attempt to create an empty blank layout");
            }
            this.parent = ctx.parentLayout();
            this.style = style;
            this.blankLine = AnsiLine.blankLine(this, width, ctx.paddingChar());
            this.blankRow = singletonList(this.blankLine);
            this.height = height;
            this.width = width;
        }

        @Override
        public int height() {
            return this.height;
        }

        @Override
        public int width() {
            return this.width;
        }

        @Override
        public List<AnsiLine> layoutRowAt(int rowNum) {
            return this.blankRow;
        }

        protected String dumpName() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String dump() {
            return format("%s(height:%d;width:%d)", dumpName(), height(), width());
        }
    }

    public static Layout blank(BuildContext ctx, int height, int width) {
        return blank(ctx, emptyStyle(), height, width);
    }

    public static Layout blank(BuildContext ctx, AnsiStyle style, int height, int width) {
        if (height <= 0 || width <= 0) {
            return LAYOUT_EMPTY;
        } else {
            return new Blank(ctx, style, height, width);
        }
    }

    static class Symbol extends Blank {
        private final char symbol;
        private final String dumpName;
        Symbol(BuildContext ctx, char symbol) {
            this(ctx, symbol, "Symbol");
        }
        Symbol(BuildContext ctx, char symbol, String dumpName) {
            super(ctx.withPaddingChar(symbol), emptyStyle(), 1, 1);  // <-- TODO: maybe empty-style is not correct
            this.symbol = symbol;
            this.dumpName = dumpName;
        }
        @Override
        protected String dumpName() {
            return this.dumpName;
        }
        @Override
        public String dump() {
            return format("%s('%s')", dumpName(), this.symbol);
        }
    }

    public static Layout symbol(BuildContext ctx, char symbol) {
        return new Symbol(ctx, symbol);
    }

    public static Layout symbol(BuildContext ctx, char symbol, String dumpName) {
        return new Symbol(ctx, symbol, dumpName);
    }

    static class HorizontalBar extends Blank {
        private final char symbol;
        private final String dumpName;
        HorizontalBar(BuildContext ctx, int width, char symbol) {
            this(ctx, width, symbol, "HorizontalBar");
        }
        HorizontalBar(BuildContext ctx, int width, char symbol, String dumpName) {
            super(ctx.withPaddingChar(symbol), emptyStyle(), 1, width);  // <-- TODO: maybe empty-style is not correct
            this.symbol = symbol;
            this.dumpName = dumpName;
        }
        @Override
        protected String dumpName() {
            return this.dumpName;
        }
        @Override
        public String dump() {
            return format("%s(w:%d'%s')", dumpName(), width(), this.symbol);
        }
    }

    public static Layout horizontalBar(BuildContext ctx, int width, char symbol) {
        return width <= 0 ? LAYOUT_EMPTY : new HorizontalBar(ctx, width, symbol);
    }

    public static Layout horizontalBar(BuildContext ctx, int width, char symbol, String dumpName) {
        return width <= 0 ? LAYOUT_EMPTY : new HorizontalBar(ctx, width, symbol, dumpName);
    }

    static class VerticalBar extends Blank {
        private final char symbol;
        private final String dumpName;
        VerticalBar(BuildContext ctx, int height, char symbol) {
            this(ctx, height, symbol, "VerticalBar");
        }
        VerticalBar(BuildContext ctx, int height, char symbol, String dumpName) {
            super(ctx.withPaddingChar(symbol), emptyStyle(), height, 1);  // <-- TODO: maybe empty-style is not correct
            this.symbol = symbol;
            this.dumpName = dumpName;
        }
        @Override
        protected String dumpName() {
            return this.dumpName;
        }
        @Override
        public String dump() {
            return format("%s(h:%d'%s')", dumpName(), height(), this.symbol);
        }
    }

    public static Layout verticalBar(BuildContext ctx, int height, char symbol) {
        return height <= 0 ? LAYOUT_EMPTY : new VerticalBar(ctx, height, symbol);
    }

    public static Layout verticalBar(BuildContext ctx, int height, char symbol, String dumpName) {
        return height <= 0 ? LAYOUT_EMPTY : new VerticalBar(ctx, height, symbol, dumpName);
    }

    abstract static class Composite extends Layout {
        protected int height;
        protected int width;

        @Override
        public int height() {
            return height;
        }

        @Override
        public int width() {
            return width;
        }
    }

    static class Horizontal extends Composite {
        private final SortedMap<Integer,Layout> childrenByPos = new TreeMap<>();

        Horizontal(BuildContext ctx, Layout... childrenArr) {
            this(ctx, asList(childrenArr));
        }

        Horizontal(BuildContext ctx, List<Layout> children) {
            if (children.isEmpty()) {
                throw new IllegalArgumentException("Attempt to create an empty horizontal layout");
            }
            this.parent = ctx.parentLayout();
            int maxHeight = 0;
            int totalWidth = 0;
            for (Layout child : children) {
                totalWidth += child.width();
                if (child.height() > maxHeight) {
                    maxHeight = child.height();
                }
                this.childrenByPos.put(totalWidth - 1, child);
            }
            this.height = maxHeight;
            this.width = totalWidth;
        }

        @Override
        public List<AnsiLine> layoutRowAt(int rowNum) {
            return childrenByPos.values().stream()
                .map(child -> child.layoutRowAt(rowNum))
                .flatMap(List::stream)
                .toList();
        }

        @Override
        public String dump() {
            return format("Horizontal(height:%d;width:%d; %d children)", height(), width(), childrenByPos.size());
        }
    }

    static class Vertical extends Composite {
        private final SortedMap<Integer,Layout> childrenByRow = new TreeMap<>();

        Vertical(BuildContext ctx, Layout... childrenArr) {
            this(ctx, asList(childrenArr));
        }

        Vertical(BuildContext ctx, List<Layout> children) {
            if (children.isEmpty()) {
                throw new IllegalArgumentException("Attempt to create an empty vertical layout");
            }
            this.parent = ctx.parentLayout();
            int totalHeight = 0;
            int maxWidth = 0;
            for (Layout child : children) {
                totalHeight += child.height();
                if (child.width() > maxWidth) {
                    maxWidth = child.width();
                }
                this.childrenByRow.put(totalHeight - 1, child);
            }
            this.height = totalHeight;
            this.width = maxWidth;
        }

        public List<AnsiLine> layoutRowAt(int rowNum) {
            if (rowNum < 0 || rowNum >= height()) {
                return emptyList();
            }
            var firstEntry = childrenByRow.firstEntry();
            if (rowNum <= firstEntry.getKey()) {
                return firstEntry.getValue().layoutRowAt(rowNum);
            }
            Integer lastEnd = childrenByRow.headMap(rowNum).lastKey();
            Integer currEnd = childrenByRow.tailMap(rowNum).firstKey();
            Layout child = childrenByRow.get(currEnd);
            return child.layoutRowAt(rowNum - lastEnd - 1);
        }

        @Override
        public String dump() {
            return format("Vertical(height:%d;width:%d; %d children)", height(), width(), childrenByRow.size());
        }
    }
}
