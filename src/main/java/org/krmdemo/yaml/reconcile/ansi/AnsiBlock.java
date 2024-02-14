package org.krmdemo.yaml.reconcile.ansi;

import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.repeat;

/**
 * This class represents a renderable rectangular block of screen. In enriches the wrapped {@link AnsiText}
 * with horizontal alignment and padding with left and right indentations.
 */
public class AnsiBlock implements AnsiStyle.Holder, AnsiSize {

    public enum Horizontal {
        LEFT, CENTER, RIGHT
    }

    private final AnsiStyle.Holder parent;
    private final AnsiStyle style;
    private final List<AnsiLine> lines;
    private final int width;
    private final int height;

    private AnsiBlock(AnsiStyle.Holder parent, AnsiStyle style, List<AnsiLine> lines, int height, int widith) {
        this.parent = parent;
        this.style = style;
        this.lines = lines;
        this.height = height;
        this.width = widith;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public int width() {
        return width;
    }

    public AnsiLine lineAt(int lineNum) {
        return lines.get(lineNum);
    }

    @Setter
    static class Builder {
        private AnsiStyle.Holder parent = null;
        private AnsiStyle style = AnsiStyle.empty();
        private AnsiText ansiText = AnsiText.ansiText();
        private Integer contentWidth = null;
        private int leftIndentWidth = 0;
        private int rightIndentWidth = 0;
        private Function<Integer, AnsiLine> leftIndent = lineNum -> AnsiLine.empty();
        private Function<Integer, AnsiLine> rightIndent = lineNum -> AnsiLine.empty();
        private Horizontal horizontal = Horizontal.LEFT;
        private char paddingChar = ' ';

        public AnsiBlock build() {
            int contentWidth = this.contentWidth != null ? this.contentWidth : ansiText.width();
            int width = contentWidth + this.leftIndentWidth + this.rightIndentWidth;
            List<AnsiLine> lines = new ArrayList<>(ansiText.height());
            AnsiBlock ansiBlock = new AnsiBlock(parent, style, lines, ansiText.height(), width);
            for (int i = 0; i < ansiText.height(); i++) {
                AnsiLine left = align(ansiBlock, leftIndentWidth, Horizontal.LEFT, leftIndent.apply(i));
                AnsiLine body = align(ansiBlock, contentWidth, horizontal, ansiText.lineAt(i));
                AnsiLine right = align(ansiBlock, rightIndentWidth, Horizontal.RIGHT, rightIndent.apply(i));
                lines.add(AnsiLine.create(ansiBlock, left, body, right));
            }
            return ansiBlock;
        }

        private AnsiLine align(AnsiBlock ansiBlock, int targetWidth, Horizontal alignHor, AnsiLine sourceLine) {
            int padding = targetWidth - sourceLine.width();
            if (padding > 0) {
                AnsiLine.Builder lineBuilder = AnsiLine.builder(ansiBlock);
                if (alignHor == Horizontal.CENTER) {
                    addPadding(lineBuilder, padding / 2);
                } else if (alignHor == Horizontal.RIGHT) {
                    addPadding(lineBuilder, padding);
                }
                lineBuilder.append(sourceLine.spans());
                if (alignHor == Horizontal.CENTER) {
                    addPadding(lineBuilder, (padding / 2) + (padding % 2));
                } else if (alignHor == Horizontal.LEFT) {
                    addPadding(lineBuilder, padding);
                }
                return lineBuilder.build();
            }
            AnsiLine line = AnsiLine.create(ansiBlock, sourceLine.spans());
            if (padding < 0) {
                line = switch(alignHor) {
                    case Horizontal.LEFT -> line.subLine(ansiBlock, 0, targetWidth);
                    case Horizontal.CENTER -> line.subLine(ansiBlock,
                        - padding / 2,
                        targetWidth + (padding / 2));
                    case Horizontal.RIGHT -> line.subLine(ansiBlock, -padding, targetWidth);
                };
            }
            return line;
        }

        private void addPadding(AnsiLine.Builder lineBuilder, int paddingWidth) {
            if (paddingWidth > 0) {
                lineBuilder.append(AnsiSpan.create(style, repeat(paddingChar, paddingWidth)));
            }
        }
    }


}
