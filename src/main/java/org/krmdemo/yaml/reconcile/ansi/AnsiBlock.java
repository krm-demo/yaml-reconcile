package org.krmdemo.yaml.reconcile.ansi;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.krmdemo.yaml.reconcile.ansi.AnsiLine.emptyLine;

/**
 * This class represents a renderable rectangular block of screen around {@link AnsiText},
 * that could be built with horizontal alignment and indentations.
 */
public class AnsiBlock extends Layout {

    private final int width;
    private final List<AnsiLine> lines;

    private AnsiBlock(int width, List<AnsiLine> lines) {
        this.lines = lines;
        this.width = width;
    }

    @Override
    public int height() {
        return lines.size();
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public Stream<AnsiLine> lines() {
        return lines.stream();
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
    public List<AnsiLine> layoutRowAt(int rowNum) {
        return rowNum < 0 || rowNum >= height() ? emptyList() : singletonList(lineAt(rowNum));
    }

    @Override
    public String dump() {
        return format("Block(height:%d;width:%d)", height(), width());
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * A builder to create an instance of immutable object {@link AnsiBlock}
     */
    public static class Builder extends LayoutBuilder.Base<Builder> {
        private AnsiLine.Provider ansiLines = emptyLayout();
        private Integer contentWidth = null;
        private int leftIndentWidth = 0;
        private int rightIndentWidth = 0;
        private Function<Integer, AnsiLine> leftIndent = lineNum -> emptyLine();
        private Function<Integer, AnsiLine> rightIndent = lineNum -> emptyLine();
        private AlignHorizontal alignment = AlignHorizontal.LEFT;

        protected Builder() {
            // force to use "AnsiBlock.builder()" to instantiate this builder
        }

        public Builder ansiText(AnsiText ansiText) {
            return ansiLines(ansiText);
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

        public Builder alignment(AlignHorizontal alignment) {
            this.alignment = alignment;
            return this;
        }

        @Override
        public int height(BuildContext ctx) {
            return ansiLines.linesCount();
        }

        @Override
        public int width(BuildContext ctx) {
            int contentWidth = this.contentWidth != null ? this.contentWidth : ansiLines.maxWidth();
            return contentWidth + this.leftIndentWidth + this.rightIndentWidth;
        }

        public AnsiBlock build(BuildContext parentCtx) {
            BuildContext ctx = parentCtx.create(this);
            int contentWidth = this.contentWidth != null ? this.contentWidth : ansiLines.maxWidth();
            List<AnsiLine> lines = new ArrayList<>(ansiLines.linesCount());
            AnsiBlock ansiBlock = new AnsiBlock(width(), lines);
            ansiBlock.parent = ctx.parentLayout();
            ansiBlock.style = style;
            for (int i = 0; i < ansiLines.linesCount(); i++) {
                AnsiLine left = align(ctx, ansiBlock, leftIndentWidth, AlignHorizontal.RIGHT, leftIndent.apply(i));
                AnsiLine body = align(ctx, ansiBlock, contentWidth, alignment, ansiLines.lineAt(i));
                AnsiLine right = align(ctx, ansiBlock, rightIndentWidth, AlignHorizontal.LEFT, rightIndent.apply(i));
                lines.add(AnsiLine.create(ansiBlock, left, body, right));
//                System.out.println(i + ") body --> " + body.renderAnsi());
            }
            return ansiBlock;
        }

        private AnsiLine align(BuildContext ctx, AnsiBlock ansiBlock,
                               int targetWidth, AlignHorizontal alignHor,
                               AnsiLine sourceLine) {
            if (targetWidth <= 0) {
                return emptyLine();
            }
            int padding = targetWidth - sourceLine.width();
            if (padding > 0) {
                AnsiLine.Builder lineBuilder = AnsiLine.builder(ansiBlock);
                if (alignHor == AlignHorizontal.CENTER) {
                    addPadding(ctx, lineBuilder, padding / 2);
                } else if (alignHor == AlignHorizontal.RIGHT) {
                    addPadding(ctx, lineBuilder, padding);
                }
                lineBuilder.append(sourceLine.spans());
                if (alignHor == AlignHorizontal.CENTER) {
                    addPadding(ctx, lineBuilder, (padding / 2) + (padding % 2));
                } else if (alignHor == AlignHorizontal.LEFT) {
                    addPadding(ctx, lineBuilder, padding);
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

        private void addPadding(BuildContext ctx, AnsiLine.Builder lineBuilder, int paddingWidth) {
            if (paddingWidth > 0) {
                lineBuilder.append(AnsiSpan.create(repeat(ctx.paddingChar(), paddingWidth)));
            }
        }
    }
}
