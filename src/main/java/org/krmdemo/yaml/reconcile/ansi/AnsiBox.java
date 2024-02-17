package org.krmdemo.yaml.reconcile.ansi;

import java.util.*;

/**
 * This class tiles the sequence of {@link AnsiBlock} either horizontally or vertically
 * according to alignment and surround them with borders (described by predefined {@link AnsiBoxStyle.Kind}.
 */
public class AnsiBox {

    private final AnsiBlock ansiBlock;

    private final AnsiBoxStyle boxStyle;

    private AnsiBox(AnsiBlock ansiBlock, AnsiBoxStyle boxStyle) {
        this.ansiBlock = ansiBlock;
        this.boxStyle = boxStyle;
    }

    /**
     * A builder to create an instance of immutable object {@link AnsiBox}
     */
    public static class Builder {

        private AnsiBoxStyle boxStyle;
        private Integer width = null;
        private Integer height = null;
        private AlignVertical vertical = AlignVertical.TOP;
        private AlignHorizontal horizontal = AlignHorizontal.LEFT;

        private final List<AnsiBlock> ansiBlocks = new ArrayList<>();

        public Builder tileHor(AnsiBlock ansiBlockHor) {
            // only one block for now... TODO: implement multiple
            ansiBlocks.clear();
            ansiBlocks.add(ansiBlockHor);
            return this;
        }

        public Builder width(Integer width) {
            this.width = width;
            return this;
        }

        public Builder height(Integer height) {
            this.height = height;
            return this;
        }

        public AnsiBox build() {
            return null; // TODO: implement !!!
        }
    }

}
