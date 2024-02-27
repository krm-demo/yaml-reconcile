package org.krmdemo.yaml.reconcile.ansi;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static org.krmdemo.yaml.reconcile.ansi.AnsiBlock.emptyBlock;
import static org.krmdemo.yaml.reconcile.ansi.AnsiSize.max;
import static org.krmdemo.yaml.reconcile.ansi.AnsiSize.sum;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.emptyStyle;

/**
 * This class tiles the sequence of {@link AnsiBlock} either horizontally or vertically
 * according to alignment and surround them with borders (described by predefined {@link AnsiBorder.Kind}.
 */
public abstract class Layout implements AnsiSize, AnsiStyle.Holder {

    protected Layout parent = null;
    protected AnsiStyle style = emptyStyle();

    protected AnsiBorder boxStyle = AnsiBorder.NONE;
    protected char paddingChar = ' ';

    @Override
    public Optional<AnsiStyle.Holder> parent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public Optional<AnsiStyle> style() {
        return Optional.of(style);
    }

    public AnsiBorder boxStyle() {
        return boxStyle;
    }

    public char paddingChar() {
        return paddingChar;
    }

    public abstract List<AnsiLine> layoutRowAt(int rowNum);

    public abstract AnsiBlock build();

    private final static Layout LAYOUT_EMPTY = single(emptyBlock());

    private static class Blank extends Layout {
        private final int height;
        private final int width;

        private Blank(int height, int width) {
            if (height <= 0 || width <= 0) {
                throw new IllegalArgumentException("Attempt to create an empty blank layout");
            }
            this.height = height;
            this.width = width;
        }

        @Override
        public int height() {
            return height;
        }

        @Override
        public int width() {
            return width;
        }

        @Override
        public List<AnsiLine> layoutRowAt(int rowNum) {
            return singletonList(AnsiLine.blankLine(this, width(), paddingChar()));
        }

        @Override
        public AnsiBlock build() {
            return AnsiBlock.blankBlock(this, height(), width(), paddingChar());
        }
    }

    static Layout blank(int height, int width) {
        if (height <= 0 || width <= 0) {
            return LAYOUT_EMPTY;
        } else {
            return new Blank(height, width);
        }
    }

    private static class Single extends Layout {
        private final AnsiBlock ansiBlock;

        private Single(AnsiBlock ansiBlock) {
            if (ansiBlock == null || ansiBlock.isEmpty()) {
                throw new IllegalArgumentException("Attempt to create a single layout from null or empty block");
            }
            this.ansiBlock = ansiBlock.withParent(this);
        }

        @Override
        public int height() {
            return ansiBlock.height();
        }

        @Override
        public int width() {
            return ansiBlock.width();
        }

        @Override
        public List<AnsiLine> layoutRowAt(int rowNum) {
            return singletonList(ansiBlock.lineAt(rowNum));
        }

        @Override
        public AnsiBlock build() {
            return ansiBlock;
        }
    }

    static Layout single(AnsiBlock ansiBlock) {
        if (ansiBlock.isEmpty()) {
            return LAYOUT_EMPTY;
        } else {
            return new Single(ansiBlock);
        }
    }

    private static class Horizontal extends Layout {
        private final List<Layout> children;

        private Horizontal(Layout... childrenArr) {
            this(stream(childrenArr));
        }

        private Horizontal(List<Layout> children) {
            this(children.stream());
        }

        private Horizontal(Stream<Layout> childrenStream) {
            this.children = childrenStream.peek(child -> child.parent = this).toList();
            if (this.children.isEmpty()) {
                throw new IllegalArgumentException("Attempt to create an empty horizontal layout");
            }
        }

        public int height() {
            return max(AnsiSize::height, children);
        }

        public int width() {
            // TODO: internal border width is not counted yet
            return sum(AnsiSize::width, children);
        }

        @Override
        public List<AnsiLine> layoutRowAt(int rowNum) {
            return null;
        }

        @Override
        public AnsiBlock build() {
            // TODO: implement
            return emptyBlock();
        }
    }

    private static class Vertical extends Layout {
        private final List<Layout> children;

        private Vertical(Layout... childrenArr) {
            this(stream(childrenArr));
        }

        private Vertical(List<Layout> children) {
            this(children.stream());
        }

        private Vertical(Stream<Layout> childrenStream) {
            this.children = childrenStream.peek(child -> child.parent = this).toList();
            if (this.children.isEmpty()) {
                throw new IllegalArgumentException("Attempt to create an empty vertical layout");
            }
        }

        public int height() {
            // TODO: internal border height is not counted yet
            return sum(AnsiSize::height, children);
        }

        public int width() {
            return max(AnsiSize::width, children);
        }

        @Override
        public List<AnsiLine> layoutRowAt(int rowNum) {
            return null;
        }

        @Override
        public AnsiBlock build() {
            // TODO: implement
            return emptyBlock();
        }
    }

    static Layout alignHorz(Layout layout, AlignHorizontal alignment, int targetWidth) {
        if (layout.width() >= targetWidth) {
            return layout;
        }
        int blankTotal = targetWidth - layout.width();
        int blankLeft = blankTotal / 2; // <-- it could be zero, if `blankTotal` equals to `1`
        int blankRight = blankTotal - blankLeft;
        if (alignment == AlignHorizontal.LEFT) {
            return new Horizontal(layout, blank(layout.height(), blankTotal));
        } else if (alignment == AlignHorizontal.RIGHT || targetWidth == layout.width() + 1) {
            return new Horizontal(blank(layout.height(), blankTotal), layout);
        } else { // it implies that alignment is AlignHorizontal.CENTER and both parts are not empty
            return new Horizontal(blank(layout.height(), blankLeft), layout, blank(layout.height(), blankRight));
        }
    }

    static Layout alignVert(Layout layout, AlignVertical alignment, int targetHeight) {
        if (layout.height() >= targetHeight) {
            return layout;
        }
        int blankTotal = targetHeight - layout.height();
        int blankTop = blankTotal / 2; // <-- it could be zero, if `blankTotal` equals to `1`
        int blankBottom = blankTotal - blankTop;
        if (alignment == AlignVertical.TOP) {
            return new Vertical(layout, blank(blankTotal, layout.width()));
        } else if (alignment == AlignVertical.BOTTOM || blankTop == 0) {
            return new Vertical(blank(blankTotal, layout.width()), layout);
        } else { // it implies that alignment is AlignVertical.MIDDLE and both parts are not empty
            return new Vertical(blank(blankTop, layout.width()), layout, blank(blankBottom, layout.width()));
        }
    }

    static Layout horizontal(AlignVertical alignment, List<Layout> children) {
        int maxHeight = max(AnsiSize::height, children);
        if (maxHeight <= 0) {
            return LAYOUT_EMPTY;
        }
        return new Horizontal(
            children.stream()
                .filter(AnsiSize::isNotEmpty)
                .map(child -> alignVert(child, alignment, maxHeight))
        );
    }

    static Layout vertical(AlignHorizontal alignment, List<Layout> children) {
        int maxWidth = max(AnsiSize::width, children);
        if (maxWidth <= 0) {
            return LAYOUT_EMPTY;
        }
        return new Vertical(
            children.stream()
                .filter(AnsiSize::isNotEmpty)
                .map(child -> alignHorz(child, alignment, maxWidth))
        );
    }
}
