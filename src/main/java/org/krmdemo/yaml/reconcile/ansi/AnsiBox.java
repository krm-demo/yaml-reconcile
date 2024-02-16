package org.krmdemo.yaml.reconcile.ansi;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Supplier;

/**
 * This class tiles the sequence of {@link AnsiBlock} either horizontally or vertically
 * according to alignment and surround them with borders (described by predefined {@link AnsiBoxStyle.Kind}.
 */
public class AnsiBox {

    private final AnsiBlock ansiBlock;

    private AnsiStyle borderStyle; // <-- foreground and background color of boreder

    private AnsiBox(AnsiBlock ansiBlock) {
        this.ansiBlock = ansiBlock;
    }

    /**
     * TODO: implement
     */
    public static class Builder {

        private AnsiBoxStyle boxStyle;
        private AlignVertical vertical;
        private AlignHorizontal horizontal;

        private final List<AnsiBlock> ansiBlocks = new ArrayList<>();

    }

}
