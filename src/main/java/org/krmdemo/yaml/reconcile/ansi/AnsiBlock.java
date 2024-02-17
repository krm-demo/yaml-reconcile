package org.krmdemo.yaml.reconcile.ansi;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.krmdemo.yaml.reconcile.ansi.AnsiLine.emptyBlock;
import static org.krmdemo.yaml.reconcile.ansi.AnsiLine.emptyLine;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.ansiStyle;
import static org.krmdemo.yaml.reconcile.ansi.AnsiStyle.emptyStyle;

/**
 * This class represents a renderable rectangular block of screen. In enriches the wrapped {@link AnsiText}
 * with horizontal alignment and padding with left and right indentations.
 */
public class AnsiBlock implements AnsiStyle.Holder, AnsiSize, AnsiLine.Provider, Renderable {

    private final AnsiStyle.Holder parent;
    private final AnsiStyle style;
    private final List<AnsiLine> lines;
    private final int width;
    private final int height;

    AnsiBlock(AnsiStyle.Holder parent, AnsiStyle style, List<AnsiLine> lines, int height, int widith) {
        this.parent = parent;
        this.style = style;
        this.lines = lines;
        this.height = height;
        this.width = widith;
    }

    @Override
    public Optional<AnsiStyle> style() {
        return Optional.ofNullable(style);
    }

    @Override
    public Optional<AnsiStyle.Holder> parent() {
        return Optional.ofNullable(this.parent);
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
    public int linesCount() {
        return height();
    }

    @Override
    public int maxWidth() {
        return width();
    }

    public AnsiLine lineAt(int lineNum) {
        return lines.get(lineNum);
    }

    @Override
    public String content() {
        return lines.stream().map(AnsiLine::content).collect(joining(lineSeparator()));
    }

    @Override
    public String renderAnsi() {
        return lines.stream().map(AnsiLine::renderAnsi).collect(joining(lineSeparator()));
    }

    /**
     * @return sequence of ansi-styles that open each span
     */
    public Stream<AnsiStyle> spanStylesOpen() {
        return  lines.stream().flatMap(AnsiLine::spans).map(AnsiSpan::styleOpen);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * A builder to create an instance of immutable object {@link AnsiBlock}
     */
    public static class Builder {
        private AnsiStyle.Holder parent = null;
        private AnsiStyle style = emptyStyle();
        private AnsiLine.Provider ansiLines = emptyBlock();
        private Integer contentWidth = null;
        private int leftIndentWidth = 0;
        private int rightIndentWidth = 0;
        private Function<Integer, AnsiLine> leftIndent = lineNum -> emptyLine();
        private Function<Integer, AnsiLine> rightIndent = lineNum -> emptyLine();
        private AlignHorizontal horizontal = AlignHorizontal.LEFT;
        private char paddingChar = ' ';

        protected Builder() {
            // force to use "AnsiBlock.builder()" to instantiate this builder
        }

        public Builder parent(AnsiStyle.Holder parent) {
            this.parent = parent;
            return this;
        }

        public Builder style(AnsiStyle style) {
            this.style = style;
            return this;
        }

        public Builder style(AnsiStyleAttr... styleAttrs) {
            this.style = ansiStyle(styleAttrs);
            return this;
        }

        public Builder ansiText(AnsiText ansiText) {
            return ansiLines(ansiText);
        }

        public Builder ansiBlock(AnsiBlock ansiBlock) {
            return ansiLines(ansiBlock);
        }

        public Builder ansiLines(AnsiLine.Provider ansiLines) {
            this.ansiLines = ansiLines;
            return this;
        }

        public Builder contentWidth(Integer contentWidth) {
            this.contentWidth = contentWidth;
            return this;
        }

        public Builder leftIndentWidth(int leftIndentWidth) {
            this.leftIndentWidth = leftIndentWidth;
            return this;
        }

        public Builder rightIndentWidth(int rightIndentWidth) {
            this.rightIndentWidth = rightIndentWidth;
            return this;
        }

        public Builder leftIndent(Function<Integer, AnsiLine> leftIndent) {
            this.leftIndent = leftIndent;
            return this;
        }

        public Builder rightIndent(Function<Integer, AnsiLine> rightIndent) {
            this.rightIndent = rightIndent;
            return this;
        }

        public Builder horizontal(AlignHorizontal horizontal) {
            this.horizontal = horizontal;
            return this;
        }

        public Builder paddingChar(char paddingChar) {
            this.paddingChar = paddingChar;
            return this;
        }

        public AnsiBlock build() {
            int contentWidth = this.contentWidth != null ? this.contentWidth : ansiLines.maxWidth();
            int width = contentWidth + this.leftIndentWidth + this.rightIndentWidth;
            List<AnsiLine> lines = new ArrayList<>(ansiLines.linesCount());
            AnsiBlock ansiBlock = new AnsiBlock(parent, style, lines, ansiLines.linesCount(), width);
            for (int i = 0; i < ansiLines.linesCount(); i++) {
                AnsiLine left = align(ansiBlock, leftIndentWidth, AlignHorizontal.RIGHT, leftIndent.apply(i));
                AnsiLine body = align(ansiBlock, contentWidth, horizontal, ansiLines.lineAt(i));
                AnsiLine right = align(ansiBlock, rightIndentWidth, AlignHorizontal.LEFT, rightIndent.apply(i));
                lines.add(AnsiLine.create(ansiBlock, left, body, right));
            }
            return ansiBlock;
        }

        private AnsiLine align(AnsiBlock ansiBlock, int targetWidth, AlignHorizontal alignHor, AnsiLine sourceLine) {
            int padding = targetWidth - sourceLine.width();
            if (padding > 0) {
                AnsiLine.Builder lineBuilder = AnsiLine.builder(ansiBlock);
                if (alignHor == AlignHorizontal.CENTER) {
                    addPadding(lineBuilder, padding / 2);
                } else if (alignHor == AlignHorizontal.RIGHT) {
                    addPadding(lineBuilder, padding);
                }
                lineBuilder.append(sourceLine.spans());
                if (alignHor == AlignHorizontal.CENTER) {
                    addPadding(lineBuilder, (padding / 2) + (padding % 2));
                } else if (alignHor == AlignHorizontal.LEFT) {
                    addPadding(lineBuilder, padding);
                }
                return lineBuilder.build();
            }
            AnsiLine line = AnsiLine.create(ansiBlock, sourceLine.spans());
            if (padding < 0) {
                line = switch(alignHor) {
                    case AlignHorizontal.LEFT -> line.subLine(ansiBlock, 0, targetWidth);
                    case AlignHorizontal.CENTER -> line.subLine(ansiBlock, - (padding/2), targetWidth - (padding/2));
                    case AlignHorizontal.RIGHT -> line.subLine(ansiBlock, -padding, sourceLine.width());
                };
            }
            return line;
        }

        private void addPadding(AnsiLine.Builder lineBuilder, int paddingWidth) {
            if (paddingWidth > 0) {
                lineBuilder.append(AnsiSpan.create(repeat(paddingChar, paddingWidth)));
            }
        }
    }


}
